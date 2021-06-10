package com.open.api.service;


import com.open.api.annotation.OpenApi;
import com.open.api.model.Test1BO;
import com.open.api.model.Test2BO;

public interface TestOneService{

    /**
     * 方法1
     */
    @OpenApi(method = "open.api.test.one.method1", desc = "接口1,方法1")
    Object testMethod1(String requestId, Test1BO test1BO);

    /**
     * 方法2
     */
    @OpenApi(method = "open.api.test.one.method2", desc = "方法2")
    Object testMethod2(String requestId, Test2BO test2BO);

}