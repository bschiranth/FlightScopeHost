package com.bschiranth1692.flightscopehost.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.bschiranth1692.flightscopehost.Activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bschiranth1692 on 8/26/17.
 */

//Utility class that has helper methods
public class Utils {

    //checks whether permission needs to be asked depending on device version
    public static boolean shouldAskPermission(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    //get the current time stamp
    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    //checks if wifi is connected or not
    public static boolean isConnected(Context context){
        ConnectivityManager cm;
        NetworkInfo networkInfo;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = cm.getActiveNetworkInfo();
        if ( networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }

    }

}
