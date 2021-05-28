package com.open.api.annotation;

import java.lang.annotation.*;

/**
 * 开放接口实现类注解
 *
 * @author wugang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenApiService {
}
