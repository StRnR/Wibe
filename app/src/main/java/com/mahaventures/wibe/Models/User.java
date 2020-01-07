package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("id")
    private int Id;
    @SerializedName("email")
    private String email;
    @SerializedName("profile")
    private Profile Profile;
    @SerializedName("roles")
    private List<UserRole> Roles;

    public int getId() {
        return Id;
    }

    public String getEmail() {
        return email;
    }

    public com.mahaventures.wibe.Models.Profile getProfile() {
        return Profile;
    }

    public List<UserRole> getRoles() {
        return Roles;
    }
}
