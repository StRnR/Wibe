package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Collections {
    @SerializedName("data")
    @Expose
    public List<Collection> data;
}
