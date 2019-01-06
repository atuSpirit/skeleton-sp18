package byog.Core;

import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.util.Random;

/* Generate room with random width and height given a previous hallway.
 * The room should be overlapped with others */
public class RoomGenerator {
    Random RANDOM;
    int maxRoomSize;

    /* The maximum allowed times to failed generate a room. If exceed this number,
     * a new start will be used. */
    private final int MAX_FAILED_TIMES = 10;

    public RoomGenerator(Random RANDOM, int maxRoomSize) {
        this.RANDOM = RANDOM;
        this.maxRoomSize = maxRoomSize;
    }

    /**
     * Generate a room connected to a given hallway.
     * @param hallway The hallway before the room
     * @return the room or null if no room not overlapping with
     *         others could be generated with no more than
     *         MAX_FAILED_TIMES trials.
     */
    Room generateRoom(Hallway hallway, OverlapDetector overlapDetector) {
        Room room;
        /* Draw a room adjacent to the end of the hallway */
        /* The times failed to generate a room in boundary and with no overlap. */
        int failed = 0;
        //Draw room whose leftBottom corner is at position start
        do {
            int width = 1 + RANDOM.nextInt(maxRoomSize);
            int height = 1 + RANDOM.nextInt(maxRoomSize);

            room = generateRoomTrial(hallway, width, height, overlapDetector);

            if (room == null) {
                failed++;
                /* To avoid the case no valid room could be generated. If the failed
                   time exceed max_failed_times, generate a new start.
                 */
                if (failed > MAX_FAILED_TIMES) {
                    break;
                }
            }
        } while (room == null);

        return room;
    }

    Room generateFirstRoom() {
        Room room = null;
        while (room == null) {
            //The start position of the first room
            int startX = RANDOM.nextInt(Game.WIDTH);
            int startY = RANDOM.nextInt(Game.HEIGHT);
            Position start = new Position(startX, startY);
            int width = 1 + RANDOM.nextInt(maxRoomSize);
            int height = 1 + RANDOM.nextInt(maxRoomSize);
            room = generateARoom(start, width, height);
        }
        return room;
    }

    Room generateARoom(Position start, int width, int height) {
        /* If the room is out of the boundary of canvas, fit it in */
        if ((start.getX() < 0) || (start.getY() < 0) ||
                ((start.getX() + width + 1) >= Game.WIDTH) ||
                ((start.getY() + height + 1) >= Game.HEIGHT)) {
//            System.out.println("The room is out of boundary. skip");
            return null;
        }

        Room room = new Room(start, width, height);

        return room;
    }

    /**
     * One trial to generate a rectangle room starting from
     * start position with given width and height.
     * @param hallway The hallway previous to the room
     * @param width The width of the room
     * @param height The height of the room
     * @return room The room generated
     */
    private Room generateRoomTrial(Hallway hallway, int width, int height,
                                   OverlapDetector overlapDetector) {
        //Generate the start position of next room linked to current hallway
        int direction = (hallway.getMiddle() == null) ? hallway.getDirection() : hallway.getlShapeDirection();
        Position start = getNextRoomStart(hallway.getEnd(), direction, width, height);

        Room room = generateARoom(start, width, height);

        if (room != null && overlapDetector.hasOverlap(room, hallway.getEnd(), direction)) {
            //        System.out.println("Overlapped room: " + room.toString());
            room = null;
        }

        return room;
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

}
