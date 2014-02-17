package com.jackson.simplempgcalculator;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	public static final String ID = "_id";
	public static final String MILES = "miles";
	public static final String GALLONS = "gallons";
	public static final String MPG = "mpg";
	public static final String PRICE = "price";
	public static final String TOTAL_COST = "total_cost";
	public static final String DATE = "date";
	
	private static final String TRIPS_TABLE_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s (%s integer primary key autoincrement, %s real, %s real, %s real, %s real, %s real, %s text)",
			TRIPS_TABLE, ID, MILES, GALLONS, MPG, PRICE, TOTAL_COST, DATE);


	
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
	
	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}
	
	public Cursor select(String select) {
		return db.rawQuery(select, null);
	}
	
	public int delete() {
		return db.delete(TRIPS_TABLE, "1", null);
	}
	
	public void deleteRow(int rowId) {
		db.rawQuery(String.format("DELETE FROM %s WHERE %s = %d", TRIPS_TABLE, ID, rowId), null);
	}
	
	public synchronized void close() {
		db.close();
	}
	
	
	/* ----- NESTED CLASSES ----- */
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db, TRIPS_TABLE_CREATE);
		}

		private void createTable(SQLiteDatabase db, String sql) {
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	
}
