package com.bschiranth1692.flightscopehost.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bschiranth1692.flightscopehost.Fragments.MainFragment;
import com.bschiranth1692.flightscopehost.R;
import com.bschiranth1692.flightscopehost.Utils.ToastMaker;
import com.bschiranth1692.flightscopehost.Utils.Utils;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentListener{

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fragment Manager for fragment transactions
        fragmentManager = getSupportFragmentManager();

        //check if device has SD card and Wifi connectivity
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            ToastMaker.makeLongToast(this,"Please insert SD card and restart the app");
        } else if(!Utils.isConnected(this)) {
            ToastMaker.makeLongToast(this,"Please turn on wifi and restart the app");
        } else {

            if(savedInstanceState == null) {
                //add new fragment
                fragmentManager.beginTransaction().add(R.id.mainRootId
                        , MainFragment.newInstance(),getString(R.string.main_fragment)).
                        commit();

            }
        }



    }

    //interface method implemented by activity, activity will execute the method
    @Override
    public void startVideoWithPath(String path) {
        //play video in new activity
        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
        intent.putExtra(getString(R.string.video_path),path); //send video path to VideoActivity
        startActivity(intent);
    }

}
