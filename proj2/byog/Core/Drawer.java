package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.Position;

import java.util.Random;

public class Drawer {
    TETile[][] world;
    Random RANDOM;

    public Drawer(TETile[][] world, Random RANDOM) {
        this.world = world;
        this.RANDOM = RANDOM;
    }

    /**
     * Draw a room according to the leftBottom position and
     * width and height of the room.
     * @param room the room to drawn
     * @param connectPosition the position a room is connected to previous hallway.
     *                        If there is no, set the parameter to null
     */
    void drawRoom(Room room, Position connectPosition) {
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
     * Draw a hallway according to direct or L shape.
     * @param hallway The hallway to be drawn.
     */
    void drawHallway(Hallway hallway) {
        if (hallway.getMiddle() == null) {
            drawDirectHallway(hallway);
        } else {
            drawLShapeHallway(hallway);
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
     * Given the hallway object which denoting the middle line,
     * draw the three lines of hallway.
     * @param hallway a direct hallway whose middle field should be null
     */
    private void drawDirectHallway(Hallway hallway) {
        Position floorStart = hallway.getStart();
        Position floorEnd = hallway.getEnd();

        int direction = hallway.getDirection();
        drawLine(floorStart, floorEnd, Tileset.FLOOR);

        Position start = Hallway.minusPosition(floorStart, direction);
        Position end = Hallway.minusPosition(floorEnd, direction);
        drawLine(start, end, Tileset.WALL);

        start = Hallway.plusPosition(floorStart, direction);
        end = Hallway.plusPosition(floorEnd, direction);
        drawLine(start, end, Tileset.WALL);
    }


    /**
     * Draw the L shape hallway.
     * @param hallway a L shape hallway to be drawn. The middle field
     *                of the hallway should not be null.
     */
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

    /* Generate a room of size one at given position.
       Used as a helper function to draw the corner of a
       L shape hallway.
     */
    private Room roomOfSizeOne(Position middle) {
        Position leftBottom = new Position(middle.getX() - 1, middle.getY() - 1);
        Room room = new Room(leftBottom, 1, 1);
        return room;
    }

    /* Draw a position with given TETile */
    void drawPoint(Position position, TETile tile) {
        this.world[position.getX()][position.getY()] = tile;
    }
}
