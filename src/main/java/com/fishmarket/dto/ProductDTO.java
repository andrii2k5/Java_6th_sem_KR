package com.fishmarket.dto;

import com.fishmarket.entity.Product.ProductType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductType type;
    private String imageUrl;
    private Integer stock;
}