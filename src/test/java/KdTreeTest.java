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
    private PointSET pointSET;

    @BeforeEach
    void setUp() {
        testee = new KdTree();
        pointSET = new PointSET();
    }

    @Test
    public void empty_point_set() {
        assertThat(testee.isEmpty()).isTrue();
        assertThat(testee.size()).isEqualTo(0);
        assertThat(testee.nearest(point(0.1d, 0.2d))).isNull();
        assertThat(testee.range(TOTAL_AREA)).isEmpty();
    }

    private static Stream<Arguments> test_point_set() {
        return Stream.of(
                Arguments.of(
                        List.of(point(0.1d, 0.2d))
                ),
                Arguments.of(
                        List.of(point(0.1d, 0.2d), point(0.2d, 0.1d))
                )
                ,
                Arguments.of(
                        List.of(
                                point(0.1d, 0.2d), point(0.2d, 0.1d),
                                point(0.3d, 0.2d), point(0.4d, 0.1d)
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
    }

    private static Stream<Arguments> test_nearest() {
        return Stream.of(
                Arguments.of(
                        point(0.1d, 0.2d),
                        List.of(point(0.1d, 0.2d))
                ),
                Arguments.of(
                        point(0.2d, 0.2d),
                        List.of(point(0.1d, 0.2d))
                ),
                Arguments.of(
                        point(0.1d, 0.2d),
                        List.of(
                                point(0.000000, 0.500000),
                                point(0.500000, 1.000000),
                                point(0.500000, 0.000000),
                                point(1.000000, 0.500000)
                        )
                ),
                Arguments.of(
                        point(0.5d, 0.5d),
                        List.of(
                                point(0.372, 0.497),
                                point(0.564, 0.413),
                                point(0.226, 0.577),
                                point(0.144, 0.179),
                                point(0.083, 0.510),
                                point(0.320, 0.708),
                                point(0.417, 0.362),
                                point(0.862, 0.825),
                                point(0.785, 0.725),
                                point(0.499, 0.208)
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("test_nearest")
    public void find_nearest(Point2D query, List<Point2D> points) {
        points.forEach(testee::insert);
        points.forEach(pointSET::insert);

        Point2D result = testee.nearest(query);

        assertThat(result).isEqualByComparingTo(pointSET.nearest(query));
        System.out.println(result.toString());
    }

    private static Stream<Arguments> test_in_range() {
        return Stream.of(
                Arguments.of(
                        new RectHV(0.3, 0.3, 0.5, 0.6),
                        List.of(point(0.1d, 0.2d))
                ),
                Arguments.of(
                        new RectHV(0.3, 0.3, 0.5, 0.6),
                        List.of(
                                point(0.000000, 0.500000),
                                point(0.500000, 1.000000),
                                point(0.500000, 0.000000),
                                point(1.000000, 0.500000)
                        )
                ),
                Arguments.of(
                        new RectHV(0.3, 0.3, 0.5, 0.6),
                        List.of(
                                point(0.372, 0.497),
                                point(0.564, 0.413),
                                point(0.226, 0.577),
                                point(0.144, 0.179),
                                point(0.083, 0.510),
                                point(0.320, 0.708),
                                point(0.417, 0.362),
                                point(0.862, 0.825),
                                point(0.785, 0.725),
                                point(0.499, 0.208)
                        )
                ),
                Arguments.of(
                        new RectHV(0.3, 0.1, 0.5, 0.2),
                        List.of(
                                point(0.9, 0.5),
                                point(0.2, 0.5),
                                point(0.3, 0.5),
                                point(0.4, 0.5),
                                point(0.1, 0.5),
                                point(0.6, 0.5),
                                point(0.5, 0.5),
                                point(0.7, 0.5)
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("test_in_range")
    public void find_in_range(RectHV rectHV, List<Point2D> points) {
        points.forEach(testee::insert);
        points.forEach(pointSET::insert);

        Iterable<Point2D> result = testee.range(rectHV);

        assertThat(result).containsAll(pointSET.range(rectHV));
    }

    private static Point2D point(double x, double y) {
        return new Point2D(x, y);
    }
}