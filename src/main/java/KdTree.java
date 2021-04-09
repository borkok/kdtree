import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class KdTree {
    private KdTreeNode root = KdTreeNode.empty();
    private int size = 0;

    boolean isEmpty() {
        return size == 0;
    }

    int size() {
        return size;
    }

    public void insert(Point2D point2D) {
        if (contains(point2D))  return;

        if (isEmpty())  root = KdTreeNode.with(point2D);
        else            root.append(point2D);

        size++;
    }

    public boolean contains(Point2D p) {
        return root.meOrDecendantEquals(p);
    }

    // draw all points to standard draw
    public void draw() {
        root.drawSubtree(new KdTreeCanvas());
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        LinkedList<Point2D> points = new LinkedList<>();
        forEach(p -> {
            if (rect.contains(p)) points.addLast(p);
        });
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        double minDistance = 1;
        Point2D champion = null;
        while (iterator().hasNext()) {
            Point2D point = iterator().next();
            double distanceSquaredTo = point.distanceSquaredTo(p);
            if (distanceSquaredTo < minDistance) {
                minDistance = distanceSquaredTo;
                champion = point;
            }
        }
        return champion;
    }

    private void forEach(Consumer<Point2D> consumer) {
        iterator().forEachRemaining(consumer);
    }

    public Iterator<Point2D> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return root.meAndAllChildren();
    }

    public static void main(String[] args) {
        KdTree kdtree = new KdTree();
        List.of(
                new Point2D(0.1d, 0.2d), new Point2D(0.2d, 0.1d),
                new Point2D(0.3d, 0.2d), new Point2D(0.4d, 0.1d))
            .forEach(kdtree::insert);

        kdtree.draw();
    }
}
