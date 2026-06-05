package com.fishmarket.controller;

import com.fishmarket.dto.OrderDTO;
import com.fishmarket.entity.Order;
import com.fishmarket.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO,
                                             Authentication authentication) {
        // Отримуємо логін авторизованого користувача
        String currentUserName = authentication.getName();
        // Викликаємо сервіс для створення замовлення
        Order order = orderService.createOrder(orderDTO, currentUserName);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
