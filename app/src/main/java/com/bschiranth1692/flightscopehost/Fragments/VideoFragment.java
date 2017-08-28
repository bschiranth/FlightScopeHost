package com.bschiranth1692.flightscopehost.Fragments;


import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bschiranth1692.flightscopehost.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    //string key to save file path
    public static final String FILE_PATH = "file_path";

    //UI objects
    VideoView videoView;
    Button playButton;

    //string to store recieved video file path
    String filePath;

    //Media controller to play video
    MediaController mediaController;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String filePath) {

        VideoFragment fragment = new VideoFragment();

        //save video file path received from VideoActivity
        Bundle args = new Bundle();
        args.putString(FILE_PATH, filePath);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retain fragment on orientaion change
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        //find views for UI objects
        playButton = (Button) view.findViewById(R.id.playButtonId);
        videoView = (VideoView) view.findViewById(R.id.videoViewId);


        if(savedInstanceState != null) {
            //get file path from previous orientaion
            filePath = savedInstanceState.getString(FILE_PATH);
        }else if(getArguments() != null) {
            //get file path saved bundle of newInstance()
            Bundle bundle = getArguments();
            filePath = bundle.getString(FILE_PATH);
        }

        // set video Uri for video view
        videoView.setVideoURI(Uri.fromFile(new File(filePath)));

        //create new media controller
        mediaController = new MediaController(getContext());
        mediaController.show(3000);

        //set the controller for the video view
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start playing video
                videoView.requestFocus();
                videoView.start();
            }
        });

        //return view for the fragment
        return view;
    }

    @Override
    public void onPause() {
        if(videoView != null) {
            videoView.suspend();
        }
        super.onPause();

    }

    @Override
    public void onResume() {
        if(videoView != null) {
            videoView.resume();
        }
        super.onResume();
    }

    @Override
    public void onDetach() {
        if(videoView != null) {
            videoView.stopPlayback();
        }
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save video file path on orientaion change
        outState.putString(FILE_PATH,filePath);
    }

}
