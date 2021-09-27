package com.example.noface.model;

public class Message {
    public Message(Integer status, String notification) {
        Status = status;
        Notification = notification;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getNotification() {
        return Notification;
    }

    public void setNotification(String notification) {
        Notification = notification;
    }

    private Integer Status;
    private String Notification;
}
