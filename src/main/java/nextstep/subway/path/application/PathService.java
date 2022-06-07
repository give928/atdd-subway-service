package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PathService {
    private static final String ERROR_MESSAGE_INVALID_SOURCE_STATION = "출발역을 입력해주세요.";
    private static final String ERROR_MESSAGE_INVALID_TARGET_STATION = "도착역을 입력해주세요.";
    private static final String ERROR_MESSAGE_SOURCE_TARGET_SAME_STATION = "출발역과 도착역을 같은 역으로 경로를 검색할 수 없습니다.";

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findPath(Long source, Long target) {
        validateStations(source, target);
        Station sourceStation = stationService.findStationById(source);
        Station targetStation = stationService.findStationById(target);
        List<Line> lines = lineService.findAll();
        return PathResponse.of(PathFinder.find(lines, sourceStation, targetStation));
    }

    private void validateStations(Long source, Long target) {
        source = Optional.ofNullable(source)
                .filter(l -> l > 0L)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_INVALID_SOURCE_STATION));
        target = Optional.ofNullable(target)
                .filter(l -> l > 0L)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_INVALID_TARGET_STATION));
        if (source.compareTo(target) == 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE_SOURCE_TARGET_SAME_STATION);
        }
    }
}
