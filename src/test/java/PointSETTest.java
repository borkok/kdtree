import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointSETTest {
    private static final RectHV TOTAL_AREA = new RectHV(0, 0, 1, 1);
    private PointSET testee;

    @BeforeEach
    void setUp() {
        testee = new PointSET();
    }

    @Test
    public void empty_point_set() {
        assertThat(testee.isEmpty()).isTrue();
        assertThat(testee.size()).isEqualTo(0);
        assertThat(testee.nearest(new Point2D(0.1d, 0.2d))).isNull();
        assertThat(testee.range(TOTAL_AREA)).isEmpty();
    }

    @Test
    public void one_element_point_set() {
        testee.insert(new Point2D(0.1d, 0.2d));

        assertThat(testee.isEmpty()).isFalse();
        assertThat(testee.size()).isEqualTo(1);
        assertThat(testee.contains(new Point2D(0.1d, 0.2d))).isTrue();
        assertThat(testee.nearest(new Point2D(0.3d, 0.6d))).isEqualByComparingTo(new Point2D(0.1d, 0.2d));
        assertThat(testee.range(TOTAL_AREA)).hasSize(1).containsOnly(new Point2D(0.1d, 0.2d));
        assertThat(testee.range(new RectHV(0.3, 0.3, 0.5, 0.6))).isEmpty();
    }

    @Test
    public void autograder_case() {
        testee.insert(point(0.0, 1.0));
        testee.insert(point(0.0, 1.0));
        assertThat(testee.range(new RectHV(0.0, 0.0, 1.0, 1.0)))
                .containsOnly(point(0.0, 1.0));
        assertThat(testee.contains(point(1.0, 1.0))).isFalse();
        assertThat(testee.nearest(point(0.0, 0.0)))
                .isEqualByComparingTo(point(0.0, 1.0));
    }

    private static Point2D point(double x, double y) {
        return new Point2D(x, y);
    }
}