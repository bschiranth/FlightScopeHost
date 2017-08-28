package com.bschiranth1692.flightscopehost.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bschiranth1692.flightscopehost.Fragments.VideoFragment;
import com.bschiranth1692.flightscopehost.R;

public class VideoActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //Fragment Manager for fragment transactions
        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if(intent != null) {

            if(savedInstanceState == null) {

                //add new fragment
                fragmentManager.beginTransaction()
                        .add(R.id.rootVideoId,
                                VideoFragment.newInstance(intent.getStringExtra(getString(R.string.video_path))),
                                getString(R.string.video_fragment))
                        .commit();
            }


        }
    }
}
