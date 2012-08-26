package com.kickstartlab.jm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LogDataSource {
	private SQLiteDatabase database;
	private JayonDbHelper dbHelper;
	private String[] allLogColumns = {
			JayonDbHelper.LOG_ID, 
			JayonDbHelper.LOG_SYNC_ID,
			JayonDbHelper.LOG_CAPTURE_TIME,
			JayonDbHelper.LOG_REPORT_TIME,
			JayonDbHelper.LOG_DELIVERY_ID,
			JayonDbHelper.LOG_TRANS_ID,
			JayonDbHelper.LOG_STATUS,
			JayonDbHelper.LOG_NOTE,
			JayonDbHelper.LOG_LAT,
			JayonDbHelper.LOG_LON
	};
	
	public LogDataSource(Context context){
		dbHelper = new JayonDbHelper(context);
	}
	
	public void open(){
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void emptyData(){
		database.delete(JayonDbHelper.TABLE_LOGS, "1", null);
	}
	
	public void saveLog(LogData log){
		ContentValues val = new ContentValues();
		val.put(JayonDbHelper.LOG_SYNC_ID,log.getSyncId());
		val.put(JayonDbHelper.LOG_CAPTURE_TIME,log.getCaptureTime());
		val.put(JayonDbHelper.LOG_REPORT_TIME,log.getReportTime());
		val.put(JayonDbHelper.LOG_DELIVERY_ID,log.getDeliveryId());
		val.put(JayonDbHelper.LOG_TRANS_ID,log.getMcTransId());
		val.put(JayonDbHelper.LOG_STATUS,log.getStatus());
		val.put(JayonDbHelper.LOG_NOTE,log.getDeliveryNote());
		val.put(JayonDbHelper.LOG_LAT,log.getLatitude());
		val.put(JayonDbHelper.LOG_LON,log.getLongitude());		
		database.insert(JayonDbHelper.TABLE_LOGS, null, val);
	}
	
	public LogData getLogData(String delivery_id){
		Log.i("InDeliID", delivery_id);
		try{
			Log.i("ProjDeliID", delivery_id);
			String[] projArgs = new String[]{
					delivery_id	
				};
			
			String orderby = JayonDbHelper.LOG_CAPTURE_TIME + " DESC";
			
			Cursor cursor = database.query(JayonDbHelper.TABLE_LOGS, allLogColumns, JayonDbHelper.LOG_DELIVERY_ID  + "= ?" , projArgs , null, null,orderby,"1");

			Log.i("RowCnt", String.valueOf(cursor.getCount()));
			if(cursor.getCount() == 1){
				cursor.moveToPosition(0);
				LogData logdata = new LogData();
				
		        logdata.setId(cursor.getLong(0));
		        logdata.setSyncId(cursor.getString(1));
		        logdata.setCaptureTime(cursor.getString(2));
		        logdata.setReportTime(cursor.getString(3));
		        logdata.setDeliveryId(cursor.getString(4));
		        logdata.setMcTransId(cursor.getString(5));
		        logdata.setStatus(cursor.getString(6));
		        logdata.setDeliveryNote(cursor.getString(7));
		        logdata.setLongitude(cursor.getString(8));
		        logdata.setLatitude(cursor.getString(9));
		        
		        cursor.close();
		        return logdata;
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

	public Cursor getAllLogs(){
		try{
			Cursor cursor = database.query(
					JayonDbHelper.TABLE_LOGS,
					allLogColumns,null,null,null,null,null
				);
			cursor.moveToFirst();
			return cursor;			
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}


	
}
