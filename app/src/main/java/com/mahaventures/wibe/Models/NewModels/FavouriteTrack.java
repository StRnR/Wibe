package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavouriteTrack {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("track")
    @Expose
    public Track track;
    @SerializedName("created_at")
    @Expose
    public List<Object> createdAt;
}
