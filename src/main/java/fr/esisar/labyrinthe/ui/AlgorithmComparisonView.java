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

        // Title
        Label titleLabel = new Label("Maze Analytics");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        // Stats grid
        TitledPane statsPane = createStatsPane(stats);

        // Algorithm comparison chart
        TitledPane chartPane = createChartPane(stats);

        this.getChildren().addAll(titleLabel, statsPane, chartPane);
    }

    private TitledPane createStatsPane(Map<String, Object> stats) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int row = 0;

        // Basic maze stats
        grid.add(createBoldLabel("Maze Dimensions:"), 0, row);
        grid.add(new Label(stats.get("dimensions").toString()), 1, row++);

        grid.add(createBoldLabel("Total Cells:"), 0, row);
        grid.add(new Label(stats.get("totalCells").toString()), 1, row++);

        grid.add(createBoldLabel("Wall Cells:"), 0, row);
        grid.add(new Label(stats.get("wallCount").toString()), 1, row++);

        grid.add(createBoldLabel("Path Cells:"), 0, row);
        grid.add(new Label(stats.get("pathCount").toString()), 1, row++);

        grid.add(createBoldLabel("Wall Ratio:"), 0, row);
        grid.add(new Label(String.format("%.2f%%", ((Double)stats.get("wallRatio")) * 100)), 1, row++);

        grid.add(createBoldLabel("Straight-line Distance:"), 0, row);
        grid.add(new Label(String.format("%.2f", stats.get("straightLineDistance"))), 1, row++);

        // Path stats
        grid.add(createBoldLabel("BFS Path Length:"), 0, row);
        grid.add(new Label(stats.get("bfsPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("DFS Path Length:"), 0, row);
        grid.add(new Label(stats.get("dfsPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("A* Path Length:"), 0, row);
        grid.add(new Label(stats.get("aStarPathLength").toString()), 1, row++);

        grid.add(createBoldLabel("Path Efficiency:"), 0, row);
        grid.add(new Label(String.format("%.2f", stats.get("pathEfficiency"))), 1, row++);

        grid.add(createBoldLabel("Maze Complexity:"), 0, row);
        grid.add(new Label(String.format("%.2f", stats.get("complexity"))), 1, row++);

        TitledPane statsPane = new TitledPane("Maze Statistics", grid);
        statsPane.setExpanded(true);
        return statsPane;
    }

    private TitledPane createChartPane(Map<String, Object> stats) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Algorithm");
        yAxis.setLabel("Path Length");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Path Length Comparison");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Solution Path Length");

        series.getData().add(new XYChart.Data<>("BFS", (Integer)stats.get("bfsPathLength")));
        series.getData().add(new XYChart.Data<>("DFS", (Integer)stats.get("dfsPathLength")));
        series.getData().add(new XYChart.Data<>("A*", (Integer)stats.get("aStarPathLength")));

        barChart.getData().add(series);

        TitledPane chartPane = new TitledPane("Algorithm Comparison", barChart);
        chartPane.setExpanded(true);
        return chartPane;
    }

    private Label createBoldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        return label;
    }
}