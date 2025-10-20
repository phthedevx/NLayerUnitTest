package com.snack.repositories;

import com.snack.entities.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductRepository {
    private List<Product> products = new ArrayList<Product>();

    public List<Product> getAll() {
        return products;
    }

    public Product getById(int id) {
        Product product = products.stream().filter(p -> p.getId() == id).findFirst().get();
        return product;
    }

    public boolean exists(int id) {
        return products.stream().anyMatch(p -> p.getId() == id);
    }

    public void append(Product product) {
//        products.add(product);
    }

    public void remove(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    public void update(int id, Product product) {
        Product productInDb = products.stream().filter(p -> p.getId() == id).findFirst().get();

        productInDb.setDescription(product.getDescription());
        productInDb.setPrice(product.getPrice());
        productInDb.setImage(product.getImage());
    }
}
