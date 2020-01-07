package com.mahaventures.wibe.Models;

import java.util.List;

public class User {
    private int Id;
    private String email;
    private Profile Profile;
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
