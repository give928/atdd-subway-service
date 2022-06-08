package nextstep.subway.path.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceFarePolicyTest {
    @DisplayName("거리별 요금을 계산한다.")
    @ParameterizedTest(name = "{displayName} distance={0}, overFare={1}")
    @CsvSource(value = {"-1, 0", "0, 0", "1, 1_250", "10, 1_250", "11, 1_350", "50, 2_050", "51, 2_150", "58, 2_150", "59, 2_250", "67, 2_350"})
    void calculateOverFare(int distance, int expected) {
        // when
        int actual = DistanceFarePolicy.calculateFare(distance);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
