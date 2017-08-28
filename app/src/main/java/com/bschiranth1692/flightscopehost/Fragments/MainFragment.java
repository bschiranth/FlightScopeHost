package com.bschiranth1692.flightscopehost.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bschiranth1692.flightscopehost.Activities.VideoActivity;
import com.bschiranth1692.flightscopehost.R;
import com.bschiranth1692.flightscopehost.Utils.ToastMaker;
import com.bschiranth1692.flightscopehost.Utils.Utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import static com.bschiranth1692.flightscopehost.Utils.Utils.getCurrentTimeStamp;
import static com.bschiranth1692.flightscopehost.Utils.Utils.shouldAskPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    //UI objects
    ProgressBar progressBar;
    TextView percentText,serverTextIp;

    private static final int PORT = 2468;   //Port
    private static final int PERMISSION_CODE = 99;  //permission code to ask for permission
    private static final String PROGRESS = "progress_value"; //key to store progress

    //Sockets to receive data
    ServerSocket serverSocket;
    Socket client;

    //Thread to open sockets
    ServerAsyncTask serverAsyncTask;

    //Wifimanager to get device IP
    WifiManager wmanager;

    //String to store server IP
    String ip;

    //file to write incoming video
    File videoFile;

    //handler to post UI updates
    Handler handler = new Handler();

    //Interface object
    OnMainFragmentListener mainFragmentListener;

    public MainFragment() {
        // Required empty public constructor
    }

    //return new instance of the fragment
    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get Server IP
        wmanager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wmanager.getConnectionInfo().getIpAddress());

        //retain fragment on orientaion change
        setRetainInstance(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //cast activity to interface
        if(context instanceof OnMainFragmentListener) {
            mainFragmentListener = (OnMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //find view for UI objects
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarId);
        percentText = (TextView) view.findViewById(R.id.textPercentId);
        serverTextIp = (TextView)  view.findViewById(R.id.serverTextId);

        serverTextIp.setText("Server IP: "+ip);

        if(savedInstanceState != null){
            //get the progress from previous orientation
            progressBar.setProgress(savedInstanceState.getInt(PROGRESS));
            percentText.setText(savedInstanceState.getInt(PROGRESS)+" %");
        }

        //ask permissions for android 6.0 and above
        String requiredPermission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int checkVal = getContext().checkCallingOrSelfPermission(requiredPermission);
        if(checkVal == PackageManager.PERMISSION_GRANTED) {
            //permission already given , start receiving video
            receiveVideo();
        }else if(Utils.shouldAskPermission()) {
            //need to ask permission, so call requestPermission with Permission code
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            requestPermissions(perms, PERMISSION_CODE);
        }

        //return view for the fragment
        return view;
    }

    //starts thread to receive incoming video
    public void receiveVideo(){

        if(serverAsyncTask != null) {
            //serverAsyncTask.cancel(true);
            return;
        }

        serverAsyncTask = new ServerAsyncTask();
        serverAsyncTask.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission given by user, start thread
                receiveVideo();
            } else if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                //permission denied by user, explain why it is needed
                ToastMaker.makeLongToast(getContext(),"Need permission to save video..Please grant permission");
            } else {
                Toast.makeText(getContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainFragmentListener = null;
    }

    private class ServerAsyncTask extends AsyncTask<Void,Integer,Void> {

        //variables to get file length
        long total = 0;
        long fileLen = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //set progress bar to 0
            progressBar.setProgress(0);
            percentText.setText("Waiting...");

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                //create server socket from Port
                serverSocket = new ServerSocket(PORT);

                //connect to the client socket
                client = serverSocket.accept();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        percentText.setText("Connected..");
                    }
                });

                //get input stream from client socket
                InputStream inputStream = client.getInputStream();

                String fileName = "VID"+Utils.getCurrentTimeStamp()+".mp4";

                //create new empty file with filename and store in SD card
                videoFile = new File(Environment.getExternalStorageDirectory(),fileName);

                //create fileoutputstream to write data to file
                FileOutputStream fileOutputStream = new FileOutputStream(videoFile);

                byte[] buffer = new byte[1024]; // 1KB buffer size

                //get datainputstream from client
                DataInputStream dis = new DataInputStream(client.getInputStream());

                //get length of video file
                fileLen = dis.readLong();

                //read input stream from buffer
                int length = 0;
                while ( (length = inputStream.read(buffer, 0, buffer.length)) != -1 ){

                    total += length;
                    int val = (int) ((total*100)/fileLen); //calculate progress values
                    publishProgress(val); //send progress to OnProgressUpdate method

                    //write video data to created video file
                    fileOutputStream.write(buffer,0,length);
                }

                //flush streams
                fileOutputStream.flush();
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //close connections
                try {
                    client.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //set progress bar values
            progressBar.setProgress(values[0]);
            percentText.setText(values[0]+" %");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            percentText.setText("Completed..Going to play video");

            //call interface method
            if(videoFile != null) {
                mainFragmentListener.startVideoWithPath(videoFile.getAbsolutePath());
            } else {
                ToastMaker.makeLongToast(getContext(),"Video not saved! Send video from client again");
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save progress value on orientaion change
        outState.putInt(PROGRESS,progressBar.getProgress());
    }

    //interface to save video path
    public interface OnMainFragmentListener {
        void startVideoWithPath(String path);
    }


}
