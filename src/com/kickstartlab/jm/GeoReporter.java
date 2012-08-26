package com.kickstartlab.jm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GeoReporter extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context,GeoReportService.class));
        Log.i("LOCATION_TRIGGER", "Loc Report Triggered");
	}
	
}
