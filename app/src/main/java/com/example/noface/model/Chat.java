package com.example.noface.model;

public class Chat {
    private String from;
    private String to;
    private String message;
    private boolean seen;
    private boolean img;

    public Chat() { }

    public Chat(String from, String to, String message, boolean seen, boolean img) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.seen = seen;
        this.img = img;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isImg() {
        return img;
    }

    public void setImg(boolean img) {
        this.img = img;
    }
}
