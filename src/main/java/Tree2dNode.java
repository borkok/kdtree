/*
 * Copyright (c) 2021. BEST S.A. and/or its affiliates. All rights reserved.
 */

import edu.princeton.cs.algs4.Point2D;

class Tree2dNode {
    private final Point2D point2D;
    private Tree2dNode previous;
    private Tree2dNode left;
    private Tree2dNode right;

    public static Tree2dNode with(Point2D p) {
        return new Tree2dNode(p);
    }

    private Tree2dNode(Point2D point2D) {
        this.point2D = point2D;
    }

    public boolean contains(Point2D p) {
        return point2D.equals(p);
    }

    public Point2D point2d() {
        return point2D;
    }
}
