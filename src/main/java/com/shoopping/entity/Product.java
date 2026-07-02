package com.shoopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 - 对应 product 表
 * 金额使用 BigDecimal 精确存储
 *
 * @author shopping-team
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    private String name;

    /** 商品描述/详情 */
    private String description;

    /** 所属分类ID（逻辑外键 → category.id） */
    private Long categoryId;

    /** 售价 */
    private BigDecimal price;

    /** 原价（划线价，可为空） */
    private BigDecimal originalPrice;

    /** 库存数量 */
    private Integer stock;

    /** 商品主图URL */
    private String mainImage;

    /** 副图URL列表（JSON数组: ["url1","url2"]） */
    private String subImages;

    /** 计量单位 */
    private String unit;

    /** 上下架状态: 0=下架, 1=上架 */
    private Integer status;

    /** 累计销量 */
    private Integer sales;

    /** 排序号 */
    private Integer sort;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0=正常, 1=已删除 */
    @TableLogic
    private Integer deleted;
}
