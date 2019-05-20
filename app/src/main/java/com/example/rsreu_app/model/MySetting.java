package com.example.rsreu_app.model;

import android.graphics.Bitmap;

public class MySetting {

    private String textSettings;
    private Bitmap imageSettings;

    public MySetting(String textSettings, Bitmap imageSettings) {
        this.textSettings = textSettings;
        this.imageSettings = imageSettings;
    }

    public String getTextSettings() {
        return textSettings;
    }

    public void setTextSettings(String textSettings) {
        this.textSettings = textSettings;
    }

    public Bitmap getImageSettings() {
        return imageSettings;
    }

    public void setImageSettings(Bitmap imageSettings) {
        this.imageSettings = imageSettings;
    }
}
