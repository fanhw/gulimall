package fhw.gulimall.common.exception;

import lombok.Getter;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-19 21:46
 */


public enum BizCodeEnum {
    UNKONW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架错误");

    @Getter
    private int code;
    @Getter
    private String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;

    }
}
