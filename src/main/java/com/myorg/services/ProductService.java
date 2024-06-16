package com.myorg.services;

import com.myorg.entities.Product;
import com.myorg.exceptions.ProductNotFoundException;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private static final List<Product> productList = new ArrayList<>();

    static {
        Product productA = Product.builder()
                .id(1L)
                .title("Product A")
                .description("Good product")
                .price(10.0)
                .build();

        Product productB = Product.builder()
                .id(2L)
                .title("Product B")
                .description("Good product")
                .price(20.0)
                .build();

        Product productC = Product.builder()
                .id(3L)
                .title("Product C")
                .description("Good product")
                .price(30.0)
                .build();

        productList.add(productA);
        productList.add(productB);
        productList.add(productC);
    }

    public List<Product> getAllProducts() {
        return productList;
    }

    @SneakyThrows
    public Product getProductById(Long id) {
        return productList.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
    }
}
