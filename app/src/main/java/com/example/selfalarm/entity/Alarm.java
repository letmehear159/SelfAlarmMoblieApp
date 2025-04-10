package com.example.selfalarm.entity;

public class Alarm {
    private long id;
    private long timestamp;  // Timestamp (mili giây) biểu thị thời điểm báo thức
    private String content;

    private int isEnabled;
    private int isRepeating;

    public Alarm() {
    }

    public Alarm(long timestamp, String content, int isRepeating) {
        this(0, timestamp, content, 1, isRepeating);
    }


    public Alarm(long id, long timestamp, String content, int isEnabled, int isRepeating) {
        this.timestamp = timestamp;
        this.id = id;
        this.content = content;
        this.isEnabled = isEnabled;
        this.isRepeating = isRepeating;
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

    public int getIsRepeating() {
        return isRepeating;
    }

    public void setIsRepeating(int isRepeating) {
        this.isRepeating = isRepeating;
    }
}
