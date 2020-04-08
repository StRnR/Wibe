package com.mahaventures.wibe.Models.NewModels.ProfileModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequestModel {
    @SerializedName("old_password")
    @Expose
    public String old_password;

    @SerializedName("new_password")
    @Expose
    public String new_password;
}
