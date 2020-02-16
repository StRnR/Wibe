package com.mahaventures.wibe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artists {

    @SerializedName("data")
    @Expose
    public List<Datum_> data = null;

}
