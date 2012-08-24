package com.kickstartlab.jm;

import android.R.bool;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class JayonMobileActivity extends TabActivity {
    /** Called when the activity is first created. */
	SharedPreferences jexPrefs;
	SharedPreferences.Editor editor;
	
	private final GeoReporter georeporter = new GeoReporter();
	private final ConnMon networkStateReceiver = new ConnMon();
    private AlarmManager alarmManager;
	private Intent alarmintent;
	private PendingIntent pendingIntent;
    private Integer repeated = 10;
	private final long REPEAT_TIME = 1000 * 10;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        
        TabHost tabHost = getTabHost();
        
        TabSpec spec1 = tabHost.newTabSpec("deliveries");        
        spec1.setIndicator("Orders");
        Intent intent1 = new Intent(this, DeliveryListActivity.class);
        spec1.setContent(intent1);
        tabHost.addTab(spec1);

        TabSpec spec2 = tabHost.newTabSpec("location");
        spec2.setIndicator("Location");
        Intent intent2 = new Intent(this, LocationActivity.class );
        spec2.setContent(intent2);
        tabHost.addTab(spec2);

        TabSpec spec3 = tabHost.newTabSpec("scan");
        spec3.setIndicator("Scan");
        Intent intent3 = new Intent(this, ScanActivity.class );
        spec3.setContent(intent3);
        tabHost.addTab(spec3);

        TabSpec spec4 = tabHost.newTabSpec("options");
        spec4.setIndicator("Options");
        Intent intent4 = new Intent(this, AdminOptionActivity.class );
        spec4.setContent(intent4);
        tabHost.addTab(spec4);
        tabHost.setCurrentTab(0);
        
        if(isFirstRun()){
        	setPrefDefaults();
        }else{
        	setSyncSession();
        }
                
        IntentFilter filter = new IntentFilter();
        registerReceiver(georeporter, filter);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    	alarmintent = new Intent(this,GeoReporter.class);    	
    	pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, alarmintent, 0);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (repeated * 1000), REPEAT_TIME, pendingIntent);			

		IntentFilter connfilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);        
		registerReceiver(networkStateReceiver, connfilter);
		
		Toast.makeText(this, "Alarm set in " + repeated + " seconds", Toast.LENGTH_LONG).show();


    }

    
    
    @Override
    protected void onDestroy() {
    	alarmManager.cancel(pendingIntent);
    	this.unregisterReceiver(georeporter);
    	this.unregisterReceiver(networkStateReceiver);
    	super.onDestroy();
    }    
    
    @Override
    protected void onPause(){
    	super.onPause();
    }
        
    public boolean isFirstRun(){
    	return jexPrefs.getBoolean("firstrun",true);
    }
    
    public void setPrefDefaults(){
    	SharedPreferences.Editor editor = jexPrefs.edit();
    	editor.putBoolean("firstrun", false);
    	editor.putString("passkey", getResources().getString(R.string.pass_key).toString());
    	editor.putString("devkey", getResources().getString(R.string.api_key).toString());
    	editor.putString("devidentifier", getResources().getString(R.string.dev_identifier).toString());
    	editor.putInt("syncsession", 1);
    	editor.commit();
    }
    
    public void setSyncSession(){
    	SharedPreferences.Editor editor = jexPrefs.edit();
    	Integer sync_id = jexPrefs.getInt("syncsession",1);
    	sync_id++;
    	editor.putInt("syncsession",sync_id);    	
    	editor.commit();
    }
    
}