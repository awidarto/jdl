package com.kickstartlab.jm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

	public static final String COLUMN_COD_COST= "cod_cost";
	public static final String COLUMN_COD_CURR= "cod_curr";
	
	public static final String COLUMN_ASSIGNED_DATE= "as_date";
	public static final String COLUMN_ASSIGNED_TIMESLOT= "as_timeslot";
	public static final String COLUMN_ASSIGNED_ZONE= "as_zone";
	public static final String COLUMN_ASSIGNED_CITY= "as_city";
	
	public static final String COLUMN_DELIVERY_TIME = "dl_time";
	public static final String COLUMN_DELIVERY_STATUS = "dl_status";
	public static final String COLUMN_DELIVERY_NOTE = "dl_note";
	public static final String COLUMN_DELIVERY_LAT = "dl_lat";
	public static final String COLUMN_DELIVERY_LON = "dl_lon";

	public static final String COLUMN_RESCHEDULE_REF = "res_ref";
	public static final String COLUMN_REVOKE_REF = "rev_ref";


	private static final String DATABASE_NAME = "jayonmobile.db";
	private static final int DATABASE_VERSION = 1;
	
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
			  + "`by_time` TEXT NULL,"
			  + "`by_zone` TEXT NULL,"
			  + "`by_city` TEXT NULL,"
			  + "`by_name` TEXT NULL,"
			  + "`by_email` TEXT NULL,"
			  + "`by_phone` TEXT NULL,"
			  + "`rec_name` TEXT NULL,"
			  + "`rec_sign` TEXT NULL,"
			  + "`cod_cost` TEXT NULL,"
			  + "`cod_curr` TEXT NULL,"
			  + "`ship_addr` TEXT NULL,"
			  + "`ship_dir` TEXT NULL,"
			  + "`ship_lat` TEXT NULL,"
			  + "`ship_lon` TEXT NULL,"
			  + "`dl_time` TEXT NULL,"
			  + "`dl_status` TEXT NULL,"
			  + "`dl_note` TEXT NULL,"
			  + "`dl_lat` TEXT NULL,"
			  + "`dl_lon` TEXT NULL,"
			  + "`res_ref` TEXT NULL,"
			  + "`rev_ref` TEXT NULL"
			+");"
			+ "create table "
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
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ORDERS);
		db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOGS);
		onCreate(db);
	}
	
}
