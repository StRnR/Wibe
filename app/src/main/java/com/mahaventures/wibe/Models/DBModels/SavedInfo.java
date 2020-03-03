package com.mahaventures.wibe.Models.DBModels;

import com.orm.SugarRecord;

public class SavedInfo extends SugarRecord {
    private String Token;
    private String Email;
    private boolean IsActive;

    public SavedInfo() {
    }

    public SavedInfo(String token, String email) {
        Token = token;
        Email = email;
        IsActive = true;
    }
}
