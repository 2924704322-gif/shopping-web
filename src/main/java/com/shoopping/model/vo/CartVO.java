package com.shoopping.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartVO {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String mainImage;
    private Integer quantity;
    private Integer checked;
    private LocalDateTime createTime;
}
