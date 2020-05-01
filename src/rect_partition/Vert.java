package rect_partition;

import java.util.HashSet;
import java.util.Set;

public class Vert {
    private int x;
    private int y;
    private Set<Integer> rectangles = new HashSet<>();

    /**
     * Constructor for an empty vert
     * 
     * @param x
     * @param y
     */
    public Vert(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public Set<Integer> getRectangles() {
        return rectangles;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vert))
            return false;
        if (o == this)
            return true;

        Vert v = (Vert) o;
        return v.x == this.x && v.y == this.y;
    }

    @Override
    public int hashCode() {
        String xy = x + String.valueOf(y);
        return Integer.valueOf(xy);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}