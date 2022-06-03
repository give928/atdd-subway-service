package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @DisplayName("지하철 노선에 등록된 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = new Line("분당선", "red");
        Station upStation = new Station("상행역");
        Station middleStation = new Station("중간역");
        Station downStation = new Station("하행역");
        line.getSections().add(new Section(line, upStation, middleStation, 10));
        line.getSections().add(new Section(line, middleStation, downStation, 10));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(upStation, middleStation, downStation);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void update() {
        // given
        Line line = new Line("분당선", "red");

        // when
        line.update("신분당선", "green");

        // then
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addLineStation() {
        // given
        Station upStation = new Station("상행역");
        Station middleStation = new Station("중간역");
        Station downStation = new Station("하행역");
        Line line = new Line("분당선", "red", upStation, middleStation, 10);

        // when
        line.addLineStation(middleStation, downStation, 10);

        // then
        assertThat(line.getStations()).containsExactly(upStation, middleStation, downStation);
    }

    @DisplayName("지하철 노선에 구간 삭제")
    @Test
    void removeLineStation() {
        // given

        // when

        // then
    }
}
