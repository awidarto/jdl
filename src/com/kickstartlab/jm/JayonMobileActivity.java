package com.kickstartlab.jm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import com.kickstartlab.jm.PassKeyDialog.OnPassKeyResult;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class JayonMobileActivity extends TabActivity implements OnTabChangeListener,OnCancelListener{
    /** Called when the activity is first created. */
	SharedPreferences jexPrefs;
	SharedPreferences.Editor editor;
	
	private final GeoReporter georeporter = new GeoReporter();
	private final ConnMon networkStateReceiver = new ConnMon();
    private AlarmManager alarmManager;
	private Intent alarmintent;
	private PendingIntent pendingIntent;
    private Integer repeated = 60 * 10;
    private String passkey;
	private int currentTab = 0;

	protected PassKeyDialog passdialog;
	private Boolean login = false;
	TabHost tabHost;
	
    private static final String DATABASE_NAME = "jayonmobile.db";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        
        tabHost = getTabHost();
        
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
        /*
        TabSpec spec3 = tabHost.newTabSpec("scan");
        spec3.setIndicator("Scan");
        Intent intent3 = new Intent(this, ScanActivity.class );
        spec3.setContent(intent3);
        tabHost.addTab(spec3);
		*/
        TabSpec spec4 = tabHost.newTabSpec("options");
        spec4.setIndicator("Options");
        Intent intent4 = new Intent(this, AdminOptionActivity.class );
        spec4.setContent(intent4);
        tabHost.addTab(spec4);
        
        tabHost.setCurrentTab(currentTab);
        
        tabHost.setOnTabChangedListener(this);
        
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

    	long alarminterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    	long firsttrigger = System.currentTimeMillis() + (alarminterval / 15 );
    	
    	alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,firsttrigger, alarminterval, pendingIntent);			
    	//alarmManager.set
		IntentFilter connfilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);        
		registerReceiver(networkStateReceiver, connfilter);
		
		Toast.makeText(this, "Alarm set in " + repeated + " seconds", Toast.LENGTH_LONG).show();
		
        passdialog = new PassKeyDialog(JayonMobileActivity.this,"passkeydialog");
    	passdialog.setDialogResult(new OnPassKeyResult() {						
    		@Override
    		public void finish(String result) {
    			// TODO Auto-generated method stub
    	        passkey = jexPrefs.getString("passkey", "98765");
    			if(result.toString().equalsIgnoreCase(passkey)){
    				currentTab = tabHost.getCurrentTab();
    				Toast.makeText(JayonMobileActivity.this, "Passkey granted", Toast.LENGTH_SHORT).show();					
    			}else{
    				login = false;
    				tabHost.setCurrentTab(currentTab);
    				Toast.makeText(JayonMobileActivity.this, "Invalid passkey", Toast.LENGTH_SHORT).show();					
    			}
    		}
    	});
    	
    	passdialog.setOnCancelListener(this);
		


    }

    
    
    @Override
    protected void onDestroy() {
    	alarmManager.cancel(pendingIntent);
    	this.unregisterReceiver(georeporter);
    	this.unregisterReceiver(networkStateReceiver);
    	
        File source =  new File("data/data/com.kickstartlab.jm/databases/" + DATABASE_NAME);
        File dest =  new File(Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME);
        
        extractDb(source,dest);
        
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


    public static void extractDb(File sourceFile, File destFile) {
    	
	    FileChannel source = null;
	    FileChannel destination = null;

        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        destination.transferFrom(source, 0, source.size());

        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        try {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }



	@Override
	public void onTabChanged(String tabId) {
		if(tabId == "options"){
			if(login == false){
				passdialog.show();
			}
		}else{
			login = false;
			currentTab = tabHost.getCurrentTab();
		}
		
	}



	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		login = false;
		tabHost.setCurrentTab(currentTab);
	}    
    
}