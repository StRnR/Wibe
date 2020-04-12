package com.mahaventures.wibe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InitModel {
    @SerializedName("LOGGED_IN")
    @Expose
    public Boolean loggedIn;
    @SerializedName("PAGE_HOME_ID")
    @Expose
    public String homePageId;
    @SerializedName("TRIAL_MONTH")
    @Expose
    public String trialMonth;
}