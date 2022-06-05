package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PathService {
    private static final String ERROR_MESSAGE_INVALID_SOURCE_STATION = "출발역을 입력해주세요.";
    private static final String ERROR_MESSAGE_INVALID_TARGET_STATION = "도착역을 입력해주세요.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_SOURCE_STATION = "지하철역이 존재하지 않습니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_TARGET_STATION = "지하철역이 존재하지 않습니다.";
    private static final String ERROR_MESSAGE_SOURCE_TARGET_SAME_STATION = "출발역과 도착역을 같은 역으로 경로를 검색할 수 없습니다.";

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        validateStations(source, target);
        Station sourceStation = stationRepository.findById(source)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_SOURCE_STATION));
        Station targetStation = stationRepository.findById(target)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_TARGET_STATION));
        List<Line> lines = lineRepository.findAll();
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
