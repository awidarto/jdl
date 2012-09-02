package com.kickstartlab.jm;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class OrderDataSource {
	
	private SQLiteDatabase database;
	private JayonDbHelper dbHelper;
	private String[] allColumns = {
			JayonDbHelper.COLUMN_ID,
			JayonDbHelper.COLUMN_ORDER_ID,
			JayonDbHelper.COLUMN_TRANS_ID,
			JayonDbHelper.COLUMN_MERCHANT_NAME,
			JayonDbHelper.COLUMN_SHIP_ADDR,
			JayonDbHelper.COLUMN_BUYER_NAME,
			JayonDbHelper.COLUMN_BUYER_PHONE,
			JayonDbHelper.COLUMN_RECIPIENT_NAME,
			JayonDbHelper.COLUMN_COD_COST,
			JayonDbHelper.COLUMN_COD_CURR,
			JayonDbHelper.COLUMN_ASSIGNED_DATE,
			JayonDbHelper.COLUMN_ASSIGNED_TIMESLOT,
			JayonDbHelper.COLUMN_DELIVERY_STATUS,
			JayonDbHelper.COLUMN_SHIP_DIR
		};
	
	public OrderDataSource(Context context) {
		dbHelper = new JayonDbHelper(context);
	}
	
	public void open(){
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void emptyData(){
		database.delete(JayonDbHelper.TABLE_ORDERS, "1", null);
	}
	
	public Integer updateStatus(String order_id,String status){
		ContentValues args = new ContentValues();
		args.put(JayonDbHelper.COLUMN_DELIVERY_STATUS, status);
		Integer result = database.update(JayonDbHelper.TABLE_ORDERS, args, JayonDbHelper.COLUMN_ORDER_ID +"=?", new String[] { order_id });
		return result;
	}

	public void saveOrderJSON(JSONObject jsonObj){
		try{
			ContentValues val = new ContentValues();

			val.put(JayonDbHelper.COLUMN_ORDER_ID, jsonObj.getString(JayonDbHelper.COLUMN_ORDER_ID));
			val.put(JayonDbHelper.COLUMN_TRANS_ID, jsonObj.getString(JayonDbHelper.COLUMN_TRANS_ID));
			val.put(JayonDbHelper.COLUMN_MERCHANT_NAME, jsonObj.getString(JayonDbHelper.COLUMN_MERCHANT_NAME));
			val.put(JayonDbHelper.COLUMN_MERCHANT_STREET, jsonObj.getString(JayonDbHelper.COLUMN_MERCHANT_STREET));
			val.put(JayonDbHelper.COLUMN_MERCHANT_ZONE, jsonObj.getString(JayonDbHelper.COLUMN_MERCHANT_ZONE));
			val.put(JayonDbHelper.COLUMN_MERCHANT_CITY, jsonObj.getString(JayonDbHelper.COLUMN_MERCHANT_CITY));
			val.put(JayonDbHelper.COLUMN_MERCHANT_PROVINCE, jsonObj.getString(JayonDbHelper.COLUMN_MERCHANT_PROVINCE));
			
			
			val.put(JayonDbHelper.COLUMN_SHIP_ADDR, jsonObj.getString(JayonDbHelper.COLUMN_SHIP_ADDR));
			val.put(JayonDbHelper.COLUMN_SHIP_DIR, jsonObj.getString(JayonDbHelper.COLUMN_SHIP_DIR));
			val.put(JayonDbHelper.COLUMN_SHIP_LAT, jsonObj.getString(JayonDbHelper.COLUMN_SHIP_LAT));
			val.put(JayonDbHelper.COLUMN_SHIP_LON, jsonObj.getString(JayonDbHelper.COLUMN_SHIP_LON));

			val.put(JayonDbHelper.COLUMN_BUYER_NAME, jsonObj.getString(JayonDbHelper.COLUMN_BUYER_NAME));
			val.put(JayonDbHelper.COLUMN_BUYER_PHONE, jsonObj.getString(JayonDbHelper.COLUMN_BUYER_PHONE));
			val.put(JayonDbHelper.COLUMN_BUYER_TIME, jsonObj.getString(JayonDbHelper.COLUMN_BUYER_TIME));
			val.put(JayonDbHelper.COLUMN_BUYER_ZONE, jsonObj.getString(JayonDbHelper.COLUMN_BUYER_ZONE));
			val.put(JayonDbHelper.COLUMN_BUYER_CITY, jsonObj.getString(JayonDbHelper.COLUMN_BUYER_CITY));
			val.put(JayonDbHelper.COLUMN_BUYER_EMAIL, jsonObj.getString(JayonDbHelper.COLUMN_BUYER_EMAIL));

			
			val.put(JayonDbHelper.COLUMN_RECIPIENT_NAME, jsonObj.getString(JayonDbHelper.COLUMN_RECIPIENT_NAME));
			val.put(JayonDbHelper.COLUMN_COD_COST, jsonObj.getString(JayonDbHelper.COLUMN_COD_COST));
			val.put(JayonDbHelper.COLUMN_COD_CURR, jsonObj.getString(JayonDbHelper.COLUMN_COD_CURR));
			
			val.put(JayonDbHelper.COLUMN_ASSIGNED_DATE, jsonObj.getString(JayonDbHelper.COLUMN_ASSIGNED_DATE));
			val.put(JayonDbHelper.COLUMN_ASSIGNED_TIMESLOT, jsonObj.getInt(JayonDbHelper.COLUMN_ASSIGNED_TIMESLOT));
			val.put(JayonDbHelper.COLUMN_ASSIGNED_ZONE, jsonObj.getString(JayonDbHelper.COLUMN_ASSIGNED_ZONE));
			val.put(JayonDbHelper.COLUMN_ASSIGNED_CITY, jsonObj.getString(JayonDbHelper.COLUMN_ASSIGNED_CITY));

			val.put(JayonDbHelper.COLUMN_DELIVERY_STATUS, jsonObj.getString(JayonDbHelper.COLUMN_DELIVERY_STATUS));
			val.put(JayonDbHelper.COLUMN_DELIVERY_LAT, jsonObj.getString(JayonDbHelper.COLUMN_DELIVERY_LAT));
			val.put(JayonDbHelper.COLUMN_DELIVERY_LON, jsonObj.getString(JayonDbHelper.COLUMN_DELIVERY_LON));


			database.insert(JayonDbHelper.TABLE_ORDERS, null, val);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public Order getOrder(String delivery_id){
		Log.i("InDeliID", delivery_id);
		try{
			Log.i("ProjDeliID", delivery_id);
			String[] projArgs = new String[]{
					delivery_id	
				};
			Cursor cursor = database.query(JayonDbHelper.TABLE_ORDERS, allColumns, JayonDbHelper.COLUMN_ORDER_ID  + "= ?" , projArgs , null, null,null,"1");

			Log.i("RowCnt", String.valueOf(cursor.getCount()));
			if(cursor.getCount() == 1){
				cursor.moveToPosition(0);
				Order order = new Order();
		        order.setDeliveryId(cursor.getString(1));
		        order.setMcTransId(cursor.getString(2));
		        order.setMcName(cursor.getString(3));
		        order.setShipAddr(cursor.getString(4));
		        order.setByName(cursor.getString(5));
		        order.setByPhone(cursor.getString(6));
		        order.setRecipient(cursor.getString(7));
		        order.setCODCost(cursor.getString(8));
		        order.setCODCurr(cursor.getString(9));
		        order.setStatus(cursor.getString(10));
		        order.setDirection(cursor.getString(13));
		        cursor.close();
		        return order;
			}else{
		        cursor.close();
				return null;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
        }catch(NullPointerException e){
        	e.printStackTrace();
        	return null;
        }
	}
	
	public boolean getIdMatch(String delivery_id){
		Cursor cursor = database.rawQuery("SELECT 1 FROM " + JayonDbHelper.TABLE_ORDERS + " WHERE " + JayonDbHelper.COLUMN_ORDER_ID + " = '?'", new String[]{delivery_id});
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public Cursor getAllOrders(){
		try{
			Cursor cursor = database.query(
					JayonDbHelper.TABLE_ORDERS,
					allColumns,null,null,null,null,null
				);
			cursor.moveToFirst();
			return cursor;			
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
