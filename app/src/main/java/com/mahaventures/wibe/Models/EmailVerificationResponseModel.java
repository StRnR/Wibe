package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

public class EmailVerificationResponseModel {
    @SerializedName("code")
    private String Code;

    public String getCode() {
        return Code;
    }
}
