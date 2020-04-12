package com.mahaventures.wibe.models;

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
