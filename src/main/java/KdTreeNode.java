import edu.princeton.cs.algs4.Point2D;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

class KdTreeNode {
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
            return Point2DBuilder.of(point2D).y(query.y()).build();
        }
        return Point2DBuilder.of(point2D).x(query.x()).build();
    }

}
