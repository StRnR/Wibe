package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MySongTrack {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("created_at")
    @Expose
    public CreatedAt createdAt;
    @SerializedName("track")
    @Expose
    public Track track;

}
