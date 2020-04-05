package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Activities.ArtistActivity;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchArtistAdapter extends RecyclerView.Adapter<SearchArtistAdapter.SearchArtistViewHolder> {
    Context context;
    List<Artist> artists;

    public SearchArtistAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    @NonNull
    @Override
    public SearchArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_album, parent, false);
        return new SearchArtistAdapter.SearchArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        SearchArtistViewHolder.artist.setText(artist.name);
        SearchArtistViewHolder.cardView.setOnClickListener(v -> goToArtist(artist.id));
        Picasso.get().load(artist.image.medium.url).into(SearchArtistViewHolder.albumImg);
    }

    private void goToArtist(String id) {
        Intent intent = new Intent(context, ArtistActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class SearchArtistViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView albumImg;
        static TextView artist;

        SearchArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_album_tile);
            albumImg = itemView.findViewById(R.id.img_artwork_album_tile);
            artist = itemView.findViewById(R.id.txt_artist_album_tile);
        }
    }
}
