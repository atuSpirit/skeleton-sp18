package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.util.Random;

/**
 * Generate a world randomly according to a random seed.
 *  Dec 16. Change the world generation algorithm to generate
 *  room and hallway alternatively. The hallway and room are
 *  connected.
 */

public class WorldGenerator2 {
    private long seed;
    private final Random RANDOM = new Random(seed);
    private final int MAX = 50; //Maximum number of rooms or hallways.
    private final int MAX_ROOM_SIZE = 10;
    private final int MAX_HALLWAY_LENGTH = 10;

    public TETile[][] world;

    public WorldGenerator2(long seed, TETile[][] world) {
        this.seed = seed;
        this.world = world;
    }

    /**
     * Draw a rectangle room starting from start position with given
     * width and length.
     * @param start The leftBottom corner of the room.
     * @param width The width of the room
     * @param length The length of the room
     */
    private void generateRoom(Position start, int width, int length) {
        /* If the room is out of the boundary of canvas, fit it in */
        width = (start.getX() + width + 1) > Game.WIDTH ? (Game.WIDTH - start.getX()) : width;
        length = (start.getY() + length + 1) > Game.HEIGHT ? (Game.HEIGHT - start.getY()) : length;

        Position rightBottom = new Position((start.getX() + width), start.getY());
        Position leftUp = new Position(start.getX(), (start.getY() + length));
        Position rightUp = new Position((start.getX() + width), (start.getY() + length));

        /* Draw bottom wall */
        drawLine(start, rightBottom, Tileset.WALL);

        /* Draw left wall */
        drawLine(start, leftUp, Tileset.WALL);

        /* Draw right wall */
        drawLine(rightBottom, rightUp, Tileset.WALL);

        /* Draw the top wall */
        drawLine(leftUp, rightUp, Tileset.WALL);

        fillFloor(new Position((start.getX() + 1), (start.getY() + 1)), width - 2, length - 2, Tileset.FLOOR);

    }

    /**
     * Draw Line from position start to position end.
     * Start and end should be guaranteed to be in the boundary before
     * calling this function.
     */
    private void drawLine(Position start, Position end, TETile tile) {
        /*
        int endX = end.getX() > Game.WIDTH ? Game.WIDTH : end.getX();
        int endY = end.getY() > Game.HEIGHT ? Game.HEIGHT : end.getY();
        end = new Position(endX, endY);
        */
        for (int xCoord = start.getX(); xCoord <= end.getX(); xCoord += 1) {
            for (int yCoord = start.getY(); yCoord <= end.getY(); yCoord += 1) {
                world[xCoord][yCoord] = TETile.colorVariant(tile, 32, 32, 32, RANDOM);
            }
        }

    }

    /*
     * Fill in the floor as a rectangle.
     * @param start The start coordinate of the floor
     * @param width The width of the floor
     * @param height The height of the floor
     * @param floorTile  The tile to be filled in the floor
     */
    private void fillFloor(Position start, int width, int height, TETile floorTile) {
        if ((start.getX() + width) > Game.WIDTH) {
            width = Game.WIDTH - start.getX();
        }
        for (int i = 0; i < height; i += 1) {
            //Fill one row
            Position end = new Position((start.getX() + width - 1), start.getY());
            drawLine(start, end, floorTile);

            //Move to next row
            start = new Position(start.getX(), (start.getY() + 1));
        }
    }

    private TETile[][] generateWorld() {
        int number = RANDOM.nextInt(MAX);

        for (int i = 0; i < number; i += 1) {
            int startX = RandomUtils.uniform(RANDOM, Game.WIDTH);
            int startY = RandomUtils.uniform(RANDOM, Game.HEIGHT);
            Position roomStart = new Position(startX, startY);
            int width = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);
            int height = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);
            int length = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);

            System.out.println(roomStart.toString() + " " + width + " " + height);

            generateRoom(roomStart, width, height);
            //generateHallway( );
        }
        return world;
    }

}
