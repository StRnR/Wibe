package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.EmailVerification;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.AuthenticationResponseModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.InitModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.RegisterResponseModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.SignInRequestModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.SignUpRequestModel;
import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.ResetPassword;
import com.mahaventures.wibe.Models.Token;

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

    @POST("profile/tracks")
    Call<Void> ConfirmEmail(@Header("Authorization") String s, @Query("track_id") String trackId);

    @POST("init/")
    Call<InitModel> Init(@Header("Authorization") String s);

    @POST("users/verify/password/")
    Call<ResetPassword> ResetPassword(@Body ResetPassword data);
}
