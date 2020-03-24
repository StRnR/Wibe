package com.mahaventures.wibe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.Collection;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.List;

public class BrowseMainAdapter extends RecyclerView.Adapter<BrowseMainAdapter.MainViewHolder> {

    List<Collection> collections;
    Context context;
    private BrowseItemAdapter horizontalAdapter;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public BrowseMainAdapter(List<Collection> collections, Context context) {
        this.collections = collections;
        this.context = context;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View theView = LayoutInflater.from(context).inflate(R.layout.main_rv, parent, false);
        return new MainViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Collection collection = collections.get(position);
        horizontalAdapter = new BrowseItemAdapter(StaticTools.getBrowseItems(collection), context);
        holder.recyclerViewHorizontal.setAdapter(horizontalAdapter);
        holder.recyclerViewHorizontal.setRecycledViewPool(recycledViewPool);
        holder.setIsRecyclable(false);
        holder.textViewTitle.setText(collection.name);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerViewHorizontal;
        private TextView textViewTitle;
        private LinearLayoutManager horizontalManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        MainViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerViewHorizontal = itemView.findViewById(R.id.home_recycler_view_horizontal);
            recyclerViewHorizontal.setHasFixedSize(true);
            recyclerViewHorizontal.setNestedScrollingEnabled(false);
            recyclerViewHorizontal.setLayoutManager(horizontalManager);
            recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());
            textViewTitle = itemView.findViewById(R.id.main_rv_title);

        }
    }
}