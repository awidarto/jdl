package com.kickstartlab.jm;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
//import android.widget.Toast;

public class DeliveryListActivity extends ListActivity implements OnItemClickListener{
	
	protected SimpleCursorAdapter adapter;
    OrderDataSource ordersource = new OrderDataSource(this);
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        ordersource.open();
        
        Cursor cursor = ordersource.getAllOrders();
        
        adapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.listitemlayout, 
        		cursor,
        		new String[]{
        			JayonDbHelper.COLUMN_ORDER_ID,
        			JayonDbHelper.COLUMN_TRANS_ID,
        			JayonDbHelper.COLUMN_ASSIGNED_DATE,
        			JayonDbHelper.COLUMN_MERCHANT_NAME
        		}
        		,new int[]{
        			R.id.delivery_id,
        			R.id.trans_id,
        			R.id.delivery_date,
        			R.id.merchant_name
        		});
        
        getListView().setOnItemClickListener(this);
        
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, vals);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitemlayout,R.id.label,vals);
        setListAdapter(adapter);
        cursor.close();
    }
    

    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        ordersource.open();        
        Cursor cursor = ordersource.getAllOrders();        
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
	}



	@Override
	public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
		//String item = (String) getListAdapter().getItem(pos);
		Cursor cur = (Cursor) getListAdapter().getItem(pos);
		//Cursor cur = (Cursor) adapter.getItem(pos);
		String item = cur.getString(cur.getColumnIndex(JayonDbHelper.COLUMN_ORDER_ID));
		Intent nextIntent = new Intent(v.getContext(),DeliveryDetailActivity.class);
		nextIntent.putExtra("delivery_id", item);
		startActivity(nextIntent);
		//Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
	}
    
}
