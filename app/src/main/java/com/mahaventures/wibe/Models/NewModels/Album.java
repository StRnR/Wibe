package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class Album {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("image")
    @Expose
    public Object image;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("links")
    @Expose
    public List<OtherLinks> links = null;
}
