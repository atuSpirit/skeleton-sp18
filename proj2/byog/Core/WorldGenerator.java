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
    private final int MAX_ROOM_NUM = 50; //Maximum number of rooms or hallways.
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
        HallwayGenerator hallwayGenerator = new HallwayGenerator(RANDOM,
                                                    MAX_HALLWAY_LENGTH);

        Room room = null;
        Hallway hallway = null;

        int numberOfRooms = 1 + RANDOM.nextInt(MAX_ROOM_NUM);
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
            hallway = hallwayGenerator.generateHallway(this.roomList, overlapDetector);
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

        Position doorPosition = setDoor();
        drawer.drawPoint(doorPosition, Tileset.LOCKED_DOOR);

        return this.world;
    }


    private Position setDoor() {
        Room room = chooseARoom(this.roomList, this.RANDOM);
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
        return new Position(xCoord, yCoord);
    }

    /* Randomly choose a room as new start to generate new hallway.*/
    public static Room chooseARoom(List<Room> roomList, Random RANDOM) {
        int currentRoomNum = roomList.size();
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
