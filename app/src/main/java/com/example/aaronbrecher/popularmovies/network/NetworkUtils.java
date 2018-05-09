package com.example.aaronbrecher.popularmovies.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aaronbrecher on 5/8/18.
 */

public class NetworkUtils {
    public static boolean hasNetworkConnection(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isConnected = true;
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) isConnected = false;
        return isConnected;
    }
}
