package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.SongsRecyclerPlaylistAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.Album;
import com.mahaventures.wibe.Models.NewModels.Tracks;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_album);
        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_mysongs:
                    startActivity(new Intent(getApplicationContext(), MySongsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_album);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(null);
        SongsRecyclerPlaylistAdapter tmpAdapter = new SongsRecyclerPlaylistAdapter(null, AlbumActivity.this);
        recyclerView.setAdapter(tmpAdapter);
        Button backBtn = findViewById(R.id.btn_back_album);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_album);
        ImageView albumArtwork = findViewById(R.id.img_artwork_album);
        TextView albumTitle = findViewById(R.id.txt_title_album);
        TextView albumArtist = findViewById(R.id.txt_owner_album);
        TextView description = findViewById(R.id.txt_album_description);

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_album) != null) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_album, miniPlayerFragment);
            fragmentTransaction.commit();
        }

        final View parent = (View) backBtn.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            backBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            parent.setTouchDelegate(new TouchDelegate(rect, backBtn));
        });

        backBtn.setOnClickListener(v -> AlbumActivity.super.onBackPressed());


        String id = getIntent().getStringExtra("id");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String s = String.format("https://api.musicify.ir/albums/%s?include=artists", id);
        Call<Album> artistCall = service.getAlbum(StaticTools.getToken(), s);
        artistCall.enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                StaticTools.LogErrorMessage(String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    albumTitle.setText(response.body().name);
                    Picasso.get().load(response.body().image.medium.url).into(albumArtwork);
                }
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
//                StaticTools.ShowToast(context, t.getMessage(), 1);
            }
        });
        String url = String.format("https://api.musicify.ir/albums/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getAlbumTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful()) {
                    albumArtist.setText(response.body().data.get(0).artists.data.get(0).name);
                    SongsRecyclerPlaylistAdapter adapter = new SongsRecyclerPlaylistAdapter(response.body().data, AlbumActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {

            }
        });
    }
}
