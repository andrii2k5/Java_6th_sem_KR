package com.fishmarket.config;

import com.fishmarket.entity.Product;
import com.fishmarket.entity.Product.ProductType;
import com.fishmarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            System.out.println("Товари вже є. Пропускаємо.");
            return;
        }

        // Вудлища (ROD)
        productRepository.save(createProduct("Вудлище Shimano Tribal", "Карбон, 3.6 м", 2899, ProductType.ROD, "/images/rod.jpg", 10));
        productRepository.save(createProduct("Вудлище Daiwa Ninja", "Телескоп, 4.5 м", 2199, ProductType.ROD, "/images/rod.jpg", 8));
        productRepository.save(createProduct("Вудлище Mikado", "Болонське, 5 м", 999, ProductType.ROD, "/images/rod.jpg", 15));

        // Котушки (REEL)
        productRepository.save(createProduct("Котушка Ryobi Ecusima", "3000, 5 підшипників", 1399, ProductType.REEL, "/images/reel.jpg", 12));
        productRepository.save(createProduct("Котушка Okuma Ceymar", "2500, металева", 1699, ProductType.REEL, "/images/reel.jpg", 7));
        productRepository.save(createProduct("Котушка Shimano Sienna", "4000, фронтальний фрикціон", 1899, ProductType.REEL, "/images/reel.jpg", 5));

        // Шнури та волосіні (LINE)
        productRepository.save(createProduct("Шнур PowerPro", "0.14 мм, 150 м", 649, ProductType.LINE, "/images/line.jpg", 25));
        productRepository.save(createProduct("Волосінь Sufix", "0.25 мм, 200 м", 249, ProductType.LINE, "/images/line.jpg", 30));
        productRepository.save(createProduct("Флюорокарбон Sunline", "0.20 мм, 50 м", 389, ProductType.LINE, "/images/line.jpg", 18));

        // Гачки (HOOK)
        productRepository.save(createProduct("Гачок Owner", "розмір 6, одиночний", 69, ProductType.HOOK, "/images/hook.jpg", 200));
        productRepository.save(createProduct("Гачок Mustad", "потрійний, розмір 4", 49, ProductType.HOOK, "/images/hook.jpg", 180));
        productRepository.save(createProduct("Гачок Gamakatsu", "офсетний, розмір 2", 89, ProductType.HOOK, "/images/hook.jpg", 120));

        // Воблери (LURE)
        productRepository.save(createProduct("Воблер Rapala", "5 см, плаваючий", 399, ProductType.LURE, "/images/lure.jpg", 18));
        productRepository.save(createProduct("Воблер Salmo", "7 см, тонучий", 329, ProductType.LURE, "/images/lure.jpg", 14));
        productRepository.save(createProduct("Воблер Kosadaka", "4 см, суспендер", 459, ProductType.LURE, "/images/lure.jpg", 9));

        System.out.println("✅ Додано 15 товарів із локальними фото.");
    }

    private Product createProduct(String name, String description, int price, ProductType type, String imageUrl, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(BigDecimal.valueOf(price));
        p.setType(type);
        p.setImageUrl(imageUrl);
        p.setStock(stock);
        return p;
    }
}