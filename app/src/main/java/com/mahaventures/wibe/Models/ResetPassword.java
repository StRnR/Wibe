package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

public class ResetPassword {
    @SerializedName("id")
    private int Id;
    @SerializedName("code")
    private String Code;
    @SerializedName("password")
    private String Password;

    public ResetPassword(int id, String code, String password) {
        Id = id;
        Code = code;
        Password = password;
    }
}
