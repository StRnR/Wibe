package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("medium")
    @Expose
    public Medium medium;
    @SerializedName("large")
    @Expose
    public Large large;

}