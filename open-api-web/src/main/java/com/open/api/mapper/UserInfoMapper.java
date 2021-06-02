package com.open.api.mapper;

import com.open.api.dao.UserInfo;
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
public interface UserInfoMapper {
    UserInfo getUserInfoByName(@Param("userName") String userName);
}
