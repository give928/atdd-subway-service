package nextstep.subway.favorite.application;

import nextstep.subway.auth.exception.AuthorizationException;
import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.FavoriteRepository;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.favorite.exception.FavoriteNotFoundException;
import nextstep.subway.member.application.MemberService;
import nextstep.subway.member.domain.Member;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private static final String ERROR_MESSAGE_NOT_OWNER = "즐겨찾기의 소유자만 삭제할 수 있습니다.";

    private final FavoriteRepository favoriteRepository;
    private final MemberService memberService;
    private final StationService stationService;

    public FavoriteService(FavoriteRepository favoriteRepository, MemberService memberService, StationService stationService) {
        this.favoriteRepository = favoriteRepository;
        this.memberService = memberService;
        this.stationService = stationService;
    }

    public FavoriteResponse createFavorite(Long memberId, FavoriteRequest favoriteRequest) {
        Member member = memberService.findMemberById(memberId);
        Station source = stationService.findStationById(favoriteRequest.getSource());
        Station target = stationService.findStationById(favoriteRequest.getTarget());
        Favorite favorite = favoriteRepository.save(new Favorite(member, source, target));
        return FavoriteResponse.of(favorite);
    }

    public List<FavoriteResponse> findMemberFavorites(Long memberId) {
        return favoriteRepository.findByMemberId(memberId).stream()
                .map(FavoriteResponse::of)
                .collect(Collectors.toList());
    }

    public void deleteFavorite(Long memberId, Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(FavoriteNotFoundException::new);
        if (!favorite.isOwner(memberId)) {
            throw new AuthorizationException(ERROR_MESSAGE_NOT_OWNER);
        }
        favoriteRepository.delete(favorite);
    }
}
