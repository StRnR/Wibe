package com.mahaventures.wibe.Models;

import com.google.gson.annotations.SerializedName;

public class UserRole {
    @SerializedName("user")
    private int User;
    @SerializedName("role")
    private int Role;
    @SerializedName("name")
    private String Name;
    @SerializedName("description")
    private String Description;

    public int getUser() {
        return User;
    }

    public int getRole() {
        return Role;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }
}
