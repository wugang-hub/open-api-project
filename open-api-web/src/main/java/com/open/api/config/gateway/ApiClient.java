package com.open.api.config.gateway;

import com.alipay.api.AlipayApiException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.open.api.config.context.ApplicationContextHelper;
import com.open.api.config.property.ApplicationProperty;
import com.open.api.enums.ApiExceptionEnum;
import com.open.api.exception.BusinessException;
import com.open.api.model.ApiModel;
import com.open.api.model.ResultModel;
import com.open.api.util.Base64Util;
import com.open.api.util.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static com.alipay.api.AlipayConstants.SIGN_ALGORITHMS;
import static com.alipay.api.AlipayConstants.SIGN_SHA256RSA_ALGORITHMS;

/**
 * Api请求客户端
 *
 * @author wugang
 */
@Service
public class ApiClient {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiClient.class);

    /**
     * jackson 序列化工具类
     */
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * Api本地容器
     */
    private final ApiContainer apiContainer;

    public ApiClient(ApiContainer apiContainer) {
        this.apiContainer = apiContainer;
    }

    @Resource
    private ApplicationProperty applicationProperty;

    /**
     * ip校验
     * @param request 请求参数request
     */
    public void checkIpAddr(HttpServletRequest request, String requestRandomId, String ip){
        LOGGER.info(requestRandomId + ">>>>>>>>ip校验开始");
        String ipAddr = getIpAddr(request);
        if(StringUtils.isEmpty(ipAddr) || !ipAddr.equals(ip)){
            LOGGER.info(requestRandomId + ">>>>>>>>ip校验失败");
            throw new BusinessException(ApiExceptionEnum.INVALID_IP);
        }
        LOGGER.info(requestRandomId + ">>>>>>>>ip校验成功");
    }

    /**
     * 验签
     *
     * @param params          请求参数
     * @param requestRandomId 请求随机标识（用于日志中分辨是否是同一次请求）
     * @param charset         请求编码
     * @param signType        签名格式
     */
    public void checkSign(String privateKey, String publicKey, Map<String, String> map, String requestRandomId, String charset, String signType) {
        try {
            //校验签名开关
            if (!applicationProperty.getIsCheckSign()) {
                return;
            }

            LOGGER.warn(requestRandomId + ">>>>>>>>开始验签");
            boolean checkSign = rsaCheckV1(map, publicKey, privateKey, charset, signType);
            if (!checkSign) {
                LOGGER.info(requestRandomId + ">>>>>>>>验签失败");
                throw new BusinessException(ApiExceptionEnum.INVALID_SIGN.getCode(), ApiExceptionEnum.INVALID_SIGN.getMsg());
            }
            LOGGER.warn(requestRandomId + ">>>>>>>>验签成功");
        } catch (BusinessException e) {
            LOGGER.error(requestRandomId + ">>>>>>>>验签异常");
            throw new BusinessException(e.getErrorCode(), e.getErrorMsg());
        }
    }

    private static boolean rsaCheckV1(Map<String, String> params, String publicKey, String privateKey, String charset, String signType) throws BusinessException {
        String sign = (String)params.get("sign");
        String content = getSignCheckContentV1(params);
        String instanceTag = SIGN_ALGORITHMS;

        //生成签名，自测用的，后面是通过第三方接口传过来
        sign = sign(content, privateKey, charset, instanceTag);

        if ("RSA".equals(signType)) {
            instanceTag = SIGN_ALGORITHMS;
        } else if ("RSA2".equals(signType)) {
            instanceTag = SIGN_SHA256RSA_ALGORITHMS;
        } else {
            throw new BusinessException(ApiExceptionEnum.NOT_SUPPORT_SIGN_TYPE);
        }

        return doCheck(content, sign, publicKey, charset, instanceTag);
    }

    /**
     * @param content:签名的参数内容
     * @param privateKey：私钥
     * @return
     */
    public static String sign(String content, String privateKey, String charset, String instanceTag) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Util.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance(instanceTag);

            signature.initSign(priKey);
            signature.update(content.getBytes(charset));

            byte[] signed = signature.sign();

            return Base64Util.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content：验证参数的内容
     * @param sign：签名
     * @param publicKey：公钥
     * @return
     */
    public static boolean doCheck(String content, String sign, String publicKey, String charset, String instanceTag) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64Util.decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            Signature signature = Signature.getInstance(instanceTag);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));

            boolean bverify = signature.verify(Base64Util.decode(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getSignCheckContentV1(Map<String, String> params) {
        if (params == null) {
            return null;
        } else {
            params.remove("sign");
            params.remove("sign_type");
            StringBuilder content = new StringBuilder();
            List<String> keys = new ArrayList(params.keySet());
            Collections.sort(keys);
            for(int i = 0; i < keys.size(); ++i) {
                String key = (String)keys.get(i);
                String value = (String)params.get(key);
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
            return content.toString();
        }
    }

    /**
     * Api调用方法
     *
     * @param method          请求方法
     * @param requestRandomId 请求随机标识
     * @param content         请求体
     * @author wugang
     */
    public ResultModel invoke(String method, String requestRandomId, String content) throws Throwable {
        //获取api方法
        ApiModel apiModel = apiContainer.get(method);
        if (null == apiModel) {
            LOGGER.info(requestRandomId + ">>>>>>>>API方法不存在");
            throw new BusinessException(ApiExceptionEnum.API_NOT_EXIST);
        }
        //获得spring bean
        Object bean = ApplicationContextHelper.getBean(apiModel.getBeanName());
        if (null == bean) {
            LOGGER.warn(requestRandomId + ">>>>>>>>API方法不存在");
            throw new BusinessException(ApiExceptionEnum.API_NOT_EXIST);
        }
        //处理业务参数
        // 忽略JSON字符串中存在，而在Java中不存在的属性
        JSON_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置下划线序列化方式
        JSON_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        Object param = JSON_MAPPER.readValue(StringUtils.isBlank(content) ? "{}" : content, Class.forName(apiModel.getParamName()));
        //校验参数
        ValidateUtils.validate(param);
        //执行对应方法
        try {
            Object obj = apiModel.getMethod().invoke(bean, requestRandomId, param);
            if(null == obj){
                throw new BusinessException(ApiExceptionEnum.RESULT_IS_NULL);
            }
            return ResultModel.success(obj);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                throw ((InvocationTargetException) e).getTargetException();
            }
            throw new BusinessException(ApiExceptionEnum.SYSTEM_ERROR);
        }
    }

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            if(ip.contains("../")||ip.contains("..\\")){
                return "";
            }
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                ip= ip.substring(0, index);
            }
            if(ip.contains("../")||ip.contains("..\\")){
                return "";
            }
            return ip;
        } else {
            ip=request.getRemoteAddr();
            if(ip.contains("../")||ip.contains("..\\")){
                return "";
            }
            if(ip.equals("0:0:0:0:0:0:0:1")){
                ip="127.0.0.1";
            }
            return ip;
        }
    }
}
