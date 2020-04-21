package com.mahaventures.wibe.services;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Query;

public interface DeleteDataService {
    @DELETE("profile/tracks")
    Call<Void> DeleteFromMySong(@Query("track_id") String track_id);
}
