package com.example.grabit.Model;

public class Recipe {
    private String name;
    private String description;
    private int image;
    private int time;
    private String difficulty;
    private String calories;

    public Recipe(String name, String description, int image, int time, String difficulty, String calories) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.time = time;
        this.difficulty = difficulty;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
} 