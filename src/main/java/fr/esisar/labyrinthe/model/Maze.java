package fr.esisar.labyrinthe.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Maze {
    private final char[][] grid;
    private final Point start;
    private final Point end;
    private final int rows;
    private final int cols;

    // Constructeur pour charger depuis un fichier
    public Maze(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        validateLines(lines);

        this.rows = lines.size();
        this.cols = lines.get(0).length();
        this.grid = new char[rows][cols];
        this.start = findPoint(lines, 'S');
        this.end = findPoint(lines, 'E');

        for (int i = 0; i < rows; i++) {
            String line = lines.get(i);
            for (int j = 0; j < cols; j++) {
                grid[i][j] = line.charAt(j);
            }
        }
    }

    // Constructeur pour génération aléatoire
    public Maze(char[][] grid, Point start, Point end) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.start = start;
        this.end = end;
    }

    // Nouveau constructeur pour initialiser un labyrinthe vide
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.start = new Point(1, 1);
        this.end = new Point(rows - 2, cols - 2);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '#';
            }
        }
    }

    // Méthodes utilitaires
    private void validateLines(List<String> lines) throws IOException {
        if (lines.isEmpty()) throw new IOException("Fichier vide");
        int firstLineLength = lines.get(0).length();
        for (String line : lines) {
            if (line.length() != firstLineLength) {
                throw new IOException("Labyrinthe non rectangulaire");
            }
        }
    }

    private Point findPoint(List<String> lines, char target) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (lines.get(i).charAt(j) == target) {
                    return new Point(i, j);
                }
            }
        }
        throw new IOException("Point '" + target + "' introuvable");
    }

    // Getters
    public char[][] getGrid() { return grid; }
    public Point getStart() { return start; }
    public Point getEnd() { return end; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    // Pour JavaFX: vérifier si une cellule est un mur
    public boolean isWall(int x, int y) {
        return x < 0 || x >= rows || y < 0 || y >= cols || grid[x][y] == '#';
    }

    // Affichage console
    public void print() {
        for (char[] row : grid) {
            System.out.println(new String(row));
        }
    }
}