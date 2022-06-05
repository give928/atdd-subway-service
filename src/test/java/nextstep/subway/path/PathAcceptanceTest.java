package nextstep.subway.path;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 조회")
class PathAcceptanceTest extends AcceptanceTest {
    /**
     * When 최단 경로를 조회하면
     * Then 최단 경로 조회됨
     */
    @DisplayName("최단 경로를 조회힌다.")
    @Test
    void findPath() {
        // given

        // when

        // then
    }

    /**
     * When 같은 출발역과 도착역의 최단 경로를 조회하면
     * Then 최단 경로 조회 실패됨
     */
    @DisplayName("같은 출발역과 도착역의 최단 경로를 조회힌다.")
    @Test
    void findPathWithSameStations() {
        // given

        // when

        // then
    }

    /**
     * When 연결되지 않은 출발역과 도착역의 최단 경로를 조회하면
     * Then 최단 경로 조회 실패됨
     */
    @DisplayName("연결되지 않은 출발역과 도착역의 최단 경로를 조회힌다.")
    @Test
    void findPathWithNotLinkedStations() {
        // given

        // when

        // then
    }

    /**
     * When 존재하지 않는 출발역이나 도착역의 최단 경로를 조회하면
     * Then 최단 경로 조회 실패됨
     */
    @DisplayName("존재하지 않는 출발역이나 도착역의 최단 경로를 조회한다.")
    @Test
    void findPathWithNotExistsStations() {
        // given

        // when

        // then
    }
}
