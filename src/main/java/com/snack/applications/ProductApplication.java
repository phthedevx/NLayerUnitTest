package com.snack.applications;

import com.snack.entities.Product;
import com.snack.repositories.ProductRepository;
import com.snack.services.ProductService;

import java.util.List;

public class ProductApplication {
    private ProductRepository productRepository;
    private ProductService productService;

    public ProductApplication(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public List<Product> getAll() {
        return this.productRepository.getAll();
    }

    public Product getById(int id) {
        return this.productRepository.getById(id);
    }

    public boolean exists(int id) {
        return this.productRepository.exists(id);
    }

    public void append(Product product) {
        this.productRepository.append(product);
        this.productService.save(product);
    }

    public void remove(int id) {
        this.productRepository.remove(id);
        this.productService.remove(id);
    }

    public void update(int id, Product product) {
        this.productRepository.update(id, product);
        this.productService.update(product);
    }

    public float sellProduct(int id, int quantity) {
        Product product = this.productRepository.getById(id);
        return product.sellProduct(quantity);
    }
}
