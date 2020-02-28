package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaLinks {

    @SerializedName("next")
    @Expose
    public String next;
    @SerializedName("previous")
    @Expose
    public String previous;

}