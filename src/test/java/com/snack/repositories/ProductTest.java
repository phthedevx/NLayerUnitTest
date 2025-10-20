package com.snack.repositories;

import com.snack.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductTest {
    private ProductRepository productRepository;
    private Product product1;

    @BeforeEach
    public void setup() {
        productRepository = new ProductRepository();
        product1 = new Product(1, "Hot Dog", 10.4f, "");
    }

    @Test
    public void verificarSeOProdutoEstaNoArray() {
        // Arrange
        productRepository.append(product1);

        // Act
        Product productId1 = productRepository.getById(1);
        // Assert
        assertNotNull(productId1);
    }
}
