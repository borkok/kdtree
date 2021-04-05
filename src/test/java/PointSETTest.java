import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointSETTest {

    @Test
    public void empty_point_set() {
        PointSET testee = new PointSET();
        assertThat(testee.isEmpty()).isTrue();
        assertThat(testee.size()).isEqualTo(0);
    }
}