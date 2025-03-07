package fr.esisar.labyrinthe.controller;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AnimatedSolver {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private final Maze maze;
    private final GraphicsContext gc;
    private final double cellSize;
    private final Consumer<Double> progressCallback;
    private AnimationTimer timer;
    private boolean isSolving = false;

    public AnimatedSolver(Maze maze, GraphicsContext gc, double cellSize, Consumer<Double> progressCallback) {
        this.maze = maze;
        this.gc = gc;
        this.cellSize = cellSize;
        this.progressCallback = progressCallback;
    }

    public CompletableFuture<char[][]> solveBFS() {
        CompletableFuture<char[][]> future = new CompletableFuture<>();
        if (isSolving) {
            future.completeExceptionally(new IllegalStateException("Already solving"));
            return future;
        }

        isSolving = true;
        char[][] grid = maze.getGrid();
        Point start = maze.getStart();
        Point end = maze.getEnd();
        int rows = maze.getRows();
        int cols = maze.getCols();

        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> parent = new HashMap<>();
        List<Point> exploredCells = new ArrayList<>();

        queue.add(start);
        visited.add(start);

        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long FRAME_DELAY = 30_000_000; // 30ms in nanoseconds

            @Override
            public void handle(long now) {
                if (now - lastUpdate < FRAME_DELAY) return;
                lastUpdate = now;

                if (queue.isEmpty()) {
                    this.stop();
                    char[][] result = reconstructPathAnimated(parent, end, future);
                    isSolving = false;
                    return;
                }

                Point current = queue.poll();
                exploredCells.add(current);

                // Visualization - show exploration
                if (!current.equals(start) && !current.equals(end)) {
                    gc.setFill(Color.LIGHTBLUE);
                    gc.fillRect(current.y() * cellSize, current.x() * cellSize, cellSize, cellSize);
                }

                // Update progress
                progressCallback.accept((double) exploredCells.size() / (rows * cols));

                if (current.equals(end)) {
                    this.stop();
                    char[][] result = reconstructPathAnimated(parent, end, future);
                    isSolving = false;
                    return;
                }

                for (int[] dir : DIRECTIONS) {
                    int nx = current.x() + dir[0];
                    int ny = current.y() + dir[1];
                    Point next = new Point(nx, ny);

                    if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && !visited.contains(next) && grid[nx][ny] != '#') {
                        visited.add(next);
                        parent.put(next, current);
                        queue.add(next);
                    }
                }
            }
        };

        timer.start();
        return future;
    }

    private char[][] reconstructPathAnimated(Map<Point, Point> parent, Point end, CompletableFuture<char[][]> future) {
        char[][] grid = maze.getGrid().clone();
        for (int i = 0; i < grid.length; i++) {
            grid[i] = grid[i].clone();
        }

        List<Point> path = new ArrayList<>();
        Point current = end;

        // Build the path in reverse
        while (parent.containsKey(current) && !current.equals(maze.getStart())) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);

        // Create animation for path visualization
        final int[] index = {0};
        AnimationTimer pathTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long PATH_DELAY = 50_000_000; // 50ms

            @Override
            public void handle(long now) {
                if (now - lastUpdate < PATH_DELAY) return;
                lastUpdate = now;

                if (index[0] >= path.size()) {
                    this.stop();
                    future.complete(grid);
                    return;
                }

                Point p = path.get(index[0]);
                grid[p.x()][p.y()] = '+';

                // Draw path segment
                gc.setFill(Color.YELLOW);
                gc.fillRect(p.y() * cellSize, p.x() * cellSize, cellSize, cellSize);

                // Highlight start and end
                gc.setFill(Color.GREEN);
                gc.fillRect(maze.getStart().y() * cellSize, maze.getStart().x() * cellSize, cellSize, cellSize);
                gc.setFill(Color.RED);
                gc.fillRect(maze.getEnd().y() * cellSize, maze.getEnd().x() * cellSize, cellSize, cellSize);

                index[0]++;
            }
        };

        pathTimer.start();
        return grid;
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            isSolving = false;
        }
    }
}