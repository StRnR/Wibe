package com.mahaventures.wibe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mahaventures.wibe.Activities.PlayerActivity;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Tools.StaticTools;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayerFragment extends Fragment {

    public static Track miniTrack;
    private Button playBtn;
    private Button skipBtn;

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
        TextView songTitle = view.findViewById(R.id.txt_songtitle_miniplayer);
        TextView artist = view.findViewById(R.id.txt_artist_miniplayer);
        ImageView cover = view.findViewById(R.id.img_cover_miniplayer);

//        if (PlayerActivity.isPlaying) {
//            songTitle.setText(miniTrack.name);
//            artist.setText(StaticTools.getArtistsName(miniTrack));
//            cover.setImageBitmap(PlayerActivity.artWork);
//        }

        //TODO: connect to media player
        playBtn.setOnClickListener(v -> {
        });

        return view;
    }

}
