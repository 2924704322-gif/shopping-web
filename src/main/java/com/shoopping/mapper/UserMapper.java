package com.shoopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoopping.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 *
 * @author shopping-team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
