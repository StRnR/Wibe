package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Collection {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("view_type")
    @Expose
    public String viewType;
    @SerializedName("artists")
    @Expose
    public Artists artists;
    @SerializedName("albums")
    @Expose
    public Albums albums;
    @SerializedName("playlists")
    @Expose
    public Playlists playlists;
    @SerializedName("tracks")
    @Expose
    public Tracks tracks;

}
