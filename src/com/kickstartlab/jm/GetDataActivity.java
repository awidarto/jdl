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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class GetDataActivity extends Activity implements OnClickListener{
	DatePicker datePicker;
	TextView txtNetResult;
	String retrieveResult;
	String datepicked;
	
	OrderDataSource ordersource = new OrderDataSource(this);
	private ProgressDialog dialog;	
    private SharedPreferences jexPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.getdatalayout);
		
		Button getData = (Button) findViewById(R.id.btGetData);
		Button uploadData = (Button) findViewById(R.id.btUploadData);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		txtNetResult = (TextView) findViewById(R.id.txtNetResult);
		
		getData.setOnClickListener(this);
		uploadData.setOnClickListener(this);

		jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);

		dialog = new ProgressDialog(this);

	}

	@Override
	public void onClick(View v) {
		StringBuilder datepicked = null;
		datepicked = new StringBuilder().append(datePicker.getYear()).append('-').append(datePicker.getMonth() + 1).append('-').append(datePicker.getDayOfMonth());			

		JSONObject jsonObj = null;
		JSONArray jsonDataArray = null;
		
		switch(v.getId()){
		case R.id.btGetData:
			GetOrderDataProcess ord = new GetOrderDataProcess();
			ord.execute(new String[]{datepicked.toString()});
			/*
			try{
				retrieveResult = getOrderData(datepicked.toString());
				if(retrieveResult.indexOf("OK:") > 0){
					jsonObj = new JSONObject(retrieveResult);
					jsonDataArray = jsonObj.getJSONArray("data");
					if(jsonDataArray.length() > 0){
						ordersource.open();
						ordersource.emptyData();
						for(int i = 0;i < jsonDataArray.length();i++){
							ordersource.saveOrderJSON(jsonDataArray.getJSONObject(i));
						}
						ordersource.close();
					}
				}
				txtNetResult.setText(retrieveResult);
				
				//txtNetResult.setText(retrieveResult);
			}catch(NegativeArraySizeException e){
				txtNetResult.setText("Negative array.");
				e.printStackTrace();
			}catch(JSONException e){
				txtNetResult.setText("JSON Exception.");
				e.printStackTrace();
			}catch (NullPointerException e){
				txtNetResult.setText("Null pointer exception.");
				e.printStackTrace();
			}catch(ClientProtocolException e){
				txtNetResult.setText(e.toString());
			}catch(IOException e){
				txtNetResult.setText(e.toString());
			}
			*/
			
			//Toast.makeText(this, "Date to retrieve is " + datepicked, Toast.LENGTH_SHORT).show();
			break;
		case R.id.btUploadData:
			Toast.makeText(this, "Date to send is " + datepicked, Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}


	}
	
	private class GetOrderDataProcess extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String datepicked = params[0];
			String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_get_data).toString() + key + "/" + datepicked;
			String txtResult = "";
			String lat,lon = "";
			
			Log.i("syncUrl",url);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				json.put("lat", "");
				json.put("lon", "");

				JSONArray postjson=new JSONArray();
				postjson.put(json);
				
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("loc", json.toString()));
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
				if(result.indexOf("OK:") > 0){					
					JSONObject jsonObj = new JSONObject(result);
					JSONArray jsonDataArray = jsonObj.getJSONArray("data");
					if(jsonDataArray.length() > 0){
						ordersource.open();
						ordersource.emptyData();
						for(int i = 0;i < jsonDataArray.length();i++){
							ordersource.saveOrderJSON(jsonDataArray.getJSONObject(i));
						}
						ordersource.close();
						Toast.makeText(GetDataActivity.this,jsonDataArray.length() + " orders fetched", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(GetDataActivity.this,"No orders for the date", Toast.LENGTH_LONG).show();
					}
				}
				txtNetResult.setText(result);
				
				dialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Fetching orders");
			dialog.show();
		}
		
	}
	
/*
	protected String getOrderData(String datepicked) throws JSONException,IOException,ClientProtocolException{
		
		String key = getResources().getText(R.string.api_key).toString();
		String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_get_data).toString() + key + "/" + datepicked;
		String txtResult = "";
		String lat,lon = "";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		JSONObject json = new JSONObject();
		
		try{
			// JSON data:
			//json.put("status", status);
			//json.put("note", editNote.getText());
			//json.put("key", key);
			//json.put("lat", "-");
			//json.put("lon", "-");

			JSONArray postjson=new JSONArray();
			postjson.put(json);

			// Post the data:
			httppost.setHeader("json",json.toString());
			httppost.getParams().setParameter("loc",postjson);

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
		}finally{
			
		}
		
		return txtResult;
	}
*/
	
}
