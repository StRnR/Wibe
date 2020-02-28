package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class OtherLinks {
    @SerializedName("rel")
    @Expose
    public String rel;
    @SerializedName("uri")
    @Expose
    public String uri;
}
