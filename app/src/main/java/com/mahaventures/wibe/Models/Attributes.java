package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("image")
    @Expose
    public Image image;
    @SerializedName("file")
    @Expose
    public String file;
}
