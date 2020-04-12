package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artist {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("image")
    @Expose
    public Images image;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("background_color")
    @Expose
    public String backgroundColor;
    @SerializedName("links")
    @Expose
    public List<OtherLinks> links;
}
