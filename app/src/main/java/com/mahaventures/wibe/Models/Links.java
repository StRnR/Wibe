package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    public String self;
    @SerializedName("first")
    @Expose
    public String first;
    @SerializedName("last")
    @Expose
    public String last;

}
