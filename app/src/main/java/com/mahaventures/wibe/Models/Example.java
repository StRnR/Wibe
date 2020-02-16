package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Example {

    @SerializedName("data")
    @Expose
    public List<Datum> data = null;
    @SerializedName("included")
    @Expose
    public List<Included> included = null;
    @SerializedName("meta")
    @Expose
    public Meta meta;
    @SerializedName("links")
    @Expose
    public Links links;

}
