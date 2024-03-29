package com.kickstartlab.jm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
//import android.widget.Toast;
import android.widget.Toast;

public class RouteListActivity extends Activity implements OnItemClickListener,OnClickListener{
	
	protected SimpleCursorAdapter adapter;
    OrderDataSource ordersource = new OrderDataSource(this);
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routelistlayout);
        ListView list = (ListView) findViewById(R.id.listDelivery);
        Button btScanList = (Button) findViewById(R.id.btScanList);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        ordersource.open();
        
        Cursor cursor = ordersource.getAllOrdersSortSeq();
        
        adapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.routeitemlayout, 
        		cursor,
        		new String[]{
        			JayonDbHelper.COLUMN_SHIP_SEQ,
        			JayonDbHelper.COLUMN_ASSIGNED_DATE,
        			JayonDbHelper.COLUMN_BUYER_NAME,
        			JayonDbHelper.COLUMN_MERCHANT_NAME,
        			JayonDbHelper.COLUMN_BUYER_ZONE,
        			JayonDbHelper.COLUMN_BUYER_CITY,
        			JayonDbHelper.COLUMN_ORDER_ID
        		}
        		,new int[]{
        			R.id.sequence,
           			R.id.delivery_date,        			
        			R.id.merchant_name,
        			R.id.buyer_name,
        			R.id.buyer_zone,
        			R.id.buyer_city
        		});
        
        list.setOnItemClickListener(this);
        btScanList.setOnClickListener(this);
        
        list.setAdapter(adapter);
        cursor.close();
        ordersource.close();
    }
    

    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        ordersource.open();        
        Cursor cursor = ordersource.getAllOrdersSortSeq();        
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
        ordersource.close();
	}



	@Override
	public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
		//String item = (String) getListAdapter().getItem(pos);
		//Cursor cur = (Cursor) getListAdapter().getItem(pos);
		Cursor cur = (Cursor) adapter.getItem(pos);
		String item = cur.getString(cur.getColumnIndex(JayonDbHelper.COLUMN_ORDER_ID));
		Intent nextIntent = new Intent(v.getContext(),DeliveryDetailActivity.class);
		nextIntent.putExtra("delivery_id", item);
		startActivity(nextIntent);
		//Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
	}



	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btScanList){
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
			//Toast.makeText(getApplicationContext(), "Scan Now",Toast.LENGTH_SHORT).show();
		}
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
		if(requestCode == 0){
			if(resultCode == RESULT_OK){
				String result = intent.getStringExtra("SCAN_RESULT");
				String[] resultsplit = result.split("\\|");
				String delivery_id = resultsplit[0];
				//do not forget this ! open your datasource
		        ordersource.open(); 
		        
		        Order order = ordersource.getOrder(delivery_id);

	        	Log.i("ID from scan",delivery_id);
		        
		        if(order == null){
					Toast.makeText(getApplicationContext(), "No Match Found",Toast.LENGTH_SHORT).show();
		        }else{
					//Toast.makeText(getApplicationContext(), delivery_id + " found",Toast.LENGTH_SHORT).show();		        	
					Intent nextIntent = new Intent(this,DeliveryDetailActivity.class);
					nextIntent.putExtra("delivery_id", delivery_id);
					startActivity(nextIntent);
		        }
		        				
			}else if(resultCode == RESULT_CANCELED){
				Toast.makeText(getApplicationContext(), "Scan Cancelled",Toast.LENGTH_SHORT).show();
			}
		}
	}
    
}
