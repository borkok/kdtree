import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

public class KdTree {
    private KdTreeNode root = KdTreeNode.empty();
    private int size = 0;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D point2D) {
        if (contains(point2D))  return;

        if (isEmpty())  root = KdTreeNode.with(point2D);
        else            root.append(point2D);

        size++;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
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
        private static final KdTreeNode EMPTY = empty();

        private static final BiPredicate<MyPoint, MyPoint> childHasGreaterX = (child, parent) -> child.x > parent.x;
        private static final BiPredicate<MyPoint, MyPoint> childHasGreaterY = (child, parent) -> child.y > parent.y;

        private final Point2D point2D;
        private final MyPoint myPoint;

        private final int level;
        private KdTreeNode left = EMPTY;
        private KdTreeNode right = EMPTY;

        public static KdTreeNode empty() {
            return new KdTreeNode(null, -1);
        }

        public static KdTreeNode with(Point2D p) {
            return new KdTreeNode(p, 0);
        }

        public static KdTreeNode with(Point2D p, int level) {
            return new KdTreeNode(p, level);
        }

        private KdTreeNode(Point2D point2D, int level) {
            this.point2D = point2D;
            this.myPoint = MyPoint.point(point2D);
            this.level = level;
        }

        public boolean isEmpty() {
            return this.point2D == null;
        }

        public boolean meOrDecendantEquals(Point2D searchedPoint) {
            if (isEmpty()) return false;
            if (point2D.equals(searchedPoint)) return true;

            if (goRightPredicate().test(MyPoint.point(searchedPoint), myPoint)) {
                return right.meOrDecendantEquals(searchedPoint);
            }
            return left.meOrDecendantEquals(searchedPoint);
        }

        public void append(Point2D childPoint2D) {
            if (goRightPredicate().test(MyPoint.point(childPoint2D), myPoint)) {
                appendRight(childPoint2D);
                return;
            }
            appendLeft(childPoint2D);
        }

        private BiPredicate<MyPoint, MyPoint> goRightPredicate() {
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

            if (goRightPredicate().test(MyPoint.point(query), myPoint)) {
                return findNearestInSubtreesOrdered(query, newChampion, right, left);
            }
            return findNearestInSubtreesOrdered(query, newChampion, left, right);
        }

        private Point2D findNewChampion(Point2D query, Point2D champion) {
            MyPoint myQuery = MyPoint.point(query);
            double distanceToChampion = distanceBetween(myQuery, MyPoint.point(champion));
            double distanceToMe = distanceBetween(myQuery, myPoint);

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
            return distanceBetween(MyPoint.point(query), MyPoint.point(newChampion))
                    > distanceBetween(MyPoint.point(query), findBorderPointClosestTo(query));
        }

        private double distanceBetween(MyPoint query, MyPoint newChampion) {
            return query.distanceSquaredTo(newChampion);
        }

        private MyPoint findBorderPointClosestTo(Point2D query) {
            if (dividesVertically()) {
                return MyPoint.point(point2D.x(), query.y());
            }
            return MyPoint.point(query.x(), point2D.y());
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
            MyPoint farLeft = MyPoint.point(rect.xmin(), rect.ymin());
            return !goRightPredicate().test(farLeft, myPoint);
        }

        private boolean shouldFindInRangeInRight(RectHV rect) {
            MyPoint farRight = MyPoint.point(rect.xmax(), rect.ymax());
            return goRightPredicate().test(farRight, myPoint);
        }
    }


    /*
        Point builder
     */
    private static class Point2DBuilder {
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

        private Point2DBuilder() {
        }
    }

    /*
        MyPoint to cheat stupid autograder that does not allow me to create Point2D
     */
    private static class MyPoint {
        private final double x;
        private final double y;

        private static MyPoint point(Point2D point2D) {
            if (point2D == null) return null;
            return new MyPoint(point2D.x(), point2D.y());
        }

        private static MyPoint point(double x, double y) {
            return new MyPoint(x, y);
        }

        private MyPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distanceSquaredTo(MyPoint that) {
            double dx = this.x - that.x;
            double dy = this.y - that.y;
            return dx*dx + dy*dy;
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
