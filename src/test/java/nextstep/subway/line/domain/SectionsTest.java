package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    @DisplayName("상행역 검색")
    @Test
    void findUpStation() {
        // given
        Line line = new Line("분당선", "red");
        Station upStation = new Station("상행역");
        Station middleStation = new Station("중간역");
        Station downStation = new Station("하행역");
        Sections sections = new Sections();
        sections.add(line, upStation, middleStation, 10);
        sections.add(line, middleStation, downStation, 10);

        // when
        Station findUpStation = sections.findUpStation();

        // then
        assertThat(findUpStation).isEqualTo(upStation);
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = new Line("분당선", "red");
        Station upStation = new Station("상행역");
        Station middleStation = new Station("중간역");
        Station downStation = new Station("하행역");
        Sections sections = new Sections();
        sections.add(line, upStation, middleStation, 10);
        sections.add(line, middleStation, downStation, 10);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(upStation, middleStation, downStation);
    }

    @DisplayName("구간 추가")
    @Test
    void add() {
        // given
        Line line = new Line("분당선", "red");
        Station upStation = new Station("상행역");
        Station downStation = new Station("하행역");
        int distance = 10;
        Sections sections = new Sections();

        // when
        sections.add(line, upStation, downStation, distance);

        // then
        assertThat(sections.get()).hasSize(1);
        assertThat(sections.get().get(0).getUpStation()).isEqualTo(upStation);
        assertThat(sections.get().get(0).getDownStation()).isEqualTo(downStation);
        assertThat(sections.get().get(0).getDistance()).isEqualTo(distance);
    }

    @DisplayName("구간 삭제")
    @Test
    void remove() {
        // given

        // when

        // then
    }
}
