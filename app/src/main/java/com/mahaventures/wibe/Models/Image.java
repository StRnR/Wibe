package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("thumbnail")
    @Expose
    public String thumbnail;
    @SerializedName("small")
    @Expose
    public String small;
    @SerializedName("medium")
    @Expose
    public String medium;
    @SerializedName("large")
    @Expose
    public String large;

}
