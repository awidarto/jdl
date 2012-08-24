package com.kickstartlab.jm;

import android.app.Application;

public class JayonApp extends Application{
	Boolean connected = true;
	@Override
	public void onCreate() {
		super.onCreate();
	}
 
	public Boolean getConnected() {
		return connected;
	}
 
	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	
}
