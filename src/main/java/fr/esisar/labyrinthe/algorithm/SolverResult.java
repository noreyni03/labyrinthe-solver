package fr.esisar.labyrinthe.algorithm;

/**
 * Représente le résultat d'un algorithme de résolution de labyrinthe.
 * Contient la grille résolue et le nombre d'étapes nécessaires pour résoudre le labyrinthe.
 */
public class SolverResult {
    private final char[][] grid;  // La grille résolue
    private final int steps;     // Le nombre d'étapes nécessaires pour résoudre le labyrinthe

    /**
     * Construit un objet SolverResult avec la grille résolue et le nombre d'étapes.
     *
     * @param grid  La grille résolue (tableau 2D de caractères représentant le labyrinthe).
     * @param steps Le nombre d'étapes nécessaires pour résoudre le labyrinthe.
     */
    public SolverResult(char[][] grid, int steps) {
        this.grid = grid;
        this.steps = steps;
    }

    /**
     * Retourne la grille résolue.
     *
     * @return Le tableau 2D de caractères représentant le labyrinthe résolu.
     */
    public char[][] getGrid() {
        return grid;
    }

    /**
     * Retourne le nombre d'étapes nécessaires pour résoudre le labyrinthe.
     *
     * @return Le nombre d'étapes sous forme d'entier.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Retourne une représentation textuelle de l'objet SolverResult.
     *
     * @return Une chaîne de caractères contenant le nombre d'étapes et la représentation de la grille.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Étapes: ").append(steps).append("\n");
        sb.append("Grille:\n");
        for (char[] row : grid) {
            sb.append(new String(row)).append("\n");
        }
        return sb.toString();
    }
}