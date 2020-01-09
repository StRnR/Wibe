package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostDataService {
    @POST("users/login/")
    Call<Token> LoginUser(@Body LoginRequestModel requestModel);
}
