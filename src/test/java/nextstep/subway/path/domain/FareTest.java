package nextstep.subway.path.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FareTest {
    public static Stream<Arguments> createFareParameter() {
        return Stream.of(Arguments.of(1, 10, 0, 0), Arguments.of(8, 10, 0, 450), Arguments.of(15, 10, 0, 720),
                         Arguments.of(20, 10, 0, 1_250), Arguments.of(20, 10, 100, 1_350),
                         Arguments.of(1, 11, 0, 0), Arguments.of(8, 11, 0, 500), Arguments.of(15, 11, 0, 800),
                         Arguments.of(20, 11, 0, 1_350), Arguments.of(20, 11, 100, 1_450),
                         Arguments.of(1, 51, 0, 0), Arguments.of(8, 51, 0, 900), Arguments.of(15, 51, 0, 1_440),
                         Arguments.of(20, 51, 0, 2_150), Arguments.of(20, 51, 100, 2_250));
    }

    @DisplayName("요금을 계산한다.")
    @ParameterizedTest(name = "{displayName} age={0}, distance={1}, extraFare={2}, expectedFare={3}")
    @MethodSource("createFareParameter")
    void createFare(int age, int distance, int extraFare, int expectedFare) {
        // when
        Fare fare = Fare.of(age, distance, extraFare);

        // then
        assertThat(fare.get()).isEqualTo(expectedFare);
    }
}
