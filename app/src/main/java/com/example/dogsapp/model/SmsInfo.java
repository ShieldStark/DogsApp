package com.example.dogsapp.model;

public class SmsInfo {
    public String to;
    public String text;
    public String imageUrl;

    public SmsInfo(String s, String s1, String imageUrl) {
        this.to=s;
        this.text=s1;
        this.imageUrl=imageUrl;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
