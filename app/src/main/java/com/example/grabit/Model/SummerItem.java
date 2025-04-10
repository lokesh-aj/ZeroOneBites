package com.example.grabit.Model;

public class SummerItem {
    @com.google.gson.annotations.SerializedName("Item Name")
    private String itemName;
    
    @com.google.gson.annotations.SerializedName("Total Orders")
    private int totalOrders;

    @com.google.gson.annotations.SerializedName("Average Rating")
    private double averageRating;

    public SummerItem(String itemName, int totalOrders, double averageRating) {
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