package nextstep.subway.path.dto;

import nextstep.subway.path.domain.Fare;
import nextstep.subway.path.domain.Path;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private final List<StationResponse> stations;
    private final int distance;
    private final int fare;

    protected PathResponse() {
        this(Collections.emptyList(), 0, 0);
    }

    public PathResponse(List<StationResponse> stations, int distance) {
        this(stations, distance, 0);
    }

    public PathResponse(List<StationResponse> stations, int distance, int fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public static PathResponse of(Path path, Fare fare) {
        List<StationResponse> stationResponses = path.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, path.getDistance(), fare.get());
    }

    public static PathResponse of(List<Station> stations, double distance) {
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, (int) distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }
}
