package com.mahaventures.wibe.services;

import com.mahaventures.wibe.models.AuthenticationResponseModel;
import com.mahaventures.wibe.models.ChangePasswordRequestModel;
import com.mahaventures.wibe.models.InitModel;
import com.mahaventures.wibe.models.RegisterResponseModel;
import com.mahaventures.wibe.models.SignInRequestModel;
import com.mahaventures.wibe.models.SignUpRequestModel;
import com.mahaventures.wibe.models.RequestModels.LoginRequestModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostDataService {
    @POST("users/login/")
    Call<Token> LoginUser(@Body LoginRequestModel requestModel);

    @POST("profile/register/")
    Call<RegisterResponseModel> Register(@Body SignUpRequestModel model);

    @POST("profile/authenticate/")
    Call<AuthenticationResponseModel> Authenticate(@Body SignInRequestModel model);

    @POST("profile/tracks")
    Call<Void> AddToMySongs(@Header("Authorization") String s, @Query("track_id") String trackId);

    @POST("profile/verify-email")
    Call<Void> VerifyEmail(@Header("Authorization") String s);

    @POST("profile/change-password")
    Call<Void> ChangePassword(@Header("Authorization") String s, ChangePasswordRequestModel model);

    @POST("init/")
    Call<InitModel> Init(@Header("Authorization") String s);

    @POST("/profile/reset-password")
    Call<ResetPassword> ResetPassword(@Header("Authorization") String s, @Query("profile") String profile);
}
