package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tracks {
    @SerializedName("data")
    @Expose
    public List<Track> data;
}
