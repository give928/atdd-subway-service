package nextstep.subway.line.exception;

public class LineNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6014450390357783596L;

    public LineNotFoundException() {
        super("지하철노선을 찾을 수 없습니다.");
    }

    public LineNotFoundException(String message) {
        super(message);
    }
}
