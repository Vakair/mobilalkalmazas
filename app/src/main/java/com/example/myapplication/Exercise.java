package com.example.myapplication;

public class Exercise {
    private String name;
    private int imageResId;

    public Exercise(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}