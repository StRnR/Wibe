package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResponseModel {

    @SerializedName("data")
    @Expose
    public AuthenticationData data;
    @SerializedName("meta")
    @Expose
    public AuthenticationMeta meta;

}