package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {
    private static final String ERROR_MESSAGE_INVALID_DISTANCE = "역과 역 사이의 거리보다 좁은 거리를 입력해주세요.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        initLine(line);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void initLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public int getExtraFare() {
        return line.getExtraFare();
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public void updateUpStation(Station station, int newDistance) {
        validateDistance(newDistance);
        this.upStation = station;
        this.distance -= newDistance;
    }

    public void updateDownStation(Station station, int newDistance) {
        validateDistance(newDistance);
        this.downStation = station;
        this.distance -= newDistance;
    }

    private void validateDistance(int newDistance) {
        if (this.distance <= newDistance) {
            throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_DISTANCE);
        }
    }
}
