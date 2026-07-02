package com.shoopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoopping.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 *
 * @author shopping-team
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
