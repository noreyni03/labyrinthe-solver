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
 * Vue pour comparer les performances des algorithmes de résolution de labyrinthes.
 */
public class AlgorithmComparisonView extends VBox {
    private final MazeAnalyzer analyzer;

    public AlgorithmComparisonView(MazeAnalyzer analyzer) {
        this.analyzer = analyzer;
        this.setPadding(new Insets(10));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f4f4f4;");

        createUI();
    }

    private void createUI() {
        Map<String, Object> stats = analyzer.analyzeMaze();

        // Titre
        Label titleLabel = new Label("Comparaison des Algorithmes");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        // Grille des statistiques
        TitledPane statsPane = createStatsPane(stats);

        // Diagramme de comparaison des algorithmes
        TitledPane chartPane = createChartPane(stats);

        this.getChildren().addAll(titleLabel, statsPane, chartPane);
    }

    private TitledPane createStatsPane(Map<String, Object> stats) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int row = 0;

        // Statistiques de base
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

        grid.add(createBoldLabel("Longueur du chemin BFS:"), 0, row);
        grid.add(new Label(stats.get("bfsPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("Longueur du chemin DFS:"), 0, row);
        grid.add(new Label(stats.get("dfsPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("Longueur du chemin A*:"), 0, row);
        grid.add(new Label(stats.get("aStarPathLength").toString()), 1, row++);

        TitledPane statsPane = new TitledPane("Statistiques des Algorithmes", grid);
        statsPane.setExpanded(true);
        return statsPane;
    }

    private TitledPane createChartPane(Map<String, Object> stats) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Algorithme");
        yAxis.setLabel("Valeur");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparaison des Performances des Algorithmes");

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

        // Série de la longueur du chemin
        XYChart.Series<String, Number> pathLengthSeries = new XYChart.Series<>();
        pathLengthSeries.setName("Longueur du chemin");
        pathLengthSeries.getData().add(new XYChart.Data<>("BFS", (Integer) stats.get("bfsPathLength")));
        pathLengthSeries.getData().add(new XYChart.Data<>("DFS", (Integer) stats.get("dfsPathLength")));
        pathLengthSeries.getData().add(new XYChart.Data<>("A*", (Integer) stats.get("aStarPathLength")));

        barChart.getData().addAll(stepsSeries, timeSeries, pathLengthSeries);

        TitledPane chartPane = new TitledPane("Graphique de Comparaison", barChart);
        chartPane.setExpanded(true);
        return chartPane;
    }

    private Label createBoldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        return label;
    }
}