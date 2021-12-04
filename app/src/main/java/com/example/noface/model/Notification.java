package com.example.noface.model;

public class Notification  {
    private int ID_Notification;
    private String ID_User;
    private String Data_Notification;
    private int IDPost;
    private String ID_User_Seen_noti;
    private int Status_Notification;

    public Notification(int ID_Notification, String ID_User, String data_Notification, int IDPost, String ID_User_Seen_noti, int status_Notification) {
        this.ID_Notification = ID_Notification;
        this.ID_User = ID_User;
        Data_Notification = data_Notification;
        this.IDPost = IDPost;
        this.ID_User_Seen_noti = ID_User_Seen_noti;
        Status_Notification = status_Notification;
    }

    public int getID_Notification() {
        return ID_Notification;
    }

    public void setID_Notification(int ID_Notification) {
        this.ID_Notification = ID_Notification;
    }

    public String getID_User() {
        return ID_User;
    }

    public void setID_User(String ID_User) {
        this.ID_User = ID_User;
    }

    public String getData_Notification() {
        return Data_Notification;
    }

    public void setData_Notification(String data_Notification) {
        Data_Notification = data_Notification;
    }

    public int getIDPost() {
        return IDPost;
    }

    public void setIDPost(int IDPost) {
        this.IDPost = IDPost;
    }

    public String getID_User_Seen_noti() {
        return ID_User_Seen_noti;
    }

    public void setID_User_Seen_noti(String ID_User_Seen_noti) {
        this.ID_User_Seen_noti = ID_User_Seen_noti;
    }

    public int getStatus_Notification() {
        return Status_Notification;
    }

    public void setStatus_Notification(int status_Notification) {
        Status_Notification = status_Notification;
    }


}
