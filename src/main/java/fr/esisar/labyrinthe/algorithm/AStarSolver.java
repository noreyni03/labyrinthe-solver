package fr.esisar.labyrinthe.algorithm;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Résout un labyrinthe en utilisant l'algorithme A*.
 * Retourne un SolverResult contenant la grille résolue et le nombre d'étapes effectuées.
 */
public class AStarSolver {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Directions possibles : haut, bas, gauche, droite

    /**
     * Résout le labyrinthe en utilisant l'algorithme A*.
     *
     * @param maze Le labyrinthe à résoudre.
     * @return Un SolverResult contenant la grille résolue et le nombre d'étapes.
     */
    public static SolverResult solve(Maze maze) {
        char[][] grid = maze.getGrid();
        Point start = maze.getStart();
        Point end = maze.getEnd();
        int rows = maze.getRows();
        int cols = maze.getCols();
        int steps = 0; // Compteur pour le nombre d'étapes

        // Maps pour le suivi des scores et des chemins
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

        // Effectuer la recherche A*
        while (!openSet.isEmpty()) {
            Point current = openSet.poll();
            steps++; // Incrémenter le compteur d'étapes

            // Si l'arrivée est atteinte, reconstruire le chemin et retourner le résultat
            if (current.equals(end)) {
                return new SolverResult(reconstructPath(maze, cameFrom), steps);
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

        // Si aucune solution n'est trouvée, retourner la grille originale et le nombre d'étapes effectuées
        return new SolverResult(grid, steps);
    }

    /**
     * Fonction heuristique (distance de Manhattan).
     *
     * @param a Le point courant.
     * @param b Le point objectif.
     * @return La distance de Manhattan entre les deux points.
     */
    private static int heuristic(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    /**
     * Reconstruit le chemin de l'arrivée au départ en utilisant la map cameFrom.
     *
     * @param maze     Le labyrinthe en cours de résolution.
     * @param cameFrom La map traçant le chemin.
     * @return Un tableau de caractères 2D représentant le labyrinthe résolu avec le chemin marqué.
     */
    private static char[][] reconstructPath(Maze maze, Map<Point, Point> cameFrom) {
        char[][] grid = new char[maze.getRows()][];
        for (int i = 0; i < maze.getRows(); i++) {
            grid[i] = Arrays.copyOf(maze.getGrid()[i], maze.getCols());
        }

        Point current = maze.getEnd();
        while (cameFrom.containsKey(current) && !current.equals(maze.getStart())) {
            if (!current.equals(maze.getEnd())) {
                grid[current.x()][current.y()] = '+'; // Marquer le chemin
            }
            current = cameFrom.get(current);
        }

        return grid;
    }
}