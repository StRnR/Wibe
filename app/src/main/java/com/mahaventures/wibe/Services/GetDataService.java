package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GetDataService {
    @GET("users/me/")
    Call<User> GetUserInfo(@Header("Authorization") String s);
}
