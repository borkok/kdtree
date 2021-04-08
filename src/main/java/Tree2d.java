import edu.princeton.cs.algs4.Point2D;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
unbalanced binary tree
based on BTree.java
 */
class Tree2d implements Iterable<Point2D> {
    private Tree2dNode root;
    private int size;

    boolean isEmpty() {
        return size == 0;
    }

    int size() {
        return size;
    }

    void add(Point2D p) {
        if (isEmpty()) {
            root = Tree2dNode.with(p);
        }
        size++;
    }

    boolean contains(Point2D p) {
        return root.contains(p);
    }

    public Iterator<Point2D> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return List.of(root.point2d()).iterator();
    }
}
