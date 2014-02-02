package com.kickstartlab.jm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JayonDbHelper extends SQLiteOpenHelper{
	public static final String TABLE_ORDERS = "m_orders";
	public static final String TABLE_LOGS = "d_logs";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ORDER_ID = "delivery_id";
	public static final String COLUMN_TRANS_ID = "mc_trans_id";
	public static final String COLUMN_MERCHANT_NAME = "mc_name";
	public static final String COLUMN_MERCHANT_STREET= "mc_street";
	public static final String COLUMN_MERCHANT_ZONE= "mc_district";
	public static final String COLUMN_MERCHANT_CITY= "mc_city";
	public static final String COLUMN_MERCHANT_PROVINCE = "mc_province";

	public static final String COLUMN_SHIP_SEQ = "seq";
	public static final String COLUMN_SHIP_ADDR = "ship_addr";
	public static final String COLUMN_SHIP_DIR = "ship_dir";
	public static final String COLUMN_SHIP_LAT = "ship_lat";
	public static final String COLUMN_SHIP_LON = "ship_lon";
	
	public static final String COLUMN_BUYER_NAME = "by_name";
	public static final String COLUMN_BUYER_PHONE = "by_phone";
	public static final String COLUMN_BUYER_TIME = "by_time";
	public static final String COLUMN_BUYER_ZONE = "by_zone";
	public static final String COLUMN_BUYER_CITY = "by_city";
	public static final String COLUMN_BUYER_EMAIL = "by_email";

	public static final String COLUMN_RECIPIENT_NAME = "rec_name";
	public static final String COLUMN_RECIPIENT_SIGN = "rec_sign";

	
	public static final String COLUMN_TOTAL_VALUE= "tot_price";
	public static final String COLUMN_DELIVERY_COST= "delivery_cost";
	public static final String COLUMN_COD_COST= "cod_cost";
	public static final String COLUMN_COD_CURR= "cod_curr";
	
	public static final String COLUMN_ASSIGNED_DATE= "as_date";
	public static final String COLUMN_ASSIGNED_TIMESLOT= "as_timeslot";
	public static final String COLUMN_ASSIGNED_ZONE= "as_zone";
	public static final String COLUMN_ASSIGNED_CITY= "as_city";
	
	public static final String COLUMN_DELIVERY_TYPE = "dl_type";
	public static final String COLUMN_DELIVERY_TIME = "dl_time";
	public static final String COLUMN_DELIVERY_STATUS = "dl_status";
	public static final String COLUMN_DELIVERY_NOTE = "dl_note";
	public static final String COLUMN_DELIVERY_LAT = "dl_lat";
	public static final String COLUMN_DELIVERY_LON = "dl_lon";

	public static final String COLUMN_RESCHEDULE_REF = "res_ref";
	public static final String COLUMN_REVOKE_REF = "rev_ref";
	
	public static final String LOG_ID = "_id";
	public static final String LOG_SYNC_ID = "sync_id";
	public static final String LOG_CAPTURE_TIME = "capture_time";
	public static final String LOG_REPORT_TIME = "report_time";
	public static final String LOG_DELIVERY_ID = "delivery_id";
	public static final String LOG_TRANS_ID = "merchant_trans_id";
	public static final String LOG_STATUS = "status";
	public static final String LOG_NOTE = "delivery_note";
	public static final String LOG_LAT = "latitude";
	public static final String LOG_LON = "longitude";


	private static final String DATABASE_NAME = "jayonmobile.db";
	private static final int DATABASE_VERSION = 6;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ORDERS + "( "
			+ "`_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			  + "`delivery_id` TEXT NULL,"
			  + "`as_date` TEXT NULL,"
			  + "`as_timeslot` INTEGER NULL,"
			  + "`as_zone` TEXT NULL,"
			  + "`as_city` TEXT NULL,"
			  + "`mc_name` TEXT NULL,"
			  + "`mc_trans_id` TEXT NULL,"
			  + "`mc_street` TEXT NULL,"
			  + "`mc_district` TEXT NULL,"
			  + "`mc_city` TEXT NULL,"
			  + "`mc_province` TEXT NULL,"
			  + "`seq` INTEGER NULL,"
			  + "`by_time` TEXT NULL,"
			  + "`by_zone` TEXT NULL,"
			  + "`by_city` TEXT NULL,"
			  + "`by_name` TEXT NULL,"
			  + "`by_email` TEXT NULL,"
			  + "`by_phone` TEXT NULL,"
			  + "`rec_name` TEXT NULL,"
			  + "`rec_sign` TEXT NULL,"
			  + "`tot_price` TEXT NULL,"
			  + "`delivery_cost` TEXT NULL,"
			  + "`cod_cost` TEXT NULL,"
			  + "`cod_curr` TEXT NULL,"
			  + "`ship_addr` TEXT NULL,"
			  + "`ship_dir` TEXT NULL,"
			  + "`ship_lat` TEXT NULL,"
			  + "`ship_lon` TEXT NULL,"
			  + "`dl_type` TEXT NULL,"
			  + "`dl_time` TEXT NULL,"
			  + "`dl_status` TEXT NULL,"
			  + "`dl_note` TEXT NULL,"
			  + "`dl_lat` TEXT NULL,"
			  + "`dl_lon` TEXT NULL,"
			  + "`res_ref` TEXT NULL,"
			  + "`rev_ref` TEXT NULL"
			+");";
	private static final String LOG_CREATE =		  
			"create table "
			+ TABLE_LOGS + "( "
			+ "`_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "`sync_id` TEXT NULL,"
			+ "`capture_time` TEXT NULL,"
			+ "`report_time` TEXT NULL,"
			+ "`delivery_id` TEXT NULL,"
			+ "`merchant_trans_id` TEXT NULL,"
			+ "`status` TEXT NULL,"
			+ "`delivery_note` TEXT NULL,"
			+ "`latitude` TEXT NULL,"
			+ "`longitude` TEXT NULL"
			+");";
	
	public JayonDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		db.execSQL(LOG_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(!colExists(db,TABLE_ORDERS,"seq" )){
			db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN `seq` INTEGER");			
		}
		if(!colExists(db,TABLE_ORDERS,"tot_price" )){
			db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN `tot_price` TEXT");
		}
		if(!colExists(db,TABLE_ORDERS,"delivery_cost" )){
			db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN `delivery_cost` TEXT");
		}
		if(!colExists(db,TABLE_ORDERS,"dl_type" )){
			db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN `dl_type` TEXT");						
		}
		if(!colExists(db,TABLE_ORDERS,"cod_bearer" )){
			db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN `cod_bearer` TEXT");						
		}
		//db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ORDERS);
		//db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOGS);
		//onCreate(db);
	}
	
	private boolean colExists(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
		Cursor mCursor = null;
		try{
	        //query 1 row
	        mCursor  = inDatabase.rawQuery( "SELECT * FROM " + inTable + " LIMIT 0", null );

	        //getColumnIndex gives us the index (0 to ...) of the column - otherwise we get a -1
	        if(mCursor.getColumnIndex(columnToCheck) != -1)
	            return true;
	        else
	            return false;

	    }catch (Exception Exp){
	        //something went wrong. Missing the database? The table?
	        Log.d("... - existsColumnInTable","When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
	        return false;
	    }finally{
	    	mCursor.close();
	    }
	}
	
}
