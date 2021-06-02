package com.open.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;

/**
 * api接口对象
 */
@Data
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestModel implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *业务方appId
     */
    private String appId;

    /**
     *请求方法
     */
    private String method;

    /**
     *版本
     */
    private String version;

    /**
     *请求标识（用于日志中分辨是否是同一次请求）
     */
    private String apiRequestId;

    /**
     *请求编码 默认：UTF-8
     */
    private String charset;

    /**
     *签名类型：RSA或RSA2
     */
    private String signType;

    /**
     *签名
     */
    private String sign;

    /**
     *业务内容参数 json 格式字符串
     */
    private String content;

}
