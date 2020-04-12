package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Collections {
    @SerializedName("data")
    @Expose
    public List<Collection> data;
}
