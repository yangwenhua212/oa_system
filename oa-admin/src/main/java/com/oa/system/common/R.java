package com.oa.system.common;

import lombok.Data;

/**
 * Common API Response
 */
@Data
public class R<T> {

    private Integer code;
    private String message;
    private T data;

    // ========== success 方法 ==========

    public static <T> R<T> ok() {
        return success();
    }

    public static <T> R<T> ok(T data) {
        return success(data);
    }

    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("Success");
        r.setData(null);
        return r;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("Success");
        r.setData(data);
        return r;
    }

    public static R<Void> successVoid() {
        return success();
    }

    public static <T> R<T> success(String message, T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    // ========== error 方法 ==========

    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.setCode(400);
        r.setMessage("Fail");
        return r;
    }

    public static <T> R<T> fail(String message) {
        R<T> r = new R<>();
        r.setCode(400);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> unauthorized() {
        R<T> r = new R<>();
        r.setCode(401);
        r.setMessage("Unauthorized");
        return r;
    }

    public static <T> R<T> unauthorized(String message) {
        R<T> r = new R<>();
        r.setCode(401);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> forbidden() {
        R<T> r = new R<>();
        r.setCode(403);
        r.setMessage("Forbidden");
        return r;
    }
}
