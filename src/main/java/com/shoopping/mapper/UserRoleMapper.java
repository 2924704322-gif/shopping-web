package com.shoopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoopping.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper 接口
 *
 * @author shopping-team
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询所有角色编码（JOIN role 表，过滤已禁用和已删除角色）
     *
     * @param userId 用户ID
     * @return 角色编码列表（如 ["ROLE_ADMIN", "ROLE_USER"]）
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
