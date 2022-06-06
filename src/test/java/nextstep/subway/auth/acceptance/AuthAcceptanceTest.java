package nextstep.subway.auth.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.member.acceptance.MemberAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthAcceptanceTest extends AcceptanceTest {
    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        // given
        MemberAcceptanceTest.회원_생성을_요청(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD, 20);

        // when
        ExtractableResponse<Response> response = 로그인_요청(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD);

        // then
        로그인_성공(response);
    }

    @DisplayName("Bearer Auth 로그인 실패")
    @TestFactory
    Stream<DynamicTest> myInfoWithBadBearerAuth() {
        return Stream.of(
                DynamicTest.dynamicTest("email 불일치", () -> {
                    // when
                    ExtractableResponse<Response> response = 로그인_요청("test@test.com", MemberAcceptanceTest.PASSWORD);
                    // then
                    로그인_실패(response);
                }),
                DynamicTest.dynamicTest("password 불일치", () -> {
                    // when
                    ExtractableResponse<Response> response = 로그인_요청(MemberAcceptanceTest.EMAIL, "test");
                    // then
                    로그인_실패(response);
                })
        );
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {
        // given
        String 유효하지_않은_토큰 = "test-token";

        // when
        ExtractableResponse<Response> response = 내_정보_조회_요청(유효하지_않은_토큰);

        // then
        내_정보_조회_실패(response);
    }

    public static ExtractableResponse<Response> 로그인_요청(String email, String password) {
        TokenRequest tokenRequest = new TokenRequest(email, password);
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .extract();
    }

    private void 로그인_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("accessToken")).isNotEmpty();
    }

    private void 로그인_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 내_정보_조회_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/members/me")
                .then().log().all()
                .extract();
    }

    private void 내_정보_조회_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
