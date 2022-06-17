package nextstep.subway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"nextstep.subway"})
public class ApiExceptionAdvice {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        if (StringUtils.hasText(e.getMessage())) {
            log.error("handleIllegalArgumentException", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return handleException(e);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        if (StringUtils.hasText(e.getMessage())) {
            log.error("handleIllegalStateException", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return handleException(e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.error("handleException", e);
        return ResponseEntity.internalServerError().build();
    }
}
