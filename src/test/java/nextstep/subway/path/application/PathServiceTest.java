package nextstep.subway.path.application;

import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {
    @InjectMocks
    private PathService pathService;
    @Mock
    private StationService stationService;
    @Mock
    private LineService lineService;

    private Station 시청역;
    private Station 서울역;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 남부터미널역;
    private Station 양재역;
    private Station 매봉역;
    private Station 도곡역;
    private Station 양재시민의숲역;
    private Station 한티역;

    private Line 일호선;
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;
    private Line 분당선;

    private LoginMember 비회원;
    private LoginMember 회원;

    /**
     * *1호선* 서울역 -10- 시청역 *1호선*
     *
     *         *3호선*           *신분당선*                    *분당선*
     * *2호선* 교대역       -10- 강남역       -10- 역삼역 -10- 선릉역 *2호선*
     *         |                 |                             |5
     *         10                10                            한티역
     *         |                 |                             |6
     *         남부터미널역 -10- 양재역       -08- 매봉역 -07- 도곡역 *3호선*
     *                           |10                           *분당선*
     *                           양재시민의숲
     *                           *신분당선*
     */
    @BeforeEach
    public void setUp() {
        시청역 = new Station(1L, "시청역");
        서울역 = new Station(2L, "서울역");
        교대역 = new Station(3L, "교대역");
        강남역 = new Station(4L, "강남역");
        역삼역 = new Station(5L, "역삼역");
        선릉역 = new Station(6L, "선릉역");
        남부터미널역 = new Station(7L, "남부터미널역");
        양재역 = new Station(8L, "양재역");
        매봉역 = new Station(9L, "매봉역");
        도곡역 = new Station(10L, "도곡역");
        양재시민의숲역 = new Station(11L, "양재시민의숲역");
        한티역 = new Station(12L, "한티역");

        일호선 = new Line(1L, "일호선", "bg-red-600", 100);
        일호선.addSection(new Section(시청역, 서울역, 10));
        이호선 = new Line(2L, "이호선", "bg-red-600", 200);
        이호선.addSection(new Section(교대역, 강남역, 10));
        이호선.addSection(new Section(강남역, 역삼역, 10));
        이호선.addSection(new Section(역삼역, 선릉역, 10));
        삼호선 = new Line(3L, "삼호선", "bg-red-600", 300);
        삼호선.addSection(new Section(교대역, 남부터미널역, 10));
        삼호선.addSection(new Section(남부터미널역, 양재역, 10));
        삼호선.addSection(new Section(양재역, 매봉역, 8));
        삼호선.addSection(new Section(매봉역, 도곡역, 7));
        신분당선 = new Line(4L, "신분당선", "bg-red-600", 400);
        신분당선.addSection(new Section(강남역, 양재역, 10));
        신분당선.addSection(new Section(양재역, 양재시민의숲역, 10));
        분당선 = new Line(5L, "분당선", "bg-red-600", 500);
        분당선.addSection(new Section(선릉역, 한티역, 6));
        분당선.addSection(new Section(한티역, 도곡역, 5));

        비회원 = new LoginMember();
    }

    @DisplayName("최단 경로를 조회힌다.")
    @Test
    void findPath() {
        // given
        List<Station> stations = Arrays.asList(양재시민의숲역, 선릉역);
        given(stationService.findStationsByIdIn(Arrays.asList(양재시민의숲역.getId(), 선릉역.getId()))).willReturn(stations);
        given(lineService.findAll()).willReturn(Arrays.asList(일호선, 이호선, 삼호선, 신분당선, 분당선));

        // when
        PathResponse pathResponse = pathService.findPath(양재시민의숲역.getId(), 선릉역.getId(), 비회원);

        // then
        최단_경로_확인됨(pathResponse, Arrays.asList(양재시민의숲역, 양재역, 매봉역, 도곡역, 한티역, 선릉역), 36, 2_350);
    }

    @DisplayName("같은 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void findPathWithSameStations() {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> pathService.findPath(강남역.getId(), 강남역.getId(), 비회원);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("연결되지 않은 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void findPathWithNotLinkedStations() {
        // given
        given(stationService.findStationsByIdIn(Arrays.asList(서울역.getId(), 강남역.getId()))).willReturn(Arrays.asList(서울역, 강남역));
        given(lineService.findAll()).willReturn(Arrays.asList(일호선, 이호선, 삼호선, 신분당선, 분당선));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> pathService.findPath(서울역.getId(), 강남역.getId(), 비회원);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("존재하지 않는 출발역이나 도착역의 최단 경로를 조회한다.")
    @Test
    void findPathWithNotExistsStations() {
        // given
        given(stationService.findStationsByIdIn(Arrays.asList(서울역.getId(), 9999L))).willThrow(StationNotFoundException.class);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> pathService.findPath(서울역.getId(), 9999L, 비회원);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(StationNotFoundException.class);
    }

    private void 최단_경로_확인됨(PathResponse pathResponse, List<Station> expectedStations, int expectedDistance, int expectedFare) {
        assertThat(pathResponse.getDistance()).isEqualTo(expectedDistance);
        assertThat(pathResponse.getFare()).isEqualTo(expectedFare);

        List<Long> stationIds = pathResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
