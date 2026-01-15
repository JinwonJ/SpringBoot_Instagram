package org.clonestudy.instagram.global.error;


import lombok.Getter;

@Getter
public class InstaException extends RuntimeException {
    private final ErrorCode code;

    public InstaException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public static InstaException badRequest(String msg) { return new InstaException(ErrorCode.BAD_REQUEST, msg); }
    public static InstaException notFound(String msg) { return new InstaException(ErrorCode.NOT_FOUND, msg); }
    public static InstaException conflict(String msg) { return new InstaException(ErrorCode.CONFLICT, msg); }
    public static InstaException forbidden(String msg) { return new InstaException(ErrorCode.FORBIDDEN, msg); }
    public static InstaException unauthorized(String msg) { return new InstaException(ErrorCode.UNAUTHORIZED, msg); }
}