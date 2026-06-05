package com.fishmarket.service;

import com.fishmarket.dto.OrderDTO;
import com.fishmarket.dto.ProductDTO;
import com.fishmarket.entity.Order;
import com.fishmarket.entity.OrderItem;
import com.fishmarket.entity.Product;
import com.fishmarket.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class MapperUtil {

    // Перетворення Product -> ProductDTO
    public ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setType(product.getType());
        dto.setImageUrl(product.getImageUrl());
        dto.setStock(product.getStock());
        return dto;
    }

    // Перетворення OrderDTO -> Order (без позицій і без суми)
    public Order toOrderEntity(OrderDTO orderDTO, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setStatus(Order.OrderStatus.NEW);
        order.setTotalPrice(BigDecimal.ZERO); // тимчасово, потім перерахуємо в сервісі
        return order;
    }

    // Створення OrderItem з Product та кількості
    public OrderItem toOrderItemEntity(Order order, Product product, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice()); // фіксуємо ціну на момент замовлення
        return item;
    }
}