package fr.esisar.labyrinthe.ui;

import fr.esisar.labyrinthe.algorithm.BFSSolver;
import fr.esisar.labyrinthe.algorithm.DFSSolver;
import fr.esisar.labyrinthe.model.Maze;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.nio.file.Path;

public class LabyrintheApp extends Application {
    private LabyrinthePane labyrinthePane;
    private Maze maze;

    @Override
    public void start(Stage stage) {
        try {
            maze = new Maze(Path.of("src/main/resources/maze1.txt"));
            BorderPane root = new BorderPane();
            labyrinthePane = new LabyrinthePane(maze);

            // Contrôles
            ComboBox<String> algorithmChoice = new ComboBox<>();
            algorithmChoice.getItems().addAll("BFS", "DFS");
            algorithmChoice.setValue("BFS");

            Button solveButton = new Button("Résoudre");
            solveButton.setOnAction(e -> {
                char[][] solvedGrid = switch (algorithmChoice.getValue()) {
                    case "BFS" -> BFSSolver.solve(maze);
                    case "DFS" -> DFSSolver.solve(maze);
                    default -> throw new IllegalStateException();
                };
                labyrinthePane.updateGrid(solvedGrid);
            });

            HBox controls = new HBox(10, algorithmChoice, solveButton);
            controls.setPadding(new Insets(10));

            root.setCenter(labyrinthePane);
            root.setTop(controls);

            stage.setTitle("Labyrinthe Project");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}