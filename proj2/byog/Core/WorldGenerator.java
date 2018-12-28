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
 * @author Hao Lin
 *  */
public class WorldGenerator {
    private long seed;
    private final Random RANDOM = new Random(seed);
    private final int MAX = 50; //Maximum number of rooms or hallways.
    private final int MAX_ROOM_SIZE = 20;
    private final int MAX_HALLWAY_LENGTH = 20;

    private TETile[][] world;

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
    private Room generateRoom(Position start, int width, int length) {
        /* If the room is out of the boundary of canvas, fit it in */
        if (((start.getX() + width + 1) >= Game.WIDTH) ||
                ((start.getY() + length + 1) >= Game.HEIGHT)) {
            System.out.println("The room is out of boundary. skip");
            return null;
        }

        //TODO  If the room is overlapped with other existing room, discard.

        Room room = new Room(start, width, length);

        return room;
    }


    /**
     * According to previousRoom and direction to generate the start of
     * the middle line of the hallway.
     * @param previousRoom The room where the hallway will grow from.
     * @param direction Which side of the room to choose to grow the hallway.
     *                  0 means the bottom side of the room, 1 means the right
     *                  side of the room, 2 means the up side of the room,
     *                  3 means the left side of the room.
     * @return
     */
    private Position getHallwayStart(Room previousRoom, int direction) {
        int xCoord;
        int yCoord;
        switch (direction) {
            case 0:
                xCoord = RandomUtils.uniform(RANDOM, previousRoom.getMinXCoord() + 1,
                        (previousRoom.getMaxXCoord() - 1));
                yCoord = previousRoom.getMinYCoord();
                break;
            case 1:
                xCoord = previousRoom.getMaxXCoord();
                yCoord = RandomUtils.uniform(RANDOM, previousRoom.getMinYCoord() + 1,
                        (previousRoom.getMaxYCoord() - 1));
                break;
            case 2:
                xCoord = RandomUtils.uniform(RANDOM, previousRoom.getMinXCoord() + 1,
                        (previousRoom.getMaxXCoord() - 1));
                yCoord = previousRoom.getMaxYCoord();
                break;
            default:
                xCoord = previousRoom.getMinXCoord();
                yCoord = RandomUtils.uniform(RANDOM, previousRoom.getMinYCoord() + 1,
                        (previousRoom.getMaxYCoord() - 1));
        }
        Position start = new Position(xCoord, yCoord);

        return start;
    }

    private Position getHallwayEnd(Position start, int direction, int length) {
        int xCoord;
        int yCoord;

        switch (direction) {
            case 0:
                xCoord = start.getX();
                yCoord = start.getY() - length + 1;
                break;
            case 1:
                xCoord = start.getX() + length - 1;
                yCoord = start.getY();
                break;
            case 2:
                xCoord = start.getX();
                yCoord = start.getY() + length - 1;
                break;
            default:
                xCoord = start.getX() - length + 1;
                yCoord = start.getY();
        }
        if ((xCoord < 0) || (xCoord >= Game.WIDTH) || (yCoord < 0) || (yCoord >= Game.HEIGHT)) {
            return null;
        }
        Position end = new Position(xCoord, yCoord);
        return end;
    }

