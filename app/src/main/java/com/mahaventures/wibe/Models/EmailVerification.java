package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

public class EmailVerification {
    @SerializedName("code")
    private String Code;

    public EmailVerification(String code) {
        Code = code;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
