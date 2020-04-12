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

import com.mahaventures.wibe.activities.MySongsActivity;
import com.mahaventures.wibe.activities.PlayerActivity;
import com.mahaventures.wibe.models.MySong;
import com.mahaventures.wibe.models.MySongTrack;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.tools.StaticTools;
import com.squareup.picasso.Picasso;

public class MySongsAdapter extends RecyclerView.Adapter<MySongsAdapter.MySongViewHolder> {

    private MySong mySong;
    private Context context;

    public MySongsAdapter(MySong mySong, Context context) {
        this.mySong = mySong;
        this.context = context;
    }

    @NonNull
    @Override
    public MySongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_song, parent, false);
        return new MySongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySongViewHolder holder, int position) {
        MySongViewHolder.addBtn.setBackgroundResource(R.drawable.ic_added);
        MySongTrack track = mySong.data.get(position);
        holder.setIsRecyclable(false);
        Picasso.get().load(track.track.image.medium.url).into(MySongViewHolder.songImg);
        MySongViewHolder.cardView.setOnClickListener(v -> {
            PlayerActivity.queue = MySongsActivity.mySongTracks;
            StaticTools.PlayTrackInQueue(context, StaticTools.getArtistsName(track.track), track.track);
        });
        MySongViewHolder.artist.setText(StaticTools.getArtistsName(track.track));
        MySongViewHolder.addBtn.setOnClickListener(v -> StaticTools.addToMySong(context, track.track.id));
        MySongViewHolder.songTitle.setText(track.track.name);
    }

    @Override
    public int getItemCount() {
        return mySong.data.size();
    }


    static class MySongViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView songImg;
        static TextView songTitle;
        static TextView artist;
        static Button addBtn;

        MySongViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_song);
            songImg = itemView.findViewById(R.id.img_cover_relative_song);
            songTitle = itemView.findViewById(R.id.txt_title_relative_song);
            artist = itemView.findViewById(R.id.txt_artist_relative_song);
            addBtn = itemView.findViewById(R.id.btn_add_relative_song);
        }
    }
}
