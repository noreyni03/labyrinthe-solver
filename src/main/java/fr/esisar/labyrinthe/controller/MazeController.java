package fr.esisar.labyrinthe.controller;

import fr.esisar.labyrinthe.algorithm.BFSSolver;
import fr.esisar.labyrinthe.algorithm.DFSSolver;
import fr.esisar.labyrinthe.algorithm.AStarSolver;
import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.generator.MazeGenerator;
import fr.esisar.labyrinthe.model.MazeAnalyzer;
import fr.esisar.labyrinthe.ui.AlgorithmComparisonView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Contrôleur pour l'application de résolution de labyrinthes.
 */
public class MazeController {
    @FXML private ScrollPane scrollPane;
    @FXML private Canvas mazeCanvas;
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private CheckBox animationCheck;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private Maze maze;
    private GraphicsContext gc;
    private double cellSize;
    private AnimatedBFSSolver animatedBFSSolver;
    private AnimatedDFSSolver animatedDFSSolver;
    private AnimatedAStarSolver animatedAStarSolver;

    /**
     * Initialise le contrôleur.
     */
    @FXML
    public void initialize() {
        gc = mazeCanvas.getGraphicsContext2D();
        algorithmCombo.getItems().addAll("BFS", "DFS", "A*");
        algorithmCombo.setValue("BFS"); // Algorithme par défaut
        progressBar.setProgress(0.0);

        // Lier la taille du Canvas à la ScrollPane
        mazeCanvas.widthProperty().bind(
                scrollPane.widthProperty()
                        .subtract(scrollPane.getPadding().getLeft() + scrollPane.getPadding().getRight())
        );
        mazeCanvas.heightProperty().bind(
                scrollPane.heightProperty()
                        .subtract(scrollPane.getPadding().getTop() + scrollPane.getPadding().getBottom())
        );

        // Redessiner lors du redimensionnement
        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> drawMaze());
    }

    /**
     * Ouvre une fenêtre pour comparer les performances des algorithmes.
     */
    @FXML
    private void handleCompareAlgorithms() {
        if (maze == null) {
            showError("Erreur", "Aucun labyrinthe chargé ou généré.");
            return;
        }

        MazeAnalyzer analyzer = new MazeAnalyzer(maze);
        AlgorithmComparisonView comparisonView = new AlgorithmComparisonView(analyzer);

        Stage stage = new Stage();
        stage.setTitle("Comparaison des Algorithmes");
        stage.setScene(new Scene(comparisonView, 800, 600));
        stage.show();
    }

    /**
     * Affiche un guide d'utilisation expliquant comment utiliser l'application.
     */
    @FXML
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guide d'Utilisation");
        alert.setHeaderText("Comment utiliser l'application Labyrinthe Solver");

        String guideText =
                "Bienvenue dans l'application Labyrinthe Solver !\n\n" +
                        "Voici comment utiliser les différentes fonctionnalités :\n\n" +
                        "1. **Charger un labyrinthe :**\n" +
                        "   - Cliquez sur le bouton 'Charger' (l'icône de dossier).\n" +
                        "   - Sélectionnez un fichier texte (.txt) représentant votre labyrinthe.\n" +
                        "   - Le labyrinthe s'affichera dans la zone centrale.\n\n" +
                        "   *Format du fichier texte :*\n" +
                        "     '#' représente un mur, ' ' un chemin, 'S' le point de départ, et 'E' le point d'arrivée.\n" +
                        "    Assurez-vous que le labyrinthe est rectangulaire (toutes les lignes ont la même longueur).\n\n" +

                        "2. **Générer un labyrinthe :**\n" +
                        "   - Cliquez sur le bouton 'Générer' (l'icône de dés).\n" +
                        "   - Un labyrinthe aléatoire sera créé et affiché.\n\n" +

                        "3. **Résoudre le labyrinthe :**\n" +
                        "   - Sélectionnez un algorithme dans la liste déroulante ('BFS', 'DFS', ou 'A*').\n" +
                        "   - Si vous souhaitez voir la résolution étape par étape, cochez la case 'Animation'.\n" +
                        "   - Cliquez sur le bouton 'Résoudre' (l'icône d'éclair).\n" +
                        "   - Le chemin trouvé sera affiché en jaune.\n\n" +

                        "4. **Comparer les algorithmes :**\n" +
                        "   - Cliquez sur le bouton 'Comparer' (l'icône de graphique).\n" +
                        "   - Une nouvelle fenêtre s'ouvrira, affichant les statistiques de performance de chaque algorithme (nombre d'étapes, temps d'exécution, longueur du chemin).\n\n" +

                        "5. **Barre de statut :**\n" +
                        "   - La barre en bas de la fenêtre affiche l'état actuel de l'application (par exemple, 'Prêt', 'Labyrinthe chargé', 'Labyrinthe résolu').\n" +
                        "   - La barre de progression montre l'avancement de l'animation (si activée).\n\n" +

                        "Conseils :\n" +
                        "- Vous pouvez zoomer et vous déplacer dans le labyrinthe en utilisant la molette de la souris et les barres de défilement.\n" +
                        "- Si une erreur survient, une boîte de dialogue s'affichera avec des informations sur le problème.";


        alert.setContentText(guideText);
        alert.getDialogPane().setMinWidth(600); // Ajuste la largeur pour une meilleure lisibilité
        alert.showAndWait();
    }

    /**
     * Gère le chargement d'un labyrinthe à partir d'un fichier.
     */
    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier de labyrinthe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                maze = new Maze(Path.of(file.toURI()));
                drawMaze();
                statusLabel.setText("Labyrinthe chargé : " + file.getName());
            } catch (Exception e) {
                showError("Erreur de chargement", "Échec du chargement du labyrinthe : " + e.getMessage());
            }
        }
    }

    /**
     * Gère la génération d'un nouveau labyrinthe.
     */
    @FXML
    private void handleGenerateMaze() {
        maze = MazeGenerator.generate(25, 25, MazeGenerator.Algorithm.RECURSIVE_BACKTRACKING);
        drawMaze();
        statusLabel.setText("Labyrinthe généré (25x25)");
    }

    /**
     * Gère la résolution du labyrinthe en utilisant l'algorithme sélectionné.
     */
    @FXML
    private void handleSolve() {
        if (maze == null) {
            showError("Erreur", "Aucun labyrinthe chargé ou généré.");
            return;
        }

        if (algorithmCombo.getValue() == null) {
            showError("Erreur", "Veuillez sélectionner un algorithme.");
            return;
        }

        // Arrêter toute animation en cours
        if (animatedBFSSolver != null) animatedBFSSolver.stop();
        if (animatedDFSSolver != null) animatedDFSSolver.stop();
        if (animatedAStarSolver != null) animatedAStarSolver.stop();

        progressBar.setProgress(0.0);

        // Calculer la taille des cellules et les décalages pour centrer le labyrinthe
        double cellWidth = mazeCanvas.getWidth() / maze.getCols();
        double cellHeight = mazeCanvas.getHeight() / maze.getRows();
        cellSize = Math.min(cellWidth, cellHeight);
        double offsetX = (mazeCanvas.getWidth() - (maze.getCols() * cellSize)) / 2;
        double offsetY = (mazeCanvas.getHeight() - (maze.getRows() * cellSize)) / 2;

        if (animationCheck.isSelected()) {
            solveWithAnimation(offsetX, offsetY);
        } else {
            solveWithoutAnimation();
        }
    }

    /**
     * Résout le labyrinthe sans animation.
     */
    private void solveWithoutAnimation() {
        char[][] solvedGrid;
        String selectedAlgorithm = algorithmCombo.getValue();
        switch (selectedAlgorithm) {
            case "BFS":
                solvedGrid = BFSSolver.solve(maze).getGrid();
                break;
            case "DFS":
                solvedGrid = DFSSolver.solve(maze).getGrid();
                break;
            case "A*":
                solvedGrid = AStarSolver.solve(maze).getGrid();
                break;
            default:
                throw new IllegalStateException("Algorithme sélectionné invalide.");
        }

        drawSolution(solvedGrid);
        statusLabel.setText("Labyrinthe résolu avec " + selectedAlgorithm);
    }

    /**
     * Résout le labyrinthe avec une animation.
     */
    private void solveWithAnimation(double offsetX, double offsetY) {
        CompletableFuture<char[][]> future;
        switch (algorithmCombo.getValue()) {
            case "BFS":
                animatedBFSSolver = new AnimatedBFSSolver(maze, gc, cellSize, offsetX, offsetY, progress -> progressBar.setProgress(progress));
                future = animatedBFSSolver.solveBFS();
                break;
            case "DFS":
                animatedDFSSolver = new AnimatedDFSSolver(maze, gc, cellSize, offsetX, offsetY, progress -> progressBar.setProgress(progress));
                future = animatedDFSSolver.solveDFS();
                break;
            case "A*":
                animatedAStarSolver = new AnimatedAStarSolver(maze, gc, cellSize, offsetX, offsetY, progress -> progressBar.setProgress(progress));
                future = animatedAStarSolver.solveAStar();
                break;
            default:
                showError("Erreur", "L'algorithme n'est pas supporté avec l'animation.");
                return;
        }

        future.thenAccept(this::drawSolution).exceptionally(e -> {
            showError("Erreur", e.getMessage());
            return null;
        });

        statusLabel.setText("Labyrinthe résolu avec " + algorithmCombo.getValue() + " (animation)");
    }

    /**
     * Dessine le labyrinthe sur le canvas, centré.
     */
    private void drawMaze() {
        if (maze == null) return;

        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());

        // Calculer la taille des cellules
        double cellWidth = mazeCanvas.getWidth() / maze.getCols();
        double cellHeight = mazeCanvas.getHeight() / maze.getRows();
        cellSize = Math.min(cellWidth, cellHeight);

        // Calculer les marges pour centrer le labyrinthe
        double offsetX = (mazeCanvas.getWidth() - (maze.getCols() * cellSize)) / 2;
        double offsetY = (mazeCanvas.getHeight() - (maze.getRows() * cellSize)) / 2;

        // Dessiner le labyrinthe
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                gc.setFill(getCellColor(i, j, maze.getGrid()));
                gc.fillRect(
                        offsetX + j * cellSize,
                        offsetY + i * cellSize,
                        cellSize,
                        cellSize
                );
            }
        }
    }

    /**
     * Dessine le labyrinthe résolu sur le canvas, centré.
     *
     * @param solvedGrid La grille résolue à dessiner.
     */
    private void drawSolution(char[][] solvedGrid) {
        if (maze == null) return;

        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());

        // Calculer la taille des cellules
        double cellWidth = mazeCanvas.getWidth() / maze.getCols();
        double cellHeight = mazeCanvas.getHeight() / maze.getRows();
        cellSize = Math.min(cellWidth, cellHeight);

        // Calculer les marges pour centrer le labyrinthe
        double offsetX = (mazeCanvas.getWidth() - (maze.getCols() * cellSize)) / 2;
        double offsetY = (mazeCanvas.getHeight() - (maze.getRows() * cellSize)) / 2;

        // Dessiner le labyrinthe résolu
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                gc.setFill(getCellColor(i, j, solvedGrid));
                gc.fillRect(
                        offsetX + j * cellSize,
                        offsetY + i * cellSize,
                        cellSize,
                        cellSize
                );
            }
        }
    }

    /**
     * Retourne la couleur pour une cellule en fonction de son contenu.
     *
     * @param x    L'indice de la ligne de la cellule.
     * @param y    L'indice de la colonne de la cellule.
     * @param grid La grille contenant la cellule.
     * @return La couleur pour la cellule.
     */
    private Color getCellColor(int x, int y, char[][] grid) {
        if (grid[x][y] == '#') return Color.BLACK; // Mur
        if (grid[x][y] == 'S') return Color.GREEN; // Départ
        if (grid[x][y] == 'E') return Color.RED;   // Arrivée
        if (grid[x][y] == '+') return Color.YELLOW; // Chemin
        return Color.WHITE; // Espace vide
    }

    /**
     * Affiche une boîte de dialogue d'erreur.
     *
     * @param title   Le titre de la boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}