package com.example.noface.model;

public class User {
    public User(String iduser, String name, String email, String password) {
        Iduser = iduser;
        Name = name;
        Email = email;
        Password = password;
    }

    public String getIduser() {
        return Iduser;
    }

    public void setIduser(String iduser) {
        Iduser = iduser;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    private String Iduser;
    private String Name;
    private String Email;
    private String Password;
}
