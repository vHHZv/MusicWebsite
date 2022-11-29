package music.website_backend.controller.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultUtil<T> {
    private String code;
    private String msg;
    private T data;

    public ResultUtil(String code) {
        this.code = code;
    }

    public ResultUtil(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultUtil(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public static ResultUtil<?> success() {
        return new ResultUtil<>("1");
    }

    public static ResultUtil<?> success(String msg) {
        return new ResultUtil<>("1", msg);
    }

    public static <T> ResultUtil<?> success(T data) {
        return new ResultUtil<>("1", data);
    }

    public static <T> ResultUtil<?> success(String msg, T data) {
        return new ResultUtil<>("1", msg, data);
    }

    public static ResultUtil<?> error() {
        return new ResultUtil<>("0");
    }

    public static ResultUtil<?> error(String msg) {
        return new ResultUtil<>("0", msg);
    }
}
