package com.fishmarket.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderDTO {
    @NotBlank(message = "Адреса обов'язкова")
    @Size(min = 5, message = "Адреса занадто коротка")
    private String shippingAddress;

    @NotNull(message = "Кошик не може бути порожнім")
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
    }
}