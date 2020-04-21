package com.mahaventures.wibe.adapters;

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

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.activities.ArtistActivity;
import com.mahaventures.wibe.activities.PlayerActivity;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.tools.StaticTools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArtistTracksAdapter extends RecyclerView.Adapter<ArtistTracksAdapter.ArtistTracksViewHolder> {
    private List<Track> result;
    private Context context;

    public ArtistTracksAdapter(List<Track> result, Context context) {
        this.result = result;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_song, parent, false);
        return new ArtistTracksAdapter.ArtistTracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistTracksViewHolder holder, int position) {
        Track track = result.get(position);
        Picasso.get().load(track.image.medium.url).into(ArtistTracksAdapter.ArtistTracksViewHolder.songImg);
        ArtistTracksAdapter.ArtistTracksViewHolder.songTitle.setText(track.name);
        holder.setIsRecyclable(false);
        String artist = StaticTools.getArtistsName(track);
        ArtistTracksAdapter.ArtistTracksViewHolder.artist.setText(artist);
        ArtistTracksAdapter.ArtistTracksViewHolder.cardView.setOnClickListener(v -> {
            PlayerActivity.from = artist;
            PlayerActivity.queue = new ArrayList<>(ArtistActivity.tracks);
            StaticTools.PlayTrackInQueue(context, artist, track);
        });
        if (!track.is_favorite) {
            ArtistTracksViewHolder.addBtn.setBackgroundResource(R.drawable.ic_add);
            ArtistTracksViewHolder.addBtn.setOnClickListener(v -> {
                ArtistTracksViewHolder.addBtn.setBackgroundResource(R.drawable.ic_added);
                StaticTools.addToMySong(context, track.id);
            });
        } else {
            ArtistTracksViewHolder.addBtn.setBackgroundResource(R.drawable.ic_added);
            ArtistTracksViewHolder.addBtn.setOnClickListener(v -> {
                ArtistTracksViewHolder.addBtn.setBackgroundResource(R.drawable.ic_add);
                StaticTools.deleteFromMySong(context, track.id);
            });
        }
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    static class ArtistTracksViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView songImg;
        static TextView songTitle;
        static TextView artist;
        static Button addBtn;

        ArtistTracksViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_song);
            songImg = itemView.findViewById(R.id.img_cover_relative_song);
            songTitle = itemView.findViewById(R.id.txt_title_relative_song);
            artist = itemView.findViewById(R.id.txt_artist_relative_song);
            addBtn = itemView.findViewById(R.id.btn_add_relative_song);
        }
    }
}
