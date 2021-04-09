/*
 * Copyright (c) 2021. BEST S.A. and/or its affiliates. All rights reserved.
 */

import edu.princeton.cs.algs4.Point2D;

class KdTreeNode {
    private final Point2D point2D;
    private KdTreeNode previous;
    private KdTreeNode left;
    private KdTreeNode right;

    public static KdTreeNode with(Point2D p) {
        return new KdTreeNode(p);
    }

    private KdTreeNode(Point2D point2D) {
        this.point2D = point2D;
    }

    public boolean contains(Point2D p) {
        return point2D.equals(p);
    }

    public Point2D point2d() {
        return point2D;
    }
}
