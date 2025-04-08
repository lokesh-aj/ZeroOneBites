package com.example.grabit.Model;

public class FoodItem {
    private String itemId;
    private String name;
    private String imageUrl;
    private String price;

    public FoodItem() {
        // Required empty constructor for Firebase
    }

    public FoodItem(String itemId, String name, String imageUrl, String price) {
        this.itemId = itemId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
} 