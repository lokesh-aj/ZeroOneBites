package com.example.grabit.Model;

public class Category {
    private String name;
    private int icon;
    private boolean selected;

    public Category(String name, int icon, boolean selected) {
        this.name = name;
        this.icon = icon;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
} 