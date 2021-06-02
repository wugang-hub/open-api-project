package com.open.api.mapper;

import com.open.api.dao.UserSecret;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wugang
 * @since 2021-06-02
 */
@Service
public interface UserSecretMapper{
    UserSecret getInfoByAppId(@Param("appId") String appId);
}
