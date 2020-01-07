package com.mahaventures.wibe.Models;

import java.util.Date;

public class Profile {
    private boolean HasActivatedTrial;
    private Date PremiumEndDate;

    public boolean isHasActivatedTrial() {
        return HasActivatedTrial;
    }

    public Date getPremiumEndDate() {
        return PremiumEndDate;
    }
}
