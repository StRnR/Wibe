package com.mahaventures.wibe.Models.NewModels.ProfileModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class AuthenticationData {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("email")
    @Expose
    public String email;
}
