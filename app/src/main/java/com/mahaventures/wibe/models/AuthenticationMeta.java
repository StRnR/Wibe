package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationMeta {
    @SerializedName("token")
    @Expose
    public String token;
}
