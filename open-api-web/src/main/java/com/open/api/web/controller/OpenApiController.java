package com.open.api.web.controller;

import com.alibaba.fastjson.JSON;
import com.open.api.config.gateway.ApiClient;
import com.open.api.model.ResultModel;
import com.open.api.util.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 统一网关
 */
@RestController
@RequestMapping("/open")
public class OpenApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiController.class);

    @Autowired
    private ApiClient apiClient;

    /**
     * 设计思路：
     * 1、每个第三方生成一对公私钥，绑定appId 存入数据库
     * 2、将appId和私钥传给第三方
     * 3、第三方根据appId和私钥生成自己的sign
     * 4、再结合其他参数一起放入接口传过来
     * 5、我们拿到参数根据appId查询数据库获取对应的公钥去验签
     * 注意：签名类型signType  和  签名sign  要保持一致，否则验签不过
     */
    /**
     * 统一网关入口
     *
     * @param appId        业务方ID
     * @param method       请求方法
     * @param version      版本（一般为 1.0）
     * @param apiRequestId 请求标识（用于日志中分辨是否是同一次请求）
     * @param charset      请求编码 默认：UTF-8
     * @param signType     签名类型：RSA或RSA2
     * @param sign         签名
     * @param content      业务内容参数 json 格式字符串
     * @author wugang
     */
    @PostMapping("/gateway")
    public ResultModel gateway(@RequestParam(value = "app_id", required = true) String appId,
                               @RequestParam(value = "method", required = true) String method,
                               @RequestParam(value = "version", required = true) String version,
                               @RequestParam(value = "api_request_id", required = true) String apiRequestId,
                               @RequestParam(value = "charset", required = true) String charset,
                               @RequestParam(value = "sign_type", required = true) String signType,
                               @RequestParam(value = "sign", required = true) String sign,
                               @RequestParam(value = "content", required = true) String content,
                               HttpServletRequest request) throws Throwable {

        Map<String, Object> params = WebUtils.getParametersStartingWith(request, "");
        LOGGER.info("【{}】>> 网关执行开始 >> method={} params = {}", apiRequestId, method, JSON.toJSONString(params));
        long start = SystemClock.millisClock().now();

        //验签
        apiClient.checkSign(params, apiRequestId, charset, signType);

        //请求接口
        ResultModel result = apiClient.invoke(method, apiRequestId, content);

        LOGGER.info("【{}】>> 网关执行结束 >> method={},result = {}, times = {} ms",
                apiRequestId, method, JSON.toJSONString(result), (SystemClock.millisClock().now() - start));

        return result;
    }
}
