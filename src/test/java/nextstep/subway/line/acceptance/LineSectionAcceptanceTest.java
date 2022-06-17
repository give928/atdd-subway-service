package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 광교역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    /**
     * When 지하철 구간 등록 요청
     * Then 지하철 구간 등록됨
     *
     * When 지하철 노선에 여러개의 역을 순서 상관 없이 등록 요청
     * Then 지하철 구간 등록됨
     *
     * When 지하철 노선에 이미 등록되어있는 구간 등록 요청
     * Then 지하철 구간 등록 실패됨
     *
     * When 지하철 노선에 등록되지 않은 역을 기준으로 구간 등록 요청
     * Then 지하철 구간 등록 실패됨
     *
     * When 지하철 노선에 등록된 역 목록 조회 요청
     * Then 등록한 지하철 구간이 반영된 역 목록이 조회됨
     *
     * When 지하철 구간 삭제 요청
     * Then 지하철 구간 삭제됨
     *
     * When 지하철 노선에 등록된 역 목록 조회 요청
     * Then 등록한 지하철 구간이 반영된 역 목록이 조회됨
     *
     * When 지하철 노선에 등록된 구간이 한 개 일때 삭제 요청
     * Then 지하철 구간 삭제 실패됨
     *
     * When 지하철 노선에 등록된 역 목록 조회 요청
     * Then 삭제한 지하철 구간이 반영된 역 목록이 조회됨
     */
    @DisplayName("지하철 구간을 관리한다.")
    @TestFactory
    Stream<DynamicTest> manageLine() {
        return Stream.of(
                dynamicTest("지하철 구간을 등록 한다.", () -> {
                    // when
                    ExtractableResponse<Response> createResponse = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 3);
                    // then
                    지하철_노선에_지하철역_등록됨(createResponse);
                }),
                dynamicTest("지하철 노선에 여러개의 역을 순서 상관 없이 등록한다.", () -> {
                    // when
                    ExtractableResponse<Response> otherCreateResponse = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 강남역, 5);
                    // then
                    지하철_노선에_지하철역_등록됨(otherCreateResponse);
                }),
                dynamicTest("지하철 노선에 이미 등록되어있는 구간은 등록되지 않는다.", () -> {
                    // when
                    ExtractableResponse<Response> duplicateCreateResponse = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 3);
                    // then
                    지하철_노선에_지하철역_등록_실패됨(duplicateCreateResponse);
                }),
                dynamicTest("지하철 노선에 등록되지 않은 역은 구간으로 등록되지 않는다.", () -> {
                    // when
                    ExtractableResponse<Response> notExistsStationCreateResponse = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 양재역, 3);
                    // then
                    지하철_노선에_지하철역_등록_실패됨(notExistsStationCreateResponse);
                }),
                dynamicTest("지하철 노선에 등록된 역 목록을 조회하면 반영된 역 목록이 조회된다.", () -> {
                    // when
                    ExtractableResponse<Response> findResponse1 = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
                    // then
                    지하철_노선에_지하철역_순서_정렬됨(findResponse1, Arrays.asList(정자역, 강남역, 양재역, 광교역));
                }),
                dynamicTest("지하철 구간을 삭제한다.", () -> {
                    // when
                    ExtractableResponse<Response> removeResponse1 = 지하철_노선에_지하철역_제외_요청(신분당선, 정자역);
                    ExtractableResponse<Response> removeResponse2 = 지하철_노선에_지하철역_제외_요청(신분당선, 강남역);
                    // then
                    지하철_노선에_지하철역_제외됨(removeResponse1);
                    지하철_노선에_지하철역_제외됨(removeResponse2);
                }),
                dynamicTest("지하철 노선에 등록된 역 목록을 조회하면 반영된 역 목록이 조회된다.", () -> {
                    // when
                    ExtractableResponse<Response> findResponse2 = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
                    // then
                    지하철_노선에_지하철역_순서_정렬됨(findResponse2, Arrays.asList(양재역, 광교역));
                }),
                dynamicTest("지하철 노선에 등록된 구간이 한 개 일때는 삭제되지 않는다.", () -> {
                    // when
                    ExtractableResponse<Response> failRemoveResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);
                    // then
                    지하철_노선에_지하철역_제외_실패됨(failRemoveResponse);
                }),
                dynamicTest("지하철 노선에 등록된 역 목록을 조회하면 반영된 역 목록이 조회된다.", () -> {
                    // when
                    ExtractableResponse<Response> findResponse3 = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
                    // then
                    지하철_노선에_지하철역_순서_정렬됨(findResponse3, Arrays.asList(양재역, 광교역));
                })
        );
    }

    @DisplayName("지하철 구간을 등록한다.")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 3);

        // then
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 광교역));
    }

    @DisplayName("지하철 노선에 여러개의 역을 순서 상관 없이 등록한다.")
    @Test
    void addLineSection2() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 2);
        지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 강남역, 5);

        // then
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(정자역, 강남역, 양재역, 광교역));
    }

    @DisplayName("지하철 노선에 이미 등록되어있는 역을 등록한다.")
    @Test
    void addLineSectionWithSameStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 광교역, 3);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록되지 않은 역을 기준으로 등록한다.")
    @Test
    void addLineSectionWithNoStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 양재역, 3);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제외한다.")
    @Test
    void removeLineSection1() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 2);
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 2);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역, 광교역));
    }

    @DisplayName("지하철 노선에 등록된 지하철역이 두개일 때 한 역을 제외한다.")
    @Test
    void removeLineSection2() {
        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 강남역);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", line.getId())
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_제외_요청(LineResponse line, StationResponse station) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", line.getId(), station.getId())
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_지하철역_제외됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_제외_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
