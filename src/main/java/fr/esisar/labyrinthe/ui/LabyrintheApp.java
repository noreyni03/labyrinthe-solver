package fr.esisar.labyrinthe.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * La classe principale de l'application Labyrinthe.
 * Cette application permet de visualiser, générer et résoudre des labyrinthes.
 * Elle utilise JavaFX pour l'interface utilisateur et charge le layout principal à partir d'un fichier FXML.
 */
public class LabyrintheApp extends Application {

    /**
     * Méthode de démarrage de l'application JavaFX.
     * Elle est appelée lorsque l'application est lancée.
     *
     * @param stage La fenêtre principale (stage) de l'application.
     * @throws Exception Si une erreur survient lors de l'initialisation.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Charge le fichier FXML pour le layout principal de l'interface utilisateur.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/esisar/labyrinthe/view/MazeView.fxml"));
        Parent root = loader.load();

        // Crée une scène avec le layout chargé et définit les dimensions.
        Scene scene = new Scene(root, 800, 600);

        // Configure la fenêtre principale (stage) avec un titre et la scène.
        stage.setTitle("Résolveur de Labyrinthe");
        stage.setScene(scene);

        // Affiche la fenêtre principale.
        stage.show();
    }

    /**
     * Le point d'entrée principal de l'application.
     * Cette méthode lance l'application JavaFX.
     *
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        // Lance l'application JavaFX.
        launch(args);
    }
}