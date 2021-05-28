package com.open.api.web.controller;

import com.alibaba.fastjson.JSON;
import com.open.api.config.gateway.ApiClient;
import com.open.api.model.ResultModel;
import com.open.api.util.SystemClock;
import com.open.api.web.bo.RequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一网关
 */
@RestController
@RequestMapping("/open")
public class OpenApiController {

    @Autowired
    private ApiClient apiClient;

    /**
     * 统一网关入口
     * @author wugang
     */
    /**
     请求body示例， 请求格式 JSON
     {
     "app_id":"101",
     "method":"open.api.test.one.method1",
     "version":"1.0",
     "api_request_id":"1111",
     "charset":"utf-8",
     "sign_type":"RSA2",
     "sign":"bpy8NrCnRq8lK9wVWI0EHNJVUgEpJFWlwOIIvMOUYfflQsvRzw8gCFNLpT/0pTOHeHwB/Cn8tMmXCzv9fnm8cUXlAAofsRYOneVseGt+ArgtsXGitjACA0L3Krbn2SsG+xEL/VbMGW2UwyLHx8FNz88ZzbORSKaERPC7tL3MWpQ=",
     "content":"{'username':'zhangsan','password':'123'}"
     }
     */
    @PostMapping("/gateway")
    public ResultModel gateway(@RequestBody RequestModel req) throws Throwable {

        //封装参数map
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("app_id", req.getAppId());
        params.put("method", req.getMethod());
        params.put("version", req.getVersion());
        params.put("api_request_id", req.getApiRequestId());
        params.put("charset", req.getCharset());
        params.put("sign_type", req.getSignType());
        params.put("sign", req.getSign());
        params.put("content", JSON.parse(req.getContent()).toString());

        //验签
        apiClient.checkSign(params, req.getApiRequestId(), req.getCharset(), req.getSignType());
        //请求接口
        ResultModel result = apiClient.invoke(req.getMethod(), req.getApiRequestId(), JSON.parse(req.getContent()).toString());

        return result;
    }
}
