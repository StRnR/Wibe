package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status_code")
    @Expose
    public int statusCode;
}
