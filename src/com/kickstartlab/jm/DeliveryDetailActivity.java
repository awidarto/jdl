package com.kickstartlab.jm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeliveryDetailActivity extends Activity implements OnClickListener,LocationListener{
	private String delivery_id,sendResult = "";
	private EditText editNote;
	LocationManager locman;
	String provider;
	Uri imageUri;
	ImageView imagecam;
	Bitmap bitmap;
	String latitude,longitude, altitude;
    Criteria criteria = new Criteria();
    TextView txtDeliveryPos;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;
    private SharedPreferences jexPrefs;
	private ProgressDialog dialog;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        OrderDataSource ordersource = new OrderDataSource(this);
        ordersource.open();

        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        
        Bundle extras = getIntent().getExtras();
        delivery_id = extras.getString("delivery_id");
        setContentView(R.layout.detailedlayout);
        
        txtDeliveryPos = (TextView) findViewById(R.id.txtDetailPos);
        TextView txtDeliveryId = (TextView) findViewById(R.id.txtDetail);
        editNote = (EditText) findViewById(R.id.editNote);
        imagecam = (ImageView) findViewById(R.id.imageCam);
        Button btDelivered = (Button) findViewById(R.id.btDelivered);
        Button btPickedUp = (Button) findViewById(R.id.btPickedUp);
        Button btRescheduled = (Button) findViewById(R.id.btRescheduled);
        Button btRevoked = (Button) findViewById(R.id.btRevoked);
        Button btEnroute = (Button) findViewById(R.id.btEnroute);
        Button btNoShow = (Button) findViewById(R.id.btNoShow);    
        Button btDirection = (Button) findViewById(R.id.btDirection);
        Button btPosition = (Button)findViewById(R.id.btUpdateLoc);
        Button btTakePic = (Button) findViewById(R.id.btTakePic);
        
        Order order = ordersource.getOrder(delivery_id);
        ordersource.close();
        
        StringBuilder order_info = new StringBuilder()
        	.append(getResources().getText(R.string.delivery_id) + " :\n")
	        .append(delivery_id).append("\n")
	        .append(getResources().getText(R.string.transaction_id).toString() + " :\n")
	        .append(order.getMcTransId()).append("\n")
	        .append(getResources().getText(R.string.merchant) + " : ")
	        .append(order.getMcName()).append("\n")
	        .append(getResources().getText(R.string.shipping) + " :\n")
	        .append(order.getRecipient()).append("\n")
	        .append(order.getShipAddr()).append("\n")
	        .append("Billing : ").append(order.getCODCurr()).append(" ").append(order.getCODCost());
        
        txtDeliveryId.setText(order_info);
        
        Log.i("Delivery_id",delivery_id);
		String imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/" + delivery_id + ".jpg";
		File file = new File(imagefile);		
		if(file.exists()){
			displayPhoto(imagefile);
		}
		
        btDelivered.setOnClickListener(this);
        btEnroute.setOnClickListener(this);
        btNoShow.setOnClickListener(this);
        btPickedUp.setOnClickListener(this);
        btRescheduled.setOnClickListener(this);
        btRevoked.setOnClickListener(this);
        btDirection.setOnClickListener(this);
        btPosition.setOnClickListener(this);
        btTakePic.setOnClickListener(this);
        
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        provider = locman.getBestProvider(criteria, false);
		locman.requestLocationUpdates(provider, 400, 1, this);

        dialog = new ProgressDialog(this);

    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		locman.removeUpdates(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locman.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locman.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SendStatusProcess sendstatus = new SendStatusProcess();

		switch(v.getId()){
			case R.id.btDelivered:
				sendstatus.execute(new String[]{delivery_id, "delivered"});
				break;
			case R.id.btEnroute:
				sendstatus.execute(new String[]{delivery_id, "enroute"});
				break;
			case R.id.btNoShow:
				sendstatus.execute(new String[]{delivery_id, "noshow"});
				break;
			case R.id.btPickedUp:
				sendstatus.execute(new String[]{delivery_id, "pickedup"});
				break;
			case R.id.btRescheduled:
				sendstatus.execute(new String[]{delivery_id, "rescheduled"});
				break;
			case R.id.btRevoked:
				sendstatus.execute(new String[]{delivery_id, "revoked"});
				break;
			case R.id.btUpdateLoc:
				/*
				if(locman.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);					
				}else{
			        provider = locman.getBestProvider(criteria, false);
					locman.requestLocationUpdates(provider, 400, 1, this);
				}
				*/				
				locman.getLastKnownLocation(provider);
				txtDeliveryPos.setText("Requesting position...");
				break;
			case R.id.btTakePic:
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  				
				String saved_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/"; 				

				File folder = new File(saved_file);
				if(!folder.exists()){
					folder.mkdirs();
				}
				
				File file = new File(saved_file + delivery_id + ".jpg");
				
				if(file.exists()){
					file.delete();
					file = new File(saved_file + delivery_id + ".jpg");
				}
				
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));  
				startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			default:
				Toast.makeText(this, "Nothing to do", Toast.LENGTH_SHORT).show();
				break;
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
		    if (resultCode == RESULT_OK) {
				try {
					// We need to recyle unused bitmaps
					if (bitmap != null) {
						bitmap.recycle();
					}
					
					String imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/" + delivery_id + ".jpg";
					
					File file = new File(imagefile);
					
					if(file.exists()){
						displayPhoto(imagefile);
					}
					
				}catch(NullPointerException e){
					e.printStackTrace();
				}

		    	
		    	//Bitmap thumbnail = (Bitmap) data.getExtras().get();
		    	//imagecam.setImageBitmap(thumbnail);
		        //use imageUri here to access the image
		    } else if (resultCode == RESULT_CANCELED) {
		        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
		    } else {
		        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
		    }
		}
	}	
	
	private void displayPhoto(String imagefile){
        FileInputStream in;
        BufferedInputStream buf;
        try {
       	    in = new FileInputStream(imagefile);
            buf = new BufferedInputStream(in);
            bitmap = BitmapFactory.decodeStream(buf);
            imagecam.setImageBitmap(bitmap);
            if (in != null) {
            	in.close();
            }
            if (buf != null) {
            	buf.close();
            }
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }
	}

	
	private class SendStatusProcess extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_put_status).toString() + key;
			String txtResult = "";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				json.put("status", params[1]);
				json.put("delivery_id", params[0]);
				json.put("notes", editNote.getText());
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
				Toast.makeText(DeliveryDetailActivity.this,"Order status updated", Toast.LENGTH_LONG).show();
			}
			
			dialog.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Updating status");
			dialog.show();
		}
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = Double.toString(location.getLatitude());
		longitude = Double.toString(location.getLongitude());
		txtDeliveryPos.setText("Loc : " + latitude +","+longitude);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Activate provider " + provider,Toast.LENGTH_SHORT).show();				
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}