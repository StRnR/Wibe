package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artists {
    @SerializedName("data")
    @Expose
    public List<Artist> data;
}
