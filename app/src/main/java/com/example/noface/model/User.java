package com.example.noface.model;

public class User {


    private String idUser;
    private String name;
    private String email;
    private String phone;
    private boolean mailChecked;
    private String avaPath;
    private String status;

    public User() { }

    public User(String idUser, String name, String email, String phone, boolean mailChecked, String avaPath, String status) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.mailChecked = mailChecked;
        this.avaPath = avaPath;
        this.status = status;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isMailChecked() {
        return mailChecked;
    }

    public void setMailChecked(boolean mailChecked) {
        this.mailChecked = mailChecked;
    }

    public String getAvaPath() {
        return avaPath;
    }

    public void setAvaPath(String avaPath) {
        this.avaPath = avaPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}