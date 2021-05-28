package com.open.api.config.gateway;


import com.open.api.annotation.OpenApi;
import com.open.api.annotation.OpenApiService;
import com.open.api.config.context.ApplicationContextHelper;
import com.open.api.model.ApiModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Api接口扫描器，添加到容器中
 *
 * @author wugang
 */
@Component
public class ApiScanner implements CommandLineRunner {
    /**
     * 方法签名拆分正则
     */
    private static final Pattern PATTERN = Pattern.compile("\\s+(.*)\\s+((.*)\\.(.*))\\((.*)\\)", Pattern.DOTALL);

    /**
     * 参数分隔符
     */
    private static final String PARAMS_SEPARATOR = ",";

    /**
     * 统计扫描次数
     */
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Resource
    private ApiContainer apiContainer;

    @Override
    public void run(String... var1) throws Exception {
        //扫描所有使用@OpenApiService注解的类
        Map<String, Object> openApiServiceBeanMap = ApplicationContextHelper.getBeansWithAnnotation(OpenApiService.class);

        if (null == openApiServiceBeanMap || openApiServiceBeanMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> map : openApiServiceBeanMap.entrySet()) {
            //获取扫描类下所有方法
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(map.getValue().getClass());
            for (Method method : methods) {
                atomicInteger.incrementAndGet();
                //找到带有OpenApi 注解的方法
                OpenApi openApi = AnnotationUtils.findAnnotation(method, OpenApi.class);
                if (null == openApi) {
                    continue;
                }
                //获取业务参数对象
                String paramName = getParamName(method);
                if (StringUtils.isBlank(paramName)) {
                    continue;
                }

                //组建ApiModel- 放入api容器
                apiContainer.put(openApi.method(), new ApiModel(map.getKey(), method, paramName));
            }
        }
    }

    /**
     * 获取业务参数对象
     *
     * @param method
     * @return
     */
    private String getParamName(Method method) {
        ArrayList<String> result = new ArrayList<>();
        final Matcher matcher = PATTERN.matcher(method.toGenericString());
        if (matcher.find()) {
            int groupCount = matcher.groupCount() + 1;
            for (int i = 0; i < groupCount; i++) {
                result.add(matcher.group(i));
            }
        }

        //获取参数部分
        if (result.size() >= 6) {
            String[] params =
                    StringUtils.splitByWholeSeparatorPreserveAllTokens(result.get(5), PARAMS_SEPARATOR);
            if (params.length >= 2) {
                return params[1];
            }
        }
        return null;

    }

}
