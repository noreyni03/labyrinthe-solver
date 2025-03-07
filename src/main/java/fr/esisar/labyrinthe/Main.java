package fr.esisar.labyrinthe;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.MazeAnalyzer;
import fr.esisar.labyrinthe.generator.MazeGenerator;
import fr.esisar.labyrinthe.ui.AlgorithmComparisonView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Générer un labyrinthe
        Maze maze = MazeGenerator.generate(25, 25, MazeGenerator.Algorithm.RECURSIVE_BACKTRACKING);

        // Afficher les statistiques
        MazeAnalyzer analyzer = new MazeAnalyzer(maze);
        AlgorithmComparisonView view = new AlgorithmComparisonView(analyzer);

        // Configurer la scène
        Scene scene = new Scene(view, 800, 600);
        stage.setTitle("Labyrinthe Analytics");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}