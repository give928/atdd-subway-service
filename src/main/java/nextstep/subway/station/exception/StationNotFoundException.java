package nextstep.subway.station.exception;

public class StationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4518827984697400376L;

    public StationNotFoundException() {
        super("지하철역을 찾을 수 없습니다.");
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
