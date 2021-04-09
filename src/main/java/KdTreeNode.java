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

        BiPredicate<Point2D, Point2D> isChildCoordGreater = findGoRightPredicate();
        if (isChildCoordGreater.test(searchedPoint, point2D)) {
            return right.meOrDecendantEquals(searchedPoint);
        }
        return left.meOrDecendantEquals(searchedPoint);
    }

    public void append(Point2D childPoint2D) {
        BiPredicate<Point2D, Point2D> goRight = findGoRightPredicate();

        if (goRight.test(childPoint2D, this.point2D)) {
            appendRight(childPoint2D);
            return;
        }
        appendLeft(childPoint2D);
    }

    private BiPredicate<Point2D, Point2D> findGoRightPredicate() {
        if (dividesVertically())    return childHasGreaterX;
        return childHasGreaterY;
    }

    private boolean dividesVertically() {
        return level % 2 == 0;
    }

    private void appendLeft(Point2D childPoint2D) {
        KdTreeNode node = createChildKdTreeNode(childPoint2D);
        if (left.isEmpty())     left = node;
        else                    left.append(childPoint2D);
    }

    private void appendRight(Point2D childPoint2D) {
        KdTreeNode node = createChildKdTreeNode(childPoint2D);
        if (right.isEmpty())    right = node;
        else                    right.append(childPoint2D);
    }

    private KdTreeNode createChildKdTreeNode(Point2D childPoint2D) {
        return KdTreeNode.with(childPoint2D, level + 1);
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
}
