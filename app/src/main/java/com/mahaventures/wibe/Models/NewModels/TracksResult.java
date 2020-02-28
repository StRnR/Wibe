package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TracksResult {
    @SerializedName("data")
    @Expose
    public List<Track> data = null;
    @SerializedName("meta")
    @Expose
    public Meta meta;
}
