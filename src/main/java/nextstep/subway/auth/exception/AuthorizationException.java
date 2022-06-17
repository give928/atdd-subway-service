package nextstep.subway.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -5104764513921174385L;

    public AuthorizationException() {
    }

    public AuthorizationException(String message) {
        super(message);
    }
}
