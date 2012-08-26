package com.kickstartlab.jm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class GeoReportService extends Service{
	String TAG = "GeoReportService";
    private SharedPreferences jexPrefs;
	LocationManager locman;
	Location rloc;
	String provider;
    Criteria criteria = new Criteria();  

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "GeoReportService");
		//return super.onStartCommand(intent, flags, startId);

		if(checkConnection() == 1){
	        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
	        
			final String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			final String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_put_loc).toString() + key;

			try {
				locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				provider = locman.getBestProvider(criteria, false);
				rloc = locman.getLastKnownLocation(provider);			

				final String lat = Double.toString(rloc.getLatitude());
				final String lon = Double.toString(rloc.getLongitude());

				Log.i(TAG,lat +","+lon);	
				
				new Thread() {
					public void run() {
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(url);
						JSONObject json = new JSONObject();
						try {
							json.put("key", key);
							json.put("lat", lat);
							json.put("lon", lon);

							JSONArray postjson = new JSONArray();
							postjson.put(json);

							List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
							nameValuePairs.add(new BasicNameValuePair("loc", json.toString()));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

							// Execute HTTP Post Request
							//System.out.print(json);
							HttpResponse response = httpclient.execute(httppost);

							// for JSON:
							if (response != null) {
								InputStream is = response.getEntity().getContent();

								BufferedReader reader = new BufferedReader(
										new InputStreamReader(is));
								StringBuilder sb = new StringBuilder();

								String line = null;
								try {
									while ((line = reader.readLine()) != null) {
										sb.append(line + "\n");
									}
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										is.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								//txtResult = sb.toString();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();			
			
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}else{
			Log.i(TAG,"Disconnected");
		}
		return 1;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "GeoReportService stopped.");		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private int checkConnection(){
		ConnectivityManager connect =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		Integer result = 1;
		
		if ( connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
			connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
			connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
			connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
			result = 1;
			
		} else if ( connect.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
			connect.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {
			result = 0;
		}

		return result;
	}
	
}
