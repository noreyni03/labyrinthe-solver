package fr.esisar.labyrinthe.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Maze {
    private final char[][] grid;
    private final Point start;
    private final Point end;
    private final int rows;
    private final int cols;

    // Chargement depuis un fichier
    public Maze(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        validateLines(lines);

        this.rows = lines.size();
        this.cols = lines.get(0).length(); // Utilisation de get(0) au lieu de getFirst()
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

    // Génération aléatoire
    public Maze(int rows, int cols) {
        this.rows = (rows % 2 == 0) ? rows - 1 : rows; // Taille impaire
        this.cols = (cols % 2 == 0) ? cols - 1 : cols;
        this.grid = generateGrid();
        this.start = new Point(1, 1);
        this.end = new Point(this.rows - 2, this.cols - 2);
    }

    // Méthodes utilitaires
    private void validateLines(List<String> lines) throws IOException {
        if (lines.isEmpty()) throw new IOException("Fichier vide");
        int firstLineLength = lines.get(0).length(); // Utilisation de get(0) au lieu de getFirst()
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

    private char[][] generateGrid() {
        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, '#');

        Stack<Point> stack = new Stack<>();
        Random rand = new Random();

        // Point de départ
        grid[start.x()][start.y()] = 'S';
        stack.push(start);

        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            List<int[]> shuffledDirs = Arrays.asList(directions);
            Collections.shuffle(shuffledDirs, rand);

            for (int[] dir : shuffledDirs) {
                int newX = current.x() + dir[0];
                int newY = current.y() + dir[1];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && grid[newX][newY] == '#') {
                    grid[current.x() + dir[0] / 2][current.y() + dir[1] / 2] = '=';
                    grid[newX][newY] = '=';
                    stack.push(current);
                    stack.push(new Point(newX, newY));
                }
            }
        }

        grid[end.x()][end.y()] = 'E';
        return grid;
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