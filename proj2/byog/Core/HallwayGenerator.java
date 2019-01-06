package byog.Core;

import byog.lab5.Position;

import java.util.List;
import java.util.Random;

public class HallwayGenerator {
    Random RANDOM;
    int maxHallwayLength;
    private final int MAX_FAILED_TIMES = 50;

    public HallwayGenerator(Random RANDOM, int maxHallwayLength) {
        this.RANDOM = RANDOM;
        this.maxHallwayLength = maxHallwayLength;
    }

    /**
     * Generate a hallway starting from a random chosen room from
     * the existing room list.
     * @return the hallway generated or null if no hallway not
     *         overlapping with others could not be generated with MAX trials.
     */
    Hallway generateHallway(List<Room> roomList, OverlapDetector overlapDetector) {
        Hallway hallway;

        //times failed to generate a non overlapping room or hallway
        int failed = -1;
        /* Choose a room from existing rooms and generate a hallway */
        do {
            failed++;
            if (failed > MAX_FAILED_TIMES) {
                return null;
            }
            Room room = WorldGenerator.chooseARoom(roomList, RANDOM);
            hallway = generateHallwayTrial(room, overlapDetector);
        } while (hallway == null);
        //    System.out.println(hallway.toString());

        return hallway;
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

        int length = 2 + RANDOM.nextInt(maxHallwayLength);
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
}
