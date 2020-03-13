package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Profile {
    @SerializedName("has_activated_trial")
    private boolean HasActivatedTrial;
    @SerializedName("premium_end_date")
    private Date PremiumEndDate;

    public boolean isHasActivatedTrial() {
        return HasActivatedTrial;
    }

    public Date getPremiumEndDate() {
        return PremiumEndDate;
    }
}
