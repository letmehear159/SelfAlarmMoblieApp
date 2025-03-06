package com.example.selfalarm;

public class ChatMessage {
    private String message;
    private boolean isSentByUser; // true nếu là tin nhắn gửi đi, false nếu là tin nhận

    public ChatMessage(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }
}