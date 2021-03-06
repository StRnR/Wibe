package com.mahaventures.wibe.services;

import com.mahaventures.wibe.models.Album;
import com.mahaventures.wibe.models.Albums;
import com.mahaventures.wibe.models.Artist;
import com.mahaventures.wibe.models.ArtistsSearch;
import com.mahaventures.wibe.models.GeneralSearch;
import com.mahaventures.wibe.models.MySong;
import com.mahaventures.wibe.models.Page;
import com.mahaventures.wibe.models.Playlist;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.models.Tracks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GetDataService {

    @GET
    Call<MySong> GetMySongs(@Header("Authorization") String s, @Url String url);

    @GET("pages/{id}/")
    Call<Page> GetPage(@Path("id") String id);

    @GET("artists/search/")
    Call<ArtistsSearch> SearchArtists(@Header("Authorization") String auth, @Query("query") String query);

    @GET
    Call<Album> getAlbum(@Header("Authorization") String auth, @Url String url);

    @GET
    Call<Tracks> getAlbumTracks(@Header("Authorization") String auth, @Url String url);

    @GET("artists/{id}/")
    Call<Artist> getArtist(@Header("Authorization") String auth, @Path("id") String id);

    @GET
    Call<Tracks> getArtistTracks(@Header("Authorization") String auth, @Url String url);

    @GET
    Call<Albums> getArtistAlbums(@Header("Authorization") String auth, @Url String url);

    @GET("playlists/{id}/")
    Call<Playlist> getPlaylist(@Header("Authorization") String auth, @Path("id") String id);

    @GET
    Call<Tracks> getPlaylistTracks(@Header("Authorization") String auth, @Url String url);

    @GET
    Call<Track> getTrack(@Header("Authorization") String auth, @Url String url);

    @GET
    Call<GeneralSearch> SearchAll(@Header("Authorization") String auth, @Url String url);
}
