package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Profile {
    //todo set correct names
    @SerializedName("albumId")
    private boolean HasActivatedTrial;
    private Date PremiumEndDate;

    public boolean isHasActivatedTrial() {
        return HasActivatedTrial;
    }

    public Date getPremiumEndDate() {
        return PremiumEndDate;
    }
}
