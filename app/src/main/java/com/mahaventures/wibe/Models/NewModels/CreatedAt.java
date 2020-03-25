package com.mahaventures.wibe.Models.NewModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class CreatedAt {
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("timezone_type")
    @Expose
    public int timezoneType;
    @SerializedName("timezone")
    @Expose
    public String timezone;
}
