package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        sections.add(new Section(line, upStation, middleStation, 10));
        sections.add(new Section(line, middleStation, downStation, 10));

        // when
        Station findUpStation = sections.findUpStation();

        // then
        assertThat(findUpStation).isEqualTo(upStation);
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        // given

        // when

        // then
    }

    @DisplayName("구간 추가")
    @Test
    void add() {
        // given

        // when

        // then
    }

    @DisplayName("구간 삭제")
    @Test
    void remove() {
        // given

        // when

        // then
    }
}
