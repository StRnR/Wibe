package com.mahaventures.wibe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayerFragment extends Fragment {

    private Button playBtn;
    private Button skipBtn;
    private Button rewindBtn;

    public MiniPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);
        playBtn = view.findViewById(R.id.btn_play_miniplayer);
        skipBtn = view.findViewById(R.id.btn_skip_miniplayer);
        rewindBtn = view.findViewById(R.id.btn_rewind_miniplayer);


        //TODO: connect to media player
        playBtn.setOnClickListener(v -> {

        });

        return view;
    }

}
