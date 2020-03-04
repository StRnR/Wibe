package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralSearch {
    @SerializedName("query")
    @Expose
    public String query;
    @SerializedName("artists")
    @Expose
    public Artists artists;
    @SerializedName("tracks")
    @Expose
    public Tracks tracks;
    @SerializedName("albums")
    @Expose
    public Albums albums;
}