    /**
     * Generate a hallway with random start position and end position.
     * If start and end are not in the same row or column, the hallway
     * should turn.
     * @param previousRoom The room adjacent to the start of the hallway.
     */
    private Hallway generateHallway(Room previousRoom) {
        //Randomly choose one side of the room to grow the hallway
        int direction = RandomUtils.uniform(RANDOM, 4);
        Position start = getHallwayStart(previousRoom, direction);
        Position middle = null;
        int LshapeDirection = -1;   //The direction
        Position end = null;

        int length = 1 + RandomUtils.uniform(RANDOM, MAX_HALLWAY_LENGTH);
        int middleLength = 0;
        System.out.println("Hallway length: " + length);

        int isLShape;
        /* If length of hallway is smaller than 5, L shape hallway
           could not be drawn.
         */
        if (length < 5) {
            isLShape = 0;
        } else {
            isLShape = RandomUtils.uniform(RANDOM, 2);
        }
         //for debugging
        if (0 == isLShape) {    //should be 0 == isLShape, changed for temporarily debugging
            end = getHallwayEnd(start, direction, length);
        } else {
            //generate middle position and L shape.
            middleLength = RandomUtils.uniform(RANDOM, length);
            /* The two arms of L shape should be at least length 1 */
            if (middleLength < 3 || middleLength > (length - 2)) {
                middle = null;
            } else {
                middle = getHallwayEnd(start, direction, middleLength);
                /* The Lshape direction could be only two possibility according to direction.
                 * 0 -> 3 or 1
                 * 1 -> 2 or 0
                 * 2 -> 1 or 3
                 * 3 -> 0 or 2
                 * */
                int rand = RandomUtils.uniform(RANDOM,1);
                if (rand == 0) {
                    rand = -1;
                }
                LshapeDirection = (4 + direction + rand ) % 4;

                end = getHallwayEnd(middle, LshapeDirection, (length - middleLength));
            }
        }

        if (end == null) return null;

        Hallway hallway = new Hallway(start, direction, length, middle, end);
        if (middle != null) {
            hallway.setlShapeDirection(LshapeDirection);
            hallway.setMiddleLength(middleLength);
        }
        return hallway;
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
        int minX = start.getX();
        int maxX = end.getX();
        int minY = start.getY();
        int maxY= end.getY();

        if (end.getX() < minX) {
            minX = end.getX();
            maxX = start.getX();
        }

        if (end.getY() < minY) {
            minY = end.getY();
            maxY = start.getY();
        }

        for (int xCoord = minX; xCoord <= maxX; xCoord += 1) {
            for (int yCoord = minY; yCoord <= maxY; yCoord += 1) {
                world[xCoord][yCoord] = TETile.colorVariant(tile, 32, 32, 32, RANDOM);
            }
        }

    }

    /* Helper function to increment hallway position by one line.
     * The xCoord and yCoord will
     * increase according to the direction of the hallway */
    private Position plusPosition(Position position, int direction) {
        int xCoord = position.getX();
        int yCoord = position.getY();

        if ((direction == 0) || (direction == 2)) {
            xCoord++;
        } else {
            yCoord++;
        }
        return new Position(xCoord, yCoord);
    }

    /* Helper function to decrease hallway position by one line.
     * The xCoord and yCoord
     * will decrease according to the direction of the hallway */
    private Position minusPosition(Position position, int direction) {
        int xCoord = position.getX();
        int yCoord = position.getY();

        if ((direction == 0) || (direction == 2)) {
            xCoord--;
        } else {
            yCoord--;
        }

        return new Position(xCoord, yCoord);
    }

    /* Given the hallway object which denoting the middle line,
     * draw the three lines of hallway.
     */
    private void drawHallway(Hallway hallway) {
        Position floorStart = hallway.getStart();
        Position floorMiddle = hallway.getMiddle();
        Position floorEnd = hallway.getEnd();


        if (hallway.getMiddle() == null) {
            int direction = hallway.getDirection();
            drawLine(floorStart, floorEnd, Tileset.FLOOR);

            Position start = minusPosition(floorStart, direction);
            Position end = minusPosition(floorEnd, direction);
            drawLine(start, end, Tileset.WALL);

            start = plusPosition(floorStart, direction);
            end = plusPosition(floorEnd, direction);
            drawLine(start, end, Tileset.WALL);
        } else {

            int direction = hallway.getDirection();
            int lShapeDirection = hallway.getlShapeDirection();
            int rightArmLength = hallway.getLength() - hallway.getMiddleLength();
            floorEnd = getHallwayEnd(floorMiddle, lShapeDirection, rightArmLength);

            drawLShapeLine(floorStart, floorMiddle, floorEnd, Tileset.FLOOR);

            Position firstWallMiddle = new Position(floorMiddle.getX() - 1,
                                                    floorMiddle.getY() - 1);
            Position firstWallStart = minusPosition(floorStart, direction);
            Position firstWallEnd = minusPosition(floorEnd, lShapeDirection);
            drawLShapeLine(firstWallStart, firstWallMiddle, firstWallEnd, Tileset.WALL);

            Position secondWallMiddle = new Position(floorMiddle.getX() + 1,
                                                    floorMiddle.getY() + 1);
            Position secondWallStart = plusPosition(floorStart, direction);
            Position secondWallEnd = plusPosition(floorEnd, lShapeDirection);
            drawLShapeLine(secondWallStart, secondWallMiddle, secondWallEnd, Tileset.WALL);
        }
    }

