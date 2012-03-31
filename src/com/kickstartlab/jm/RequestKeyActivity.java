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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RequestKeyActivity extends Activity implements OnClickListener{
	private TextView txtReqResult;
	private EditText edDeviceIdentifier, edAdminPasskey,edAdminUsername;
	private String sendResult = "";	

    private SharedPreferences jexPrefs;
	private SharedPreferences.Editor edit; 
	
	private ProgressDialog dialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.requestkeylayout);
        Button btReqKey = (Button)findViewById(R.id.btRequestKey);
        edDeviceIdentifier = (EditText) findViewById(R.id.edReqIdentifier);
        edAdminPasskey = (EditText) findViewById(R.id.edAdminPasskey);
        edAdminUsername = (EditText) findViewById(R.id.edAdminUsername);
        txtReqResult = (TextView)findViewById(R.id.txtReqResult);

        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);

        txtReqResult.setText(jexPrefs.getString("devkey", "nokey"));

        edit = jexPrefs.edit();
        
        btReqKey.setOnClickListener(this);
        
        dialog = new ProgressDialog(this);
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.btRequestKey:
				RequestKeyProcess req = new RequestKeyProcess();
				req.execute();
				break;
			default:
				break;
		}
		
	}
	
	private class RequestKeyProcess extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String key = getResources().getText(R.string.api_key).toString();
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_get_key).toString() +'/'+getResources().getText(R.string.master_key).toString();
			String txtResult = "";
			String lat,lon = "";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				json.put("user", edAdminUsername.getText());
				json.put("pass", edAdminPasskey.getText());
				json.put("identifier", edDeviceIdentifier.getText());

				JSONArray postjson=new JSONArray();
				postjson.put(json);
				
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("req", json.toString()));
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
			try{
				JSONObject json = new JSONObject(result);
				
				if(json.getString("status").equalsIgnoreCase("OK:NEWKEY")){
					
					edit.putString("devkey",json.getString("keydata"));
					edit.putString("devidentifier",json.getString("identifier").toUpperCase());
					edit.commit();
					txtReqResult.setText(jexPrefs.getString("devkey", "nokey"));
					Toast.makeText(RequestKeyActivity.this,"New Key : " + jexPrefs.getString("devkey", "nokey"), Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(RequestKeyActivity.this, json.getString("status"), Toast.LENGTH_LONG).show();
				}
				
				dialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Requesting key");
			dialog.show();
		}
		
	}

}
