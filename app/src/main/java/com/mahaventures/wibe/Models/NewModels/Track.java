package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Track {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("file")
    @Expose
    public String file;
    @SerializedName("image")
    @Expose
    public List<Image> image;
    @SerializedName("background_color")
    @Expose
    public String backgroundColor;
}
