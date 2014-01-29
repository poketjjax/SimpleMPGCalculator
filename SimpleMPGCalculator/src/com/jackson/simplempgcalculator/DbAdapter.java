package com.jackson.simplempgcalculator;


import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbAdapter {



	/* CONSTANTS */
	private static final int DB_VERSION = 1;
	public static final String TRIPS_TABLE = "trips";
	private static final String ID = "id";
	public static final String MILES = "miles";
	public static final String GALLONS = "gallons";
	public static final String MPG = "mpg";
	public static final String PRICE = "price";
	public static final String TOTAL_COST = "total_cost";
	
	private static final String TRIPS_TABLE_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s (%s integer primary key autoincrement, %s real, %s real, %s real, %s real, %s real)",
			TRIPS_TABLE, ID, MILES, GALLONS, MPG, PRICE, TOTAL_COST);


	
	/* ----- VARIABLES ----- */
	private Context ctx;
	private SQLiteDatabase db;
	private DbHelper dbHelper;
	
	/* ----- CONSTRUCTORS ----- */
	public DbAdapter(Context context) {
		this.ctx = context;
		dbHelper = new DbHelper(ctx, TRIPS_TABLE, null, DB_VERSION);
	}

	/* ----- CUSTOM METHODS ----- */
	public synchronized void open() {
		db = dbHelper.getWritableDatabase();
	}

	public synchronized void close() {
		db.close();
	}
	
	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}
	
	
	/* ----- NESTED CLASSES ----- */
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.e("df", "make it to oncreate of dbhelper");
			createTable(db, TRIPS_TABLE_CREATE);
		}

		private void createTable(SQLiteDatabase db, String sql) {
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.e("df", "make it to onupgrade of dbhelper");
			
		}
	}
	
}