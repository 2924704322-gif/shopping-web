package com.shoopping.model.product;

import com.shoopping.model.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品查询参数
 *
 * @author shopping-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQueryParam extends PageParam {

    /** 关键字（模糊匹配商品名称） */
    private String keyword;

    /** 分类ID筛选 */
    private Long categoryId;

    /** 最低价格 */
    private String priceMin;

    /** 最高价格 */
    private String priceMax;

    /** 上下架: 0=下架, 1=上架（null=查全部，公开接口强制为1） */
    private Integer status;

    /** 排序字段: price/sales（默认 createTime） */
    private String sortField;

    /** 是否升序（默认降序） */
    private boolean asc = false;
}
