package fr.esisar.labyrinthe.model;

import fr.esisar.labyrinthe.algorithm.AStarSolver;
import fr.esisar.labyrinthe.algorithm.BFSSolver;
import fr.esisar.labyrinthe.algorithm.DFSSolver;
import fr.esisar.labyrinthe.algorithm.SolverResult;

import java.util.HashMap;
import java.util.Map;

/**
 * La classe `MazeAnalyzer` fournit des fonctionnalités pour analyser un labyrinthe
 * et obtenir des statistiques sur sa structure, ainsi que sur les performances de
 * différents algorithmes de résolution (BFS, DFS, A*).
 */
public class MazeAnalyzer {
    private final Maze maze;

    /**
     * Construit un objet `MazeAnalyzer` pour le labyrinthe donné.
     *
     * @param maze Le labyrinthe à analyser.
     * @throws IllegalArgumentException si le labyrinthe est null.
     */
    public MazeAnalyzer(Maze maze) {
        if (maze == null) {
            throw new IllegalArgumentException("Le labyrinthe ne peut pas être null.");
        }
        this.maze = maze;
    }

    /**
     * Analyse le labyrinthe et retourne une carte de statistiques.
     *
     * @return Une carte (Map) contenant des statistiques sur le labyrinthe et les
     *         algorithmes de résolution. Les clés de la carte sont des chaînes
     *         de caractères, et les valeurs sont des objets de type Object.
     *         Les statistiques incluent :
     *         <ul>
     *             <li>dimensions: Les dimensions du labyrinthe (ex: "25x25").</li>
     *             <li>totalCells: Le nombre total de cellules dans le labyrinthe.</li>
     *             <li>wallCount: Le nombre de murs dans le labyrinthe.</li>
     *             <li>pathCount: Le nombre de chemins (espaces vides) dans le labyrinthe.</li>
     *             <li>wallRatio: Le ratio de murs par rapport au nombre total de cellules.</li>
     *             <li>straightLineDistance: La distance en ligne droite entre le début et la fin.</li>
     *             <li>bfsSteps: Le nombre d'étapes pour résoudre le labyrinthe avec BFS.</li>
     *             <li>dfsSteps: Le nombre d'étapes pour résoudre le labyrinthe avec DFS.</li>
     *             <li>aStarSteps: Le nombre d'étapes pour résoudre le labyrinthe avec A*.</li>
     *             <li>bfsTime: Le temps en nanosecondes pour résoudre le labyrinthe avec BFS.</li>
     *             <li>dfsTime: Le temps en nanosecondes pour résoudre le labyrinthe avec DFS.</li>
     *             <li>aStarTime: Le temps en nanosecondes pour résoudre le labyrinthe avec A*.</li>
     *             <li>bfsPathLength: La longueur du chemin trouvé par BFS.</li>
     *             <li>dfsPathLength: La longueur du chemin trouvé par DFS.</li>
     *             <li>aStarPathLength: La longueur du chemin trouvé par A*.</li>
     *             <li>pathEfficiency: L'efficacité du chemin (ratio du chemin BFS / distance en ligne droite).</li>
     *             <li>complexity: La complexité du labyrinthe (ratio du chemin DFS / chemin BFS).</li>
     *         </ul>
     */
    public Map<String, Object> analyzeMaze() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques de base
        stats.put("dimensions", maze.getRows() + "×" + maze.getCols());
        stats.put("totalCells", maze.getRows() * maze.getCols());

        // Calculer le nombre de murs et de chemins
        int wallCount = 0;
        int pathCount = 0;
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                if (maze.getGrid()[i][j] == '#') {
                    wallCount++;
                } else {
                    pathCount++;
                }
            }
        }
        stats.put("wallCount", wallCount);
        stats.put("pathCount", pathCount);
        stats.put("wallRatio", (double) wallCount / (maze.getRows() * maze.getCols()));

        // Calculer la distance en ligne droite entre le début et la fin
        Point start = maze.getStart();
        Point end = maze.getEnd();
        double straightLineDistance = Math.sqrt(
                Math.pow(end.x() - start.x(), 2) +
                        Math.pow(end.y() - start.y(), 2)
        );
        stats.put("straightLineDistance", straightLineDistance);

        // Mesurer les performances de BFS, DFS et A*
        long startTime = System.nanoTime();
        SolverResult bfsResult = BFSSolver.solve(maze);
        long bfsTime = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        SolverResult dfsResult = DFSSolver.solve(maze);
        long dfsTime = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        SolverResult aStarResult = AStarSolver.solve(maze);
        long aStarTime = System.nanoTime() - startTime;

        // Ajouter les mesures de performance des algorithmes
        stats.put("bfsSteps", bfsResult.getSteps());
        stats.put("dfsSteps", dfsResult.getSteps());
        stats.put("aStarSteps", aStarResult.getSteps());

        stats.put("bfsTime", bfsTime);
        stats.put("dfsTime", dfsTime);
        stats.put("aStarTime", aStarTime);

        stats.put("bfsPathLength", countPathCells(bfsResult.getGrid()));
        stats.put("dfsPathLength", countPathCells(dfsResult.getGrid()));
        stats.put("aStarPathLength", countPathCells(aStarResult.getGrid()));

        // Calculer l'efficacité du chemin (ratio de l'optimal à la ligne droite)
        stats.put("pathEfficiency", (double) countPathCells(bfsResult.getGrid()) / straightLineDistance);

        // Calculer la complexité du labyrinthe (ratio entre le chemin le plus long possible et le chemin réel)
        stats.put("complexity", (double) countPathCells(dfsResult.getGrid()) / countPathCells(bfsResult.getGrid()));

        return stats;
    }

    /**
     * Compte le nombre de cellules de chemin marquées avec '+' dans la grille résolue.
     *
     * @param grid La grille résolue.
     * @return Le nombre de cellules de chemin.
     * @throws IllegalArgumentException si la grille est null
     */
    private int countPathCells(char[][] grid) {
        if (grid == null) {
            throw new IllegalArgumentException("La grille ne peut pas être null.");
        }
        int count = 0;
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                if (grid[i][j] == '+') {
                    count++;
                }
            }
        }
        return count;
    }
}