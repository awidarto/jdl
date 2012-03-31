package com.kickstartlab.jm;

import android.R.bool;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class JayonMobileActivity extends TabActivity {
    /** Called when the activity is first created. */
	SharedPreferences jexPrefs;
	
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
        }

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
    	editor.commit();
    }
    
}