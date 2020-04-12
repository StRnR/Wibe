package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequestModel {
    @SerializedName("old_password")
    @Expose
    private String old_password;

    @SerializedName("new_password")
    @Expose
    private String new_password;

    public ChangePasswordRequestModel(String old_password, String new_password) {
        this.old_password = old_password;
        this.new_password = new_password;
    }
}
