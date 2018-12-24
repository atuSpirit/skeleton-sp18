package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.util.Random;

/**
 *  Generate a world randomly according to a random seed.
 *  Dec 16. Change the world generation algorithm to generate
 *  room and hallway alternatively. The hallway and room are
 *  connected.
 *  */
public class WorldGenerator {
    private long seed;
    private final Random RANDOM = new Random(seed);
    private final int MAX = 50; //Maximum number of rooms or hallways.
    private final int MAX_ROOM_SIZE = 10;
    private final int MAX_HALLWAY_LENGTH = 10;

    public TETile[][] world;

    public WorldGenerator(long seed, TETile[][] world) {
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
        if (((start.getX() + width + 1) >= Game.WIDTH) || ((start.getY() + length + 1) >= Game.HEIGHT)) {
            System.out.println("The room is out of boundary. skip");
            return;
        }
        //width = (start.getX() + width + 1) > Game.WIDTH ? (Game.WIDTH - start.getX()) : width;
        //length = (start.getY() + length + 1) > Game.HEIGHT ? (Game.HEIGHT - start.getY()) : length;

        Position rightBottom = new Position((start.getX() + width + 1), start.getY());
        Position leftUp = new Position(start.getX(), (start.getY() + length + 1));
        Position rightUp = new Position((start.getX() + width + 1), (start.getY() + length + 1));

        /* Draw bottom wall */
        drawLine(start, rightBottom, Tileset.WALL);

        /* Draw left wall */
        drawLine(start, leftUp, Tileset.WALL);

        /* Draw right wall */
        drawLine(rightBottom, rightUp, Tileset.WALL);

        /* Draw the top wall */
        drawLine(leftUp, rightUp, Tileset.WALL);

        fillFloor(new Position((start.getX() + 1), (start.getY() + 1)), width, length, Tileset.FLOOR);

    }

    /**
     * Generate a hallway with random start position and end position.
     * If start and end are not in the same row or column, the hallway
     * should turn.
     */
    private void generateHallway1(Position start, Position end) {

    }

    /**
     * Generate a hallway starting from position start with length toward
     * isHorizontal. If a hallway is out of boundary, it won't be drawn.
     * @param start The leftBottom position of the hallway
     * @param isHorizontal The isHorizontal of the hallway. true means horizontal, false means vertical
     * @param length The length of the hallway
     */
    private void generateHallway(Position start, boolean isHorizontal, int length) {
        int endX = start.getX();
        int endY = start.getY();

        if (isHorizontal) {
            //If the hallway cannot fit in the canvas, it won't be drawn
            if ((start.getY() + 2) > (Game.HEIGHT - 1)) {
                System.out.println("Hallway is out of boundary!");
                return;
//                throw new RuntimeException("Hallway is out of boundary!");
            }

            //If the length is too long to be out of boundary, only part of it will be drawn
            endX = (endX + length) > (Game.WIDTH - 1) ? (Game.WIDTH - 1) : (endX + length);
            Position end = new Position(endX, endY);
            drawLine(start, end, Tileset.WALL);

            start = new Position(start.getX(), (start.getY() + 1));
            end = new Position(end.getX(), (end.getY() + 1));
            drawLine(start, end, Tileset.FLOOR);

            start = new Position(start.getX(), (start.getY() + 1));
            end = new Position(end.getX(), (end.getY() + 1));
            drawLine(start, end, Tileset.WALL);
        } else {
            //If the hallway cannot fit in the canvas, it won't be drawn
            if ((start.getX() + 2) > (Game.WIDTH - 1)) {
                System.out.println("Hallway is out of boundary!");
                return;
       //         throw new RuntimeException("Hallway is out of boundary!");
            }

            //If the length is too long to be out of boundary, only part of it will be drawn
            endY = (endY + length) > (Game.HEIGHT - 1) ? (Game.HEIGHT - 1) : (endY + length);
            Position end = new Position(endX, endY);
            drawLine(start, end, Tileset.WALL);

            start = new Position((start.getX() + 1), start.getY());
            end = new Position((end.getX() + 1), end.getY());
            drawLine(start, end, Tileset.FLOOR);

            start = new Position((start.getX() + 1), start.getY());
            end = new Position((end.getX() + 1), end.getY());
            drawLine(start, end, Tileset.WALL);
        }

        /*
        switch (isHorizontal) {
            case '0':
                endX = (endX + length) > Game.WIDTH ? Game.WIDTH : (endX + length);
                break;
            case '1':
                endX = (endX - length) < 0 ? 0 : (endX - length);
                break;
            case '2':
                endY = (endY + length) > Game.HEIGHT ? Game.HEIGHT : (endY + length);
                break;
            case '3':
                endY = (endY - length) < 0 ? 0 : (endY - length);
                break;
            default:
                throw new IllegalArgumentException("Wrong isHorizontal parameter. Should be {0, 1, 2, 3}");
        }

        Position end = new Position(endX, endY);
        drawLine(start, end, Tileset.WALL);
        fillFloor(start, end, );
        drawLine()
*/
    }

