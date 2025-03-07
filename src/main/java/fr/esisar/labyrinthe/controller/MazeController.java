package fr.esisar.labyrinthe.controller;

import fr.esisar.labyrinthe.algorithm.BFSSolver;
import fr.esisar.labyrinthe.algorithm.DFSSolver;
import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.generator.MazeGenerator;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;

public class MazeController {
    @FXML private Canvas mazeCanvas;
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private CheckBox animationCheck;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private Maze maze;
    private GraphicsContext gc;
    private double cellSize = 30;

    @FXML
    public void initialize() {
        gc = mazeCanvas.getGraphicsContext2D();
        algorithmCombo.getItems().addAll("BFS", "DFS");
    }

    // Chargement d'un labyrinthe
    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un labyrinthe");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                maze = new Maze(Path.of(file.toURI()));
                drawMaze();
                statusLabel.setText("Labyrinthe chargé : " + file.getName());
            } catch (Exception e) {
                showError("Erreur de chargement", e.getMessage());
            }
        }
    }

    // Génération d'un labyrinthe
    @FXML
    private void handleGenerateMaze() {
        maze = MazeGenerator.generate(25, 25, MazeGenerator.Algorithm.RECURSIVE_BACKTRACKING);
        drawMaze();
        statusLabel.setText("Labyrinthe généré (25x25)");
    }

    // Résolution avec animation
    @FXML
    private void handleSolve() {
        if (maze == null || algorithmCombo.getValue() == null) {
            showError("Erreur", "Sélectionnez un labyrinthe et un algorithme.");
            return;
        }

        char[][] solvedGrid = solveMaze();
        drawSolution(solvedGrid);
    }

    private char[][] solveMaze() {
        return switch (algorithmCombo.getValue()) {
            case "BFS" -> BFSSolver.solve(maze);
            case "DFS" -> DFSSolver.solve(maze);
            default -> throw new IllegalStateException();
        };
    }

    private void drawSolution(char[][] solvedGrid) {
        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                gc.setFill(getCellColor(i, j, solvedGrid));
                gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawMaze() {
        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                gc.setFill(getCellColor(i, j, maze.getGrid()));
                gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }

    private Color getCellColor(int x, int y, char[][] grid) {
        if (grid[x][y] == '#') return Color.BLACK;
        if (grid[x][y] == 'S') return Color.GREEN;
        if (grid[x][y] == 'E') return Color.RED;
        if (grid[x][y] == '+') return Color.YELLOW;
        return Color.WHITE;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}