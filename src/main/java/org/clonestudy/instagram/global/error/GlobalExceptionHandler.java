package org.clonestudy.instagram.global.error;

import lombok.extern.slf4j.Slf4j;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외(InstaException)
     */
    @ExceptionHandler(InstaException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsta(InstaException e) {
        HttpStatus status = switch (e.getCode()) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        // ✅ 서버 로그에 원인 기록
        log.warn("InstaException: code={}, message={}", e.getCode(), e.getMessage());

        return ResponseEntity.status(status)
                .body(ApiResponse.fail(e.getCode().name(), e.getMessage()));
    }

    /**
     * DTO Validation 실패(@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", msg);

        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("BAD_REQUEST", msg));
    }

    /**
     * DB 유니크 제약/무결성 위반 (이메일 중복 등)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException", e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail("CONFLICT", "이미 존재하는 값입니다. (email/username 중복 가능)"));
    }

    /**
     * ✅ 나머지 모든 예외: INTERNAL_ERROR
     * 개발 단계에서는 message를 내려서 원인 파악을 쉽게 함
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handle(Exception e) {
        log.error("Unhandled exception", e);

        // 개발 단계: 원인 메시지를 내려서 바로 잡기 쉽게
        String detail = (e.getMessage() == null || e.getMessage().isBlank())
                ? e.getClass().getSimpleName()
                : e.getClass().getSimpleName() + ": " + e.getMessage();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("INTERNAL_ERROR", detail));
    }
}
