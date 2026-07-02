package com.shoopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoopping.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类 Mapper 接口
 *
 * @author shopping-team
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
