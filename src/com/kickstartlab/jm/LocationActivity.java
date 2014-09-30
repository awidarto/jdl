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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.widget.ZoomControls;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.maps.android.ui.IconGenerator;

@SuppressWarnings("deprecation")
public class LocationActivity extends FragmentActivity implements OnClickListener,LocationListener {
	TextView textcurrentloc;
	LocationManager locman;
	String provider,sendResult = "";
	String latitude,longitude, altitude;
	MapView mapview;
	MapController mc;
    Drawable greenPoint;
    Drawable redPoint;
    CheckBox chkUseGps,chkSatView;
    Criteria criteria = new Criteria();
    private SharedPreferences jexPrefs;
	private ProgressDialog dialog;
    OrderDataSource ordersource = new OrderDataSource(this);

    private GoogleMap googleMap;
    
    static final LatLng JAKARTA = new LatLng(-6.175286, 106.827106);
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routemaplayout);
        
        textcurrentloc = (TextView) findViewById(R.id.my_location);
        
        GoogleMapOptions options = new GoogleMapOptions();
        
        try {
            // Loading map
            initializeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        Button reportLocation = (Button) findViewById(R.id.btReportLocation);
        Button getmylocation = (Button) findViewById(R.id.btMyPosition);
        chkUseGps = (CheckBox)findViewById(R.id.checkForceGps);
        chkSatView = (CheckBox)findViewById(R.id.checkSatView);
        textcurrentloc = (TextView) findViewById(R.id.txtCurrentLoc);

        reportLocation.setOnClickListener(this);
        getmylocation.setOnClickListener(this);
        
        chkUseGps.setOnClickListener(this);
        chkSatView.setOnClickListener(this);
        */

        /*Create marker icon*/
        greenPoint = this.getResources().getDrawable(R.drawable.geo_brown);
        redPoint = this.getResources().getDrawable(R.drawable.geo_red);
        
        
        /*Create map view*/
        /*
        mapview = (MapView) findViewById(R.id.map);
        mapview.setBuiltInZoomControls(true);
        ZoomButtonsController zbc = mapview.getZoomButtonsController();
        ViewGroup container = zbc.getContainer();
        for (int i = 0; i < container.getChildCount(); i++) {
          View child = container.getChildAt(i);
          if (child instanceof ZoomControls) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.TOP|Gravity.RIGHT;
            child.requestLayout();
            break;
          } 
        }
        mc = mapview.getController();
 
        //set default geopoint, Monas central Jakarta
        Double dlat = Double.parseDouble("-6.175286");
        Double dlon = Double.parseDouble("106.827106");
        GeoPoint p = new GeoPoint((int)(dlat * 1E6), (int)(dlon*1E6));
        
        //go to default geopoint
        mc.animateTo(p);
        mc.setZoom(13);
        
        List<Overlay> mapoverlays = mapview.getOverlays();
        JayonItemOverlay itemoverlay = new JayonItemOverlay(greenPoint, this);
        OverlayItem overlayitem = new OverlayItem(p, "Jayon Rider", "I'm here");
        itemoverlay.addOverlay(overlayitem);
        mapoverlays.clear();
        mapoverlays.add(itemoverlay);
		*/
        ordersource.open();
        
        Cursor cursor = ordersource.getAllOrdersSortSeq();

        Integer idxlat = cursor.getColumnIndex(JayonDbHelper.COLUMN_SHIP_LAT);
		Integer idxlon = cursor.getColumnIndex(JayonDbHelper.COLUMN_SHIP_LON);
        
        if(cursor.getCount() > 0){
        	cursor.moveToFirst();
        	Double prevLat = 0.0;
        	Double prevLon = 0.0;
        	while(cursor.isAfterLast() == false){
    			Log.i("idxlat", idxlat.toString());
    			Log.i("idxlon", idxlon.toString());
        		
        		if( cursor.isNull(idxlat) || cursor.isNull(idxlon) ){
        			Log.i("lat or lon", "is null");
        		}else{
        			String elat = cursor.getString(idxlat);
            		String elon = cursor.getString(idxlon);
            		Log.i("lat", elat);
            		Log.i("lon", elon);
        			    		
            		if( "".equalsIgnoreCase(elat) || "".equalsIgnoreCase(elon) || "null".equalsIgnoreCase(elat) || "null".equalsIgnoreCase(elon) ){
            			
            		}else{
                		Double dlat = Double.valueOf(elat);
                		Double dlon = Double.valueOf(elon);
                		String recv = cursor.getString(cursor.getColumnIndex(JayonDbHelper.COLUMN_BUYER_NAME));
                		Integer icseq = cursor.getInt(cursor.getColumnIndex(JayonDbHelper.COLUMN_SHIP_SEQ));
                		                		
                		if(googleMap != null){        			
                			if(cursor.isFirst()){
                				prevLat = dlat;
                				prevLon = dlon;
                			}else{
                				PolylineOptions popt = new PolylineOptions()
                			     .add(new LatLng(prevLat,prevLon), new LatLng(dlat, dlon))
                			     .width(5)
                			     .color(Color.RED);
                				googleMap.addPolyline(popt);
                				prevLat = dlat;
                				prevLon = dlon;
                			}
                			
                			IconGenerator iconGen = new IconGenerator(this);
                			iconGen.setStyle(IconGenerator.STYLE_ORANGE);
                			
                			String lblIcon = String.valueOf(icseq);
                			Bitmap bmIcon = iconGen.makeIcon(lblIcon);
                					
                			
                			googleMap.addMarker(new MarkerOptions()
                				.position(new LatLng(dlat, dlon))
                				.icon(BitmapDescriptorFactory.fromBitmap(bmIcon))
                				.title(recv) );
                		}        			
            		}
            		
        			
        		}
        		cursor.moveToNext();
        	}
        }

        ordersource.close();
        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locman.getBestProvider(criteria, false);
        locman.getLastKnownLocation(provider);

        dialog = new ProgressDialog(this);
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.btReportLocation:
				Location rloc = locman.getLastKnownLocation(provider);
				SendStatusProcess sendstatus = new SendStatusProcess();
				sendstatus.execute(new Location[]{rloc});
				break;
			case R.id.btMyPosition:
		        provider = locman.getBestProvider(criteria, false);
				locman.requestLocationUpdates(provider, 100, 1, this);
				textcurrentloc.setText("Requesting position...");
				break;
			case R.id.checkSatView:
				mapview.setSatellite(chkSatView.isChecked());
				Toast.makeText(this, "Set Satellite View", Toast.LENGTH_SHORT).show();
				break;
			case R.id.checkForceGps:
				if(chkUseGps.isChecked()){
					criteria.setAccuracy(Criteria.ACCURACY_FINE);
					Toast.makeText(this, "Set Force GPS", Toast.LENGTH_SHORT).show();
				}else{
					criteria.setAccuracy(Criteria.ACCURACY_COARSE);
					Toast.makeText(this, "Unset Force GPS", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				Toast.makeText(this, "None to Do", Toast.LENGTH_SHORT).show();
				break;
		}
		
	}
	
	public void displayLocation(Location location){
		if(location.equals(null)){
			textcurrentloc.setText("Position Unknown");
		}else{
			latitude = Double.toString(location.getLatitude());
			textcurrentloc.setText("Loc : " + location.getLatitude() +","+ location.getLongitude());
		}
	}

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }else{
            	googleMap.setMyLocationEnabled(true);
            	googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            	googleMap.getUiSettings().setCompassEnabled(true);
            	googleMap.getUiSettings().setTiltGesturesEnabled(true);
            	//googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JAKARTA, 15));
            	//googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
            	
            	CameraPosition cameraPosition = new CameraPosition.Builder()
	                .target(JAKARTA)      // Sets the center of the map to Mountain View
	                .zoom(11)                   // Sets the zoom
	                //.bearing(45)                // Sets the orientation of the camera to east
	                .tilt(45)                   // Sets the tilt of the camera to 30 degrees
	                .build();                   // Creates a CameraPosition from the builder
            	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            	
            }
        }
    }	
	
	public class SendStatusProcess extends AsyncTask<Location, Void, String>{

		@Override
		protected String doInBackground(Location... params) {
			String key = jexPrefs.getString("devkey", getResources().getText(R.string.api_key).toString());
			String url = getResources().getText(R.string.api_url).toString() + getResources().getText(R.string.api_put_loc).toString() + key;
			String txtResult = "";
			Location loc = params[0];
			
			String lat = Double.toString(loc.getLatitude());
			String lon = Double.toString(loc.getLongitude());
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			JSONObject json = new JSONObject();
			try{
				json.put("key", key);
				json.put("lat", lat);
				json.put("lon", lon);

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
			if(result.indexOf("OK:") > 0){					
				Toast.makeText(LocationActivity.this,"Position reported", Toast.LENGTH_LONG).show();
			}
			
			dialog.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Reporting position");
			dialog.show();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		locman.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		locman.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locman.removeUpdates(this);
	}


	@Override
	public void onLocationChanged(Location location) {
        
		displayLocation(location);

        /*
        GeoPoint p = new GeoPoint((int)(location.getLatitude() * 1E6), (int)(location.getLongitude()*1E6)); 
        List<Overlay> mapoverlays = mapview.getOverlays();
        JayonItemOverlay itemoverlay = new JayonItemOverlay(greenPoint, this);
        OverlayItem overlayitem = new OverlayItem(p, "Jayon Rider", "I'm here");
        itemoverlay.addOverlay(overlayitem);
        mapoverlays.clear();
        mapoverlays.add(itemoverlay);
        
        mc.animateTo(p);
        
		*/
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
