package com.example.mydemo.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

 public class CommonHelper {
	public static boolean GetNetWorkStatus(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
