package com.example.rsreu_app.model;

import android.graphics.Bitmap;

public class MyItem {

    private String title;
    private String url;
    private String summary;
    private String date;
    private String author;
    private byte[] img;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public MyItem(String title, String url, String summary, String date, String author, byte[] img){
        this.title = title;
        this.url = url;
        this.summary = summary;
        this.date = date;
        this.author = author;
        this.img = img;
    }


    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public byte[] getImg() {
        return img;
    }

    // img может быть null
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyItem myItem = (MyItem) o;

        if (!title.equals(myItem.title)) return false;
        if (!url.equals(myItem.url)) return false;
        if (!summary.equals(myItem.summary)) return false;
        if (!date.equals(myItem.date)) return false;
        if (!author.equals(myItem.author)) return false;
        return img != null ? img.equals(myItem.img) : myItem.img == null;
    }

}
