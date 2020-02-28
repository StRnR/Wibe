package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images {

    @SerializedName("medium")
    @Expose
    public Image medium;
    @SerializedName("large")
    @Expose
    public Image large;

}