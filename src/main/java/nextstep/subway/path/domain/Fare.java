package nextstep.subway.path.domain;

public class Fare {
    private final int value;

    private Fare(int value) {
        this.value = value;
    }

    public static Fare of(Integer age, int distance, int extraFare) {
        int distanceFare = DistanceFarePolicy.calculateFare(distance);
        AgeFarePolicy ageFarePolicy = AgeFarePolicy.valueOf(age);
        return new Fare(ageFarePolicy.calculateFare(distanceFare + extraFare));
    }

    public int get() {
        return value;
    }
}
