package com.shoopping.model.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品详情视图（含描述和副图列表）
 *
 * @author shopping-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetailVO extends ProductVO {

    /** 商品描述 */
    private String description;

    /** 副图URL列表（从 JSON 反序列化） */
    private List<String> subImages;
}
