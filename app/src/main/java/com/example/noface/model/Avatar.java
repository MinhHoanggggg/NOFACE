package com.example.noface.model;

public class Avatar {
    private int avaId;
    private String avaPath;

    public Avatar(int avaId, String avaPath) {
        this.avaId = avaId;
        this.avaPath = avaPath;
    }

    public int getAvaId() {
        return avaId;
    }

    public void setAvaId(int avaId) {
        this.avaId = avaId;
    }

    public String getAvaPath() {
        return avaPath;
    }

    public void setAvaPath(String avaPath) {
        this.avaPath = avaPath;
    }
}
