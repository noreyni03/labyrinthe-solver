package fr.esisar.labyrinthe.model;

import fr.esisar.labyrinthe.algorithm.AStarSolver;
import fr.esisar.labyrinthe.algorithm.BFSSolver;
import fr.esisar.labyrinthe.algorithm.DFSSolver;

import java.util.HashMap;
import java.util.Map;

public class MazeAnalyzer {
    private final Maze maze;

    public MazeAnalyzer(Maze maze) {
        this.maze = maze;
    }

    public Map<String, Object> analyzeMaze() {
        Map<String, Object> stats = new HashMap<>();

        // Basic statistics
        stats.put("dimensions", maze.getRows() + "×" + maze.getCols());
        stats.put("totalCells", maze.getRows() * maze.getCols());

        // Calculate wall and path counts
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

        // Calculate straight-line distance between start and end
        Point start = maze.getStart();
        Point end = maze.getEnd();
        double straightLineDistance = Math.sqrt(
                Math.pow(end.x() - start.x(), 2) +
                        Math.pow(end.y() - start.y(), 2)
        );
        stats.put("straightLineDistance", straightLineDistance);

        // Find actual path lengths for different algorithms
        char[][] bfsResult = BFSSolver.solve(maze);
        char[][] dfsResult = DFSSolver.solve(maze);
        char[][] aStarResult = AStarSolver.solve(maze);

        stats.put("bfsPathLength", countPathCells(bfsResult));
        stats.put("dfsPathLength", countPathCells(dfsResult));
        stats.put("aStarPathLength", countPathCells(aStarResult));

        // Calculate path efficiency (ratio of optimal to straight-line)
        stats.put("pathEfficiency", (double) countPathCells(bfsResult) / straightLineDistance);

        // Calculate maze complexity (ratio between longest possible path and actual path)
        stats.put("complexity", (double) countPathCells(dfsResult) / countPathCells(bfsResult));

        return stats;
    }

    private int countPathCells(char[][] grid) {
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