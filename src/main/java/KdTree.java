import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Collections;
import java.util.Iterator;
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
        if (isEmpty())  return Collections.emptyList();
        return root.range(rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D query) {
        if (query == null) throw new IllegalArgumentException();
        if (isEmpty())  return null;
        if (contains(query)) return query;

        return root.nearest(query);
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
                point(0.372,0.497),
                point(0.564,0.413),
                point(0.226,0.577),
                point(0.144,0.179),
                point(0.083,0.510),
                point(0.320,0.708),
                point(0.417,0.362),
                point(0.862,0.825),
                point(0.785,0.725),
                point(0.499,0.208)
        ).forEach(kdtree::insert);

        kdtree.draw();
        new RectHV(0.3, 0.3, 0.5, 0.6).draw();
    }

    private static Point2D point(double x, double y) {
        return new Point2D(x, y);
    }
}
