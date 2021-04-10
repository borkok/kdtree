import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

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

    public Iterator<Point2D> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return root.meAndAllChildren();
    }

    public static void main(String[] args) {
        KdTree kdtree = new KdTree();
        List.of(new Point2D(0.372, 0.497)).forEach(kdtree::insert);
        kdtree.draw();
        new RectHV(0.3, 0.3, 0.5, 0.6).draw();
    }

    /*****************************************************
     * HELPER CLASSES
     **************************************/

    /*
     * Tree Node
     */
    private static class KdTreeNode {
        private static final KdTreeNode EMPTY = new KdTreeNode(null, -1);

        private static final BiPredicate<Point2D, Point2D> childHasGreaterX = (child, parent) -> child.x() > parent.x();
        private static final BiPredicate<Point2D, Point2D> childHasGreaterY = (child, parent) -> child.y() > parent.y();

        private final Point2D point2D;

        private final int level;
        private KdTreeNode left = EMPTY;
        private KdTreeNode right = EMPTY;

        public static KdTreeNode empty() {
            return EMPTY;
        }

        public static KdTreeNode with(Point2D p) {
            return new KdTreeNode(p, 0);
        }

        public static KdTreeNode with(Point2D p, int level) {
            return new KdTreeNode(p, level);
        }

        private KdTreeNode(Point2D point2D, int level) {
            this.point2D = point2D;
            this.level = level;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }

        public boolean meOrDecendantEquals(Point2D searchedPoint) {
            if (isEmpty()) return false;
            if (point2D.equals(searchedPoint)) return true;

            if (goRightPredicate().test(searchedPoint, point2D)) {
                return right.meOrDecendantEquals(searchedPoint);
            }
            return left.meOrDecendantEquals(searchedPoint);
        }

        public void append(Point2D childPoint2D) {
            if (goRightPredicate().test(childPoint2D, this.point2D)) {
                appendRight(childPoint2D);
                return;
            }
            appendLeft(childPoint2D);
        }

        private BiPredicate<Point2D, Point2D> goRightPredicate() {
            if (dividesVertically())    return childHasGreaterX;
            return childHasGreaterY;
        }

        private boolean dividesVertically() {
            return level % 2 == 0;
        }

        private void appendLeft(Point2D childPoint2D) {
            if (left.isEmpty())     left = KdTreeNode.with(childPoint2D, level + 1);
            else                    left.append(childPoint2D);
        }

        private void appendRight(Point2D childPoint2D) {
            if (right.isEmpty())    right = KdTreeNode.with(childPoint2D, level + 1);
            else                    right.append(childPoint2D);
        }

        public Iterator<Point2D> meAndAllChildren() {
            if (isEmpty()) {
                return Collections.emptyIterator();
            }
            return meAndAllChildrenList().iterator();
        }

        private List<Point2D> meAndAllChildrenList() {
            if (isEmpty()) {
                return Collections.emptyList();
            }

            LinkedList<Point2D> points = new LinkedList<>();
            points.addLast(point2D);
            points.addAll(left.meAndAllChildrenList());
            points.addAll(right.meAndAllChildrenList());
            return points;
        }

        public void drawSubtree(KdTreeCanvas kdTreeCanvas) {
            if (isEmpty())  return;

            drawMe(kdTreeCanvas);

            left.drawSubtree(kdTreeCanvas);
            right.drawSubtree(kdTreeCanvas);
        }

        private void drawMe(KdTreeCanvas kdTreeCanvas) {
            kdTreeCanvas.drawPoint(point2D);
            if (dividesVertically())    kdTreeCanvas.drawVerticalLine(point2D);
            else                        kdTreeCanvas.drawHorizontalLine(point2D);
        }

        public Point2D nearest(Point2D query) {
            return nearest(query, point2D);
        }

        private Point2D nearest(Point2D query, Point2D champion) {
            if (isEmpty()) return champion;

            Point2D newChampion = findNewChampion(query, champion);

            if (goRightPredicate().test(query, this.point2D)) {
                return findNearestInSubtreesOrdered(query, newChampion, right, left);
            }
            return findNearestInSubtreesOrdered(query, newChampion, left, right);
        }

        private Point2D findNewChampion(Point2D query, Point2D champion) {
            double distanceToChampion = distanceBetween(query, champion);
            double distanceToMe = distanceBetween(query, point2D);

            if (distanceToMe < distanceToChampion)    return point2D;
            return champion;
        }

        private Point2D findNearestInSubtreesOrdered(Point2D query, Point2D champion,
                                                     KdTreeNode first, KdTreeNode second) {
            Point2D newChampion = first.nearest(query, champion);
            if (shouldCheckOtherSubtree(query, newChampion)) {
                return second.nearest(query, newChampion);
            }
            return newChampion;
        }

        private boolean shouldCheckOtherSubtree(Point2D query, Point2D newChampion) {
            return distanceBetween(query, newChampion) > distanceBetween(query, findBorderPointClosestTo(query));
        }

        private double distanceBetween(Point2D query, Point2D newChampion) {
            return query.distanceSquaredTo(newChampion);
        }

        private Point2D findBorderPointClosestTo(Point2D query) {
            if (dividesVertically()) {
                return Point2DBuilder.init().x(point2D).y(query).build();
            }
            return Point2DBuilder.init().y(point2D).x(query).build();
        }

        public Iterable<Point2D> range(RectHV rect) {
            return findInRange(rect);
        }

        private List<Point2D> findInRange(RectHV rect) {
            if (isEmpty())  return Collections.emptyList();

            LinkedList<Point2D> points = new LinkedList<>();
            if (rect.contains(point2D)) points.add(point2D);
            if (shouldFindInRangeInLeft(rect))    points.addAll(left.findInRange(rect));
            if (shouldFindInRangeInRight(rect))   points.addAll(right.findInRange(rect));
            return points;
        }

        private boolean shouldFindInRangeInLeft(RectHV rect) {
            Point2D farLeft = new Point2D(rect.xmin(), rect.ymin());
            return !goRightPredicate().test(farLeft, point2D);
        }

        private boolean shouldFindInRangeInRight(RectHV rect) {
            Point2D farRight = new Point2D(rect.xmax(), rect.ymax());
            return goRightPredicate().test(farRight, point2D);
        }
    }


    /*
        Point builder
     */
    private static class Point2DBuilder {
        private double x;
        private double y;

        static Point2D above(Point2D point2D) {
            return new Point2D(point2D.x(), 0);
        }

        static Point2D below(Point2D point2D) {
            return new Point2D(point2D.x(), 1);
        }

        static Point2D left(Point2D point2D) {
            return new Point2D(0, point2D.y());
        }

        static Point2D right(Point2D point2D) {
            return new Point2D(1, point2D.y());
        }

        public static Point2DBuilder init() {
            return new Point2DBuilder();
        }

        private Point2DBuilder() {
        }

        Point2DBuilder x(Point2D point2D) {
            x = point2D.x();
            return this;
        }

        Point2DBuilder y(Point2D point2D) {
            y = point2D.y();
            return this;
        }

        Point2D build() {
            return new Point2D(x, y);
        }
    }


    /*
        Tree canvas and drawing logic
     */
    private static class KdTreeCanvas {
        public void drawPoint(Point2D point2D) {
            StdDraw.setPenRadius(0.015);
            StdDraw.setPenColor(StdDraw.BLACK);
            point2D.draw();
        }

        void drawHorizontalLine(Point2D point2D) {
            drawLine(
                    Point2DBuilder.left(point2D),
                    Point2DBuilder.right(point2D)
            );
        }

        void drawVerticalLine(Point2D point2D) {
            drawLine(
                    Point2DBuilder.above(point2D),
                    Point2DBuilder.below(point2D)
            );
        }

        private void drawLine(Point2D from, Point2D to) {
            StdDraw.setPenRadius(0.005);
            if (from.x() == to.x())     StdDraw.setPenColor(StdDraw.RED);
            else                        StdDraw.setPenColor(StdDraw.BLUE);
            from.drawTo(to);
        }
    }
}
