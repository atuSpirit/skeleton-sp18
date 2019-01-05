package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/**
 *  Generate a world randomly according to a random seed.
 *  Generate the room and hallway alternatively. The hallway and room are
 *  connected. The hallway could be L shape.
 * @author Hao Lin
 *  */
public class WorldGenerator {
    /* The maximum allowed times to failed generate a room. If exceed this number,
     * a new start will be used. */
    private static final int MAX_FAILED_TIMES = 10;
    private long seed;
    private Random RANDOM;
    private final int MAX = 50; //Maximum number of rooms or hallways.
    private final int MAX_ROOM_SIZE = 10;
    private final int MAX_HALLWAY_LENGTH = 10;

    private TETile[][] world;
    private List<Room> roomList;


    public WorldGenerator(long seed, TETile[][] world) {
        this.seed = seed;
        RANDOM = new Random(seed);
        this.world = world;
        roomList = new ArrayList<>();
    }

    /**
     * Draw a rectangle room starting from start position with given
     * width and length.
     * @param start The leftBottom corner of the room.
     * @param width The width of the room
     * @param length The length of the room
     * @return room The room generated.
     */
    private Room generateRoom(Position start, int width, int length) {
        /* If the room is out of the boundary of canvas, fit it in */
        if ((start.getX() < 0) || (start.getY() < 0) ||
                ((start.getX() + width + 1) >= Game.WIDTH) ||
                ((start.getY() + length + 1) >= Game.HEIGHT)) {
//            System.out.println("The room is out of boundary. skip");
            return null;
        }

        Room room = new Room(start, width, length);

        return room;
    }

