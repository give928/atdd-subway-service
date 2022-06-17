package nextstep.subway.favorite.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.AuthAcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.member.acceptance.MemberAcceptanceTest;
import nextstep.subway.station.acceptance.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("즐겨찾기 관련 기능")
class FavoriteAcceptanceTest extends AcceptanceTest {
    public static final String FAVORITES_URI = "/favorites";

    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 선릉역;

    private TokenResponse 토큰;

    /**
     * Given 지하철역 등록되어 있음
     * And 지하철 노선 등록되어 있음
     * And 지하철 노선에 지하철역 등록되어 있음
     * And 회원 등록되어 있음
     * And 로그인 되어있음
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = StationAcceptanceTest.지하철역_등록되어_있음("역삼역").as(StationResponse.class);
        선릉역 = StationAcceptanceTest.지하철역_등록되어_있음("선릉역").as(StationResponse.class);

        LineResponse 이호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10)).as(LineResponse.class);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(이호선, 강남역, 역삼역, 10);
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(이호선, 역삼역, 선릉역, 10);

        MemberAcceptanceTest.회원_생성을_요청(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD, 20);

        토큰 = AuthAcceptanceTest.로그인_요청(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD).as(TokenResponse.class);
    }

    /**
     * When 즐겨찾기 생성을 요청
     * Then 즐겨찾기 생성됨
     *
     * When 출발역과 도착역을 동일한 역으로 즐겨찾기 생성을 요청
     * Then 즐겨찾기 생성 안됨
     *
     * When 즐겨찾기 목록 조회 요청
     * Then 즐겨찾기 목록 조회됨
     *
     * When 즐겨찾기 삭제 요청
     * Then 즐겨찾기 삭제됨
     */
    @DisplayName("즐겨찾기를 관리")
    @Test
    void manageFavorite() {
        // when
        ExtractableResponse<Response> createResponse = 즐겨찾기_생성_요청(교대역, 선릉역);
        // then
        즐겨찾기_생성됨(createResponse);

        // when
        ExtractableResponse<Response> failCreateResponse = 즐겨찾기_생성_요청(강남역, 강남역);
        // then
        즐겨찾기_생성_안됨(failCreateResponse);

        // when
        ExtractableResponse<Response> findResponse = 즐겨찾기_목록_조회_요청();
        // then
        즐겨찾기_목록_조회됨(findResponse, Collections.singletonList(new FavoriteResponse(1L, 교대역, 선릉역)));

        // when
        ExtractableResponse<Response> deleteResponse = 즐겨찾기_삭제_요청(createResponse.header("Location"));
        // then
        즐겨찾기_삭제됨(deleteResponse);
    }

    private ExtractableResponse<Response> 즐겨찾기_생성_요청(StationResponse source, StationResponse target) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(토큰.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new FavoriteRequest(source.getId(), target.getId()))
                .when().post(FAVORITES_URI)
                .then().log().all()
                .extract();
    }

    private void 즐겨찾기_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/favorites/1");
    }

    private void 즐겨찾기_생성_안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 즐겨찾기_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .auth().oauth2(토큰.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(FAVORITES_URI)
                .then().log().all()
                .extract();
    }

    private void 즐겨찾기_목록_조회됨(ExtractableResponse<Response> findResponse, List<FavoriteResponse> expected) {
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<FavoriteResponse> actual = findResponse.jsonPath().getList("", FavoriteResponse.class);
        assertThat(actual).hasSize(expected.size());
        List<Long> actualIds = actual.stream()
                .map(FavoriteResponse::getId)
                .collect(Collectors.toList());
        List<Long> expectedIds = expected.stream()
                .map(FavoriteResponse::getId)
                .collect(Collectors.toList());
        assertThat(actualIds).containsExactlyElementsOf(expectedIds);
    }

    private ExtractableResponse<Response> 즐겨찾기_삭제_요청(String location) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(토큰.getAccessToken())
                .when().delete(location)
                .then().log().all()
                .extract();
    }

    private void 즐겨찾기_삭제됨(ExtractableResponse<Response> deleteResponse) {
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
