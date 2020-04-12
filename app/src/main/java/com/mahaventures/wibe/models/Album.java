package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("image")
    @Expose
    public Images image;
    @SerializedName("background_color")
    @Expose
    public String backgroundColor;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("links")
    @Expose
    public List<OtherLinks> links;
}
