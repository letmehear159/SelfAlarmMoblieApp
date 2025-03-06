package com.example.selfalarm.activity.musicActivity;
import java.io.Serializable;

public class AudioModel implements Serializable {
    int resourceId; // Lưu ID của file nhạc trong raw
    String title;
    String duration;

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public AudioModel(int resourceId, String title, String duration) {
        this.resourceId = resourceId;
        this.title = title;
        this.duration = duration;
    }
    public AudioModel(int resourceId, String title) {
        this.resourceId = resourceId;
        this.title = title;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}