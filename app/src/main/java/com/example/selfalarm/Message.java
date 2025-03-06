package com.example.selfalarm;

public class Message {
    private String sender;
    private String lastMessage;
    private String time;

    public Message(String sender, String lastMessage, String time) {
        this.sender = sender;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTime() {
        return time;
    }
}