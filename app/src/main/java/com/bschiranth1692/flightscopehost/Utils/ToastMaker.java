package com.bschiranth1692.flightscopehost.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by bschiranth1692 on 8/26/17.
 */

//Utility class to create toasts
public class ToastMaker {

    //make short toast
    public static void makeShortToast(Context context, String str) {
        Toast.makeText(context,str.toString(),Toast.LENGTH_SHORT).show();
    }

    //make long toast
    public static void makeLongToast(Context context, String str) {
        Toast.makeText(context,str.toString(),Toast.LENGTH_SHORT).show();
    }

}
