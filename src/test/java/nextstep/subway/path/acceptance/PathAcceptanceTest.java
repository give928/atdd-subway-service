package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.ui.PathController;
import nextstep.subway.station.acceptance.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("지하철 경로 조회")
class PathAcceptanceTest extends AcceptanceTest {
    @MockBean
    private PathController pathController;

    private LineResponse 일호선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 신분당선;
    private LineResponse 분당선;
    private StationResponse 시청역;
    private StationResponse 서울역;
    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 선릉역;
    private StationResponse 남부터미널역;
    private StationResponse 양재역;
    private StationResponse 매봉역;
    private StationResponse 도곡역;
    private StationResponse 양재시민의숲역;
    private StationResponse 한티역;

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
        super.setUp();

        시청역 = StationAcceptanceTest.지하철역_등록되어_있음("시청역").as(StationResponse.class);
        서울역 = StationAcceptanceTest.지하철역_등록되어_있음("서울역").as(StationResponse.class);
        교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = StationAcceptanceTest.지하철역_등록되어_있음("역삼역").as(StationResponse.class);
        선릉역 = StationAcceptanceTest.지하철역_등록되어_있음("선릉역").as(StationResponse.class);
        남부터미널역 = StationAcceptanceTest.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        매봉역 = StationAcceptanceTest.지하철역_등록되어_있음("매봉역").as(StationResponse.class);
        도곡역 = StationAcceptanceTest.지하철역_등록되어_있음("도곡역").as(StationResponse.class);
        양재시민의숲역 = StationAcceptanceTest.지하철역_등록되어_있음("양재시민의숲역").as(StationResponse.class);
        한티역 = StationAcceptanceTest.지하철역_등록되어_있음("한티역").as(StationResponse.class);

        일호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("일호선", "bg-red-600", 시청역.getId(), 서울역.getId(), 10)).as(LineResponse.class);
        이호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10)).as(LineResponse.class);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(이호선, 강남역, 역삼역, 10);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(이호선, 역삼역, 선릉역, 10);
        삼호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("삼호선", "bg-red-600", 교대역.getId(), 남부터미널역.getId(), 10)).as(LineResponse.class);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(삼호선, 남부터미널역, 양재역, 10);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(삼호선, 양재역, 매봉역, 8);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(삼호선, 매봉역, 도곡역, 7);
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)).as(LineResponse.class);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 양재시민의숲역, 10);
        분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("분당선", "bg-red-600", 선릉역.getId(), 한티역.getId(), 6)).as(LineResponse.class);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(분당선, 한티역, 도곡역, 5);
    }

    /**
     * When 최단 경로를 조회하면
     * Then 최단 경로 조회됨
     */
    @DisplayName("최단 경로를 조회힌다.")
    @Test
    void findPath() {
        // given
        when(pathController.findPath(양재시민의숲역.getId(), 선릉역.getId())).thenReturn(
                ResponseEntity.ok().body(new PathResponse(Arrays.asList(양재시민의숲역, 양재역, 매봉역, 도곡역, 한티역, 선릉역), 10)));

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(양재시민의숲역, 선릉역);

        // then
        최단_경로_조회됨(response);
        최단_경로_지하철역_순서_정렬됨(response, Arrays.asList(양재시민의숲역, 양재역, 매봉역, 도곡역, 한티역, 선릉역));
    }

    /**
     * When 같은 출발역과 도착역의 최단 경로를 조회하면
     * Then 최단 경로 조회 실패됨
     */
    @DisplayName("같은 출발역과 도착역의 최단 경로를 조회힌다.")
    @Test
    void findPathWithSameStations() {
        // given
        when(pathController.findPath(강남역.getId(), 강남역.getId())).thenReturn(ResponseEntity.badRequest().build());

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(강남역, 강남역);

        // then
        최단_경로_조회_실패됨(response);
    }

    /**
     * When 연결되지 않은 출발역과 도착역의 최단 경로를 조회하면
     * Then 최단 경로 조회 실패됨
     */
    @DisplayName("연결되지 않은 출발역과 도착역의 최단 경로를 조회힌다.")
    @Test
    void findPathWithNotLinkedStations() {
        // given
        when(pathController.findPath(서울역.getId(), 강남역.getId())).thenReturn(ResponseEntity.badRequest().build());

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(서울역, 강남역);

        // then
        최단_경로_조회_실패됨(response);
    }

    /**
     * When 존재하지 않는 출발역이나 도착역의 최단 경로를 조회하면
     * Then 최단 경로 조회 실패됨
     */
    @DisplayName("존재하지 않는 출발역이나 도착역의 최단 경로를 조회한다.")
    @Test
    void findPathWithNotExistsStations() {
        // given
        StationResponse 임시역 = new StationResponse(9999L, "임시역", LocalDateTime.now(), LocalDateTime.now());
        when(pathController.findPath(강남역.getId(), 임시역.getId())).thenReturn(ResponseEntity.badRequest().build());

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(강남역, 임시역);

        // then
        최단_경로_조회_실패됨(response);
    }

    public static ExtractableResponse<Response> 최단_경로_조회_요청(StationResponse source, StationResponse target) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={source}&target={target}", source.getId(), target.getId())
                .then().log().all()
                .extract();
    }

    private void 최단_경로_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 최단_경로_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        PathResponse path = response.as(PathResponse.class);
        List<Long> stationIds = path.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    private void 최단_경로_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
