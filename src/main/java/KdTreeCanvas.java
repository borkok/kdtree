import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

public class KdTreeCanvas {
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
