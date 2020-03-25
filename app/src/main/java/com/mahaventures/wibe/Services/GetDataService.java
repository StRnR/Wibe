package com.mahaventures.wibe.Services;

import com.mahaventures.wibe.Models.NewModels.Album;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.Models.NewModels.ArtistsSearch;
import com.mahaventures.wibe.Models.NewModels.GeneralSearch;
import com.mahaventures.wibe.Models.NewModels.MySong;
import com.mahaventures.wibe.Models.NewModels.Page;
import com.mahaventures.wibe.Models.NewModels.Playlist;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.NewModels.Tracks;
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

    @GET
    Call<MySong> GetMySongs(@Header("Authorization") String s, @Url String url);

    @GET("pages/{id}/")
    Call<Page> GetPage(@Path("id") String id);

    @GET("users/verify/password/")
    Call<ResetPasswordResponseModel> GetResetPasswordId(@Query("email") String email);

    @GET("artists/search/")
    Call<ArtistsSearch> SearchArtists(@Query("query") String query);

    @GET("albums/{id}/")
    Call<Album> getAlbum(@Path("id") String id);

//    @GET("albums/{id}/tracks/")
//    Call<Tracks> getAlbumTracks(@Path("id") String id);

    @GET
    Call<Tracks> getAlbumTracks(@Url String url);

    @GET("artists/{id}/")
    Call<Artist> getArtist(@Path("id") String id);

//    @GET("artists/{id}/tracks/")
//    Call<Album> getArtistTracks(@Path("id") String id);

    @GET
    Call<Tracks> getArtistTracks(@Url String url);

    @GET("playlist/{id}/")
    Call<Playlist> getPlaylist(@Path("id") String id);

//    @GET("playlist/{id}/tracks/")
//    Call<Tracks> getPlaylistTracks(@Path("id") String id);

    @GET
    Call<Tracks> getPlaylistTracks(@Url String url);

    @GET
    Call<Track> getTrack(@Url String url);

    @GET
    Call<TracksResult> SearchTracks(@Url String url);

    @GET
    Call<GeneralSearch> SearchAll(@Url String url);

    @GET
    Call<Track> GetTrackById(@Url String url);
}
