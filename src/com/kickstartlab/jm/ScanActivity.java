package com.kickstartlab.jm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ScanActivity extends Activity implements OnClickListener,LocationListener {
	TextView txtResult;
	Button scanPackage;
	private String delivery_id,sendResult = "";
	EditText editNote;
	LocationManager locman;
	String provider;
	Uri imageUri;
	ImageView imagecam;
	Bitmap bitmap;
	String latitude,longitude, altitude;
    Criteria criteria = new Criteria();
    TextView txtDeliveryPos,txtScanStatus;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;
    private SharedPreferences jexPrefs;
    OrderDataSource ordersource = new OrderDataSource(this);
    Order order = new Order();
	private ProgressDialog dialog;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanlayout);

        txtDeliveryPos = (TextView) findViewById(R.id.txtScanDetailPos);
        scanPackage = (Button) findViewById(R.id.btScan);
        txtResult = (TextView) findViewById(R.id.txtScanDetail);
        txtScanStatus = (TextView) findViewById(R.id.txtScanStatus);
        editNote = (EditText) findViewById(R.id.editScanNote);
        imagecam = (ImageView) findViewById(R.id.imageScanCam);
        Button btDelivered = (Button) findViewById(R.id.btScanDelivered);
        Button btPickedUp = (Button) findViewById(R.id.btScanPickedUp);
        Button btRescheduled = (Button) findViewById(R.id.btScanRescheduled);
        Button btRevoked = (Button) findViewById(R.id.btScanRevoked);
        Button btEnroute = (Button) findViewById(R.id.btScanEnroute);
        Button btNoShow = (Button) findViewById(R.id.btScanNoShow);    
        Button btDirection = (Button) findViewById(R.id.btScanDirection);
        Button btPosition = (Button)findViewById(R.id.btScanUpdateLoc);
        Button btTakePic = (Button) findViewById(R.id.btScanTakePic);

        btDelivered.setOnClickListener(this);
        btEnroute.setOnClickListener(this);
        btNoShow.setOnClickListener(this);
        btPickedUp.setOnClickListener(this);
        btRescheduled.setOnClickListener(this);
        btRevoked.setOnClickListener(this);
        btDirection.setOnClickListener(this);
        btPosition.setOnClickListener(this);
        btTakePic.setOnClickListener(this);
        
        scanPackage.setOnClickListener(this);
        
        //Log.i("Delivery_id",delivery_id);
		String imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/" + delivery_id + ".jpg";
		File file = new File(imagefile);		
		if(file.exists()){
			displayPhoto(imagefile);
		}
        
        
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        provider = locman.getBestProvider(criteria, false);
		locman.requestLocationUpdates(provider, 400, 1, this);

        dialog = new ProgressDialog(this);
		
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
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

    	
		if(requestCode == 0){
			if(resultCode == RESULT_OK){
				String result = intent.getStringExtra("SCAN_RESULT");
				String[] resultsplit = result.split("\\|");
				delivery_id = resultsplit[0];
				String trx_id = resultsplit[1];
				//do not forget this ! open your datasource
		        ordersource.open();

		        Order order = ordersource.getOrder(delivery_id);
		        
		        if(order == null){
		            txtResult.setText(delivery_id);
		            txtScanStatus.setText("No Match");
		        }else{
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
		        
		            txtResult.setText(order_info);
		            txtScanStatus.setText("Match Found");
		        }
		        ordersource.close();
		        				
			}else if(resultCode == RESULT_CANCELED){
				txtResult.setText("");				
	            txtScanStatus.setText("Scan cancelled");
			}
		}
	}

	@Override
	public void onClick(View v) {
		SendStatusProcess sendstatus = new SendStatusProcess();
		switch(v.getId()){
			case R.id.btScan:
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
				//Toast.makeText(this, "Package scanned", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btScanDelivered:
				sendstatus.execute(new String[]{delivery_id, "delivered"});
				break;
			case R.id.btScanEnroute:
				sendstatus.execute(new String[]{delivery_id, "enroute"});
				break;
			case R.id.btScanNoShow:
				sendstatus.execute(new String[]{delivery_id, "noshow"});
				Toast.makeText(this, "no one show up for " + delivery_id, Toast.LENGTH_SHORT).show();
				break;
			case R.id.btScanPickedUp:
				sendstatus.execute(new String[]{delivery_id, "pickedup"});
				break;
			case R.id.btScanRescheduled:
				sendstatus.execute(new String[]{delivery_id, "rescheduled"});
				break;
			case R.id.btScanRevoked:
				sendstatus.execute(new String[]{delivery_id, "revoked"});
				break;
			case R.id.btScanUpdateLoc:
				locman.getLastKnownLocation(provider);
				txtDeliveryPos.setText("Requesting position...");
				break;
			case R.id.btScanTakePic:
				
				//Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
				
				//define the file-name to save photo taken by Camera activity
				
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

				//Toast.makeText(this, saved_file, Toast.LENGTH_LONG).show();
				startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			default:
				Toast.makeText(this, "None to Do", Toast.LENGTH_SHORT).show();
				break;
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
				Toast.makeText(ScanActivity.this,"Order status updated", Toast.LENGTH_LONG).show();
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
		latitude = Double.toString(location.getLatitude());
		longitude = Double.toString(location.getLongitude());
		txtDeliveryPos.setText("Loc : " + latitude +","+longitude);		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Activate provider " + provider,Toast.LENGTH_SHORT).show();						
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}	
}
