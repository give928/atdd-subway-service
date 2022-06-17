package nextstep.subway.favorite.domain;

import nextstep.subway.member.domain.Member;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FavoriteTest {
    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "test1@test.com", "password", 20);
    }

    @DisplayName("출발역 또는 도착역이 없으면 즐겨찾기를 생성할 수 없다.")
    @Test
    void nullStations() {
        // given
        Station station = new Station(1L, "교대역");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Favorite(member, station, null);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("출발역과 도착역이 같으면 즐겨찾기를 생성할 수 없다.")
    @Test
    void sameSourceTarget() {
        // given
        Station station = new Station(1L, "교대역");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Favorite(member, station, station);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("즐겨찾기 소유자를 확인한다.")
    @Test
    void isOwner() {
        // given
        Favorite favorite = new Favorite(member, new Station(1L, "교대역"), new Station(2L, "강남역"));

        // when
        boolean owner = favorite.isOwner(member.getId());

        // then
        assertThat(owner).isTrue();
    }
}
