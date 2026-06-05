package com.fishmarket.service;

import com.fishmarket.dto.ProductDTO;
import com.fishmarket.entity.Product;
import com.fishmarket.entity.Product.ProductType;
import com.fishmarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapper;

    public List<ProductDTO> getProductsByType(ProductType type, String sort) {
        List<Product> products;
        if ("asc".equalsIgnoreCase(sort)) {
            products = productRepository.findByTypeOrderByPriceAsc(type);
        } else if ("desc".equalsIgnoreCase(sort)) {
            products = productRepository.findByTypeOrderByPriceDesc(type);
        } else {
            products = productRepository.findByType(type);
        }
        return products.stream()
                .map(mapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
        return mapper.toProductDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setType(dto.getType());
        product.setImageUrl(dto.getImageUrl());
        product.setStock(dto.getStock());
        Product saved = productRepository.save(product);
        return mapper.toProductDTO(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}