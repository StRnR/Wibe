package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.MySong;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongsRecyclerPlaylistAdapter extends RecyclerView.Adapter<SongsRecyclerPlaylistAdapter.PlaylistSongsViewHolder> {

    private MySong mySong;
    private Context context;
    public static String ACTION = "pay";

    public SongsRecyclerPlaylistAdapter(MySong mySong, Context context) {
        this.mySong = mySong;
        this.context = context;
    }

    @NonNull
    @Override
    public SongsRecyclerPlaylistAdapter.PlaylistSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_song_layout, parent, false);
        SongsRecyclerPlaylistAdapter.PlaylistSongsViewHolder playlistSongsViewHolder = new SongsRecyclerPlaylistAdapter.PlaylistSongsViewHolder(view);
        return playlistSongsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    //todo: add to my songs should work in mysongs,too: on first click it removes the song from mysongs
    private void addToMySongs(String id) {
        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        Call<Void> call = service.AddToMySongs(StaticTools.getToken(), id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    StaticTools.ShowToast(context, "added", 0);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    static class PlaylistSongsViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView songImg;
        static TextView songTitle;
        static TextView artist;
        static Button addBtn;

        PlaylistSongsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_song);
            songImg = itemView.findViewById(R.id.img_cover_relative_song);
            songTitle = itemView.findViewById(R.id.txt_title_relative_song);
            artist = itemView.findViewById(R.id.txt_artist_relative_song);
            addBtn = itemView.findViewById(R.id.btn_add_relative_song);
        }
    }
}
