package nextstep.subway.favorite.application;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MemberService memberService;
    private final StationService stationService;

    public FavoriteService(FavoriteRepository favoriteRepository, MemberService memberService, StationService stationService) {
        this.favoriteRepository = favoriteRepository;
        this.memberService = memberService;
        this.stationService = stationService;
    }

    @Transactional
    public FavoriteResponse createFavorite(Long memberId, FavoriteRequest favoriteRequest) {
        Member member = memberService.findMemberById(memberId);
        List<Station> stations = stationService.findStationsByIdIn(Arrays.asList(favoriteRequest.getSource(), favoriteRequest.getTarget()));
        Station sourceStation = stations.get(0);
        Station targetStation = stations.get(1);
        Favorite favorite = favoriteRepository.save(new Favorite(member, sourceStation, targetStation));
        return FavoriteResponse.of(favorite);
    }

    public List<FavoriteResponse> findMemberFavorites(Long memberId) {
        return favoriteRepository.findByMemberId(memberId).stream()
                .map(FavoriteResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFavorite(Long id, Long memberId) {
        Favorite favorite = favoriteRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(FavoriteNotFoundException::new);
        favoriteRepository.delete(favorite);
    }
}
