package com.fishmarket.service;

import com.fishmarket.dto.OrderDTO;
import com.fishmarket.dto.OrderDTO.OrderItemDTO;
import com.fishmarket.entity.Order;
import com.fishmarket.entity.OrderItem;
import com.fishmarket.entity.Product;
import com.fishmarket.entity.User;
import com.fishmarket.repository.OrderRepository;
import com.fishmarket.repository.ProductRepository;
import com.fishmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MapperUtil mapper;

    @Transactional
    public Order createOrder(OrderDTO orderDTO, String username) {
        // 1. Створюємо тимчасового користувача для замовлення
        //    (базу даних ми поки не чіпаємо)
        User user = new User();
        user.setEmail(username + "@temp.local"); // Простий email для логу
        user.setName(username);

        // 2. Перевіряємо, щоб кількість кожного товару не перевищувала 3
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            if (itemDTO.getQuantity() > 3) {
                throw new IllegalArgumentException("Не можна замовити більше 3 одиниць одного товару");
            }
        }

        // 3. Створюємо пусте замовлення
        Order order = mapper.toOrderEntity(orderDTO, user);
        BigDecimal total = BigDecimal.ZERO;

        // 4. Додаємо позиції та обчислюємо суму
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Товар з id " + itemDTO.getProductId() + " не знайдено"));
            OrderItem item = mapper.toOrderItemEntity(order, product, itemDTO.getQuantity());
            order.getItems().add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setTotalPrice(total);
        return orderRepository.save(order);
    }
}
