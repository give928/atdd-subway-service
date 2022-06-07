package nextstep.subway;

import nextstep.subway.auth.exception.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice(basePackages = {"nextstep.subway"})
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class, EntityNotFoundException.class})
    public ResponseEntity<String> handleValidationException(RuntimeException e) {
        if (StringUtils.hasText(e.getMessage())) {
            log.error("handleValidationException", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return handleException(e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<String> handleAuthorizationException(AuthorizationException e) {
        log.error("handleAuthorizationException", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.error("handleException", e);
        return ResponseEntity.internalServerError().build();
    }
}