    /**
     * Draw L shape line.
     * @param start The start position of the L shape
     * @param middle The position of L corner
     * @param end The end position of the L line
     * @param tile The tile to fill the L line
     */
    private void drawLShapeLine(Position start, Position middle, Position end, TETile tile) {
        drawLine(start, middle, tile);
        drawLine(middle, end, tile);
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
        Room room = generateRoom(start, width, height);

        testLShapeHallwayDrawing();

        return world;
    }

    private TETile[][] generateWorld() {
        int numberOfRooms = RANDOM.nextInt(MAX);

        //The start position of the first room
        int startX = RandomUtils.uniform(RANDOM, Game.WIDTH);
        int startY = RandomUtils.uniform(RANDOM, Game.HEIGHT);
        Position start = new Position(startX, startY);

        Room room = null;
        Hallway hallway = null;

        for (int i = 0; i < numberOfRooms; i += 1) {
            //Draw room whose leftBottom corner is at position start
            while (room == null) {
                int width = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);
                int height = 1 + RandomUtils.uniform(RANDOM, MAX_ROOM_SIZE);
                room = generateRoom(start, width, height);
            }
            drawRoom(room);

            //Draw hallway adjacent to room
            while (hallway == null) {
                hallway = generateHallway(room);
            }
            drawHallway(hallway);
            //Generate the start position of next room linked to current hallway
            start = getNextRoomStart(hallway.getEnd());
        }

        return this.world;
    }

    private void drawRoom(Room room) {
        Position leftBottom = room.getLeftBottom();
        int width = room.getWidth();
        int height = room.getHeight();

        Position rightBottom = new Position((leftBottom.getX() + width + 1), leftBottom.getY());
        Position leftUp = new Position(leftBottom.getX(), (leftBottom.getY() + height + 1));
        Position rightUp = new Position((leftBottom.getX() + width + 1), (leftBottom.getY() + height + 1));


        /* Draw bottom wall */
        drawLine(leftBottom, rightBottom, Tileset.WALL);

        /* Draw left wall */
        drawLine(leftBottom, leftUp, Tileset.WALL);

        /* Draw right wall */
        drawLine(rightBottom, rightUp, Tileset.WALL);

        /* Draw the top wall */
        drawLine(leftUp, rightUp, Tileset.WALL);

        fillFloor(new Position((leftBottom.getX() + 1), (leftBottom.getY() + 1)), width, height, Tileset.FLOOR);
    }


    /* According to the end of previousHallway, generate the
       start of current room.
     */
    private Position getNextRoomStart(Position hallwayEnd) {
        return null;
    }

    private void testLShapeHallwayDrawing() {
        Position start = new Position(20, 10);
        Position middle = new Position(22, 10);
        Position end = new Position(22, 8);
        int direction = 1;
        int length = 6;
        Hallway hallway = new Hallway(start, 1, length, middle, end);
        hallway.setlShapeDirection(0);
        hallway.setMiddleLength(3);
        drawHallway(hallway);
    }

    public static void main(String[] args) {
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(Game.WIDTH, Game.HEIGHT);

        long seed = 8765431;
        TETile[][] worldFrame = new TETile[Game.WIDTH][Game.HEIGHT];
        Game.initializeTheWorld(worldFrame);
        WorldGenerator worldGenerator = new WorldGenerator(seed, worldFrame);
        worldFrame = worldGenerator.generateWorldTest();

        //worldFrame = worldGenerator.generateWorld();

        teRenderer.renderFrame(worldFrame);

    }



}
