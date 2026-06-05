package com.fishmarket.repository;

import com.fishmarket.entity.Product;
import com.fishmarket.entity.Product.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(ProductType type);
    List<Product> findByTypeOrderByPriceAsc(ProductType type);
    List<Product> findByTypeOrderByPriceDesc(ProductType type);
}