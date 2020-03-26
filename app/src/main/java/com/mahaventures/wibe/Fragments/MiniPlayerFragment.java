package com.mahaventures.wibe.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

public class MiniPlayerFragment extends Fragment {

    public static final String ACTION_PLAY = "play_action";
    public static final String ACTION_NEXT = "next_action";
    public static Track miniTrack;
    public static Context context;
    public static boolean isPlaying;
    public static boolean isLoaded;
    public static boolean isPrepared;
    private Button playBtn;
    private Button skipBtn;
    private Button fragmentClicker;
    private ProgressBar songProgressBar;
    int counter;

    public MiniPlayerFragment() {
        // Required empty public constructor
        isPrepared = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);
        playBtn = view.findViewById(R.id.btn_play_miniplayer);
        skipBtn = view.findViewById(R.id.btn_skip_miniplayer);
        fragmentClicker = view.findViewById(R.id.fragment_clicker);
        songProgressBar = view.findViewById(R.id.progressbar_miniplayer);
        TextView songTitle = view.findViewById(R.id.txt_songtitle_miniplayer);
        TextView artist = view.findViewById(R.id.txt_artist_miniplayer);
        ImageView cover = view.findViewById(R.id.img_cover_miniplayer);
        isLoaded = false;
        int color = Color.rgb(255, 255, 255);
        songProgressBar.setProgressTintList(ColorStateList.valueOf(color));
        counter = 0;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
                        try {
                            if (counter < 2) {
                                songProgressBar.setMax(PlayerActivity.maxProgress);
                                songProgressBar.setProgress(PlayerActivity.progressPosition);
                            }
                        } catch (Exception e) {
                            songProgressBar.setProgress(0);
                        }
                        if (isPlaying) {
                            playBtn.setBackgroundResource(R.drawable.ic_pause);
                        } else {
                            playBtn.setBackgroundResource(R.drawable.ic_play);
                        }
                        if (isPrepared) {
                            for (int i = 0; i < 3; i++) {
                                if (i == 1)
                                    try {
                                        Thread.sleep(50);
                                    } catch (Exception e) {

                                    }
                                counter = 0;
                                isPrepared = false;
                                isLoaded = true;
                                cover.setImageBitmap(PlayerActivity.getArtWork());
                                artist.setText(PlayerActivity.getArtistsName());
                                songTitle.setText(PlayerActivity.getTrackName());
                            }

                        }
////                        if (!isLoaded) {
//                            ConstraintLayout cl = view.findViewById(R.id.miniplayer_fragment_layout);
//                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) cl.getLayoutParams();
//                            lp.height = 0;
//                            cl.setLayoutParams(lp);
//                        }
                    });
                } catch (Exception e) {
                    StaticTools.LogErrorMessage(e.getMessage());
                }
            }
        }, 0, 100);

        skipBtn.setOnClickListener(v -> {
            if (context != null) {
                Intent intent = new Intent(context, MiniPlayerBroadCastReceiver.class)
                        .setAction(ACTION_NEXT);
                getActivity().sendBroadcast(intent);
            }
        });

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
                if (isLoaded)
                    getActivity().startActivity(intent);
            } catch (Exception e) {
                StaticTools.LogErrorMessage(e.getMessage());
            }
        });

        return view;
    }

}
