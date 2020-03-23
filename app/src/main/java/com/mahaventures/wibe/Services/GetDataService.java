package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.NewModels.ArtistsSearch;
import com.mahaventures.wibe.Models.NewModels.FavouriteTrack;
import com.mahaventures.wibe.Models.NewModels.GeneralSearch;
import com.mahaventures.wibe.Models.NewModels.Page;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.NewModels.TracksResult;
import com.mahaventures.wibe.Models.ResponseModels.ResetPasswordResponseModel;
import com.mahaventures.wibe.Models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GetDataService {
    @GET("users/me/")
    Call<User> GetUserInfo(@Header("Authorization") String s);

    @GET("users/verify/email/")
    Call<Void> SendVerificationEmail(@Header("Authorization") String s);

    @GET("profile/tracks")
    Call<FavouriteTrack> GetMySongs(@Header("Authorization") String s);

    @GET("pages/{id}")
    Call<Page> GetPage(@Path("id") String id);

    @GET("users/verify/password/")
    Call<ResetPasswordResponseModel> GetResetPasswordId(@Query("email") String email);

    @GET("artists/search/")
    Call<ArtistsSearch> SearchArtists(@Query("query") String query);

    @GET
    Call<TracksResult> SearchTracks(@Url String url);

    @GET
    Call<GeneralSearch> SearchAll(@Url String url);

    @GET
    Call<Track> GetTrackById(@Url String url);
}
