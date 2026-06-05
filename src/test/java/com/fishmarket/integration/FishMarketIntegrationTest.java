package com.fishmarket.integration;

import com.fishmarket.dto.OrderDTO;
import com.fishmarket.dto.OrderDTO.OrderItemDTO;
import com.fishmarket.dto.ProductDTO;
import com.fishmarket.entity.Product.ProductType;
import com.fishmarket.entity.User;
import com.fishmarket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FishMarketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String adminToken;
    private String userToken;
    private Long testProductId;

    @BeforeEach
    void setUp() {
        adminToken = login("admin", "admin123");
        userToken = login("user", "user123");
        testProductId = createTestProduct();

        createUserIfNotExists("admin", "admin@temp.local", User.Role.ADMIN);
        createUserIfNotExists("user", "user@temp.local", User.Role.USER);
    }

    private void createUserIfNotExists(String username, String email, User.Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setName(username);
            user.setRole(role);
            user.setProvider("LOCAL");
            userRepository.save(user);
        }
    }

    private String login(String username, String password) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("username", username, "password", password));
        ResponseEntity<Map<String, String>> response = restTemplate.exchange("/api/auth/login", HttpMethod.POST, request,
                new ParameterizedTypeReference<Map<String, String>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("token"));
        return response.getBody().get("token");
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Long createTestProduct() {
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName("Тестовий товар");
        newProduct.setDescription("Опис для тестів");
        newProduct.setPrice(BigDecimal.valueOf(100));
        newProduct.setType(ProductType.LURE);
        newProduct.setImageUrl("/images/test.jpg");
        newProduct.setStock(10);

        HttpEntity<ProductDTO> request = new HttpEntity<>(newProduct, authHeaders(adminToken));
        ResponseEntity<ProductDTO> response = restTemplate.postForEntity("/api/products", request, ProductDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody().getId();
    }

    // ========== US-05: Автентифікація (позитивні сценарії) ==========
    @Test
    void testLoginAdminPositive() {
        String token = login("admin", "admin123");
        assertNotNull(token);
    }

    @Test
    void testLoginUserPositive() {
        String token = login("user", "user123");
        assertNotNull(token);
    }

    // ========== US-01: Перегляд товарів ==========
    @Test
    void testGetProductsByTypePositive() {
        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange("/api/products?type=ROD", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ProductDTO>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    void testGetProductsSortedAsc() {
        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange("/api/products?type=ROD&sort=asc", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ProductDTO>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProductDTO> products = response.getBody();
        assertTrue(products.size() > 1);
        for (int i = 0; i < products.size() - 1; i++) {
            assertTrue(products.get(i).getPrice().compareTo(products.get(i+1).getPrice()) <= 0);
        }
    }

    @Test
    void testGetProductsInvalidType() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/products?type=INVALID", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== US-03: Оформлення замовлення (тільки перевірка на пусту адресу) ==========
    @Test
    void testCreateOrderWithoutAddress() {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(testProductId);
        item.setQuantity(1);
        OrderDTO order = new OrderDTO();
        order.setShippingAddress("");
        order.setItems(List.of(item));

        HttpEntity<OrderDTO> request = new HttpEntity<>(order, authHeaders(userToken));
        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== US-04: Адміністрування товарів ==========
    @Test
    void testAdminCreateProductPositive() {
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName("Ще один тестовий товар");
        newProduct.setDescription("Опис");
        newProduct.setPrice(BigDecimal.valueOf(150));
        newProduct.setType(ProductType.HOOK);
        newProduct.setImageUrl("/images/test2.jpg");
        newProduct.setStock(3);

        HttpEntity<ProductDTO> request = new HttpEntity<>(newProduct, authHeaders(adminToken));
        ResponseEntity<ProductDTO> response = restTemplate.postForEntity("/api/products", request, ProductDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void testUserCreateProductForbidden() {
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName("Спроба юзера");
        newProduct.setPrice(BigDecimal.valueOf(100));
        newProduct.setType(ProductType.ROD);

        HttpEntity<ProductDTO> request = new HttpEntity<>(newProduct, authHeaders(userToken));
        ResponseEntity<String> response = restTemplate.exchange("/api/products", HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testAdminDeleteProductPositive() {
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName("Для видалення");
        newProduct.setPrice(BigDecimal.valueOf(30));
        newProduct.setType(ProductType.LINE);
        HttpEntity<ProductDTO> createRequest = new HttpEntity<>(newProduct, authHeaders(adminToken));
        ResponseEntity<ProductDTO> createResponse = restTemplate.postForEntity("/api/products", createRequest, ProductDTO.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Long productId = createResponse.getBody().getId();

        HttpEntity<Void> deleteRequest = new HttpEntity<>(authHeaders(adminToken));
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/products/" + productId, HttpMethod.DELETE, deleteRequest, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void testUserDeleteProductForbidden() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders(userToken));
        ResponseEntity<String> response = restTemplate.exchange("/api/products/" + testProductId, HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}