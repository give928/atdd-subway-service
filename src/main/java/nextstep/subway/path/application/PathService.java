package nextstep.subway.path.application;

import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.Fare;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

    public PathResponse findPath(Long source, Long target, LoginMember loginMember) {
        Path path = findPath(source, target);
        Fare fare = Fare.of(loginMember.getAge(), path.getDistance(), path.getMaxExtraFare());
        return PathResponse.of(path, fare);
    }

    private Path findPath(Long source, Long target) {
        validateStations(source, target);
        List<Line> lines = lineService.findAll();
        List<Station> stations = stationService.findStationsByIdIn(Arrays.asList(source, target));
        Station sourceStation = stations.get(0);
        Station targetStation = stations.get(1);
        return PathFinder.find(lines, sourceStation, targetStation);
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
