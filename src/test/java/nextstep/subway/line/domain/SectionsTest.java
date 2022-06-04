package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = new Line("분당선", "red");
        Station upStation = new Station("상행역");
        Station middleUpStation = new Station("중간상행역");
        Station middleStation = new Station("중간역");
        Station middleDownStation = new Station("중간하행역");
        Station downStation = new Station("하행역");
        Sections sections = new Sections();
        sections.add(new Section(line, middleStation, middleDownStation, 10));
        sections.add(new Section(line, middleUpStation, middleStation, 10));
        sections.add(new Section(line, upStation, middleUpStation, 10));
        sections.add(new Section(line, middleDownStation, downStation, 10));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(upStation, middleUpStation, middleStation, middleDownStation, downStation);
    }

    @DisplayName("구간 추가")
    @Test
    void add() {
        // given
        Sections sections = new Sections();
        Section section = new Section(new Line("분당선", "red"), new Station("상행역"), new Station("하행역"), 10);

        // when
        sections.add(section);

        // then
        assertThat(sections.get()).hasSize(1);
        assertThat(sections.get().get(0).getUpStation()).isEqualTo(section.getUpStation());
        assertThat(sections.get().get(0).getDownStation()).isEqualTo(section.getDownStation());
        assertThat(sections.get().get(0).getDistance()).isEqualTo(section.getDistance());
    }

    @DisplayName("구간 삭제")
    @Test
    void remove() {
        // given
        Line line = new Line("분당선", "red");
        Station upStation = new Station("상행역");
        Station middleStation = new Station("중간역");
        Station downStation = new Station("하행역");
        Sections sections = new Sections();
        sections.add(new Section(line, upStation, middleStation, 10));
        sections.add(new Section(line, middleStation, downStation, 10));

        // when
        sections.remove(downStation);

        // then
        assertThat(sections.get()).hasSize(1);
        assertThat(sections.get().get(0).getUpStation()).isEqualTo(upStation);
        assertThat(sections.get().get(0).getDownStation()).isEqualTo(middleStation);
    }
}
