package com.example.myapplication;

public class MuscleGroupItem {
    private String title;
    private int imageResId;

    public MuscleGroupItem(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}
