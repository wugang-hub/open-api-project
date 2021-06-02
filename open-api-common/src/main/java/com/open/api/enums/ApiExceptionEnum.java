package com.open.api.enums;

/**
 * 异常枚举
 *
 * @author wugang
 */
public enum ApiExceptionEnum implements EnumInterface {

    /**
     * api异常枚举
     */
    SYSTEM_ERROR("SYSTEM_ERROR", "系统异常"),
    API_NOT_EXIST("API_NOT_EXIST", "API方法不存在"),
    INVALID_PUBLIC_PARAM("INVALID_PUBLIC_PARAM", "无效公共参数"),
    INVALID_REQUEST_ERROR("INVALID_REQUEST_ERROR", " 请求方式"),
    INVALID_PARAM("INVALID_PARAM", "无效参数"),
    INVALID_SIGN("INVALID_SIGN", "无效签名"),
    INVALID_IP("INVALID_IP", "无效IP地址"),
    RESULT_IS_NULL("RESULT_IS_NULL", "无数据返回"),
    APP_ID_IS_NULL("APP_ID_IS_NULL", "appId为空"),
    ;

    ApiExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;

    private String msg;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
