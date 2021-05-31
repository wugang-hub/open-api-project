package com.open.api.exception;

import java.text.MessageFormat;

/**
 * 基础异常
 * @author wugang
 */
public class BaseException extends RuntimeException {

    protected boolean success;
    protected String errorMsg;
    protected String errorCode;
    protected Object data;

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(Boolean success, String code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.success = success;
        this.errorCode = code;
        this.errorMsg = MessageFormat.format(msgFormat, args);
        this.data = null;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Boolean getSuccess(){
        return this.success;
    }

    public Object getData() {
        return this.data;
    }
}