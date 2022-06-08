package nextstep.subway.path.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AgeFarePolicyTest {
    public static Stream<Arguments> valueOfParameter() {
        return Stream.of(Arguments.of(0, AgeFarePolicy.BABY), Arguments.of(5, AgeFarePolicy.BABY),
                         Arguments.of(6, AgeFarePolicy.CHILD), Arguments.of(12, AgeFarePolicy.CHILD),
                         Arguments.of(13, AgeFarePolicy.TEENAGER), Arguments.of(18, AgeFarePolicy.TEENAGER),
                         Arguments.of(19, AgeFarePolicy.ADULT), Arguments.of(null, AgeFarePolicy.ADULT));
    }
    public static Stream<Arguments> calculateFareParameter() {
        return Stream.of(Arguments.of(AgeFarePolicy.BABY, 1_250, 0), Arguments.of(AgeFarePolicy.BABY, 1_550, 0),
                         Arguments.of(AgeFarePolicy.CHILD, 1_250, 450), Arguments.of(AgeFarePolicy.CHILD, 1_550, 600),
                         Arguments.of(AgeFarePolicy.TEENAGER, 1_250, 720), Arguments.of(AgeFarePolicy.TEENAGER, 1_550, 960),
                         Arguments.of(AgeFarePolicy.ADULT, 1_250, 1_250), Arguments.of(AgeFarePolicy.ADULT, 1_550, 1_550));
    }

    @DisplayName("연령별 요금 정책을 찾는다.")
    @ParameterizedTest(name = "{displayName} age={0}, farePolicy={1}")
    @MethodSource("valueOfParameter")
    void valueOf(Integer age, AgeFarePolicy expected) {
        // when
        AgeFarePolicy actual = AgeFarePolicy.valueOf(age);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("연령별 할인이 적용된 요금을 계산한다.")
    @ParameterizedTest(name = "{displayName} ageFarePolicy={0}, distanceOverFare={1}, lineOverFare={2}, expectedFare={3}")
    @MethodSource("calculateFareParameter")
    void calculateFare(AgeFarePolicy ageFarePolicy, int distanceFare, int expectedFare) {
        // when
        int actualFare = ageFarePolicy.calculateFare(distanceFare);

        // then
        assertThat(actualFare).isEqualTo(expectedFare);
    }
}
