package com.mahaventures.wibe.models;

import com.orm.SugarRecord;

public class SavedInfo extends SugarRecord {
    private String Token;
    private String Email;
    private String Name;

    public SavedInfo() {
    }

    public SavedInfo(String name, String token, String email) {
        Name = name;
        Token = token;
        Email = email;
    }

    public String getToken() {
        return Token;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }
}
