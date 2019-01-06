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
    private long seed;
    private Random RANDOM;
    private final int MAX = 50; //Maximum number of rooms or hallways.
    private final int MAX_ROOM_SIZE = 10;
    private final int MAX_HALLWAY_LENGTH = 10;

    private TETile[][] world;
    private List<Room> roomList;
    private Position doorPosition;


    public WorldGenerator(long seed, TETile[][] world) {
        this.seed = seed;
        RANDOM = new Random(seed);
        this.world = world;
        roomList = new ArrayList<>();
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
     * One try to generate a hallway with random start position and end position.
     * If start and end are not in the same row or column, the hallway
     * should turn. The minimal length of a hallway is 2. If the length is 1,
     * it only open a dot on the wall of a room.
     * @param previousRoom The room adjacent to the start of the hallway.
     * @return a hallway or null if the hallway generated is out of boundary or
     *         overlapped with others.
     */
    private Hallway generateHallwayTrial(Room previousRoom,
                                         OverlapDetector overlapDetector) {
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
        if (overlapDetector.hasOverlap(hallway)) {
//            System.out.println("hallway " + hallway.toString() +
//                    " overlapped, discarded. ");
            return null;
        }
        return hallway;
    }


    public TETile[][] generateWorldTest() {
        RoomGenerator roomGenerator = new RoomGenerator(RANDOM, MAX_ROOM_SIZE);
        //Position start = new Position(Game.WIDTH, Game.HEIGHT);
        Position start = new Position(70, 27);
        int width = 16;
        int height = 5;
        //Position end = new Position(10, 1);
        TETile tile = Tileset.WALL;
       // drawLine(start, end, tile);
       // fillFloor(start, width, height, Tileset.FLOOR);
        Room room = roomGenerator.generateARoom(start, width, height);

        //testGenerateMiddle();
        testLShapeHallwayDrawing();

        return world;
    }

    /**
     * Generate room and hallways alternatively.  Room and hallway are connected.
     * @return world where room and hallway are set.
     */
    private TETile[][] generateWorld() {
        Drawer drawer = new Drawer(this.world, RANDOM);
        RoomGenerator roomGenerator = new RoomGenerator(RANDOM, MAX_ROOM_SIZE);
        OverlapDetector overlapDetector = new OverlapDetector(this.world);

        Room room = null;
        Hallway hallway = null;

        int numberOfRooms = 1 + RANDOM.nextInt(MAX);
        /* To avoid the case no valid room or hallway could be generated. If the failed
           time exceed max_failed_times, generate a new start.
        */
        System.out.println("The number of room generated: " + numberOfRooms);
        boolean restart = false;

        /* Generate the first room */
        room = roomGenerator.generateFirstRoom();
        //System.out.println(room.toString());
        drawer.drawRoom(room, null);
        roomList.add(room);

        int roomIndex = 1;
        while (roomIndex < numberOfRooms) {
            hallway = generateHallway(overlapDetector);
            /* If no hallway not overlapping with others could not be
               generated, stop.
             */
            if (hallway == null) {
                break;
            } else {
                drawer.drawHallway(hallway);
            }

            room = roomGenerator.generateRoom(hallway, overlapDetector);

            if (room != null) {
                //System.out.println(room.toString());
                drawer.drawRoom(room, hallway.getEnd());
                roomList.add(room);
                roomIndex++;
            } else {
                /* If no room not overlapping with others could not be
                   generated, stop generating room. Go back to generate
                   another hallway starting from another room.
                 */
                closeHallwayEnd(hallway.getEnd());
            }
        }

        setDoor();

        return this.world;
    }

    /**
     * Generate a hallway starting from a random chosen room from
     * the existing room list.
     * @return the hallway generated or null if no hallway not
     *         overlapping with others could not be generated with MAX trials.
     */
    private Hallway generateHallway(OverlapDetector overlapDetector) {
        Hallway hallway;

        //times failed to generate a non overlapping room or hallway
        int failed = -1;
        /* Choose a room from existing rooms and generate a hallway */
        do {
            failed++;
            if (failed > MAX) {
                return null;
            }
            Room room = chooseARoomAsStart();
            hallway = generateHallwayTrial(room, overlapDetector);
        } while (hallway == null);
    //    System.out.println(hallway.toString());

        return hallway;
    }



    private void setDoor() {
        Room room = chooseARoomAsStart();
        int direction = RANDOM.nextInt(4);
        int xCoord, yCoord;
        switch (direction) {
            case 0:
                xCoord = room.getMinXCoord() + 1 + RANDOM.nextInt(room.getWidth());
                yCoord = room.getMinYCoord();
                break;
            case 1:
                xCoord = room.getMaxXCoord();
                yCoord = room.getMinYCoord() + 1 + RANDOM.nextInt(room.getHeight());
                break;
            case 2:
                xCoord = room.getMinXCoord() + 1 + RANDOM.nextInt(room.getWidth());
                yCoord = room.getMaxYCoord();
                break;
            default:
                xCoord = room.getMinXCoord();
                yCoord = room.getMinYCoord() + 1 + RANDOM.nextInt(room.getHeight());
        }
        
        this.doorPosition = new Position(xCoord, yCoord);

        System.out.println("The door position is " + this.doorPosition.toString());
        this.world[xCoord][yCoord] = Tileset.LOCKED_DOOR;
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
