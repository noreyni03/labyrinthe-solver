package fr.esisar.labyrinthe.ui;

import fr.esisar.labyrinthe.model.Maze;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LabyrinthePane extends Pane {
    private static final int CELL_SIZE = 30;
    private final Maze maze;
    private char[][] currentGrid;

    public LabyrinthePane(Maze maze) {
        this.maze = maze;
        this.currentGrid = maze.getGrid();
        drawMaze();
    }

    public void updateGrid(char[][] newGrid) {
        this.currentGrid = newGrid;
        this.getChildren().clear();
        drawMaze();
    }

    private void drawMaze() {
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                Rectangle cell = new Rectangle(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(getColorForCell(i, j));
                this.getChildren().add(cell);
            }
        }
    }

    private Color getColorForCell(int x, int y) {
        if (currentGrid[x][y] == '#') return Color.BLACK;
        if (currentGrid[x][y] == '+') return Color.YELLOW;
        if (currentGrid[x][y] == 'S') return Color.GREEN;
        if (currentGrid[x][y] == 'E') return Color.RED;
        return Color.WHITE;
    }
}