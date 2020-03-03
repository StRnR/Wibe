package com.mahaventures.wibe.Models.NewModels.ProfileModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class AuthenticationMeta {
    @SerializedName("token")
    @Expose
    public String token;
}
