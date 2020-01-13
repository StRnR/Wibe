package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.RequestModels.SignUpRequestModel;
import com.mahaventures.wibe.Models.Token;
import com.mahaventures.wibe.Models.TokenRegister;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PostDataService {
    @POST("users/login/")
    Call<Token> LoginUser(@Body LoginRequestModel requestModel);

    @POST("users/register/")
    Call<TokenRegister> SignUpUser(@Body SignUpRequestModel requestModel);

    @POST("users/verify/email/")
    Call<Void> ConfirmEmail(@Header("Authorization") String s, @Body String code);
}
