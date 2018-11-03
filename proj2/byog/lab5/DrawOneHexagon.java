package byog.lab5;

/* outdated one, leave for zefeng to check why main and unit test cannot be put together*/
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class DrawOneHexagon {
    private static final int HEIGHT = 50;
    private static final int WIDTH = 50;

    private static final long SEED = 12345;
    private static final Random random = new Random(SEED);
    /* Add a hexagon of size s to position p in the world using tile t
     * @param world  the world to draw on
     * @param p the bottom left of the hexagon
     * @param s the size of the hexagon
     * @param t the tile to be drawn
     */
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        if (s < 2) {
            throw new IllegalArgumentException("The size of the hexagon must be greater than 2!");
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
    private static void addRow(TETile[][] world, Position startPosition, int width, TETile t) {
        for (int i = 0; i < width; i++) {
            int xCoord = startPosition.x + i;
            int yCoord = startPosition.y;
            world[xCoord][yCoord] = TETile.colorVariant(t, 32, 32, 32, random);
        }
    }

    /* Compute the width of yi row.
     * @param yi the index of row relative to the bottom row of a hexagon
     * @param s the size of a hexagon
     */
    private static int widthOfThisRow(int yi, int s) {
        int width = s;
        return yi < s ? (s + 2 * yi) : (s + 2 * (2 * s - yi - 1));
    }

    /* Get the offset compared to the bottom row of a hexagon.
     * @param yi the index of row compared to the bottom row, starting from zero
     * @param s the size of the hexagon
     */
    private static int xStartOffset(int yi, int s) {
        int offset = yi;
        if (yi >= s) {
            offset = 2 * s - 1 - offset;
        }
        return -offset;
    }

    public static void main(String[] args) {
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(WIDTH, HEIGHT);

        //Initialize the world
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        int size = 5;
        Position p = new Position(20, 20);
        TETile t = randomPickTile();
        addHexagon(world, p, size, t);

    }

    private static TETile randomPickTile() {
        int tileNum = random.nextInt(5);
        switch (tileNum) {
            case 0: return TETile.colorVariant(Tileset.FLOWER, 5, 5, 5, random);
            case 1: return Tileset.GRASS;
            case 2: return Tileset.WALL;
            case 3: return Tileset.FLOOR;
            case 4: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }

    @Test
    public void testWidthOfThisRow() {
        assertEquals(4, widthOfThisRow(0, 4));
        assertEquals(6, widthOfThisRow(1, 4));
        assertEquals(8, widthOfThisRow(2, 4));
        assertEquals(10, widthOfThisRow(3, 4));
        assertEquals(10, widthOfThisRow(4, 4));
        assertEquals(8, widthOfThisRow(5, 4));
        assertEquals(6, widthOfThisRow(6, 4));
        assertEquals(4, widthOfThisRow(7, 4));


    }
    @Test
    public void testXStartOffset() {
        assertEquals(0, xStartOffset(0, 4));
        assertEquals(-1, xStartOffset(1, 4));
        assertEquals(-2, xStartOffset(2, 4));
        assertEquals(-3, xStartOffset(3, 4));
        assertEquals(-3, xStartOffset(4, 4));
        assertEquals(-2, xStartOffset(5, 4));
        assertEquals(-1, xStartOffset(6, 4));
        assertEquals(0, xStartOffset(7, 4));
    }

}
