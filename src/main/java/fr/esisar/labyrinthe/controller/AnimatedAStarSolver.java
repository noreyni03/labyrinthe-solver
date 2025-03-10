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
 * Classe responsable de la résolution animée d'un labyrinthe en utilisant l'algorithme A* (A-Star).
 * Elle anime le processus de recherche et de reconstruction du chemin sur un canvas.
 */
public class AnimatedAStarSolver {
    // Directions possibles : haut, bas, gauche, droite
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // Le labyrinthe à résoudre
    private final Maze maze;

    // Le contexte graphique sur lequel dessiner
    private final GraphicsContext gc;

    // La taille d'une cellule du labyrinthe en pixels
    private final double cellSize;

    // Callback pour signaler la progression de la recherche
    private final Consumer<Double> progressCallback;

    // Minuteur pour l'animation
    private AnimationTimer timer;

    // Indique si un algorithme de résolution est en cours
    private boolean isSolving = false;

    /**
     * Constructeur de la classe AnimatedAStarSolver.
     *
     * @param maze             Le labyrinthe à résoudre.
     * @param gc               Le contexte graphique sur lequel dessiner.
     * @param cellSize         La taille d'une cellule du labyrinthe en pixels.
     * @param progressCallback Callback pour signaler la progression de la recherche (entre 0.0 et 1.0).
     */
    public AnimatedAStarSolver(Maze maze, GraphicsContext gc, double cellSize, Consumer<Double> progressCallback) {
        this.maze = maze;
        this.gc = gc;
        this.cellSize = cellSize;
        this.progressCallback = progressCallback;
    }

    /**
     * Résout le labyrinthe en utilisant l'algorithme A*, avec une animation.
     *
     * @return Un CompletableFuture qui contiendra la grille résolue lorsque la résolution sera terminée.
     *         La grille contiendra le chemin marqué avec des '+'.
     * @throws IllegalStateException Si un autre algorithme de résolution est déjà en cours.
     */
    public CompletableFuture<char[][]> solveAStar() {
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

        // Structures de données pour A*
        Map<Point, Integer> gScore = new HashMap<>(); // Coût du départ au nœud courant
        Map<Point, Integer> fScore = new HashMap<>(); // Coût total estimé (gScore + heuristique)
        Map<Point, Point> cameFrom = new HashMap<>(); // Suivi du chemin

        // File de priorité pour l'ensemble ouvert, ordonnée par fScore
        PriorityQueue<Point> openSet = new PriorityQueue<>(
                Comparator.comparingInt(p -> fScore.getOrDefault(p, Integer.MAX_VALUE))
        );

        // Initialisation de A*
        openSet.add(start);
        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));

        List<Point> exploredCells = new ArrayList<>();

        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long FRAME_DELAY = 30_000_000; // 30ms en nanosecondes

            @Override
            public void handle(long now) {
                if (now - lastUpdate < FRAME_DELAY) return;
                lastUpdate = now;

                if (openSet.isEmpty()) {
                    this.stop();
                    char[][] result = reconstructPathAnimated(cameFrom, end, future);
                    isSolving = false;
                    return;
                }

                Point current = openSet.poll();
                exploredCells.add(current);

                // Visualisation - afficher l'exploration
                if (!current.equals(start) && !current.equals(end)) {
                    gc.setFill(Color.LIGHTBLUE);
                    gc.fillRect(current.y() * cellSize, current.x() * cellSize, cellSize, cellSize);
                }

                // Mettre à jour la progression
                progressCallback.accept((double) exploredCells.size() / (rows * cols));

                // Si l'arrivée est atteinte, reconstruire le chemin
                if (current.equals(end)) {
                    this.stop();
                    char[][] result = reconstructPathAnimated(cameFrom, end, future);
                    isSolving = false;
                    return;
                }

                // Explorer les voisins
                for (int[] dir : DIRECTIONS) {
                    int nx = current.x() + dir[0];
                    int ny = current.y() + dir[1];
                    Point neighbor = new Point(nx, ny);

                    // Vérifier si le voisin est dans les limites et n'est pas un mur
                    if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && grid[nx][ny] != '#') {
                        int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;

                        // Si ce chemin vers le voisin est meilleur, mettre à jour les scores et le chemin
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
        };

        timer.start();
        return future;
    }

    /**
     * Fonction heuristique (distance de Manhattan).
     *
     * @param a Le point courant.
     * @param b Le point objectif.
     * @return La distance de Manhattan entre les deux points.
     */
    private int heuristic(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    /**
     * Reconstruit et anime le chemin à partir du tableau des parents (`cameFrom`).
     *
     * @param cameFrom La map traçant le chemin.
     * @param end      Le point d'arrivée.
     * @param future   Le CompletableFuture à compléter lorsque le chemin est reconstruit.
     * @return Une copie de la grille avec le chemin marqué.
     */
    private char[][] reconstructPathAnimated(Map<Point, Point> cameFrom, Point end, CompletableFuture<char[][]> future) {
        char[][] grid = maze.getGrid().clone();
        for (int i = 0; i < grid.length; i++) {
            grid[i] = grid[i].clone();
        }

        List<Point> path = new ArrayList<>();
        Point current = end;

        // Construire le chemin en remontant de la fin vers le début
        while (cameFrom.containsKey(current) && !current.equals(maze.getStart())) {
            path.add(current);
            current = cameFrom.get(current);
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

                // Dessiner le segment du chemin
                gc.setFill(Color.YELLOW);
                gc.fillRect(p.y() * cellSize, p.x() * cellSize, cellSize, cellSize);

                // Mettre en évidence le début et la fin
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