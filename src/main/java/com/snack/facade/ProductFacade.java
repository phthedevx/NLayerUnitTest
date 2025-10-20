package com.snack.facade;

import com.snack.applications.ProductApplication;
import com.snack.entities.Product;

import java.util.List;

public class ProductFacade {
    private ProductApplication productApplication;

    public ProductFacade(ProductApplication productApplication) {
        this.productApplication = productApplication;
    }

    public List<Product> getAll() {
        return this.productApplication.getAll();
    }

    public Product getById(int id) {
        return this.productApplication.getById(id);
    }

    public boolean exists(int id) {
        return this.productApplication.exists(id);
    }

    public void append(Product product) {
        this.productApplication.append(product);
    }

    public void remove(int id) {
        this.productApplication.remove(id);
    }

    public void update(int id, Product product) {
        this.productApplication.update(id, product);
    }

    public float sellProduct(int id, int quantity) {
        return this.productApplication.sellProduct(id, quantity);
    }
}
