package nextstep.subway.member.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.AuthAcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;
    public static final String MY_INFO_URI = "/members/me";

    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> findResponse = 회원_정보_조회_요청(createResponse);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);
        // then
        회원_삭제됨(deleteResponse);
    }

    @DisplayName("내 정보를 관리한다.")
    @TestFactory
    Stream<DynamicTest> manageMyInfo() {
        // given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);

        return Stream.of(
                DynamicTest.dynamicTest("내 정보 조회", () -> {
                    // given
                    TokenResponse 토큰 = AuthAcceptanceTest.로그인_요청(EMAIL, PASSWORD).as(TokenResponse.class);
                    // when
                    ExtractableResponse<Response> response = 내_정보_조회_요청(토큰);
                    // then
                    내_정보_조회됨(response, EMAIL, AGE);
                }),
                DynamicTest.dynamicTest("내 정보 수정", () -> {
                    // given
                    TokenResponse 토큰 = AuthAcceptanceTest.로그인_요청(EMAIL, PASSWORD).as(TokenResponse.class);
                    // when
                    ExtractableResponse<Response> response = 내_정보_수정_요청(토큰, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
                    // then
                    내_정보_수정됨(response);
                }),
                DynamicTest.dynamicTest("내 정보 삭제", () -> {
                    // given
                    TokenResponse 토큰 = AuthAcceptanceTest.로그인_요청(NEW_EMAIL, NEW_PASSWORD).as(TokenResponse.class);
                    // when
                    ExtractableResponse<Response> response = 내_정보_삭제_요청(토큰);
                    // then
                    내_정보_삭제됨(response);
                })
        );
    }

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 회원_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 회원_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 회원_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 내_정보_조회_요청(TokenResponse tokenResponse) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(tokenResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(MY_INFO_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 내_정보_수정_요청(TokenResponse tokenResponse, String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .auth().oauth2(tokenResponse.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().put(MY_INFO_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 내_정보_삭제_요청(TokenResponse tokenResponse) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(tokenResponse.getAccessToken())
                .when().delete(MY_INFO_URI)
                .then().log().all()
                .extract();
    }

    private void 내_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 내_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 내_정보_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
