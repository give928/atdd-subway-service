package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int MINIMUM_SECTION_SIZE = 1;
    private static final String ERROR_MESSAGE_CANNOT_REMOVE_LAST_SECTION = "구간이 한 개 뿐인 경우 삭제할 수 없습니다.";
    private static final String ERROR_MESSAGE_CANNOT_ADD_EXISTS_SECTION = "이미 등록된 구간 입니다.";
    private static final String ERROR_MESSAGE_CANNOT_ADD_NOT_LINKABLE_SECTION = "등록할 수 없는 구간 입니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> values = new ArrayList<>();

    public List<Section> get() {
        return values;
    }

    public boolean add(Section section) {
        if (values.isEmpty()) {
            return values.add(section);
        }
        return link(section);
    }

    private boolean link(Section section) {
        boolean existsUpStation = existsStation(section.getUpStation());
        boolean existsDownStation = existsStation(section.getDownStation());
        validateAdd(existsUpStation, existsDownStation);
        if (existsUpStation) {
            findUpStationSection(section.getUpStation())
                    .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));
        }
        if (existsDownStation) {
            findDownStationSection(section.getDownStation())
                    .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));
        }
        return values.add(section);
    }

    private void validateAdd(boolean existsUpStation, boolean existsDownStation) {
        if (existsUpStation && existsDownStation) {
            throw new IllegalArgumentException(ERROR_MESSAGE_CANNOT_ADD_EXISTS_SECTION);
        }
        if (!existsUpStation && !existsDownStation) {
            throw new IllegalArgumentException(ERROR_MESSAGE_CANNOT_ADD_NOT_LINKABLE_SECTION);
        }
    }

    public List<Station> getStations() {
        List<Section> sections = new ArrayList<>(values);

        List<Station> stations = initStations(sections);
        while (!sections.isEmpty()) {
            addLinkedStations(sections, stations);
        }
        return stations;
    }

    private List<Station> initStations(List<Section> sections) {
        List<Station> stations = new ArrayList<>();
        if (!sections.isEmpty()) {
            Section startSection = sections.get(FIRST_INDEX);
            stations.add(startSection.getUpStation());
            stations.add(startSection.getDownStation());
            sections.remove(startSection);
        }
        return stations;
    }

    private void addLinkedStations(List<Section> sections, List<Station> stations) {
        Station lastDownStation = stations.get(stations.size() - 1);
        Station lastUpStation = stations.get(FIRST_INDEX);

        List<Section> linkedSections = sections.stream()
                .filter(section -> section.getUpStation().equals(lastDownStation) || section.getDownStation().equals(
                        lastUpStation))
                .collect(Collectors.toList());

        linkedSections.forEach(section -> {
            if (section.getUpStation().equals(lastDownStation)) {
                stations.add(section.getDownStation());
                sections.remove(section);
            }
            if (section.getDownStation().equals(lastUpStation)) {
                stations.add(FIRST_INDEX, section.getUpStation());
                sections.remove(section);
            }
        });
    }

    public void remove(Station station) {
        validateRemove();

        Optional<Section> upSection = findUpStationSection(station);
        Optional<Section> downSection = findDownStationSection(station);
        if (upSection.isPresent() && downSection.isPresent()) {
            mergeSection(upSection.get(), downSection.get());
        }
        upSection.ifPresent(values::remove);
        downSection.ifPresent(values::remove);
    }

    private void validateRemove() {
        if (values.size() <= MINIMUM_SECTION_SIZE) {
            throw new IllegalArgumentException(ERROR_MESSAGE_CANNOT_REMOVE_LAST_SECTION);
        }
    }

    private boolean existsStation(Station station) {
        return values.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    private Optional<Section> findUpStationSection(Station station) {
        return values.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny();
    }

    private Optional<Section> findDownStationSection(Station station) {
        return values.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny();
    }

    private void mergeSection(Section upSection, Section downSection) {
        Line newLine = downSection.getLine();
        Station newUpStation = downSection.getUpStation();
        Station newDownStation = upSection.getDownStation();
        int newDistance = upSection.getDistance() + downSection.getDistance();
        values.add(new Section(newLine, newUpStation, newDownStation, newDistance));
    }
}
