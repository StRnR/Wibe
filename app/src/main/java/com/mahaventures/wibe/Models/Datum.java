package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("attributes")
    @Expose
    public Attributes attributes;
    @SerializedName("relationships")
    @Expose
    public Relationships relationships;

}
