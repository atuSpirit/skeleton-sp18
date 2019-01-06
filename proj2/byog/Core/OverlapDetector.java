package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

public class OverlapDetector {
    TETile[][] world;

    public OverlapDetector(TETile[][] world) {
        this.world = world;
    }

    /**
     * Detect whether the room is overlapped with any existing room or hallway.
     * The connected Position's overlap should not count.
     * @param room
     * @param connectPosition
     * @param direction
     * @return
     */
    boolean hasOverlap(Room room, Position connectPosition, int direction) {
        /* The overlap with hallway connector does not count as overlap */
        int minXCoord = room.getMinXCoord();
        int maxXCoord = room.getMaxXCoord();
        int minYCoord = room.getMinYCoord();
        int maxYCoord = room.getMaxYCoord();

        Position plusPosition = Hallway.plusPosition(connectPosition, direction);
        Position minusPosition = Hallway.minusPosition(connectPosition, direction);

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

    boolean hasOverlap(Hallway hallway) {
        if (hallway.getMiddle() == null) {
            return hasOverlapDirectHallway(hallway);
        } else {
            return hasOverlapDirectHallway(hallway.getFirstArm()) ||
                    hasOverlapDirectHallway(hallway.getSecondArm());
        }
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

        Position start = Hallway.minusPosition(floorStart, direction);
        Position end = Hallway.minusPosition(floorEnd, direction);
        overlapped = lineOverlap(start, end);
        if (overlapped == true) {
            return true;
        }

        start = Hallway.plusPosition(floorStart, direction);
        end = Hallway.plusPosition(floorEnd, direction);
        overlapped = lineOverlap(start, end);

        return overlapped;
    }


}
