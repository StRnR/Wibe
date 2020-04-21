package com.mahaventures.wibe.services;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface DeleteDataService {
    @DELETE("profile/tracks")
    Call<Void> DeleteFromMySong(@Header("Authorization") String s, @Query("track_id") String track_id);
}
