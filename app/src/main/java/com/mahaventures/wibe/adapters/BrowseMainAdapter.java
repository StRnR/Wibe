package com.mahaventures.wibe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.models.Collection;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.tools.StaticTools;

import java.util.List;

public class BrowseMainAdapter extends RecyclerView.Adapter<BrowseMainAdapter.MainViewHolder> {

    private List<Collection> collections;
    private Context context;
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
        BrowseItemAdapter horizontalAdapter = new BrowseItemAdapter(StaticTools.getBrowseItems(collection), context);
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

        MainViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerViewHorizontal = itemView.findViewById(R.id.home_recycler_view_horizontal);
            recyclerViewHorizontal.setHasFixedSize(true);
            recyclerViewHorizontal.setNestedScrollingEnabled(false);
            LinearLayoutManager horizontalManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewHorizontal.setLayoutManager(horizontalManager);
            recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());
            textViewTitle = itemView.findViewById(R.id.main_rv_title);

        }
    }
}