package com.snack.entities;

public class Product {
    private int id;
    private String description;
    private float price;
    private String image;

    public Product(int id, String description, float price, String image) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String imagePath) {
        this.image = imagePath;
    }

    public float sellProduct(int quantity) {
        return this.price * quantity;
    }

    @Override
    public String toString() {
        String formatText = "%-10s %-20s %-20s%n";

        return String.format(formatText,
                this.id,
                this.description,
                String.format("$ %.2f", this.price));
    }
}