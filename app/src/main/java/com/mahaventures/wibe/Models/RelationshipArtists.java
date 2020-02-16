package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RelationshipArtists {

    @SerializedName("data")
    @Expose
    public List<RelationshipArtistsData> data = null;

}
