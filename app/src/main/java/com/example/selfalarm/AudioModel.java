package com.example.selfalarm;

import java.io.Serializable;

public class AudioModel implements Serializable {
    private int resourceId;
    private String title;
    private String duration;

    public AudioModel(int resourceId, String title, String duration) {
        this.resourceId = resourceId;
        this.title = title;
        this.duration = duration;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }
}