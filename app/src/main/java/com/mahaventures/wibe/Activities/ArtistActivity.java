package com.mahaventures.wibe.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistActivity extends AppCompatActivity {
    private String id;
    //todo text view ha ro inja begir

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        id = getIntent().getStringExtra("id");
        //todo inja vasl kon be xml
        getArtistData(id);
    }

    private void getArtistData(String id) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Artist> call = service.getArtist(StaticTools.getToken(), id);
        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                if (response.isSuccessful()){
                    //todo set parameters
                }
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {

            }
        });
    }
}
