import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class KdTreeTest {
    private static final RectHV TOTAL_AREA = new RectHV(0, 0, 1, 1);
    private KdTree testee;

    @BeforeEach
    void setUp() {
        testee = new KdTree();
    }

    @Test
    public void empty_point_set() {
        assertThat(testee.isEmpty()).isTrue();
        assertThat(testee.size()).isEqualTo(0);
        assertThat(testee.nearest(new Point2D(0.1d, 0.2d))).isNull();
        assertThat(testee.range(TOTAL_AREA)).isEmpty();
    }

    private static Stream<Arguments> test_point_set() {
        return Stream.of(
                Arguments.of(
                        List.of(new Point2D(0.1d, 0.2d))
                ),
                Arguments.of(
                        List.of(new Point2D(0.1d, 0.2d), new Point2D(0.2d, 0.1d))
                )
                ,
                Arguments.of(
                        List.of(
                                new Point2D(0.1d, 0.2d), new Point2D(0.2d, 0.1d),
                                new Point2D(0.3d, 0.2d), new Point2D(0.4d, 0.1d)
                                )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("test_point_set")
    public void given_point_set(List<Point2D> points) {
        //WHEN
        points.forEach(testee::insert);

        //THEN
        assertThat(testee.isEmpty()).isFalse();
        assertThat(testee.size()).isEqualTo(points.size());
        assertThat(points).allMatch(testee::contains);
        assertThat(testee.iterator()).toIterable().containsOnlyOnceElementsOf(points);
    }

    private static Stream<Arguments> test_nearest() {
        return Stream.of(
                Arguments.of(
                        new Point2D(0.1d, 0.2d),
                        List.of(new Point2D(0.1d, 0.2d)),
                        new Point2D(0.1d, 0.2d)
                ),
                Arguments.of(
                        new Point2D(0.2d, 0.2d),
                        List.of(new Point2D(0.1d, 0.2d)),
                        new Point2D(0.1d, 0.2d)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("test_nearest")
    public void find_nearest(Point2D query, List<Point2D> points, Point2D result) {
        points.forEach(testee::insert);
        assertThat(testee.nearest(query)).isEqualByComparingTo(result);
    }

    @Test
    public void find_in_range() {
        //assertThat(testee.range(TOTAL_AREA)).hasSize(1).containsOnly(new Point2D(0.1d, 0.2d));
        ///assertThat(testee.range(new RectHV(0.3, 0.3, 0.5, 0.6))).isEmpty();
    }
}