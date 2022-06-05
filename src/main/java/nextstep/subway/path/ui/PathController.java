package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/paths")
public class PathController {
    @GetMapping
    public ResponseEntity<PathResponse> findPath(@RequestParam("source") Long source,
                                                 @RequestParam("target") Long target) {
        return ResponseEntity.ok().body(new PathResponse(Collections.emptyList(), 10));
    }
}
