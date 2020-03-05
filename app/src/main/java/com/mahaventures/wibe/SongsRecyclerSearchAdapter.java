package com.mahaventures.wibe;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Activities.PlayerActivity;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.stream.Collectors;

public class SongsRecyclerSearchAdapter extends RecyclerView.Adapter<SongsRecyclerSearchAdapter.SearchSongsViewHolder> {

    private List<Track> result;
    private Context context;

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
        String artists = "";
        if (track.artists != null) {
            if (track.artists.data.size() == 1) {
                artists = track.artists.data.get(0).name;
            } else {
                List<String> strings = track.artists.data.stream().map(x -> x.name).collect(Collectors.toList());
                artists = TextUtils.join(", ", strings);
            }
        }
        SearchSongsViewHolder.artist.setText(artists);
        SearchSongsViewHolder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("id", track.id);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return result != null ? result.size() : 0;
    }

    public static class SearchSongsViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView songImg;
        static TextView songTitle;
        static TextView artist;

        SearchSongsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.searchCV);
            songImg = itemView.findViewById(R.id.img_cover_relative_song);
            songTitle = itemView.findViewById(R.id.txt_title_relative_song);
            artist = itemView.findViewById(R.id.txt_artist_relative_song);
        }
    }
}
