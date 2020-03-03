package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.EmailVerification;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.AuthenticationResponseModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.RegisterResponseModel;
import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.ResetPassword;
import com.mahaventures.wibe.Models.Token;
import com.mahaventures.wibe.Models.TokenRegister;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostDataService {
    @POST("users/login/")
    Call<Token> LoginUser(@Body LoginRequestModel requestModel);

    @POST("profile/register/")
    Call<RegisterResponseModel> Register(@Query("email") String email, @Query("password") String password);

    @POST("profile/authenticate/")
    Call<AuthenticationResponseModel> Authenticate(@Query("email") String email, @Query("password") String password);

    @POST("users/verify/email/")
    Call<Void> ConfirmEmail(@Header("Authorization") String s, @Body EmailVerification code);

    @POST("users/verify/password/")
    Call<ResetPassword> ResetPassword(@Body ResetPassword data);
}
