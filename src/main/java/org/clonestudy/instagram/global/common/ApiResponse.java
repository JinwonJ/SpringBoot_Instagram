package org.clonestudy.instagram.global.common;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorBody error
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null);
    }
    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorBody(code, message));
    }

    public record ErrorBody(String code, String message) {}
}
//public record ApiResponse<T>(
//        boolean success, T data, ErrorBody error
//
//) {
//    public static <T> ApiResponse<T> ok(T data){
//        return new ApiResponse<>(true, data, null);
//    }
//    public static ApiResponse<Void> ok(){
//        return new ApiResponse<>(true, null, null);
//    }
//    public static ApiResponse<Void> fail(String conde, String message){
//        return new ApiResponse<>(false, null, new ErrorBdoy(code, message));
//    }
//    public record ErrorBdoy(String code, string message) {}
//
//}