package com.kickstartlab.jm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GeoReporter extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context,GeoReportService.class));
        Log.i("LOCATION_TRIGGER", "Loc Report Triggered");
	}
	
}
