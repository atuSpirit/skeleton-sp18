package byog.Core;

import byog.TileEngine.TETile;
import byog.lab5.Position;

/* The Room class.  The position of leftBottom.
 * The width and height are the width and height of the inner space,
 * which means the floor width and height.
 */
public class Room {
    private Position leftBottom;
    private int width;
    private int height;

    Room(Position leftBottom, int width, int height) {
        this.leftBottom = leftBottom;
        this.width = width;
        this.height = height;
    }

    Position getLeftBottom() {
        return leftBottom;
    }

    void setLeftBottom(Position leftBottom) {
        this.leftBottom = leftBottom;
    }

    int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    int getMaxXCoord() {
        return leftBottom.getX() + width + 1;
    }

    int getMinXCoord() {
        return leftBottom.getX();
    }

    int getMinYCoord() {
        return leftBottom.getY();
    }

    int getMaxYCoord() {
        return leftBottom.getY() + height + 1;
    }

    @Override
    public String toString() {
        String roomStr = String.format("Left corner is %s, width: %d, height: %d\n",
                leftBottom.toString(), width, height);
        roomStr += String.format("minX: %d, maxX: %d, minY: %d, maxY: %d",
                getMinXCoord(), getMaxXCoord(), getMinYCoord(), getMaxYCoord());
        return roomStr;
    }
}