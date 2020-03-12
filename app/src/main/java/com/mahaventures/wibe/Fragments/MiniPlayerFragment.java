package com.mahaventures.wibe.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mahaventures.wibe.Activities.PlayerActivity;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.MiniPlayerBroadCastReceiver;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayerFragment extends Fragment {

    public static String ACTION_PLAY = "play_action";
    public static Track miniTrack;
    public static Context context;
    public static boolean isPlaying;
    public static boolean isPrepared;
    private Button playBtn;
    private Button skipBtn;
    private Button fragmentClicker;
    private ProgressBar songProgressBar;

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
        fragmentClicker = view.findViewById(R.id.fragment_clicker);
        songProgressBar = view.findViewById(R.id.progressbar_miniplayer);
        TextView songTitle = view.findViewById(R.id.txt_songtitle_miniplayer);
        TextView artist = view.findViewById(R.id.txt_artist_miniplayer);
        ImageView cover = view.findViewById(R.id.img_cover_miniplayer);

//        if (PlayerActivity.isPlaying) {
//            songTitle.setText(miniTrack.name);
//            artist.setText(StaticTools.getArtistsName(miniTrack));
//            cover.setImageBitmap(PlayerActivity.artWork);
//        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
                        if (isPlaying) {
                            playBtn.setBackgroundResource(R.drawable.ic_pause);
                        } else {
                            playBtn.setBackgroundResource(R.drawable.ic_play);
                        }
                        if (isPrepared) {
                            isPrepared = false;
                            cover.setImageBitmap(PlayerActivity.getArtWork());
                            artist.setText(PlayerActivity.getArtistsName());
                            songTitle.setText(PlayerActivity.getTrackName());
                        }
                    });
                } catch (Exception e) {
                    StaticTools.LogErrorMessage(e.getMessage());
                }
            }
        }, 0, 100);

        //TODO: connect to media player
        playBtn.setOnClickListener(v -> {
            if (context != null) {
                Intent intent = new Intent(context, MiniPlayerBroadCastReceiver.class)
                        .setAction(ACTION_PLAY);
                getActivity().sendBroadcast(intent);
            }
        });

        fragmentClicker.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                getActivity().startActivity(intent);
            } catch (Exception e) {
                StaticTools.LogErrorMessage(e.getMessage());
            }
        });

        return view;
    }

}
