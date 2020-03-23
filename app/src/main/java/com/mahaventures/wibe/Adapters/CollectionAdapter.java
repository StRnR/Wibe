package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.Collection;
import com.mahaventures.wibe.R;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    private Collection collection;
    private Context context;


    public CollectionAdapter(Collection collection, Context context) {
        this.collection = collection;
        this.context = context;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //todo shiit
        return new CollectionViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_browse, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
//        Collection collection = collection.get(position);

    }

    @Override
    public int getItemCount() {
        return collection.albums.data.size() + collection.artists.data.size() + collection.playlists.data.size() + collection.tracks.data.size();
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewGenre;
        private ImageView imageViewMovie;


        public CollectionViewHolder(View itemView) {
            super(itemView);

        }
    }
}
