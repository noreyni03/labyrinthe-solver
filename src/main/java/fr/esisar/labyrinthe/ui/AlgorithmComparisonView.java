package fr.esisar.labyrinthe.ui;

import fr.esisar.labyrinthe.model.MazeAnalyzer;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

/**
 * La classe `AlgorithmComparisonView` est une vue JavaFX qui affiche une comparaison
 * des algorithmes de résolution de labyrinthe (BFS, DFS, A*). Elle présente des
 * statistiques et un diagramme à barres pour la longueur du chemin, le nombre d'étapes,
 * et le temps d'exécution.
 */
public class AlgorithmComparisonView extends VBox {
    private final MazeAnalyzer analyzer;

    /**
     * Construit une vue `AlgorithmComparisonView` pour l'objet `MazeAnalyzer` donné.
     *
     * @param analyzer L'objet `MazeAnalyzer` contenant les statistiques à afficher.
     * @throws IllegalArgumentException si l'analyzer est null.
     */
    public AlgorithmComparisonView(MazeAnalyzer analyzer) {
        if(analyzer == null){
            throw new IllegalArgumentException("L'analyzer ne peut pas être null");
        }
        this.analyzer = analyzer;
        this.setPadding(new Insets(10));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f4f4f4;");

        createUI();
    }

    /**
     * Crée les composants de l'interface utilisateur pour la vue de comparaison.
     */
    private void createUI() {
        Map<String, Object> stats = analyzer.analyzeMaze();

        // Titre
        Label titleLabel = new Label("Analyse du Labyrinthe");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        // Grille des statistiques
        TitledPane statsPane = createStatsPane(stats);

        // Diagramme de comparaison des algorithmes
        TitledPane chartPane = createChartPane(stats);

        this.getChildren().addAll(titleLabel, statsPane, chartPane);
    }

    /**
     * Crée un `TitledPane` contenant les statistiques du labyrinthe et des algorithmes.
     *
     * @param stats Les statistiques à afficher.
     * @return Un `TitledPane` avec la grille des statistiques.
     */
    private TitledPane createStatsPane(Map<String, Object> stats) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int row = 0;

        // Statistiques de base du labyrinthe
        grid.add(createBoldLabel("Dimensions du Labyrinthe:"), 0, row);
        grid.add(new Label(stats.get("dimensions").toString()), 1, row++);

        grid.add(createBoldLabel("Nombre Total de Cellules:"), 0, row);
        grid.add(new Label(stats.get("totalCells").toString()), 1, row++);

        grid.add(createBoldLabel("Cellules Murs:"), 0, row);
        grid.add(new Label(stats.get("wallCount").toString()), 1, row++);

        grid.add(createBoldLabel("Cellules Chemins:"), 0, row);
        grid.add(new Label(stats.get("pathCount").toString()), 1, row++);

        grid.add(createBoldLabel("Ratio de Murs:"), 0, row);
        grid.add(new Label(String.format("%.2f%%", ((Double) stats.get("wallRatio")) * 100)), 1, row++);

        grid.add(createBoldLabel("Distance en Ligne Droite:"), 0, row);
        grid.add(new Label(String.format("%.2f", stats.get("straightLineDistance"))), 1, row++);

        // Statistiques des chemins
        grid.add(createBoldLabel("Longueur du Chemin BFS:"), 0, row);
        grid.add(new Label(stats.get("bfsPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("Longueur du Chemin DFS:"), 0, row);
        grid.add(new Label(stats.get("dfsPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("Longueur du Chemin A*:"), 0, row);
        grid.add(new Label(stats.get("aStarPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("Efficacité du Chemin:"), 0, row);
        grid.add(new Label(String.format("%.2f", stats.get("pathEfficiency"))), 1, row++);

        grid.add(createBoldLabel("Complexité du Labyrinthe:"), 0, row);
        grid.add(new Label(String.format("%.2f", stats.get("complexity"))), 1, row++);

        // Statistiques de performance des algorithmes
        grid.add(createBoldLabel("Étapes BFS:"), 0, row);
        grid.add(new Label(stats.get("bfsSteps").toString()), 1, row++);

        grid.add(createBoldLabel("Étapes DFS:"), 0, row);
        grid.add(new Label(stats.get("dfsSteps").toString()), 1, row++);

        grid.add(createBoldLabel("Étapes A*:"), 0, row);
        grid.add(new Label(stats.get("aStarSteps").toString()), 1, row++);

        grid.add(createBoldLabel("Temps BFS (ms):"), 0, row);
        grid.add(new Label(String.format("%.3f", (Long) stats.get("bfsTime") / 1_000_000.0)), 1, row++);

        grid.add(createBoldLabel("Temps DFS (ms):"), 0, row);
        grid.add(new Label(String.format("%.3f", (Long) stats.get("dfsTime") / 1_000_000.0)), 1, row++);

        grid.add(createBoldLabel("Temps A* (ms):"), 0, row);
        grid.add(new Label(String.format("%.3f", (Long) stats.get("aStarTime") / 1_000_000.0)), 1, row++);

        TitledPane statsPane = new TitledPane("Statistiques du Labyrinthe", grid);
        statsPane.setExpanded(true);
        return statsPane;
    }

    /**
     * Crée un `TitledPane` contenant un diagramme à barres comparant les performances des algorithmes.
     *
     * @param stats Les statistiques à afficher.
     * @return Un `TitledPane` avec le diagramme à barres.
     */
    private TitledPane createChartPane(Map<String, Object> stats) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Algorithme");
        yAxis.setLabel("Valeur");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparaison des Performances des Algorithmes");

        // Série de la longueur du chemin
        XYChart.Series<String, Number> pathLengthSeries = new XYChart.Series<>();
        pathLengthSeries.setName("Longueur du Chemin");
        pathLengthSeries.getData().add(new XYChart.Data<>("BFS", (Integer) stats.get("bfsPathLength")));
        pathLengthSeries.getData().add(new XYChart.Data<>("DFS", (Integer) stats.get("dfsPathLength")));
        pathLengthSeries.getData().add(new XYChart.Data<>("A*", (Integer) stats.get("aStarPathLength")));

        // Série des étapes
        XYChart.Series<String, Number> stepsSeries = new XYChart.Series<>();
        stepsSeries.setName("Étapes");
        stepsSeries.getData().add(new XYChart.Data<>("BFS", (Integer) stats.get("bfsSteps")));
        stepsSeries.getData().add(new XYChart.Data<>("DFS", (Integer) stats.get("dfsSteps")));
        stepsSeries.getData().add(new XYChart.Data<>("A*", (Integer) stats.get("aStarSteps")));

        // Série du temps
        XYChart.Series<String, Number> timeSeries = new XYChart.Series<>();
        timeSeries.setName("Temps (ms)");
        timeSeries.getData().add(new XYChart.Data<>("BFS", (Long) stats.get("bfsTime") / 1_000_000.0));
        timeSeries.getData().add(new XYChart.Data<>("DFS", (Long) stats.get("dfsTime") / 1_000_000.0));
        timeSeries.getData().add(new XYChart.Data<>("A*", (Long) stats.get("aStarTime") / 1_000_000.0));

        barChart.getData().addAll(pathLengthSeries, stepsSeries, timeSeries);

        TitledPane chartPane = new TitledPane("Comparaison des Algorithmes", barChart);
        chartPane.setExpanded(true);
        return chartPane;
    }

    /**
     * Crée un label en gras pour une utilisation dans la grille des statistiques.
     *
     * @param text Le texte du label.
     * @return Un label en gras.
     */
    private Label createBoldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        return label;
    }
}