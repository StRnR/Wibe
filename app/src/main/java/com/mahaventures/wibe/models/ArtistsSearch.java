package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArtistsSearch {
    @SerializedName("data")
    @Expose
    public List<Artist> data;
    @SerializedName("meta")
    @Expose
    public Meta meta;
}
