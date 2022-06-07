package nextstep.subway.station.exception;

import nextstep.subway.common.exception.ValidationException;

public class StationNotFoundException extends ValidationException {
    private static final long serialVersionUID = -4518827984697400376L;

    public StationNotFoundException() {
        super("지하철역을 찾을 수 없습니다.");
    }
}
