package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attributes_ {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("image")
    @Expose
    public List<Object> image = null;

}
