package fr.esisar.labyrinthe.controller;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Classe responsable de la résolution animée d'un labyrinthe.
 * Utilise l'algorithme de parcours en largeur (BFS) pour trouver le chemin
 * et anime le processus de recherche et de reconstruction du chemin sur un canvas.
 */
public class AnimatedBFSSolver {
    /**
     * Directions possibles : haut, bas, gauche, droite.
     */
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    /**
     * Le labyrinthe à résoudre.
     */
    private final Maze maze;
    /**
     * Le contexte graphique sur lequel dessiner.
     */
    private final GraphicsContext gc;
    /**
     * La taille d'une cellule du labyrinthe en pixels.
     */
    private final double cellSize;
    /**
     * Décalage horizontal pour centrer le labyrinthe.
     */
    private final double offsetX;
    /**
     * Décalage vertical pour centrer le labyrinthe.
     */
    private final double offsetY;
    /**
     * Callback pour signaler la progression de la recherche.
     */
    private final Consumer<Double> progressCallback;
    /**
     * Minuteur pour l'animation.
     */
    private AnimationTimer timer;
    /**
     * Indique si un algorithme de résolution est en cours.
     */
    private boolean isSolving = false;

    /**
     * Constructeur de la classe AnimatedSolver.
     *
     * @param maze             Le labyrinthe à résoudre.
     * @param gc               Le contexte graphique sur lequel dessiner.
     * @param cellSize         La taille d'une cellule du labyrinthe en pixels.
     * @param offsetX          Décalage horizontal pour centrer le labyrinthe.
     * @param offsetY          Décalage vertical pour centrer le labyrinthe.
     * @param progressCallback Callback pour signaler la progression de la recherche (entre 0.0 et 1.0).
     */
    public AnimatedBFSSolver(Maze maze, GraphicsContext gc, double cellSize, double offsetX, double offsetY, Consumer<Double> progressCallback) {
        this.maze = maze;
        this.gc = gc;
        this.cellSize = cellSize;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.progressCallback = progressCallback;
    }

    /**
     * Résout le labyrinthe en utilisant l'algorithme de parcours en largeur (BFS), avec une animation.
     *
     * @return Un CompletableFuture qui contiendra la grille résolue lorsque la résolution sera terminée.
     *         La grille contiendra le chemin marqué avec des '+'.
     * @throws IllegalStateException Si un autre algorithme de résolution est déjà en cours.
     */
    public CompletableFuture<char[][]> solveBFS() {
        CompletableFuture<char[][]> future = new CompletableFuture<>();
        if (isSolving) {
            future.completeExceptionally(new IllegalStateException("Un algorithme de résolution est déjà en cours."));
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
            private final long FRAME_DELAY = 30_000_000; // 30ms en nanosecondes

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

                // Visualisation - afficher l'exploration avec décalage
                if (!current.equals(start) && !current.equals(end)) {
                    gc.setFill(Color.LIGHTBLUE);
                    gc.fillRect(offsetX + current.y() * cellSize, offsetY + current.x() * cellSize, cellSize, cellSize);
                }

                // Mettre à jour la progression
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

        // Construire le chemin en remontant de la fin vers le début
        while (parent.containsKey(current) && !current.equals(maze.getStart())) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);

        // Créer l'animation pour la visualisation du chemin
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

                // Dessiner le segment du chemin avec décalage
                gc.setFill(Color.YELLOW);
                gc.fillRect(offsetX + p.y() * cellSize, offsetY + p.x() * cellSize, cellSize, cellSize);

                // Mettre en évidence le début et la fin avec décalage
                gc.setFill(Color.GREEN);
                gc.fillRect(offsetX + maze.getStart().y() * cellSize, offsetY + maze.getStart().x() * cellSize, cellSize, cellSize);
                gc.setFill(Color.RED);
                gc.fillRect(offsetX + maze.getEnd().y() * cellSize, offsetY + maze.getEnd().x() * cellSize, cellSize, cellSize);

                index[0]++;
            }
        };

        pathTimer.start();
        return grid;
    }

    /**
     * Arrête l'animation en cours.
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
            isSolving = false;
        }
    }
}