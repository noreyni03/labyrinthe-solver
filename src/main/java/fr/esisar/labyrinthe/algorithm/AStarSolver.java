package fr.esisar.labyrinthe.algorithm;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class AStarSolver {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static char[][] solve(Maze maze) {
        char[][] grid = maze.getGrid();
        Point start = maze.getStart();
        Point end = maze.getEnd();
        int rows = maze.getRows();
        int cols = maze.getCols();

        // Déclarer fScore AVANT la PriorityQueue
        Map<Point, Integer> gScore = new HashMap<>();
        Map<Point, Integer> fScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();

        // Utiliser une référence finale pour le comparateur
        final Map<Point, Integer> finalFScore = fScore;

        PriorityQueue<Point> openSet = new PriorityQueue<>(
                Comparator.comparingInt(p -> finalFScore.getOrDefault(p, Integer.MAX_VALUE))
        );

        // Initialisation
        openSet.add(start);
        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));

        while (!openSet.isEmpty()) {
            Point current = openSet.poll();

            if (current.equals(end)) {
                return reconstructPath(maze, cameFrom);
            }

            for (int[] dir : DIRECTIONS) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];
                Point neighbor = new Point(nx, ny);

                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && grid[nx][ny] != '#') {
                    int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;

                    if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        cameFrom.put(neighbor, current);
                        gScore.put(neighbor, tentativeGScore);
                        fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));

                        if (!openSet.contains(neighbor)) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }

        return grid;
    }

    // Heuristique (distance de Manhattan)
    private static int heuristic(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    // Reconstruction du chemin
    private static char[][] reconstructPath(Maze maze, Map<Point, Point> cameFrom) {
        char[][] grid = new char[maze.getRows()][];
        for (int i = 0; i < maze.getRows(); i++) {
            grid[i] = Arrays.copyOf(maze.getGrid()[i], maze.getCols());
        }

        Point current = maze.getEnd();
        while (cameFrom.containsKey(current) && !current.equals(maze.getStart())) {
            if (!current.equals(maze.getEnd())) {
                grid[current.x()][current.y()] = '+';
            }
            current = cameFrom.get(current);
        }

        return grid;
    }
}