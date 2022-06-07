package nextstep.subway.favorite.application;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.FavoriteRepository;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.member.application.MemberService;
import nextstep.subway.member.domain.Member;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {
    @InjectMocks
    private FavoriteService favoriteService;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private StationService stationService;

    private Station source;
    private Station target;
    private Member member;
    private Favorite favorite;

    @BeforeEach
    public void setUp() {
        source = new Station(1L, "교대역");
        target = new Station(2L, "강남역");
        member = new Member(1L, "test1@test.com", "password", 20);
        favorite = new Favorite(1L, member, source, target);
    }

    @DisplayName("즐겨찾기 생성")
    @Test
    void createFavorite() {
        // given
        given(memberService.findMemberById(member.getId())).willReturn(member);
        given(stationService.findStationById(source.getId())).willReturn(source);
        given(stationService.findStationById(target.getId())).willReturn(target);
        given(favoriteRepository.save(any())).willReturn(favorite);

        // when
        FavoriteResponse favoriteResponse = favoriteService.createFavorite(member.getId(), new FavoriteRequest(source.getId(), target.getId()));

        // then
        assertThat(favoriteResponse.getSource().getId()).isEqualTo(source.getId());
        assertThat(favoriteResponse.getTarget().getId()).isEqualTo(target.getId());
    }

    @DisplayName("즐겨찾기 목록 조회")
    @Test
    void findMemberFavorites() {
        // given
        given(favoriteRepository.findByMemberId(member.getId())).willReturn(Collections.singletonList(favorite));

        // when
        List<FavoriteResponse> favoriteResponses = favoriteService.findMemberFavorites(1L);

        // then
        assertThat(favoriteResponses).hasSize(1);
        assertThat(favoriteResponses.get(0).getSource().getId()).isEqualTo(source.getId());
        assertThat(favoriteResponses.get(0).getTarget().getId()).isEqualTo(target.getId());
    }

    @DisplayName("즐겨찾기 삭제")
    @Test
    void deleteFavorite() {
        // given
        given(favoriteRepository.findById(favorite.getId())).willReturn(Optional.of(favorite));

        // when
        favoriteService.deleteFavorite(favorite.getMember().getId(), favorite.getId());

        // then
        List<FavoriteResponse> favorites = favoriteService.findMemberFavorites(favorite.getMember().getId());
        assertThat(favorites).isEmpty();
    }
}
