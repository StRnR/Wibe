package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Albums {
    @SerializedName("data")
    @Expose
    public List<Album> data;
}
