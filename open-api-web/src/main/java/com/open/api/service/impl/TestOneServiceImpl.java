package com.open.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.open.api.annotation.OpenApiService;
import com.open.api.model.Test1BO;
import com.open.api.service.TestOneService;
import org.springframework.stereotype.Service;

/**
 * 测试开放接口1
 * <p>
 * 注解@OpenApiService > 开放接口自定义注解，用于启动时扫描接口
 */
@Service
@OpenApiService
public class TestOneServiceImpl implements TestOneService {

    /**
     * 方法1
     */
    @Override
    public Object testMethod1(String requestId, Test1BO test1BO) {
        return JSON.toJSONString(test1BO);
    }
}