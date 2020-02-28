package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.ResponseModels.ResetPasswordResponseModel;
import com.mahaventures.wibe.Models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("users/me/")
    Call<User> GetUserInfo(@Header("Authorization") String s);

    @GET("users/verify/email/")
    Call<Void> SendVerificationEmail(@Header("Authorization") String s);

    @GET("users/verify/password/")
    Call<ResetPasswordResponseModel> GetResetPasswordId(@Query("email") String email);

    @GET("tracks/search")
    Call<Track> GetTrackById(@Query("query") String id);
}
