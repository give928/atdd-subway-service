package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> values = new ArrayList<>();

    public List<Section> get() {
        return values;
    }

    public void add(Section section) {
        List<Station> stations = getStations();
        boolean isUpStationExisted = stations.stream().anyMatch(it -> it == section.getUpStation());
        boolean isDownStationExisted = stations.stream().anyMatch(it -> it == section.getDownStation());

        if (isUpStationExisted && isDownStationExisted) {
            throw new IllegalArgumentException("이미 등록된 구간 입니다.");
        }

        if (!stations.isEmpty() && stations.stream().noneMatch(it -> it == section.getUpStation()) &&
                stations.stream().noneMatch(it -> it == section.getDownStation())) {
            throw new IllegalArgumentException("등록할 수 없는 구간 입니다.");
        }

        if (stations.isEmpty()) {
            values.add(section);
            return;
        }

        if (isUpStationExisted) {
            values.stream()
                    .filter(it -> it.getUpStation() == section.getUpStation())
                    .findFirst()
                    .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));

            values.add(section);
        } else if (isDownStationExisted) {
            values.stream()
                    .filter(it -> it.getDownStation() == section.getDownStation())
                    .findFirst()
                    .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));

            values.add(section);
        } else {
            throw new IllegalArgumentException("등록할 수 없는 구간 입니다.");
        }
    }

    public Station findUpStation() {
        Station downStation = values.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = values.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    public List<Station> getStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = values.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    public void remove(Station station) {
        if (values.size() <= 1) {
            throw new IllegalArgumentException("구간이 한 개 뿐인 경우 삭제할 수 없습니다.");
        }

        Optional<Section> upLineStation = values.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();
        Optional<Section> downLineStation = values.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst();

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            Line newLine = downLineStation.get().getLine();
            Station newUpStation = downLineStation.get().getUpStation();
            Station newDownStation = upLineStation.get().getDownStation();
            int newDistance = upLineStation.get().getDistance() + downLineStation.get().getDistance();
            values.add(new Section(newLine, newUpStation, newDownStation, newDistance));
        }

        upLineStation.ifPresent(values::remove);
        downLineStation.ifPresent(values::remove);
    }
}
