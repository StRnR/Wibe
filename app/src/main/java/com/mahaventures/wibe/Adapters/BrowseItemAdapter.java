package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Activities.AlbumActivity;
import com.mahaventures.wibe.Activities.PlaylistActivity;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.Models.NewModels.MyModels.ArtistWithTracks;
import com.mahaventures.wibe.Models.NewModels.MyModels.BrowseItem;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.NewModels.Tracks;
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


    public BrowseItemAdapter(List<BrowseItem> items, Context context) {
        this.items = items;
        this.context = context;
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
            holder.bgColor.setBackgroundColor(Color.parseColor(item.color));
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
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
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
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }


    //todo after adding artist
    private void artist(String id) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        ;
        Call<Artist> artistCall = service.getArtist(StaticTools.getToken(), id);
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
        String url = String.format("https://api.musicify.ir/artists/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getArtistTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful())
                    artist.tracks = response.body().data;
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {

            }
        });
    }

    private void album(String id) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView bgColor;
        private ImageView artwork;
        private CardView cardView;


        CollectionViewHolder(View itemView) {
            super(itemView);
            artwork = itemView.findViewById(R.id.img_artwork_browse_tile);
            title = itemView.findViewById(R.id.txt_title_browse_tile);
            cardView = itemView.findViewById(R.id.card_view_browse_tile);
            bgColor = itemView.findViewById(R.id.img_bg_browse_tile);
        }
    }
}
