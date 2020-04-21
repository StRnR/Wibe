package com.mahaventures.wibe.adapters;

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

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.activities.AlbumActivity;
import com.mahaventures.wibe.models.Album;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.mahaventures.wibe.tools.StaticTools.ArtistActivityTag;
import static com.mahaventures.wibe.tools.StaticTools.SearchActivityTag;

public class SearchAlbumAdapter extends RecyclerView.Adapter<SearchAlbumAdapter.SearchAlbumViewHolder> {

    private Context context;
    private List<Album> albums;
    private int selector;

    public SearchAlbumAdapter(Context context, List<Album> albums, int selector) {
        this.context = context;
        this.albums = albums;
        this.selector = selector;
    }

    @NonNull
    @Override
    public SearchAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_album, parent, false);
        return new SearchAlbumAdapter.SearchAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAlbumViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Album album = albums.get(position);
        SearchAlbumViewHolder.artist.setText("");
        SearchAlbumViewHolder.title.setText(album.name);
        Picasso.get().load(album.image.medium.url).into(SearchAlbumViewHolder.albumImg);
        SearchAlbumViewHolder.cardView.setOnClickListener(v -> goToAlbum(album.id));
    }

    private void goToAlbum(String id) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("id", id);
        if (selector == 1)
            intent.putExtra("from", SearchActivityTag);
        else
            intent.putExtra("from", ArtistActivityTag);
            context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class SearchAlbumViewHolder extends RecyclerView.ViewHolder {
        static CardView cardView;
        static ImageView albumImg;
        static TextView title;
        static TextView artist;

        SearchAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_album_tile);
            albumImg = itemView.findViewById(R.id.img_artwork_album_tile);
            title = itemView.findViewById(R.id.txt_title_album_tile);
            artist = itemView.findViewById(R.id.txt_artist_album_tile);
        }
    }
}
