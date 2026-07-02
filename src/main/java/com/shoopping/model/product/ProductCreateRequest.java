package com.shoopping.model.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建请求
 *
 * @author shopping-team
 */
@Data
public class ProductCreateRequest {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    /** 商品描述 */
    private String description;

    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /** 原价（划线价） */
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock = 0;

    /** 主图URL */
    private String mainImage;

    /** 副图URL列表（存储时序列化为 JSON） */
    private List<String> subImages;

    /** 计量单位 */
    private String unit = "件";

    /** 上下架: 0=下架, 1=上架 */
    private Integer status = 0;

    /** 排序号 */
    private Integer sort = 0;
}
