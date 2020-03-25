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

import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SongsRecyclerPlaylistAdapter extends RecyclerView.Adapter<SongsRecyclerPlaylistAdapter.PlaylistSongsViewHolder> {

    private List<Track> result;
    private Context context;


    public SongsRecyclerPlaylistAdapter(List<Track> result, Context context) {
        this.result = result;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_song_layout, parent, false);
        return new PlaylistSongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsRecyclerPlaylistAdapter.PlaylistSongsViewHolder holder, int position) {
        Track track = result.get(position);
        holder.setIsRecyclable(false);
        holder.cardView.setOnClickListener(v -> {
            List<Track> tracks = new ArrayList<>();
            tracks.addAll(result);
            tracks.removeIf(t -> t.id.equals(track.id));
            tracks.add(0, track);
            StaticTools.PlayQueue(context, StaticTools.getArtistsName(track), tracks);
        });
        Picasso.get().load(track.image.medium.url).into(holder.songImg);
        holder.artist.setText(StaticTools.getArtistsName(track));
        holder.addBtn.setOnClickListener(v -> {
            StaticTools.addToMySong(context, track.id);
        });
        holder.songTitle.setText(track.name);
    }

    @Override
    public int getItemCount() {
        return result != null ? result.size() : 0;
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
