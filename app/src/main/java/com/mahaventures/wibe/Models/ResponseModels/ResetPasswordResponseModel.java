package com.mahaventures.wibe.Models.ResponseModels;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponseModel {
    @SerializedName("id")
    private int Id;

    public int getId() {
        return Id;
    }
}
