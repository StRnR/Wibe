package com.mahaventures.wibe.Models.NewModels.ProfileModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResponseModel {
    @SerializedName("data")
    @Expose
    public User data;
}
