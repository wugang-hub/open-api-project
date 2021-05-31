package com.open.api.exception;


import com.open.api.enums.EnumInterface;

/**
 * 业务-统一异常
 *
 * @author wugang
 */
public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(false, code, message);
    }

    public BusinessException(EnumInterface enums, Object... args) {
        super(false, enums.getCode(), enums.getMsg(), args);
    }

}