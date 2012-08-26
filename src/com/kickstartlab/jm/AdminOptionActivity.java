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
import android.content.Intent;
import android.content.SharedPreferences;
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
	protected ViewFlipper flipper;
	protected PassKeyDialog passdialog;
	protected TextView deviceinfo,txtSendResult;

    private SharedPreferences jexPrefs;
	private ProgressDialog dialog;
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.optionlayout);
        
        Button syncData = (Button) findViewById(R.id.btSync);
        Button updateKey = (Button) findViewById(R.id.btUpdateKey);
        Button changePassKey = (Button) findViewById(R.id.btChangePassKey);
        Button lockOption = (Button) findViewById(R.id.btLockAdmin);
        Button courierDispatch = (Button) findViewById(R.id.btCrDispatch);
        Button courierReturn = (Button) findViewById(R.id.btCrReturn);
        deviceinfo = (TextView) findViewById(R.id.txtDevInfo);
        txtSendResult = (TextView)findViewById(R.id.txtSendResult);
        
        syncData.setOnClickListener(this);
        updateKey.setOnClickListener(this);
        lockOption.setOnClickListener(this);
        changePassKey.setOnClickListener(this);
        courierDispatch.setOnClickListener(this);
        courierReturn.setOnClickListener(this);
        
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
    	
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.btSync:
				
				if(login == true){
					Intent intent = new Intent(v.getContext(),GetDataActivity.class);
					startActivity(intent);
					//Toast.makeText(this, "Synched", Toast.LENGTH_SHORT).show();					
				}else{
					passdialog.show();
				}
				
				break;
			case R.id.btUpdateKey:
				if(login == true){
					Intent intent = new Intent(v.getContext(),RequestKeyActivity.class);
					startActivity(intent);
					//Toast.makeText(this, "Update Key", Toast.LENGTH_SHORT).show();
				}else{
					passdialog.show();
				}
				break;
			case R.id.btChangePassKey:
				if(login == true){
					Intent intent = new Intent(v.getContext(),ChangePassActivity.class);
					startActivity(intent);
					//Toast.makeText(this, "Change PassKey", Toast.LENGTH_SHORT).show();
				}else{
					passdialog.show();
				}
				break;
			case R.id.btLockAdmin:
				if(login == true){
					login = false;
					Toast.makeText(AdminOptionActivity.this, "Option locked.", Toast.LENGTH_SHORT).show();					
				}else{
					Toast.makeText(AdminOptionActivity.this, "Option already locked.", Toast.LENGTH_SHORT).show();					
				}
				break;
			case R.id.btCrDispatch:
				if(login == true){
					SendStatusProcess sendstatus = new SendStatusProcess();
					sendstatus.execute(new String[]{"departed"});
				}else{
					passdialog.show();
				}
				break;
			case R.id.btCrReturn:
				if(login == true){
					SendStatusProcess sendstatus = new SendStatusProcess();
					sendstatus.execute(new String[]{"returned"});
				}else{
					passdialog.show();
				}
				break;
			default:
				Toast.makeText(this, "None to Do", Toast.LENGTH_SHORT).show();
				break;
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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



}
