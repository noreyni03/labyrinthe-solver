package fr.esisar.labyrinthe.algorithm;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;

import java.util.Arrays;
import java.util.Stack;

/**
 * Cette classe implémente l'algorithme de recherche en profondeur (DFS - Depth-First Search)
 * pour résoudre un labyrinthe.
 * Elle retourne un objet SolverResult contenant la grille résolue et le nombre d'étapes effectuées.
 */
public class DFSSolver {
    /**
     * Directions possibles : haut, bas, gauche, droite.
     */
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Résout le labyrinthe en utilisant l'algorithme de recherche en profondeur (DFS).
     *
     * @param maze Le labyrinthe à résoudre.
     * @return Un objet SolverResult contenant la grille résolue (avec le chemin marqué)
     *         et le nombre d'étapes effectuées pour trouver la solution.
     */
    public static SolverResult solve(Maze maze) {
        char[][] grid = maze.getGrid(); // Récupère la grille du labyrinthe.
        Point start = maze.getStart(); // Récupère le point de départ.
        Point end = maze.getEnd(); // Récupère le point d'arrivée.
        int rows = maze.getRows(); // Récupère le nombre de lignes.
        int cols = maze.getCols(); // Récupère le nombre de colonnes.

        Stack<Point> stack = new Stack<>(); // Pile pour la recherche DFS.
        boolean[][] visited = new boolean[rows][cols]; // Tableau pour suivre les cellules visitées.
        Point[][] parent = new Point[rows][cols]; // Tableau pour reconstruire le chemin.
        int steps = 0; // Compteur pour le nombre d'étapes.

        // Initialisation de la recherche DFS.
        stack.push(start); // Ajoute le point de départ à la pile.
        visited[start.x()][start.y()] = true; // Marque le point de départ comme visité.

        // Boucle principale de la recherche DFS.
        while (!stack.isEmpty()) {
            Point current = stack.pop(); // Récupère et retire le point du sommet de la pile.
            steps++; // Incrémente le compteur d'étapes.

            // Si le point actuel est le point d'arrivée, on a trouvé une solution.
            if (current.equals(end)) {
                return new SolverResult(reconstructPath(maze, parent), steps);
            }

            // Explore les voisins du point actuel.
            for (int[] dir : DIRECTIONS) {
                int nx = current.x() + dir[0]; // Calcule la coordonnée x du voisin.
                int ny = current.y() + dir[1]; // Calcule la coordonnée y du voisin.

                // Vérifie si le voisin est dans les limites de la grille,
                // s'il n'a pas déjà été visité et s'il n'est pas un mur.
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols
                        && !visited[nx][ny] && grid[nx][ny] != '#') {
                    visited[nx][ny] = true; // Marque le voisin comme visité.
                    parent[nx][ny] = current; // Enregistre le point actuel comme parent du voisin pour le chemin.
                    stack.push(new Point(nx, ny)); // Ajoute le voisin à la pile pour exploration future.
                }
            }
        }

        // Si la pile est vide et qu'on n'a pas atteint la fin, il n'y a pas de solution.
        return new SolverResult(grid, steps); // Retourne la grille originale et le nombre d'étapes.
    }

    /**
     * Reconstruit le chemin de l'arrivée au départ en utilisant le tableau `parent`.
     *
     * @param maze   Le labyrinthe en cours de résolution.
     * @param parent Le tableau qui permet de retracer le chemin en remontant de chaque cellule à son parent.
     * @return Un tableau de caractères 2D représentant le labyrinthe résolu avec le chemin marqué par '+'.
     */
    private static char[][] reconstructPath(Maze maze, Point[][] parent) {
        char[][] grid = new char[maze.getRows()][];
        // Crée une copie de la grille originale pour ne pas la modifier directement.
        for (int i = 0; i < maze.getRows(); i++) {
            grid[i] = Arrays.copyOf(maze.getGrid()[i], maze.getCols());
        }

        Point current = maze.getEnd(); // Commence au point d'arrivée.
        // Remonte le chemin en suivant les parents jusqu'à atteindre le point de départ ou un point sans parent.
        while (parent[current.x()][current.y()] != null && !current.equals(maze.getStart())) {
            // Marque le chemin avec '+' sauf pour la case d'arrivée.
            if (!current.equals(maze.getEnd())) {
                grid[current.x()][current.y()] = '+';
            }
            current = parent[current.x()][current.y()]; // Passe au parent du point actuel.
        }

        return grid; // Retourne la grille avec le chemin marqué.
    }
}