package com.example.grabit.Model;

import com.google.gson.annotations.SerializedName;

public class PopularItem {
    @SerializedName("Item Name")
    private String itemName;
    
    @SerializedName("Total Orders")
    private int totalOrders;
    
    @SerializedName("Average Rating")
    private double averageRating;

    public PopularItem() {
        // Required empty constructor
    }

    public PopularItem(String itemName, int totalOrders, double averageRating) {
        this.itemName = itemName;
        this.totalOrders = totalOrders;
        this.averageRating = averageRating;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
} 