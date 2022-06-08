package nextstep.subway.path.domain;

import java.util.stream.Stream;

public enum AgeFarePolicy {
    ADULT(19, 0, 0),
    TEENAGER(13, 350, 0.2),
    CHILD(6, 350, 0.5),
    BABY(0, 0, 1);

    private final int minAge;
    private final int deductionFare;
    private final double discountRate;

    AgeFarePolicy(int minAge, int deductionFare, double discountRate) {
        this.minAge = minAge;
        this.deductionFare = deductionFare;
        this.discountRate = discountRate;
    }

    public static AgeFarePolicy valueOf(Integer age) {
        if (age == null) {
            return ADULT;
        }
        return Stream.of(values())
                .filter(ageFarePolicy -> ageFarePolicy.minAge <= age)
                .findFirst()
                .orElse(ADULT);
    }

    public int calculateFare(int fare) {
        return (int) ((fare - deductionFare) * (1 - discountRate));
    }
}
