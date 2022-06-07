package nextstep.subway.favorite.ui;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    @PostMapping
    public ResponseEntity<Void> createFavorite(@AuthenticationPrincipal LoginMember loginMember,
                                               @RequestBody FavoriteRequest favoriteRequest) {
        return ResponseEntity.created(URI.create("/favorites/1")).build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> findMemberFavorites(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity.ok().body(Collections.singletonList(
                new FavoriteResponse(1L, new StationResponse(1L, "교대역", LocalDateTime.now(), LocalDateTime.now()),
                                     new StationResponse(2L, "강남역", LocalDateTime.now(), LocalDateTime.now()))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal LoginMember loginMember,
                                               @PathVariable("id") Long id) {
        return ResponseEntity.noContent().build();
    }
}
