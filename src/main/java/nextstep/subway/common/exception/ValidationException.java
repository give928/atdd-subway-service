package nextstep.subway.common.exception;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = -6579042878080503835L;

    public ValidationException(String message) {
        super(message);
    }
}
