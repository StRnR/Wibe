package com.mahaventures.wibe.adapters;

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

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.activities.AlbumActivity;
import com.mahaventures.wibe.activities.ArtistActivity;
import com.mahaventures.wibe.activities.BrowseActivity;
import com.mahaventures.wibe.activities.PlayerActivity;
import com.mahaventures.wibe.activities.PlaylistActivity;
import com.mahaventures.wibe.models.BrowseItem;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mahaventures.wibe.tools.StaticTools.BrowseActivityTag;

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
        return new CollectionViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_browse, parent, false));
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
                    PlayerActivity.from = "playing from browse";
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
        intent.putExtra("from", BrowseActivityTag);
        context.startActivity(intent);
    }


    //todo after adding artist
    private void artist(String id) {
        Intent intent = new Intent(context, ArtistActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("from", BrowseActivityTag);
        context.startActivity(intent);
    }

    private void album(String id) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("from", BrowseActivityTag);
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
