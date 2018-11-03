package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;

/**
 * Draws a world consisting of 19 hexagonal regions.
 * @author Hao Lin
 */
public class HexWorld {
    private static final int HEIGHT = 50;
    private static final int WIDTH = 50;

    private static final long SEED = 12347;
    private static final Random RANDOM = new Random(SEED);


    /* Add a hexagon of size s to position p in the world using tile t
     * @param world  the world to draw on
     * @param p the bottom left of the hexagon
     * @param s the size of the hexagon
     * @param t the tile to be drawn
     */
    public void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        if (s < 2) {
            throw new IllegalArgumentException("The size of the hexagon must " 
                                                     + "be greater than 2!");
        }
        for (int yi = 0; yi < 2 * s; yi++) {
            int thisRowY = p.y + yi;
            int thisRowX = p.x + xStartOffset(yi, s);
            Position startPosition = new Position(thisRowX, thisRowY);
            int width = widthOfThisRow(yi, s);

            addRow(world, startPosition, width, t);
        }

    }

    /* Add a row to startPosition in the world
     * @param world the word to draw on
     * @param startPosition the start of position of this row
     * @param width the width of this row
     * @param t the tile to be filled
     */
    private void addRow(TETile[][] world, Position startPosition, int width, TETile t) {
        for (int i = 0; i < width; i++) {
            int xCoord = startPosition.x + i;
            int yCoord = startPosition.y;
            world[xCoord][yCoord] = TETile.colorVariant(t, 32, 32, 32, RANDOM);
        }
    }

    /* Compute the width of yi row.
     * @param yi the index of row relative to the bottom row of a hexagon
     * @param s the size of a hexagon
     * @Return the width of this row.
     */
    public int widthOfThisRow(int yi, int s) {
        return yi < s ? (s + 2 * yi) : (s + 2 * (2 * s - yi - 1));
    }

    /* Get the offset compared to the bottom row of a hexagon.
     * @param yi the index of row compared to the bottom row, starting from zero
     * @param s the size of the hexagon
     */
    public int xStartOffset(int yi, int s) {
        int offset = yi;
        if (yi >= s) {
            offset = 2 * s - 1 - offset;
        }
        return -offset;
    }

    /* Find the first hexagon in the world, which is in the bottom middle
     * of the world
     * @Param hexagonSize the size of the hexagon
     */
    public Position findFirstHexagon(int hexagonSize) {
        int middleOfWorld = WIDTH / 2;
        int middleOfHexagon = hexagonSize / 2;

        return new Position(middleOfWorld - middleOfHexagon, 0);
    }

    /* Find the position of the right upper hexagon of the given hexagon
     * @param currentHexagon the bottom left corner of the given hexagon
     * @param hexagonSize the size of the hexagon
     */
    public Position findRightHexagon(Position currentHexagon, int hexagonSize) {
        int x = currentHexagon.getX() + 2 * hexagonSize - 1;

        int y = currentHexagon.getY() + hexagonSize;
        return new Position(x, y);
    }

    /* Find the position of the left upper hexagon of the given hexagon
     * The x should be larger than minX. Otherwise null should be returned.
     * @param currentHexagon the bottom left corner of the given hexagon
     * @param hexagonSize the size of the hexagon
     */
    public Position findLeftHexagon(Position currentHexagon, int hexagonSize) {
        int x = currentHexagon.getX() - 2 * hexagonSize + 1;
        int y = currentHexagon.getY() + hexagonSize;

        return new Position(x, y);
    }

    /* Draw a column of hexagon of given size, starting from the bottom one
     * @Param world the world to be drawn.
     * @Param hexagonSize the size of the hexagon
     * @Param bottomHexagon the bottom left corner coordinate of the bottom
     *        hexagon in a column.
     * @Param num number of hexagons in a column.
     * */
    public void drawHexagonsByColumn(TETile[][] world, int hexagonSize,
                                     Position bottomHexagon, int num) {
        int xCoord = bottomHexagon.getX();
        int yCoord = bottomHexagon.getY();

        for (int i = 0; i < num; i++) {
            Position p = new Position(xCoord, yCoord + 2 * i * hexagonSize);
            TETile t = randomPickTile();
            addHexagon(world, p, hexagonSize, t);
            System.out.println(p.toString());
        }
    }

    /* Draw the hexagon column by column.  The number of hexagons in each
     * column is 3, 4, 5, 4, 3.
     */
    public void hexWorldDrawer(TETile[][] world, int hexagonSize) {
        Position firstHexagon = findFirstHexagon(hexagonSize);
        drawHexagonsByColumn(world, hexagonSize, firstHexagon, 5);

        Position bottomOfLeftColumn = findLeftHexagon(firstHexagon, hexagonSize);
        drawHexagonsByColumn(world, hexagonSize, bottomOfLeftColumn, 4);

        Position bottomOfSecondLeftColumn = findLeftHexagon(bottomOfLeftColumn, hexagonSize);
        drawHexagonsByColumn(world, hexagonSize, bottomOfSecondLeftColumn, 3);

        Position bottomOfRightColumn = findRightHexagon(firstHexagon, hexagonSize);
        drawHexagonsByColumn(world, hexagonSize, bottomOfRightColumn, 4);

        Position bottomOfSecondRightColumn = findRightHexagon(bottomOfRightColumn, hexagonSize);
        drawHexagonsByColumn(world, hexagonSize, bottomOfSecondRightColumn, 3);
    }


    public static void main(String[] args) {
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(WIDTH, HEIGHT);

        /* Initialize the world */
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }

        /* Draw the world with 19 hexagons */
        int hexagonSize = 4;
        HexWorld hexWorld = new HexWorld();
        hexWorld.hexWorldDrawer(world, hexagonSize);

        teRenderer.renderFrame(world);
    }

    /* Randomly pick a tile */
    public TETile randomPickTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.FLOWER;
            case 1: return Tileset.GRASS;
            case 2: return Tileset.WALL;
            case 3: return Tileset.FLOOR;
            case 4: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

}
