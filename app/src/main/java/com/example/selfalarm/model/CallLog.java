package com.example.selfalarm.model;

public class CallLog {
    public static final int CALL_TYPE_INCOMING = 1;
    public static final int CALL_TYPE_OUTGOING = 2;
    public static final int CALL_TYPE_MISSED = 3;

    private long id;
    private String phoneNumber;
    private String name; // Tên liên hệ (nếu có)
    private long timestamp;
    private int duration; // Thời lượng cuộc gọi tính bằng giây
    private int callType; // 1: incoming, 2: outgoing, 3: missed

    public CallLog() {
    }

    public CallLog(String phoneNumber, String name, long timestamp, int duration, int callType) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.timestamp = timestamp;
        this.duration = duration;
        this.callType = callType;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }
}