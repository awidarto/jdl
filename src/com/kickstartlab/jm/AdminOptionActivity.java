package com.kickstartlab.jm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kickstartlab.jm.PassKeyDialog.OnPassKeyResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class AdminOptionActivity extends Activity implements OnClickListener{
	private Boolean login = false;
	protected String passkey,devidentifier,sendResult = "";
	protected String latitude = "";
	protected String longitude = "";
	protected String notes = "";
    protected Criteria criteria = new Criteria();
    protected String provider;
	protected ViewFlipper flipper;
	protected PassKeyDialog passdialog;
	protected TextView deviceinfo,txtSendResult;

    private SharedPreferences jexPrefs;
	private ProgressDialog dialog;
	
	protected LocationManager locman;
	protected Location loc;
	
	LogDataSource logdatasource = new LogDataSource(this);	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.optionlayout);
        
        Button syncData = (Button) findViewById(R.id.btSync);
        Button updateKey = (Button) findViewById(R.id.btUpdateKey);
        Button changePassKey = (Button) findViewById(R.id.btChangePassKey);
        Button courierDispatch = (Button) findViewById(R.id.btCrDispatch);
        Button courierReturn = (Button) findViewById(R.id.btCrReturn);
        Button syncLog = (Button) findViewById(R.id.btSyncOut);
        deviceinfo = (TextView) findViewById(R.id.txtDevInfo);
        txtSendResult = (TextView)findViewById(R.id.txtSendResult);
        
        syncData.setOnClickListener(this);
        updateKey.setOnClickListener(this);
        changePassKey.setOnClickListener(this);
        courierDispatch.setOnClickListener(this);
        courierReturn.setOnClickListener(this);
        syncLog.setOnClickListener(this);
        
        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);

        passkey = jexPrefs.getString("passkey", "98765");
        deviceinfo.setText("Device Id : " + jexPrefs.getString("devidentifier", ""));
        
        passdialog = new PassKeyDialog(AdminOptionActivity.this,"passkeydialog");
    	passdialog.setDialogResult(new OnPassKeyResult() {						
    		@Override
    		public void finish(String result) {
    			// TODO Auto-generated method stub
    	        passkey = jexPrefs.getString("passkey", "98765");
    			if(result.toString().equalsIgnoreCase(passkey)){
    				login = true;
    				Toast.makeText(AdminOptionActivity.this, "Passkey granted", Toast.LENGTH_SHORT).show();					
    			}else{
    				Toast.makeText(AdminOptionActivity.this, "Invalid passkey", Toast.LENGTH_SHORT).show();					
    			}
    		}
    	});

        dialog = new ProgressDialog(this);

        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locman.getBestProvider(criteria, false);
        
    }

	@Override
	public void onClick(View v) {

		SendStatusProcess sendstatus = new SendStatusProcess();
		Intent intent;
		
		switch(v.getId()){
			case R.id.btSync:
				intent = new Intent(v.getContext(),GetDataActivity.class);
				startActivity(intent);				
				break;
			case R.id.btSyncOut:
				UploadLogProcess uploadlog = new UploadLogProcess();
				uploadlog.execute(new String[]{"nodata"});
				break;
			case R.id.btUpdateKey:
				intent = new Intent(v.getContext(),RequestKeyActivity.class);
				startActivity(intent);
				break;
			case R.id.btChangePassKey:
				intent = new Intent(v.getContext(),ChangePassActivity.class);
				startActivity(intent);
				break;
			case R.id.btCrDispatch:
				loc = locman.getLastKnownLocation(provider);
				latitude = Double.toString(loc.getLatitude());
				longitude = Double.toString(loc.getLongitude());
				sendstatus.execute(new String[]{"departed"});
				break;
			case R.id.btCrReturn:
				loc = locman.getLastKnownLocation(provider);
				latitude = Double.toString(loc.getLatitude());
				longitude = Double.toString(loc.getLongitude());
				sendstatus.execute(new String[]{"returned"});
				break;
			default:
				Toast.makeText(this, "None to Do", Toast.LENGTH_SHORT).show();
				break;
		}
		
	}

	@Override
	protected void onResume() {

		super.onResume();
        passkey = jexPrefs.getString("passkey", "98765");
        deviceinfo.setText("Device Id : " + jexPrefs.getString("devidentifier", ""));
        
		login = false;
	}

	
	private class SendStatusProcess extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			String devid = jexPrefs.getString("devidentifier", getResources().getText(R.string.dev_identifier).toString());
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_put_status).toString() + key;
			String txtResult = "";

			Integer sync_id = jexPrefs.getInt("syncsession",1);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				json.put("status", params[0]);
				json.put("delivery_id", "");
				json.put("notes", devid + " " + params[0]);
				json.put("key", key);
				json.put("lat", latitude);
				json.put("lon", longitude);
				json.put("sync_id", sync_id);

				JSONArray postjson=new JSONArray();
				postjson.put(json);
				
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("trx", json.toString()));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				//System.out.print(json);
				HttpResponse response = httpclient.execute(httppost);

				// for JSON:
				if(response != null)
				{
					InputStream is = response.getEntity().getContent();

					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
					txtResult = sb.toString();
				}				
			}catch(Exception e){
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return txtResult;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("JSONResult",result);
			if(result.indexOf("OK:") > 0){					
				Toast.makeText(AdminOptionActivity.this,"Courier status updated", Toast.LENGTH_LONG).show();
			}
			
			dialog.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Updating status");
			dialog.show();
		}
		
	}

	
	protected String sendStatus(String status) throws JSONException{
		String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
		//String key = getResources().getText(R.string.api_key).toString();
		String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_put_status).toString() + key;
		String txtResult = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		JSONObject json = new JSONObject();
		
		try{
			// JSON data:
			json.put("status", status);
			json.put("key", key);
			json.put("delivery_id", "");
			json.put("lat", latitude);
			json.put("lon", longitude);
			json.put("notes", notes);

			JSONArray postjson=new JSONArray();
			postjson.put(json);
			
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("trx", json.toString()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			// Post the data:
			//httppost.setHeader("json",json.toString());
			//httppost.getParams().setParameter("loc",postjson);

			// Execute HTTP Post Request
			System.out.print(json);

			HttpResponse response = httpclient.execute(httppost);

			// for JSON:
			if(response != null)
			{
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
				txtResult = sb.toString();
			}
			
			
		}catch(ClientProtocolException e){
			txtResult = e.toString();
		}catch(IOException e){
			txtResult = e.toString();
		}
		
		return txtResult;
	}

	
	private class UploadLogProcess extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			String devid = jexPrefs.getString("devidentifier", getResources().getText(R.string.dev_identifier).toString());
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_put_log).toString() + key;
			String txtResult = "";
						
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				logdatasource.open();
				Cursor logcursor = logdatasource.getAllLogs();
				
				if(logcursor.getCount() > 0){
					  	logcursor.moveToFirst();
				        while (logcursor.isAfterLast() == false) {
				        	JSONObject js = new JSONObject();
					        js.put("_id",logcursor.getLong(0));
					        js.put("sync_id",logcursor.getString(1));
					        js.put("capture_time",logcursor.getString(2));
					        js.put("report_time",logcursor.getString(3));
					        js.put("delivery_id",logcursor.getString(4));
					        js.put("merchant_trans_id",logcursor.getString(5));
					        js.put("status",logcursor.getString(6));
					        js.put("delivery_note",devid + " " +logcursor.getString(7));
					        js.put("latitude",logcursor.getString(8));
					        js.put("longitude",logcursor.getString(9));

					        String idx = logcursor.getString(0);					        
					        json.put(idx, js);					        
					        logcursor.moveToNext();
				        }					
				}
				
				logcursor.close();
				logdatasource.close();

				JSONArray postjson=new JSONArray();
				
				postjson.put(json);
				
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("trx", json.toString()));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				//System.out.print(json);
				HttpResponse response = httpclient.execute(httppost);

				// for JSON:
				if(response != null)
				{
					InputStream is = response.getEntity().getContent();

					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
					txtResult = sb.toString();
				}				
			}catch(Exception e){
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return txtResult;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("JSONResult",result);
			if(result.indexOf("OK:") > 0){					
				Toast.makeText(AdminOptionActivity.this,"Report Log uploaded", Toast.LENGTH_LONG).show();
			}
			
			dialog.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Uploading Report Log");
			dialog.show();
		}
		
	}



}
