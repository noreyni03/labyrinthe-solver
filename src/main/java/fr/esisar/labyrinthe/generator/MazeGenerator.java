package fr.esisar.labyrinthe.generator;

import fr.esisar.labyrinthe.model.Maze;
import fr.esisar.labyrinthe.model.Point;

import java.util.*;

public class MazeGenerator {
    public enum Algorithm {
        RECURSIVE_BACKTRACKING,
        PRIM,
        KRUSKAL,
        RANDOM_ROOMS
    }

    public static Maze generate(int rows, int cols, Algorithm algorithm) {
        switch (algorithm) {
            case RECURSIVE_BACKTRACKING:
                return new RecursiveBacktrackingGenerator().generate(rows, cols);
            case PRIM:
                return new PrimGenerator().generate(rows, cols);
            case KRUSKAL:
                return new KruskalGenerator().generate(rows, cols);
            case RANDOM_ROOMS:
                return new RoomGenerator().generate(rows, cols);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    private static class RecursiveBacktrackingGenerator {
        private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        public Maze generate(int rows, int cols) {
            // Ensure odd dimensions
            rows = (rows % 2 == 0) ? rows + 1 : rows;
            cols = (cols % 2 == 0) ? cols + 1 : cols;

            char[][] grid = new char[rows][cols];
            for (char[] row : grid) Arrays.fill(row, '#');

            // Choose random start point (must be odd coordinates)
            Random rand = new Random();
            int startX = rand.nextInt(rows / 2) * 2 + 1;
            int startY = rand.nextInt(cols / 2) * 2 + 1;

            // Carve paths
            carvePassages(startX, startY, grid, rand);

            // Place start and end
            Point start = new Point(1, 1);
            grid[start.x()][start.y()] = 'S';

            // Find a good end point (far from start)
            int maxDistance = 0;
            Point end = null;

            for (int i = 1; i < rows - 1; i += 2) {
                for (int j = 1; j < cols - 1; j += 2) {
                    if (grid[i][j] != '#' && grid[i][j] != 'S') {
                        int distance = Math.abs(i - start.x()) + Math.abs(j - start.y());
                        if (distance > maxDistance) {
                            maxDistance = distance;
                            end = new Point(i, j);
                        }
                    }
                }
            }

            if (end != null) {
                grid[end.x()][end.y()] = 'E';
            } else {
                // Fallback
                grid[rows - 2][cols - 2] = 'E';
            }

            return createMazeFromGrid(grid);
        }

        private void carvePassages(int x, int y, char[][] grid, Random rand) {
            grid[x][y] = ' ';

            // Shuffle directions for randomness
            List<int[]> directions = Arrays.asList(DIRECTIONS.clone());
            Collections.shuffle(directions, rand);

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                int px = x + dir[0]/2;
                int py = y + dir[1]/2;

                if (nx > 0 && nx < grid.length - 1 && ny > 0 && ny < grid[0].length - 1 && grid[nx][ny] == '#') {
                    grid[px][py] = ' ';  // Carve passage between cells
                    carvePassages(nx, ny, grid, rand);
                }
            }
        }
    }

    private static class PrimGenerator {
        private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        public Maze generate(int rows, int cols) {
            // Ensure odd dimensions
            rows = (rows % 2 == 0) ? rows + 1 : rows;
            cols = (cols % 2 == 0) ? cols + 1 : cols;

            char[][] grid = new char[rows][cols];
            for (char[] row : grid) Arrays.fill(row, '#');

            Random rand = new Random();
            // Start with a random cell
            int startX = rand.nextInt(rows / 2) * 2 + 1;
            int startY = rand.nextInt(cols / 2) * 2 + 1;
            grid[startX][startY] = ' ';

            // Use priority queue for frontier cells (simulates random selection)
            List<Point> frontier = new ArrayList<>();

            // Add frontier cells around starting point
            addFrontierCells(startX, startY, grid, frontier);

            while (!frontier.isEmpty()) {
                // Pick a random frontier cell
                int idx = rand.nextInt(frontier.size());
                Point current = frontier.get(idx);
                frontier.remove(idx);

                // Connect to a random "in" neighbor
                List<Point> neighbors = getInMazeNeighbors(current.x(), current.y(), grid);
                if (!neighbors.isEmpty()) {
                    Point neighbor = neighbors.get(rand.nextInt(neighbors.size()));

                    // Carve passage
                    int passageX = (current.x() + neighbor.x()) / 2;
                    int passageY = (current.y() + neighbor.y()) / 2;
                    grid[passageX][passageY] = ' ';
                    grid[current.x()][current.y()] = ' ';

                    // Add new frontier cells
                    addFrontierCells(current.x(), current.y(), grid, frontier);
                }
            }

            // Place start and end points
            grid[1][1] = 'S';
            grid[rows - 2][cols - 2] = 'E';

            return createMazeFromGrid(grid);
        }

        private void addFrontierCells(int x, int y, char[][] grid, List<Point> frontier) {
            for (int[] dir : DIRECTIONS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx > 0 && nx < grid.length - 1 && ny > 0 && ny < grid[0].length - 1) {
                    if (grid[nx][ny] == '#') {
                        frontier.add(new Point(nx, ny));
                    }
                }
            }
        }

        private List<Point> getInMazeNeighbors(int x, int y, char[][] grid) {
            List<Point> neighbors = new ArrayList<>();

            for (int[] dir : DIRECTIONS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx > 0 && nx < grid.length - 1 && ny > 0 && ny < grid[0].length - 1) {
                    if (grid[nx][ny] == ' ') {
                        neighbors.add(new Point(nx, ny));
                    }
                }
            }

            return neighbors;
        }
    }

    private static class KruskalGenerator {
        public Maze generate(int rows, int cols) {
            // Ensure odd dimensions
            rows = (rows % 2 == 0) ? rows + 1 : rows;
            cols = (cols % 2 == 0) ? cols + 1 : cols;

            char[][] grid = new char[rows][cols];
            for (char[] row : grid) Arrays.fill(row, '#');

            // Initialize all cells as individual sets
            int cellCount = (rows / 2) * (cols / 2);
            DisjointSet sets = new DisjointSet(cellCount);

            // Generate all possible walls
            List<Wall> walls = new ArrayList<>();
            for (int i = 1; i < rows - 1; i += 2) {
                for (int j = 1; j < cols - 1; j += 2) {
                    grid[i][j] = ' ';  // Mark cell as passage

                    int cellId = getCellId(i, j, cols);

                    // Add horizontal wall
                    if (j + 2 < cols - 1) {
                        walls.add(new Wall(
                                new Point(i, j),
                                new Point(i, j + 2),
                                new Point(i, j + 1)
                        ));
                    }

                    // Add vertical wall
                    if (i + 2 < rows - 1) {
                        walls.add(new Wall(
                                new Point(i, j),
                                new Point(i + 2, j),
                                new Point(i + 1, j)
                        ));
                    }
                }
            }

            // Shuffle walls
            Collections.shuffle(walls, new Random());

            // Kruskal's algorithm
            for (Wall wall : walls) {
                int cell1 = getCellId(wall.cell1.x(), wall.cell1.y(), cols);
                int cell2 = getCellId(wall.cell2.x(), wall.cell2.y(), cols);

                if (sets.find(cell1) != sets.find(cell2)) {
                    // Remove wall
                    grid[wall.wallPoint.x()][wall.wallPoint.y()] = ' ';
                    sets.union(cell1, cell2);
                }
            }

            // Place start and end
            grid[1][1] = 'S';
            grid[rows - 2][cols - 2] = 'E';

            return createMazeFromGrid(grid);
        }

        private int getCellId(int x, int y, int cols) {
            return (x / 2) * (cols / 2) + (y / 2);
        }

        private static class Wall {
            Point cell1;
            Point cell2;
            Point wallPoint;

            Wall(Point cell1, Point cell2, Point wallPoint) {
                this.cell1 = cell1;
                this.cell2 = cell2;
                this.wallPoint = wallPoint;
            }
        }

        private static class DisjointSet {
            private final int[] parent;
            private final int[] rank;

            DisjointSet(int size) {
                parent = new int[size];
                rank = new int[size];
                for (int i = 0; i < size; i++) {
                    parent[i] = i;
                }
            }

            int find(int x) {
                if (parent[x] != x) {
                    parent[x] = find(parent[x]);
                }
                return parent[x];
            }

            void union(int x, int y) {
                int rootX = find(x);
                int rootY = find(y);

                if (rootX == rootY) return;

                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    if (rank[rootX] == rank[rootY]) {
                        rank[rootX]++;
                    }
                }
            }
        }
    }

    private static class RoomGenerator {
        private static final int MIN_ROOM_SIZE = 3;
        private static final int MAX_ROOM_SIZE = 8;
        private static final int MIN_ROOMS = 3;
        private static final int MAX_ROOMS = 10;

        public Maze generate(int rows, int cols) {
            char[][] grid = new char[rows][cols];
            for (char[] row : grid) Arrays.fill(row, '#');

            Random rand = new Random();
            List<Room> rooms = new ArrayList<>();

            // Generate random rooms
            int numRooms = MIN_ROOMS + rand.nextInt(MAX_ROOMS - MIN_ROOMS + 1);
            for (int i = 0; i < numRooms; i++) {
                int roomWidth = MIN_ROOM_SIZE + rand.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
                int roomHeight = MIN_ROOM_SIZE + rand.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
                int x = 1 + rand.nextInt(rows - roomHeight - 1);
                int y = 1 + rand.nextInt(cols - roomWidth - 1);

                Room room = new Room(x, y, roomWidth, roomHeight);
                if (isRoomValid(room, rooms)) {
                    rooms.add(room);
                    carveRoom(room, grid);
                }
            }

            // Connect rooms with corridors
            for (int i = 0; i < rooms.size() - 1; i++) {
                connectRooms(rooms.get(i), rooms.get(i + 1), grid, rand);
            }

            // Place start and end in first and last room
            placeStartEnd(rooms, grid);

            return createMazeFromGrid(grid);
        }

        private boolean isRoomValid(Room newRoom, List<Room> existingRooms) {
            for (Room room : existingRooms) {
                if (newRoom.intersects(room)) return false;
            }
            return true;
        }

        private void carveRoom(Room room, char[][] grid) {
            for (int i = room.x; i < room.x + room.height; i++) {
                for (int j = room.y; j < room.y + room.width; j++) {
                    grid[i][j] = ' ';
                }
            }
        }

        private void connectRooms(Room a, Room b, char[][] grid, Random rand) {
            int x1 = a.centerX();
            int y1 = a.centerY();
            int x2 = b.centerX();
            int y2 = b.centerY();

            // Random walk to connect centers
            while (x1 != x2 || y1 != y2) {
                if (x1 < x2) x1++;
                else if (x1 > x2) x1--;

                if (y1 < y2) y1++;
                else if (y1 > y2) y1--;

                grid[x1][y1] = ' ';
            }
        }

        private void placeStartEnd(List<Room> rooms, char[][] grid) {
            if (rooms.isEmpty()) return;

            // Start in first room
            Room first = rooms.get(0);
            grid[first.x + 1][first.y + 1] = 'S';

            // End in last room
            Room last = rooms.get(rooms.size() - 1);
            grid[last.x + last.height - 2][last.y + last.width - 2] = 'E';
        }

        private static class Room {
            int x, y, width, height;

            Room(int x, int y, int width, int height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            int centerX() { return x + height/2; }
            int centerY() { return y + width/2; }

            boolean intersects(Room other) {
                return (x < other.x + other.width &&
                        x + width > other.x &&
                        y < other.y + other.height &&
                        y + height > other.y);
            }
        }
    }

    private static Maze createMazeFromGrid(char[][] grid) {
        // Find start and end points
        Point start = null;
        Point end = null;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'S') start = new Point(i, j);
                if (grid[i][j] == 'E') end = new Point(i, j);
            }
        }

        if (start == null) start = new Point(1, 1);
        if (end == null) end = new Point(grid.length - 2, grid[0].length - 2);

        return new Maze(grid, start, end);
    }
}