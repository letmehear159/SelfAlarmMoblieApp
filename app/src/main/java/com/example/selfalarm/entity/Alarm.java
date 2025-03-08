package com.example.selfalarm.entity;

public class Alarm {
    private long id;
    private long timestamp;  // Timestamp (mili giây) biểu thị thời điểm báo thức
    private String content;

    private int isEnabled;

    public Alarm() {
    }

    public Alarm(long timestamp, String content) {
        this.id = 0;
        this.timestamp = timestamp;
        this.content = content;
        this.isEnabled = 1;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(int isEnabled) {
        this.isEnabled = isEnabled;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
