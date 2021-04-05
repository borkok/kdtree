import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.LinkedList;

//Throw an IllegalArgumentException if any argument is null
public class PointSET {
    private final SET<Point2D> point2DSet;

    // construct an empty set of points
    public PointSET() {
        point2DSet = new SET<>();
    }

    public boolean isEmpty() {
        return point2DSet.isEmpty();
    }

    public int size() {
        return point2DSet.size();
    }

    public void insert(Point2D p) {
        point2DSet.add(p);
    }

    public boolean contains(Point2D p) {
        return point2DSet.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        point2DSet.iterator().forEachRemaining(Point2D::draw);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        LinkedList<Point2D> points = new LinkedList<>();
        point2DSet.iterator().forEachRemaining(p -> {
            if (rect.contains(p)) points.addLast(p);
        });
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        double minDistance = 1;
        Point2D champion = null;
        for (Point2D point : point2DSet) {
            double distanceSquaredTo = point.distanceSquaredTo(p);
            if (distanceSquaredTo < minDistance) {
                minDistance = distanceSquaredTo;
                champion = point;
            }
        }
        return champion;
    }
}
