package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relationships {

    @SerializedName("artists")
    @Expose
    public RelationshipArtists artists;
    @SerializedName("album")
    @Expose
    public Album album;

}
