package com.open.api.controller;

import com.alibaba.fastjson.JSON;
import com.open.api.config.gateway.ApiClient;
import com.open.api.dao.UserSecret;
import com.open.api.enums.ApiExceptionEnum;
import com.open.api.mapper.UserInfoMapper;
import com.open.api.mapper.UserSecretMapper;
import com.open.api.model.RequestModel;
import com.open.api.model.ResultModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一校验入口
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
 "content":"{'username':'admin','password':'admin'}" //如果为数组"{'item_list':[{'username':'admin','password':'123'}]}"
 }
 */
@RestController
@RequestMapping("/open")
public class OpenApiController {

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private UserSecretMapper userSecretMapper;

    @PostMapping("/gateway")
    public ResultModel gateway(@RequestBody RequestModel req, HttpServletRequest request) throws Throwable {
        ResultModel result = null;
        //判断参数是否为空
        if(StringUtils.isEmpty(req.getAppId()) || StringUtils.isEmpty(req.getMethod()) ||
                StringUtils.isEmpty(req.getVersion()) || StringUtils.isEmpty(req.getApiRequestId()) ||
                StringUtils.isEmpty(req.getCharset()) || StringUtils.isEmpty(req.getSignType()) ||
                StringUtils.isEmpty(req.getSign()) || StringUtils.isEmpty(req.getContent())){
            return ResultModel.error(ApiExceptionEnum.INVALID_PUBLIC_PARAM.getCode(), ApiExceptionEnum.INVALID_PUBLIC_PARAM.getMsg());
        }

        //封装参数map
        Map<String, String> params = new HashMap<String, String>();
        params.put("app_id", req.getAppId());
        params.put("method", req.getMethod());
        params.put("version", req.getVersion());
        params.put("api_request_id", req.getApiRequestId());
        params.put("charset", req.getCharset());
        params.put("sign_type", req.getSignType());
        params.put("sign", req.getSign());
        params.put("content", JSON.parse(req.getContent()).toString());

        //通过appId获取对用的公私钥
        UserSecret userSecret = userSecretMapper.getInfoByAppId(req.getAppId());
        if(userSecret == null){
            return ResultModel.error(ApiExceptionEnum.APP_ID_IS_NULL.getCode(), ApiExceptionEnum.APP_ID_IS_NULL.getMsg());
        }
        //ip校验
        apiClient.checkIpAddr(request, req.getApiRequestId(), userSecret.getIp());
        //验签
        apiClient.checkSign(userSecret.getPrivateKey(), userSecret.getPublicKey(), params, req.getApiRequestId(), req.getCharset(), req.getSignType());
        //请求接口
        result = apiClient.invoke(req.getMethod(), req.getApiRequestId(), JSON.parse(req.getContent()).toString());

        return result;
    }
}
