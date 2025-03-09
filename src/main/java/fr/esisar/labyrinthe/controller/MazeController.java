package fr.esisar.labyrinthe.controller;

import fr.esisar.labyrinthe.algorithm.BFSSolver;
import fr.esisar.labyrinthe.algorithm.DFSSolver;
import fr.esisar.labyrinthe.algorithm.AStarSolver;
import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.generator.MazeGenerator;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Contrôleur pour l'application de résolution de labyrinthes.
 * Gère les interactions de l'interface utilisateur et les opérations sur les labyrinthes.
 */
public class MazeController {
    @FXML private Canvas mazeCanvas;
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private CheckBox animationCheck;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private Maze maze;
    private GraphicsContext gc;
    private double cellSize = 30;
    private AnimatedSolver animatedSolver;

    /**
     * Initialise le contrôleur.
     * Configure le canvas et la boîte de sélection d'algorithme.
     */
    @FXML
    public void initialize() {
        gc = mazeCanvas.getGraphicsContext2D();
        algorithmCombo.getItems().addAll("BFS", "DFS", "A*");
        algorithmCombo.setValue("BFS"); // Algorithme par défaut
        progressBar.setProgress(0.0);
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
        if (animatedSolver!=null){
            animatedSolver.stop();
        }
        progressBar.setProgress(0.0);
        if (animationCheck.isSelected()) {

            solveWithAnimation();

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
    private void solveWithAnimation(){
        animatedSolver = new AnimatedSolver(maze, gc, cellSize, progress -> {
            progressBar.setProgress(progress);
        });
        CompletableFuture<char[][]> future;
        switch (algorithmCombo.getValue()) {
            case "BFS":
                future = animatedSolver.solveBFS();
                break;
            default:
                showError("Erreur", "L'algorithme n'est pas supporté avec l'animation.");
                return;
        }
        future.thenAccept(this::drawSolution).exceptionally(e -> {
            showError("Erreur",e.getMessage());
            return null;
        });
        statusLabel.setText("Labyrinthe résolu avec " + algorithmCombo.getValue() + " (animation)");
    }

    /**
     * Dessine le labyrinthe sur le canvas.
     */
    private void drawMaze() {
        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                gc.setFill(getCellColor(i, j, maze.getGrid()));
                gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }

    /**
     * Dessine le labyrinthe résolu sur le canvas.
     *
     * @param solvedGrid La grille résolue à dessiner.
     */
    private void drawSolution(char[][] solvedGrid) {
        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                gc.setFill(getCellColor(i, j, solvedGrid));
                gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
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