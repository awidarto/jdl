package com.kickstartlab.jm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnMon extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String result = "";
		ConnectivityManager connect =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				
		if ( connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
			connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
			connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
			connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
			result = "Connected";
			
		} else if ( connect.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
			connect.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {
			result = "Disconnected";

		}
		Log.i("ConnMon", "Network connection changed : " + result);
	}

}
