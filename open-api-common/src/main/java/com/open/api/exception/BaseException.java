package com.open.api.exception;

import java.text.MessageFormat;

/**
 * 基础异常
 * @author wugang
 */
public class BaseException extends RuntimeException {

    protected boolean status;
    protected String errorMsg;
    protected String errorCode;

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(Boolean status, String code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.status = status;
        this.errorCode = code;
        this.errorMsg = MessageFormat.format(msgFormat, args);
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

}