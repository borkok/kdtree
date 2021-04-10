import edu.princeton.cs.algs4.Point2D;

class Point2DBuilder {
    private double x;
    private double y;

    static Point2DBuilder of(Point2D point2D) {
        return new Point2DBuilder(point2D);
    }

    private Point2DBuilder(Point2D point2D) {
        x = point2D.x();
        y = point2D.y();
    }

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

    Point2DBuilder x(double newX) {
        x = newX;
        return this;
    }

    Point2DBuilder y(double newY) {
        y = newY;
        return this;
    }

    Point2D build() {
        return new Point2D(x, y);
    }
}
