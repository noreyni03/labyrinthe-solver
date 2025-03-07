package fr.esisar.labyrinthe.algorithm;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;
import java.util.LinkedList;
import java.util.Queue;

public class BFSSolver {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static char[][] solve(Maze maze) {
        char[][] grid = maze.getGrid();
        Point start = maze.getStart();
        Point end = maze.getEnd();
        int rows = maze.getRows();
        int cols = maze.getCols();

        Queue<Point> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];

        queue.add(start);
        visited[start.x()][start.y()] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.equals(end)) break;

            for (int[] dir : DIRECTIONS) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols
                        && !visited[nx][ny] && grid[nx][ny] != '#') {
                    visited[nx][ny] = true;
                    parent[nx][ny] = current;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        return reconstructPath(maze, parent);
    }

    public static char[][] reconstructPath(Maze maze, Point[][] parent) {
        char[][] grid = maze.getGrid().clone();
        Point current = maze.getEnd();

        while (current != null && !current.equals(maze.getStart())) {
            if (!current.equals(maze.getEnd())) {
                grid[current.x()][current.y()] = '+';
            }
            current = parent[current.x()][current.y()];
        }

        return grid;
    }
}