package com.kickstartlab.jm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	private String delivery_id;
	private EditText editNote;
	LocationManager locman;
	String provider;
	Uri imageUri;
	ImageView imagecam;
	Bitmap bitmap,upimage;
	String latitude,longitude,altitude;
    Criteria criteria = new Criteria();
    TextView txtDeliveryPos;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;
    private SharedPreferences jexPrefs;
	private ProgressDialog dialog;
	Integer sync_id;
	LogDataSource logdatasource = new LogDataSource(this);
	LogData lastlog = new LogData();
	String last;
	private static final int DIRECTION_DIALOG_ID = 1;
	private static final int UPLOAD_DIALOG_ID = 2;
	String imagefile,direction;
	Button btUploadPic;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        OrderDataSource ordersource = new OrderDataSource(this);
        ordersource.open();
        logdatasource.open();

        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        
        Bundle extras = getIntent().getExtras();
        delivery_id = extras.getString("delivery_id");
        setContentView(R.layout.detailedlayout);
        
        txtDeliveryPos = (TextView) findViewById(R.id.txtDetailPos);
        TextView txtDeliveryId = (TextView) findViewById(R.id.txtDetail);
        TextView txtDeliveryType = (TextView) findViewById(R.id.txtDeliveryType);

        editNote = (EditText) findViewById(R.id.editNote);
        imagecam = (ImageView) findViewById(R.id.imageCam);
        Button btDelivered = (Button) findViewById(R.id.btDelivered);
        //Button btPickedUp = (Button) findViewById(R.id.btPickedUp);
        Button btRescheduled = (Button) findViewById(R.id.btRescheduled);
        Button btRevoked = (Button) findViewById(R.id.btDeliRevoked);
        //Button btEnroute = (Button) findViewById(R.id.btEnroute);
        Button btNoShow = (Button) findViewById(R.id.btNoShow);    
        //Button btDirection = (Button) findViewById(R.id.btDirection);
        Button btPosition = (Button)findViewById(R.id.btUpdateLoc);
        Button btTakePic = (Button) findViewById(R.id.btTakePic);
        Button btUploadNote = (Button) findViewById(R.id.btUploadNote);
        btUploadPic = (Button) findViewById(R.id.btUploadPic);
        
        Order order = ordersource.getOrder(delivery_id);
        ordersource.close();

		try {
			lastlog = logdatasource.getLogData(delivery_id);
			last = lastlog.getStatus();			
		} catch (Exception e) {
			last = "new";
			e.printStackTrace();
		}finally{
			logdatasource.close();
		}

        direction = order.getDirection();
		Log.i("DIR",direction);
				
		Locale locale  = new Locale("id", "ID");
		String pattern = "###,###,###.##";

		DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		formatter.applyPattern(pattern);		
		
        StringBuilder recipient = new StringBuilder()
	        .append(order.getRecipient()).append("\n")
	        .append(order.getShipAddr()).append("\n")
	        .append(order.getBy_zone()).append("\n")
	        .append(order.getBy_city());        

        TextView txtBuyer = (TextView) findViewById(R.id.txtBuyer);
        TextView txtRecipient = (TextView) findViewById(R.id.txtRecipient);
        TextView txtPhone = (TextView) findViewById(R.id.txtPhone);
        TextView txtTotalValue = (TextView) findViewById(R.id.txtTotalValue);
        TextView txtDeliveryCost = (TextView) findViewById(R.id.txtDeliveryCost);
        TextView txtCODSurcharge = (TextView) findViewById(R.id.txtCODSurcharge);

        txtBuyer.setText(order.getByName());
        txtRecipient.setText(recipient.toString());
        txtPhone.setText("+" + order.getByPhone());
        txtTotalValue.setText(formatter.format( Double.parseDouble(order.getTot_price()) ));
        txtDeliveryCost.setText(formatter.format( Double.parseDouble(order.getDelivery_cost()) ));
        txtCODSurcharge.setText(formatter.format( Double.parseDouble(order.getCODCost()) ));
        
        txtDeliveryType.setText(order.getDl_type());
        if("COD".equalsIgnoreCase(order.getDl_type()) || "CCOD".equalsIgnoreCase(order.getDl_type()) ){
            txtDeliveryType.setBackgroundColor(Color.RED);
        }else{
            txtDeliveryType.setBackgroundColor(Color.LTGRAY);
        }
        
        Log.i("Delivery_id",delivery_id);
        
		imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/" + delivery_id + ".jpg";
		
		File file = new File(imagefile);		
		
		if(file.exists()){
			Toast.makeText(this, "Photo found", Toast.LENGTH_SHORT).show();
			displayPhoto(imagefile);
		}else{
			imagecam.setVisibility(View.GONE);
			btUploadPic.setVisibility(View.GONE);
		}
		
        btDelivered.setOnClickListener(this);
        //btEnroute.setOnClickListener(this);
        btNoShow.setOnClickListener(this);
        //btPickedUp.setOnClickListener(this);
        btRescheduled.setOnClickListener(this);
        btRevoked.setOnClickListener(this);
        //btDirection.setOnClickListener(this);
        btPosition.setOnClickListener(this);
        btTakePic.setOnClickListener(this);
        btUploadNote.setOnClickListener(this);
        btUploadPic.setOnClickListener(this);
        
        //btTakePic.setEnabled(false);

        btDelivered.setEnabled(disableByStatus(last,"delivered"));
        //btEnroute.setEnabled(disableByStatus(last,"enroute"));
        btNoShow.setEnabled(disableByStatus(last,"noshow"));
        //btPickedUp.setEnabled(disableByStatus(last,"pickedup"));
        btRescheduled.setEnabled(disableByStatus(last,"rescheduled"));
        btRevoked.setEnabled(disableByStatus(last,"revoked"));
        
        
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        sync_id = jexPrefs.getInt("syncsession",1);
        
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
			/*
			case R.id.btEnroute:
				sendstatus.execute(new String[]{delivery_id, "enroute"});
				break;
			*/
			case R.id.btNoShow:
				sendstatus.execute(new String[]{delivery_id, "noshow"});
				break;
			/*
			case R.id.btPickedUp:
				sendstatus.execute(new String[]{delivery_id, "pickedup"});
				break;
			*/
			case R.id.btRescheduled:
				sendstatus.execute(new String[]{delivery_id, "rescheduled"});
				break;
			case R.id.btDeliRevoked:
				sendstatus.execute(new String[]{delivery_id, "revoked"});
				break;
			/*
			case R.id.btDirection:
				showDialog(DIRECTION_DIALOG_ID);
				break;
			*/
			case R.id.btUploadNote:
				sendstatus.execute(new String[]{delivery_id, "syncnote"});
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
			case R.id.btUploadPic:
				imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/" + delivery_id + ".jpg";
				
				File cfile = new File(imagefile);		
				
				if(cfile.exists()){
					uploadPhoto(imagefile);
				}else{
					Toast.makeText(this, "Photo not found", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				Toast.makeText(this, "Nothing to do", Toast.LENGTH_SHORT).show();
				break;
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		//return super.onCreateDialog(id);
		Builder builder = new AlertDialog.Builder(this);
		
		if(id == DIRECTION_DIALOG_ID){
	        builder.setTitle(R.string.dirtitle);
	        builder.setMessage(direction);
	        
	        builder.setPositiveButton(android.R.string.ok, 
	        		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//Toast.makeText(getApplicationContext(), "Ok !", Toast.LENGTH_SHORT).show();
						}
					});

	        builder.setNegativeButton(android.R.string.no, 
	        		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//
						}
					});
			
		}else if(id == UPLOAD_DIALOG_ID){
	        builder.setTitle(R.string.uploadtitle);
	        builder.setMessage(R.string.uploadconfirm);
	        
	        builder.setPositiveButton(android.R.string.ok, 
	        		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							uploadPhoto(imagefile);
							//Toast.makeText(getApplicationContext(), "Ok !", Toast.LENGTH_SHORT).show();
						}
					});

	        builder.setNegativeButton(R.string.notnow, 
	        		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(getApplicationContext(), "Picture will be uploaded later, you may use Upload button under the picture.", Toast.LENGTH_SHORT).show();
						}
					});

		}
        
        return builder.create();				
	}

	public String getCurrentDate(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public boolean disableByStatus(String last, String status){
		
		Boolean result = true;

		if(last.equalsIgnoreCase("enroute")){
			if(status.equalsIgnoreCase("pickedup")){
				result = false;
			}else{
				result = true;
			}
		}

		if(last.equalsIgnoreCase("delivered") || last.equalsIgnoreCase("noshow") || last.equalsIgnoreCase("revoked") || last.equalsIgnoreCase("rescheduled")){
			if(status.equalsIgnoreCase("pickedup") || status.equalsIgnoreCase("enroute") 
					|| status.equalsIgnoreCase("delivered")
					|| status.equalsIgnoreCase("noshow")
					|| status.equalsIgnoreCase("revoked")
					|| status.equalsIgnoreCase("rescheduled")){
				result = false;
			}else{
				result = true;
			}
		}
		
		if(last.equalsIgnoreCase(status)){
			result = false;
		}
		
		return result;
				
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
					
					imagefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/jayonex/" + delivery_id + ".jpg";
					
					File file = new File(imagefile);
					
					if(file.exists()){
						displayPhoto(imagefile);
						imagecam.setVisibility(View.VISIBLE);
						btUploadPic.setVisibility(View.VISIBLE);
						showDialog(UPLOAD_DIALOG_ID);
					}
					
				}catch(NullPointerException e){
					e.printStackTrace();
				}

		    	
		    	//Bitmap thumbnail = (Bitmap) data.getExtras().get();
		    	//imagecam.setImageBitmap(thumbnail);
		        //use imageUri here to access the image
		    } else if (resultCode == RESULT_CANCELED) {
		        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
		    } else {
		        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
		    }
		}
	}	
	
	private void uploadPhoto(String imagefile){
		UploadPicture uploadpicture = new UploadPicture();
		uploadpicture.execute(new String[]{delivery_id, imagefile});
	}
	
	private void displayPhoto(String imagefile){
        FileInputStream in;
        BufferedInputStream buf;
        try {
       	    in = new FileInputStream(imagefile);
            buf = new BufferedInputStream(in);
            bitmap = BitmapFactory.decodeStream(buf);
            
            int nwidth = bitmap.getWidth()/2;
            int nheight = bitmap.getHeight()/2;
          
            //Half Scaled
            bitmap = Bitmap.createScaledBitmap(bitmap,nwidth, nheight, false);
                        
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
			Integer sync_id = jexPrefs.getInt("syncsession",1);
			
			String now = getCurrentDate();
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				json.put("status", params[1]);
				json.put("delivery_id", params[0]);
				json.put("capture_time", now);
				json.put("notes", editNote.getText());
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
			
			
			
			LogData logdata = new LogData();
			
			logdata.setStatus(params[1]);
			logdata.setDeliveryId(params[0]);
			logdata.setCaptureTime(now);
			logdata.setDeliveryNote(editNote.getText().toString());
			logdata.setSyncId(sync_id.toString());
			logdata.setLatitude(latitude);
			logdata.setLongitude(longitude);
			
			logdatasource.open();
			logdatasource.saveLog(logdata);
			logdatasource.close();
			
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

	private class UploadPicture extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... params) {

			String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_upload_pic).toString() + key;
			String txtResult = "";
			
			if(checkConnection() == 1){
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				
				try{
					//compress image first
					Bitmap bm = BitmapFactory.decodeFile(params[1]);				
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
		            bm.compress(CompressFormat.JPEG, 25, bos);
		            byte[] bdata = bos.toByteArray();
		            ByteArrayBody bbody = new ByteArrayBody(bdata, delivery_id +".jpg");
					
					MultipartEntity entity = new MultipartEntity();
					entity.addPart("delivery_id", new StringBody(params[0]));
					entity.addPart("receiverpic", bbody);
					httppost.setEntity(entity);
					
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
				
			}else{
				txtResult = "Connection Lost";
			}

			return txtResult;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("JSONResult",result);
			if(result.indexOf("OK:") > 0){					

				LogData logdata = new LogData();
				Integer sync_id = jexPrefs.getInt("syncsession",1);
				
				logdata.setStatus("upload_pic");
				logdata.setDeliveryId(delivery_id);
				logdata.setCaptureTime(getCurrentDate());
				logdata.setDeliveryNote("Picture Uploaded");
				logdata.setSyncId(sync_id.toString());
				logdata.setLatitude(latitude);
				logdata.setLongitude(longitude);
				
				logdatasource.open();
				logdatasource.saveLog(logdata);
				logdatasource.close();
				
				Toast.makeText(DeliveryDetailActivity.this,"Picture uploaded", Toast.LENGTH_LONG).show();
			}
						
			dialog.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Uploading picture");
			dialog.show();
		}		
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
		
	}

}
