package com.open.api.service;


import com.open.api.annotation.OpenApi;
import com.open.api.model.Test1BO;

public interface TestOneService{

    /**
     * 方法1
     */
    @OpenApi(method = "open.api.test.one.method1", desc = "接口1,方法1")
    Object testMethod1(String requestId, Test1BO test1BO);

}