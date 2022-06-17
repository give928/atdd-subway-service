package nextstep.subway.path.domain;

import java.util.stream.Stream;

public enum DistanceFarePolicy {
    LONG(51, Integer.MAX_VALUE, 8, 100),
    MIDDLE(11, 50, 5, 100),
    SHORT(1, 10, 10, 1_250);

    private final int minDistance;
    private final int maxDistance;
    private final int eachDistance;
    private final int overFare;

    DistanceFarePolicy(int minDistance, int maxDistance, int eachDistance, int overFare) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.eachDistance = eachDistance;
        this.overFare = overFare;
    }

    public static int calculateFare(int distance) {
        return Stream.of(values())
                .mapToInt(policy -> policy.calculateOverFare(distance))
                .sum();
    }

    private int calculateOverFare(int distance) {
        int overDistance = calculateOverDistance(distance);
        if (overDistance <= 0) {
            return 0;
        }
        return (int) (Math.ceil((double) overDistance / eachDistance) * overFare);
    }

    private int calculateOverDistance(int distance) {
        if (distance > maxDistance) {
            distance = maxDistance;
        }
        return distance - (minDistance - 1);
    }
}
