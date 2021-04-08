import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;

public class KdTree {
    private final Tree2d tree2d;

    // construct an empty set of points
    public KdTree() {
        tree2d = new Tree2d();
    }

    public boolean isEmpty() {
        return tree2d.isEmpty();
    }

    public int size() {
        return tree2d.size();
    }

    public void insert(Point2D p) {
        tree2d.add(p);
    }

    public boolean contains(Point2D p) {
        return tree2d.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        tree2d.forEach(Point2D::draw);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        LinkedList<Point2D> points = new LinkedList<>();
        tree2d.forEach(p -> {
            if (rect.contains(p)) points.addLast(p);
        });
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        double minDistance = 1;
        Point2D champion = null;
        for (Point2D point : tree2d) {
            double distanceSquaredTo = point.distanceSquaredTo(p);
            if (distanceSquaredTo < minDistance) {
                minDistance = distanceSquaredTo;
                champion = point;
            }
        }
        return champion;
    }
}
