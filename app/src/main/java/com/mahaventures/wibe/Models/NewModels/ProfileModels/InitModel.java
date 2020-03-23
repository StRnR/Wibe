package com.mahaventures.wibe.Models.NewModels.ProfileModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InitModel {
    @SerializedName("logged_in")
    @Expose
    public Boolean loggedIn;
    @SerializedName("HOME_PAGE_ID")
    @Expose
    public String homePageId;
}
