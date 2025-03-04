package com.example.grabit.Model;
public class GridItem {
    private String title;
    private int imageResource;

    public GridItem(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResource() {
        return imageResource;
    }
}
