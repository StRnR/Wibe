package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.MyModels.BrowseItem;
import com.mahaventures.wibe.R;
import com.squareup.picasso.Picasso;

import java.util.List;

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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView artwork;


        CollectionViewHolder(View itemView) {
            super(itemView);
            artwork = itemView.findViewById(R.id.img_artwork_browse_tile);
            title = itemView.findViewById(R.id.txt_title_browse_tile);
        }
    }
}