package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

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


}
