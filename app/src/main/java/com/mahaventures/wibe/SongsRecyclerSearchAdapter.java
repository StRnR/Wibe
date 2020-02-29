package com.mahaventures.wibe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongsRecyclerSearchAdapter extends RecyclerView.Adapter<SongsRecyclerSearchAdapter.SearchSongsViewHolder> {

    @Override
    public SearchSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_songs_layout, parent, false);
        SearchSongsViewHolder searchSongsViewHolder =  new SearchSongsViewHolder(view);

        return searchSongsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSongsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class SearchSongsViewHolder extends RecyclerView.ViewHolder {

        ImageView songImg;
        TextView songTitle;
        TextView artist;

        public SearchSongsViewHolder(@NonNull View itemView) {
            super(itemView);
            songImg = itemView.findViewById(R.id.img_cover_relative_song);
            songTitle = itemView.findViewById(R.id.txt_title_relative_song);
            artist = itemView.findViewById(R.id.txt_artist_relative_song);
        }
    }
}
