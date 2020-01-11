package com.mahaventures.wibe.Models.RequestModels;

import com.google.gson.annotations.SerializedName;

public class SignUpRequestModel {
    @SerializedName("email")
    private String Email;
    @SerializedName("password")
    private String Password;
    @SerializedName("device")
    private String Device;
    @SerializedName("notification")
    private String Notification;

    public SignUpRequestModel(String email, String password, String device, String notification) {
        Email = email;
        Password = password;
        Device = device;
        Notification = notification;
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

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public String getNotification() {
        return Notification;
    }

    public void setNotification(String notification) {
        Notification = notification;
    }
}
