package com.shoopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoopping.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper 接口
 *
 * @author shopping-team
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
