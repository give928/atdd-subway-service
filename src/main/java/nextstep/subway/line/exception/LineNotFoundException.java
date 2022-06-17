package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ValidationException;

public class LineNotFoundException extends ValidationException {
    private static final long serialVersionUID = 6014450390357783596L;

    public LineNotFoundException() {
        super("지하철노선을 찾을 수 없습니다.");
    }
}
