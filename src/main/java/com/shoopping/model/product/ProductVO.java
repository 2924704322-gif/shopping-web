package com.shoopping.model.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品列表视图对象
 *
 * @author shopping-team
 */
@Data
public class ProductVO {

    private Long id;
    private String name;
    private Long categoryId;
    /** 分类名称（JOIN 查询填充） */
    private String categoryName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String mainImage;
    private String unit;
    /** 上下架: 0=下架, 1=上架 */
    private Integer status;
    /** 累计销量 */
    private Integer sales;
    private Integer sort;
    private LocalDateTime createTime;
}