    /*
     * Fill in the floor as a rectangle.
     * @param start The start coordinate of the floor
     * @param width The width of the floor
     * @param height The height of the floor
     * @param floorTile  The tile to be filled in the floor
     */
    private void fillFloor(Position start, int width, int height, TETile floorTile) {
        for (int i = 0; i < height; i += 1) {
            //Fill one row
            Position end = new Position((start.getX() + width - 1), start.getY());
            drawLine(start, end, floorTile);

            //Move to next row
            start = new Position(start.getX(), (start.getY() + 1));
        }
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

    public TETile[][] generateWorldTest() {
        //Position start = new Position(Game.WIDTH, Game.HEIGHT);
        Position start = new Position(70, 27);
        int width = 16;
        int height = 5;
        //Position end = new Position(10, 1);
        TETile tile = Tileset.WALL;
       // drawLine(start, end, tile);
       // fillFloor(start, width, height, Tileset.FLOOR);
        generateRoom(start, width, height);

        start = new Position(30, 20);
        generateHallway(start, true, 10);

        return world;
    }

    private TETile[][] generateWorld() {
        int numberOfHallway = RANDOM.nextInt(MAX);
        int numberOfRooms = RANDOM.nextInt(MAX);


        System.out.println("Number of rooms: " + numberOfRooms);
        System.out.println("Number of hallways: " + numberOfHallway);

        for (int i = 0; i < numberOfRooms; i += 1) {
            int startX = RandomUtils.uniform(RANDOM, Game.WIDTH);
            int startY = RandomUtils.uniform(RANDOM, Game.HEIGHT);
            Position start = new Position(startX, startY);
            int width = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);
            int height = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);

            System.out.println(start.toString() + " " + width + " " + height);

            generateRoom(start, width, height);
        }

        /**／
        for (int i = 0; i < numberOfHallway; i += 1) {
            int startX = RandomUtils.uniform(RANDOM, Game.WIDTH);
            int startY = RandomUtils.uniform(RANDOM, Game.HEIGHT);
            Position start = new Position(startX, startY);
            boolean isHorizontal = RandomUtils.bernoulli(RANDOM);
            int length = 1 + RandomUtils.uniform(RANDOM, MAX_HALLWAY_LENGTH);

            generateHallway(start, isHorizontal, length);
        }

/**/
        return this.world;
    }

    public static void main(String[] args) {
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(Game.WIDTH, Game.HEIGHT);

        long seed = 123456;
        TETile[][] worldFrame = new TETile[Game.WIDTH][Game.HEIGHT];
        Game.initializeTheWorld(worldFrame);
        WorldGenerator worldGenerator = new WorldGenerator(seed, worldFrame);
        //worldFrame = worldGenerator.generateWorldTest();

        worldFrame = worldGenerator.generateWorld();

        teRenderer.renderFrame(worldFrame);

    }



}
