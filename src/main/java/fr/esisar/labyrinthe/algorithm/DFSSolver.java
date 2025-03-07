package fr.esisar.labyrinthe.algorithm;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;
import java.util.Stack;

public class DFSSolver {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static char[][] solve(Maze maze) {
        char[][] grid = maze.getGrid();
        Point start = maze.getStart();
        Point end = maze.getEnd();
        int rows = maze.getRows();
        int cols = maze.getCols();

        Stack<Point> stack = new Stack<>();
        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];

        stack.push(start);
        visited[start.x()][start.y()] = true;

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            if (current.equals(end)) break;

            for (int[] dir : DIRECTIONS) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols
                        && !visited[nx][ny] && grid[nx][ny] != '#') {
                    visited[nx][ny] = true;
                    parent[nx][ny] = current;
                    stack.push(new Point(nx, ny));
                }
            }
        }

        return BFSSolver.reconstructPath(maze, parent);
    }
}