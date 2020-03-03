package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeneralSearch {
    @SerializedName("tracks")
    @Expose
    public List<Track> tracks = null;
    @SerializedName("artists")
    @Expose
    public List<Artist> artists = null;
    @SerializedName("albums")
    @Expose
    public List<Album> albums = null;
}