    /**
     * Detect whether the room is overlapped with any existing room or hallway.
     * The connected Position's overlap should not count.
     * @param room
     * @param connectPosition
     * @param direction
     * @return
     */
    private boolean hasOverlap(Room room, Position connectPosition, int direction) {
        /* The overlap with hallway connector does not count as overlap */
        int minXCoord = room.getMinXCoord();
        int maxXCoord = room.getMaxXCoord();
        int minYCoord = room.getMinYCoord();
        int maxYCoord = room.getMaxYCoord();

        Position plusPosition = plusPosition(connectPosition, direction);
        Position minusPosition = minusPosition(connectPosition, direction);

        for (int xCoord = minXCoord; xCoord < maxXCoord; xCoord++) {
            for (int yCoord = minYCoord; yCoord < maxYCoord; yCoord++) {
                /* Skip the connect positions between the room and the previous hallway */
                if ((xCoord == connectPosition.getX() && yCoord == connectPosition.getY())
                || (xCoord == plusPosition.getX() && yCoord == plusPosition.getY())
                || (xCoord == minusPosition.getX() && yCoord == minusPosition.getY())) {
                    continue;
                }
                if (world[xCoord][yCoord] != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean lineOverlap(Position start, Position end) {
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
                /* start position of a hallway is the connected position.
                 * So the position is connected to the previous room.
                 */
                if (xCoord == start.getX() && yCoord == start.getY()) {
                    continue;
                }
                if (world[xCoord][yCoord] != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }



    /* Check whether hallway is overlapped with existing room and hallway.
     * Only the middle line will be checked.  The walls are allowed to overlap.
     * The first position of middle line is connected to room. So it is allowed
     * to overlap.
     */
    private boolean hasOverlapDirectHallway(Hallway hallway) {
        boolean overlapped = false;
        Position floorStart = hallway.getStart();
        Position floorEnd = hallway.getEnd();

        int direction = hallway.getDirection();
        overlapped = lineOverlap(floorStart, floorEnd);
        if (overlapped == true) {
            return true;
        }

        Position start = minusPosition(floorStart, direction);
        Position end = minusPosition(floorEnd, direction);
        overlapped = lineOverlap(start, end);
        if (overlapped == true) {
            return true;
        }

        start = plusPosition(floorStart, direction);
        end = plusPosition(floorEnd, direction);
        overlapped = lineOverlap(start, end);

        return overlapped;
    }

    private boolean hasOverlap(Hallway hallway) {
        if (hallway.getMiddle() == null) {
            return hasOverlapDirectHallway(hallway);
        } else {
            return hasOverlapDirectHallway(hallway.getFirstArm()) ||
                    hasOverlapDirectHallway(hallway.getSecondArm());
        }
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
        int min, max;
        switch (direction) {
            case 0:
                min = previousRoom.getMinXCoord() + 1;
                max = previousRoom.getMaxXCoord() - 1;
                if (min == max) {
                    xCoord = min;
                } else {
                    xCoord = min + RANDOM.nextInt(max - min);
                }
                yCoord = previousRoom.getMinYCoord();
                break;
            case 1:
                min = previousRoom.getMinYCoord() + 1;
                max = previousRoom.getMaxYCoord() - 1;
                xCoord = previousRoom.getMaxXCoord();
                if (min == max) {
                    yCoord = min;
                } else {
                    yCoord = min + RANDOM.nextInt(max - min);
                }
                break;
            case 2:
                min = previousRoom.getMinXCoord() + 1;
                max = previousRoom.getMaxXCoord() - 1;
                if (min == max) {
                    xCoord = min;
                } else {
                    xCoord = min + RANDOM.nextInt(max - min);
                }
                yCoord = previousRoom.getMaxYCoord();
                break;
            default:
                min = previousRoom.getMinYCoord() + 1;
                max = previousRoom.getMaxYCoord() - 1;
                xCoord = previousRoom.getMinXCoord();
                if (min == max) {
                    yCoord = min;
                } else {
                    yCoord = min + RANDOM.nextInt(max - min);
                }
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
        /* The range of middle line */
        if ((xCoord < 1) || (xCoord > (Game.WIDTH - 2)) || (yCoord < 1) || (yCoord > (Game.HEIGHT - 2))) {
            return null;
        }

        Position end = new Position(xCoord, yCoord);
        return end;
    }

    /**
     * Generate a hallway with random start position and end position.
     * If start and end are not in the same row or column, the hallway
     * should turn. The minimal length of a hallway is 2. If the length is 1,
     * it only open a dot on the wall of a room.
     * @param previousRoom The room adjacent to the start of the hallway.
     */
    private Hallway generateHallway(Room previousRoom) {
        //Randomly choose one side of the room to grow the hallway
        int direction = RANDOM.nextInt(4);
        Position start = getHallwayStart(previousRoom, direction);

        Position middle = null;
        int LshapeDirection = -1;   //The direction
        Position end = null;

        int length = 2 + RANDOM.nextInt(MAX_HALLWAY_LENGTH);
        int middleLength = 0;
       // System.out.println("Hallway length: " + length);

        int isLShape;
        /* If length of hallway is smaller than 5, L shape hallway
           could not be drawn.
         */
        if (length < 5) {
            isLShape = 0;
        } else {
            isLShape = RANDOM.nextInt(2);
        }

        if (isLShape == 1) {
            //generate middle position and L shape.
            middleLength = RANDOM.nextInt(length);
            /* The two arms of L shape should be at least length 1 */
            if (middleLength < 3 || middleLength > (length - 2)) {
                middle = null;
            } else {
                middle = getHallwayEnd(start, direction, middleLength);
            }
            if (middle == null) {
                isLShape = 0;
            } else {
            /* The Lshape direction could be only two possibility according to direction.
             * 0 -> 3 or 1
             * 1 -> 2 or 0
             * 2 -> 1 or 3
             * 3 -> 0 or 2
             * */
                int rand = RANDOM.nextInt(1);
                if (rand == 0) {
                    rand = -1;
                }
                LshapeDirection = (4 + direction + rand) % 4;

                end = getHallwayEnd(middle, LshapeDirection, (length - middleLength));
            }

        }

        if (0 == isLShape) {
            end = getHallwayEnd(start, direction, length);
        }

        if (end == null) return null;

        Hallway hallway = new Hallway(start, direction, length, middle, end);
        if (middle != null) {
            hallway.setlShapeDirection(LshapeDirection);
            hallway.setMiddleLength(middleLength);
        }

        /* If the generated hallway has overlap with existing room and hallway,
            it will be discarded.
         */
        if (hasOverlap(hallway)) {
//            System.out.println("hallway " + hallway.toString() +
//                    " overlapped, discarded. ");
            return null;
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
            yCoord--;
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
            yCoord++;
        }

        return new Position(xCoord, yCoord);
    }

    /* Given the hallway object which denoting the middle line,
     * draw the three lines of hallway.
     */
    private void drawDirectHallway(Hallway hallway) {
        Position floorStart = hallway.getStart();
        Position floorEnd = hallway.getEnd();

        int direction = hallway.getDirection();
        drawLine(floorStart, floorEnd, Tileset.FLOOR);

        Position start = minusPosition(floorStart, direction);
        Position end = minusPosition(floorEnd, direction);
        drawLine(start, end, Tileset.WALL);

        start = plusPosition(floorStart, direction);
        end = plusPosition(floorEnd, direction);
        drawLine(start, end, Tileset.WALL);
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

    private void drawLShapeHallway(Hallway hallway) {
        Room room = roomOfSizeOne(hallway.getMiddle());
        drawRoom(room, null);
        Hallway hallway1 = new Hallway(hallway.getStart(), hallway.getDirection(),
                hallway.getMiddleLength(), null, hallway.getMiddle());
        drawDirectHallway(hallway1);

        Hallway hallway2 = new Hallway(hallway.getMiddle(), hallway.getlShapeDirection(),
                hallway.getLength() - hallway.getMiddleLength(), null,
                hallway.getEnd());
        drawDirectHallway(hallway2);
        drawLShapeLine(hallway.getStart(), hallway.getMiddle(), hallway.getEnd(), Tileset.FLOOR);

    }

    private Room roomOfSizeOne(Position middle) {
        Position leftBottom = new Position(middle.getX() - 1, middle.getY() - 1);
        Room room = new Room(leftBottom, 1, 1);
        return room;
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

        //testGenerateMiddle();
        testLShapeHallwayDrawing();

        return world;
    }

    /**
     * Generate room and hallways alternatively.  Room and hallway are connected.
     * @return world where room and hallway are set.
     */
    private TETile[][] generateWorld() {
        int numberOfRooms = 1 + RANDOM.nextInt(MAX);
        /* To avoid the case no valid room or hallway could be generated. If the failed
           time exceed max_failed_times, generate a new start.
        */
        boolean restart = false;

        //The start position of the first room
        int startX = RANDOM.nextInt(Game.WIDTH);
        int startY = RANDOM.nextInt(Game.HEIGHT);
        Position start = new Position(startX, startY);

        Room room = null;
        Hallway hallway = null;

        //Draw room whose leftBottom corner is at position start
        while (room == null) {
            int width = 1 + RANDOM.nextInt(MAX_ROOM_SIZE);
            int height = 1 + RANDOM.nextInt(MAX_ROOM_SIZE);
            room = generateRoom(start, width, height);
        }

        System.out.println(room.toString());
        drawRoom(room, null);
        roomList.add(room);

        int roomIndex = 1;
        while (roomIndex < numberOfRooms) {
            //times failed to generate a non overlapping room or hallway
            int failed = -1;
            /* Choose a room from existing rooms and generate a hallway */
            do {
                failed++;
                if (failed > numberOfRooms) {
                    return this.world;
                }
                room = chooseARoomAsStart();
                hallway = generateHallway(room);
            } while (hallway == null);

            drawHallway(hallway);
            System.out.println(hallway.toString());


            /* Draw a room adjacent to the end of the hallway */
            /* The times failed to generate a room in boundary and with no overlap. */
            failed = 0;
            //Draw room whose leftBottom corner is at position start
            do {
                int width = 1 + RANDOM.nextInt(MAX_ROOM_SIZE);
                int height = 1 + RANDOM.nextInt(MAX_ROOM_SIZE);

                //Generate the start position of next room linked to current hallway
                int direction = (hallway.getMiddle() == null) ? hallway.getDirection() : hallway.getlShapeDirection();
                start = getNextRoomStart(hallway.getEnd(), direction, width, height);
                room = generateRoom(start, width, height);

                /* If the generated room is overlapped with existing room or hallway,
                    it will be discarded.
                */
                if (room == null) {
                    failed++;
                } else if (hasOverlap(room, hallway.getEnd(), direction)) {
//                    System.out.println("The generated room " + room.toString() +
//                            " is overlapped, discarded.");
                    room = null;
                    failed++;
                }

                /* To avoid the case no valid room could be generated. If the failed
                   time exceed max_failed_times, generate a new start.
                 */
                if (failed > MAX_FAILED_TIMES) {
                    break;
                }
            } while (room == null);

            if (room != null) {
                System.out.println(room.toString());
                drawRoom(room, hallway.getEnd());
                roomList.add(room);
                roomIndex++;
                System.out.println("roomIndex: " + roomIndex);
            } else {
                closeHallwayEnd(hallway.getEnd());
            }


        }

        return this.world;
    }

    /* Randomly choose a room as new start to generate new hallway.*/
    private Room chooseARoomAsStart() {
        int currentRoomNum = this.roomList.size();
        int randomPickIndex = RANDOM.nextInt(currentRoomNum);
        System.out.println("Start room is " + randomPickIndex);
        return roomList.get(randomPickIndex);
    }

    /* Seal the end of the hallway by setting the end position to Wall.*/
    private void closeHallwayEnd(Position end) {
        world[end.getX()][end.getY()] = Tileset.WALL;
    }

    /**
     * Draw a hallway according to direct or L shape.
     * @param hallway The hallway to be drawn.
     */
    private void drawHallway(Hallway hallway) {
        if (hallway.getMiddle() == null) {
            drawDirectHallway(hallway);
        } else {
            drawLShapeHallway(hallway);
        }
    }

    /**
     * Draw a room according to the leftBottom position and
     * width and height of the room.
     * @param room the room to drawn
     * @param connectPosition the position a room is connected to previous hallway.
     *                        If there is no, set the parameter to null
     */
    private void drawRoom(Room room, Position connectPosition) {
        Position leftBottom = room.getLeftBottom();
        int width = room.getWidth();
        int height = room.getHeight();

        Position rightBottom = new Position((leftBottom.getX() + width + 1),
                leftBottom.getY());
        Position leftUp = new Position(leftBottom.getX(),
                (leftBottom.getY() + height + 1));
        Position rightUp = new Position((leftBottom.getX() + width + 1),
                (leftBottom.getY() + height + 1));

        /* Draw bottom wall */
        drawLine(leftBottom, rightBottom, Tileset.WALL);

        /* Draw left wall */
        drawLine(leftBottom, leftUp, Tileset.WALL);

        /* Draw right wall */
        drawLine(rightBottom, rightUp, Tileset.WALL);

        /* Draw the top wall */
        drawLine(leftUp, rightUp, Tileset.WALL);

        fillFloor(new Position((leftBottom.getX() + 1), (leftBottom.getY() + 1)), width, height, Tileset.FLOOR);

        //Reset the connect position to floor
        if (connectPosition != null) {
            world[connectPosition.getX()][connectPosition.getY()] = Tileset.FLOOR;
        }
    }

    /**
     * According to the end of the middle line of previousHallway, generate the
     * start of current room.
     * @param hallwayEnd    the end position of the middle line of previous hallway
     * @param direction the direction of the hallway
     * @param width the width of the floor area of the room to be generated
     * @param height the height of the floor area of the room to be generated
     * @return the left bottom corner of the room
     */
    private Position getNextRoomStart(Position hallwayEnd, int direction, int width, int height) {
        int xCoord, yCoord;

        switch (direction) {
            case 0:
                xCoord = hallwayEnd.getX() - 1 - RANDOM.nextInt(width);
                yCoord = hallwayEnd.getY() - height - 1;
                break;
            case 1:
                xCoord = hallwayEnd.getX();
                yCoord = hallwayEnd.getY() - 1 - RANDOM.nextInt(height);
                break;
            case 2:
                xCoord = hallwayEnd.getX() - 1 - RANDOM.nextInt(width);
                yCoord = hallwayEnd.getY();
                break;
            default:
                xCoord = hallwayEnd.getX() - width - 1;
                yCoord = hallwayEnd.getY() - 1 - RANDOM.nextInt(height);

        }
        return new Position(xCoord, yCoord);
    }

    private void testLShapeHallwayDrawing() {
        Position start = new Position(20, 10);
        Position middle = new Position(22, 10);
        Position end = new Position(22, 12);
        int direction = 1;
        int length = 6;

        Hallway hallway = new Hallway(start, direction, length, middle, end);
        hallway.setlShapeDirection(2);
        hallway.setMiddleLength(3);
    //    drawHallway(hallway);

        start = new Position(20, 10);
        middle = new Position(22, 10);
        end = new Position(22, 8);
        direction = 1;
        length = 6;

        hallway = new Hallway(start, direction, length, middle, end);
        hallway.setlShapeDirection(0);
        hallway.setMiddleLength(3);
  //      drawHallway(hallway);

        start = new Position(20, 10);
        middle = new Position(20, 8);
        end = new Position(22, 8);
        direction = 0;
        length = 6;

        hallway = new Hallway(start, direction, length, middle, end);
        hallway.setlShapeDirection(1);
        hallway.setMiddleLength(3);
//        drawHallway(hallway);

        start = new Position(20, 10);
        middle = new Position(20, 12);
        end = new Position(18, 12);
        direction = 2;
        length = 6;

        hallway = new Hallway(start, direction, length, middle, end);
        hallway.setlShapeDirection(3);
        hallway.setMiddleLength(3);
  //      drawHallway(hallway);
    }

    public static void main(String[] args) {
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(Game.WIDTH, Game.HEIGHT);

        long seed = System.currentTimeMillis();
        TETile[][] worldFrame = new TETile[Game.WIDTH][Game.HEIGHT];
        Game.initializeTheWorld(worldFrame);
        WorldGenerator worldGenerator = new WorldGenerator(seed, worldFrame);
        //worldFrame = worldGenerator.generateWorldTest();

        worldFrame = worldGenerator.generateWorld();

        teRenderer.renderFrame(worldFrame);

    }



}
