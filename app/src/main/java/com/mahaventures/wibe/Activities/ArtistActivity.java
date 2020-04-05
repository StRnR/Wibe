package com.mahaventures.wibe.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistActivity extends AppCompatActivity {
    public static TextView artistName;
    public static ImageView artistArtwork;
    public static ImageView artistBlurred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        String id = getIntent().getStringExtra("id");
        artistArtwork = findViewById(R.id.img_artwork_artist_page);
        artistBlurred = findViewById(R.id.img_blur_artist);
        artistName = findViewById(R.id.txt_name_artist);
        Button backBtn = findViewById(R.id.btn_back_artist);
        TextView songsHeader = findViewById(R.id.txt_songs_header_artist);
        TextView albumsHeader = findViewById(R.id.txt_albums_header_search);
        TextView songsShowMore = findViewById(R.id.txt_showmore_songs_artist);
        RecyclerView songsRv = findViewById(R.id.recycler_songs_artist);
        RecyclerView albumsRv = findViewById(R.id.recycler_albums_artist);
        getArtistData(id);
    }

    private void getArtistData(String id) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Artist> call = service.getArtist(StaticTools.getToken(), id);
        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                if (response.isSuccessful()) {
                    //todo set parameters
                }
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {
                StaticTools.ServerError(ArtistActivity.this, t.getMessage());
            }
        });
    }
}
