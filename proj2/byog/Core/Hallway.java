package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.io.StringBufferInputStream;

/* The class denoting hallway. A hallway contains three lines:
   two walls and one floor line between the two walls. The
   start, middle and end position are all denoting the middle line,
   which is the floor line.
 */
public class Hallway {
    private Position start;
    private int direction;
    private int length;
    private Position middle;
    private int middleLength;
    private Position end;
    private int lShapeDirection;

    Hallway(Position start, int direction, int length, Position middle, Position end) {
        this.start = start;
        this.direction = direction;
        this.length = length;
        this.middle = middle;
        this.end = end;
        lShapeDirection = -1;
    }

    Position getStart() {
        return start;
    }

    void setStart(Position start) {
        this.start = start;
    }

    int getDirection() {
        return direction;
    }

    void setDirection(int direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    Position getMiddle() {
        return middle;
    }

    void setMiddle(Position middle) {
        this.middle = middle;
    }


    public int getMiddleLength() {
        return middleLength;
    }

    public void setMiddleLength(int middleLength) {
        this.middleLength = middleLength;
    }


    Position getEnd() {
        return end;
    }

    void setEnd(Position end) {
        this.end = end;
    }


    int getlShapeDirection() {
        return lShapeDirection;
    }

    void setlShapeDirection(int lShapeDirection) {
        this.lShapeDirection = lShapeDirection;
    }

    Hallway getFirstArm() {
        Hallway firstArm = new Hallway(start, direction, middleLength, null, middle);
        return firstArm;
    }

    Hallway getSecondArm() {
        Hallway secondArm = new Hallway(middle, lShapeDirection,
                length - middleLength, null, end);
        return secondArm;
    }

    /* Helper function to increment hallway position by one line.
     * The xCoord and yCoord will
     * increase according to the direction of the hallway */
    static Position plusPosition(Position position, int direction) {
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
    static Position minusPosition(Position position, int direction) {
        int xCoord = position.getX();
        int yCoord = position.getY();

        if ((direction == 0) || (direction == 2)) {
            xCoord--;
        } else {
            yCoord++;
        }

        return new Position(xCoord, yCoord);
    }

    @Override
    public String toString() {
        String hallway = null;
        if (middle == null) {
            hallway = String.format("Start: %s, End: %s, Direction: %d, Length: %d\n", start.toString(),
                    end.toString(), direction, length);
        } else {
            hallway = String.format("Start: %s, Middle: %s, End: %s, Direction: %d, Length: %d, " +
                            "Ldirection: %d\n", start.toString(), middle.toString(),
                    end.toString(), direction, length, lShapeDirection);
        }
        return hallway;
    }


}
