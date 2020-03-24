package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Activities.PlayerActivity;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PlaySongBroadcastReceiver;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongsRecyclerSearchAdapter extends RecyclerView.Adapter<SongsRecyclerSearchAdapter.SearchSongsViewHolder> {

    private List<Track> result;
    private Context context;
    public static String ACTION = "pay";

    public SongsRecyclerSearchAdapter(List<Track> result, Context context) {
        this.result = result;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_songs_layout, parent, false);
        SearchSongsViewHolder searchSongsViewHolder = new SearchSongsViewHolder(view);
        return searchSongsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSongsViewHolder holder, int position) {
        Track track = result.get(position);
        Picasso.get().load(track.image.medium.url).into(SearchSongsViewHolder.songImg);
        SearchSongsViewHolder.songTitle.setText(track.name);
        holder.setIsRecyclable(false);
        String artist = StaticTools.getArtistsName(track);
        SearchSongsViewHolder.artist.setText(artist);
        SearchSongsViewHolder.cardView.setOnClickListener(v -> {
            SearchSongsViewHolder.cardView.setClickable(false);
            PlayerActivity.mArtistString = artist;
            PlayerActivity.mTrackNameString = track.name;
            Intent intent = new Intent(context, PlayerActivity.class);
            PlayerActivity.trackNumber = 0;
            PlayerActivity.queue.removeIf(track1 -> track1.id.equals(track.id));
            PlayerActivity.queue.add(0, track);
            SearchSongsViewHolder.cardView.setClickable(true);
            Intent intent1 = new Intent(context, PlaySongBroadcastReceiver.class)
                    .setAction(ACTION);
            context.sendBroadcast(intent1);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    context.startActivity(intent);
                }
            }, 100);
        });
        SearchSongsViewHolder.addBtn.setOnClickListener(v -> {
            SearchSongsViewHolder.addBtn.setBackgroundResource(R.drawable.ic_added);
            addToMySongs(track.id);
        });
    }

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

    @Override
    public int getItemCount() {
        return result != null ? result.size() : 0;
    }

    static class SearchSongsViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView songImg;
        static TextView songTitle;
        static TextView artist;
        static Button addBtn;

        SearchSongsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_song);
            songImg = itemView.findViewById(R.id.img_cover_relative_song);
            songTitle = itemView.findViewById(R.id.txt_title_relative_song);
            artist = itemView.findViewById(R.id.txt_artist_relative_song);
            addBtn = itemView.findViewById(R.id.btn_add_relative_song);
        }
    }
}
