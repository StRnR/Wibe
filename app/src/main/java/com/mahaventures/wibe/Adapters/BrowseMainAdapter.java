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

import java.util.List;

public class BrowseMainAdapter extends RecyclerView.Adapter<BrowseMainAdapter.MainViewHolder> {

    List<Collection> collections;
    Context context;
    private CollectionAdapter horizontalAdapter;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public BrowseMainAdapter(List<Collection> collections, Context context) {
        this.collections = collections;
        this.context = context;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //todo shiit
        View theView = LayoutInflater.from(context).inflate(R.layout.activity_browse, parent, false);
        return new MainViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerViewHorizontal;
        private TextView textViewCategory;
        private LinearLayoutManager horizontalManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

//            recyclerViewHorizontal = itemView.findViewById(R.id.home_recycler_view_horizontal);
//            recyclerViewHorizontal.setHasFixedSize(true);
//            recyclerViewHorizontal.setNestedScrollingEnabled(false);
//            recyclerViewHorizontal.setLayoutManager(horizontalManager);
//            recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());
//
//            textViewCategory = itemView.findViewById(R.id.tv_movie_category);

        }
    }
}