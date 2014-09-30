package com.kickstartlab.jm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.Toast;

public class DeliveryListActivity extends Activity implements OnItemClickListener,OnClickListener{
	
	protected SimpleCursorAdapter adapter;
	protected OrderListAdapter aadapter;

    ArrayList<Order> mArrayList;
	
    OrderDataSource ordersource = new OrderDataSource(this);
    
    TextView orderCount;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverylistlayout);
        ListView list = (ListView) findViewById(R.id.listDelivery);
        Button btScanList = (Button) findViewById(R.id.btScanList);
        orderCount = (TextView) findViewById(R.id.orderCount);
        
        TextView vCOD = (TextView) findViewById(R.id.codCount);
        TextView vCCOD = (TextView) findViewById(R.id.ccodCount);
        TextView vDO = (TextView) findViewById(R.id.doCount);
        TextView vPS = (TextView) findViewById(R.id.psCount);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        ordersource.open();
        
        Cursor cursor = ordersource.getAllOrdersSortMc();
                
        Integer sq = 0;        
        mArrayList = new ArrayList<Order>();

    	Log.i("CURSOR_COUNT", String.valueOf( cursor.getCount() )   );
    	
    	orderCount.setText(String.valueOf( cursor.getCount() ));
    	
    	
    	
        /*
        while(cursor.moveToNext()){
        	sq++;
        	Order ord = new Order();
        	ord.setSeq(sq);
        	ord.setDeliveryId(cursor.getString( cursor.getColumnIndex(JayonDbHelper.COLUMN_ORDER_ID) ));
        	ord.setMcName(cursor.getString(cursor.getColumnIndex(JayonDbHelper.COLUMN_MERCHANT_NAME)));
        	ord.setByName(cursor.getString(cursor.getColumnIndex(JayonDbHelper.COLUMN_BUYER_NAME)));
        	ord.setAs_date(cursor.getString(cursor.getColumnIndex(JayonDbHelper.COLUMN_ASSIGNED_DATE)));
        	ord.setDeliveryId(cursor.getString( 1 ));
        	ord.setMcName(cursor.getString(3));
        	ord.setByName(cursor.getString(6));
        	ord.setAs_date(cursor.getString(cursor.getColumnIndex(JayonDbHelper.COLUMN_ASSIGNED_DATE)));
        	        	
        	mArrayList.add(ord);
        }
    	 */
    	Integer codcount = 0;
    	Integer ccodcount = 0;
    	Integer docount = 0;
    	Integer pscount = 0;
    	
    	cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
        	
        	Log.i("dtype index",String.valueOf(cursor.getColumnIndex(JayonDbHelper.COLUMN_DELIVERY_TYPE) ) );
        	
        	String dtype = cursor.getString( cursor.getColumnIndex(JayonDbHelper.COLUMN_DELIVERY_TYPE) );
        	
        	Log.i("dtype", dtype);

        	if(dtype.equalsIgnoreCase("COD")){
        		codcount++;        		
        	}
        	
        	if(dtype.equalsIgnoreCase("CCOD")){
        		ccodcount++;
        	}
        	if(dtype.equalsIgnoreCase("DO") || dtype.equalsIgnoreCase("Delivery Only")){
        		docount++;
        	}
        	if(dtype.equalsIgnoreCase("PS")){
        		pscount++;
        	}
        	
        	cursor.moveToNext();

        }
    	
        vCOD.setText("COD : " + codcount.toString());
        vCCOD.setText("CCOD : " + ccodcount.toString());
        vDO.setText("DO : " + docount.toString());
        vPS.setText("PS : " + pscount.toString());
    	
        Log.d("sequence", String.valueOf(sq));
        
        
        adapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.listitemlayout, 
        		cursor,
        		new String[]{
        			JayonDbHelper.COLUMN_ASSIGNED_DATE,
        			JayonDbHelper.COLUMN_MERCHANT_NAME,
        			JayonDbHelper.COLUMN_RECIPIENT_NAME,
        			JayonDbHelper.COLUMN_ORDER_ID
        		}
        		,new int[]{
        			R.id.delivery_date,
        			R.id.merchant_name,
        			R.id.buyer_name
        		});
        
        //ArrayAdapter<Order> aadapter = new ArrayAdapter<Order>(this,R.layout.listitemlayout,arr);
        
        //aadapter = new OrderListItem(this,R.layout.listitemlayout, mArrayList);
        //aadapter = new OrderListAdapter(this, mArrayList);
        
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
        Cursor cursor = ordersource.getAllOrdersSortMc();      
    	orderCount.setText(String.valueOf( cursor.getCount() ));

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
		//Order ord = (Order) aadapter.getItem(pos);
		//String item = ord.getDeliveryId();
		
		Log.i("delivery id", item);
		Intent nextIntent = new Intent(v.getContext(),DeliveryDetailActivity.class);
		nextIntent.putExtra("delivery_id", item);
		startActivity(nextIntent);
		//Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
	}



	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btScanList){
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "SCAN_MODE");
			startActivityForResult(intent, 0);
			//Toast.makeText(getApplicationContext(), "Scan Now",Toast.LENGTH_SHORT).show();
		}
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
		if(requestCode == 0){
			if(resultCode == RESULT_OK){
				String result = intent.getStringExtra("SCAN_RESULT");
				
				String delivery_id = "";
				
				if(result.indexOf("|") > 0 ){
					String[] resultsplit = result.split("\\|");
					delivery_id = resultsplit[0];					
				}else{
					delivery_id = result;										
				}
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
