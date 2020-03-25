package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.Album;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.Models.NewModels.MyModels.AlbumWithTracks;
import com.mahaventures.wibe.Models.NewModels.MyModels.ArtistWithTracks;
import com.mahaventures.wibe.Models.NewModels.MyModels.BrowseItem;
import com.mahaventures.wibe.Models.NewModels.MyModels.PlaylistWithTracks;
import com.mahaventures.wibe.Models.NewModels.Playlist;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseItemAdapter extends RecyclerView.Adapter<BrowseItemAdapter.CollectionViewHolder> {
    private List<BrowseItem> items;
    private Context context;
    private GetDataService service;


    public BrowseItemAdapter(List<BrowseItem> items, Context context) {
        this.items = items;
        this.context = context;
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CollectionViewHolder(LayoutInflater.from(context).inflate(R.layout.browse_tile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        BrowseItem item = items.get(position);
        holder.setIsRecyclable(false);
        holder.title.setText(item.title);
        if (item.image != null && !item.image.equals(""))
            Picasso.get().load(item.image).into(holder.artwork);
        if (item.color != null && !item.color.equals("")) {
            //todo Arshia: set color item.color example:
        }
        holder.cardView.setOnClickListener(v -> {
            switch (item.type) {
                case Album:
                    album(item.id);
                    break;
                case Artist:
                    artist(item.id);
                    break;
                case Playlist:
                    playlist(item.id);
                    break;
                case Track:
                    track(item.id);
                    break;
            }
        });
    }

    private void track(String id) {
        String url = String.format("https://api.musicify.ir/tracks/%s?include=artists", id);
        Call<Track> call = service.getTrack(url);
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful()) {
                    StaticTools.PlayTrack(context, StaticTools.getArtistsName(response.body()), response.body());
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {

            }
        });
    }

    private void playlist(String id) {
        Call<Playlist> playlistCall = service.getPlaylist(id);
        PlaylistWithTracks playlist = new PlaylistWithTracks();
        playlistCall.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful())
                    playlist.playlist = response.body();
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {

            }
        });
        //todo
    }

    private void artist(String id) {
        Call<Artist> artistCall = service.getArtist(id);
        ArtistWithTracks artist = new ArtistWithTracks();
        artistCall.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                if (response.isSuccessful())
                    artist.artist = response.body();
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {

            }
        });
        //todo
    }

    private void album(String id) {
        Call<Album> artistCall = service.getAlbum(id);
        AlbumWithTracks album = new AlbumWithTracks();
        artistCall.enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                if (response.isSuccessful())
                    album.album = response.body();
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {

            }
        });
        //todo
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView artwork;
        private CardView cardView;


        CollectionViewHolder(View itemView) {
            super(itemView);
            artwork = itemView.findViewById(R.id.img_artwork_browse_tile);
            title = itemView.findViewById(R.id.txt_title_browse_tile);
            cardView = itemView.findViewById(R.id.card_view_browse_tile);
        }
    }
}
