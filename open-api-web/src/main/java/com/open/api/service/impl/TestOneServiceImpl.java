package com.open.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.api.annotation.OpenApiService;
import com.open.api.dao.UserInfo;
import com.open.api.mapper.UserInfoMapper;
import com.open.api.model.Test1BO;
import com.open.api.model.Test2BO;
import com.open.api.service.TestOneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试开放接口1
 * <p>
 * 注解@OpenApiService > 开放接口自定义注解，用于启动时扫描接口
 */
@Service
@OpenApiService
public class TestOneServiceImpl implements TestOneService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 方法1
     */
    @Override
    public Object testMethod1(String requestId, Test1BO test1BO) throws JsonProcessingException {
        String userName = test1BO.getUsername();
        UserInfo userInfo = userInfoMapper.getUserInfoByName(userName);
        String json = new ObjectMapper().writeValueAsString(userInfo);
        return JSON.parse(json);
    }

    @Override
    public Object testMethod2(String requestId, Test2BO test2BO) throws JsonProcessingException {
        List<Test2BO.Item> lists =  test2BO.getItemList();
        //数组转json字符串
        String json = JSON.toJSONString(lists, SerializerFeature.WriteDateUseDateFormat );
        return JSON.parse(json);
    }

}