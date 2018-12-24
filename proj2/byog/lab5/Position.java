package byog.lab5;


//import javafx.geometry.Pos;

public class Position implements Comparable<Position>  {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        Position p = (Position) o;
        return ((x == p.x) && (y == p.y));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return new String("(" +x + ", " + y + ")");
    }

    @Override
    public int compareTo(Position o) {
        if ((x == o.x) && (y == o.y)) {
            return 0;
        }
        if ((x < o.x) || (x == o.x) && (y < o.y)) {
            return -1;
        }
        //If x > o.x || (x == o.x) && (y > o.y) return 1;
        return 1;
    }
}
