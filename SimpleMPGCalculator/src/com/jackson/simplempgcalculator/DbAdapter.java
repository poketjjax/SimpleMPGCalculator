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
	/* Must increment this number when a table structure or constant is changed */
	private static final int DB_VERSION = 5;
	public static final String TRIPS_TABLE = "trips";
	public static final String ID = "_id";
	public static final String MILES = "miles";
	public static final String GALLONS = "gallons";
	public static final String MPG = "mpg";
	public static final String PRICE = "price";
	public static final String TOTAL_COST = "total_cost";
	public static final String DATE = "date";
	//select statements to be stored in an array for the sorting spinner 
	public static final String orderByIdAsc = "SELECT * FROM trips ORDER BY _id";
	public static final String orderByIdDesc = "SELECT * FROM trips ORDER BY _id DESC";
	public static final String orderByMPGAsc = "SELECT * FROM trips ORDER BY mpg";
	public static final String orderByMPGDesc = "SELECT * FROM trips ORDER BY mpg DESC";
	public static final String orderByCostAsc = "SELECT * FROM trips ORDER BY total_cost";
	public static final String orderByCostDesc = "SELECT * FROM trips ORDER BY total_cost DESC";
	public static final String orderByPriceAsc = "SELECT * FROM trips ORDER BY price";
	public static final String orderByPriceDesc = "SELECT * FROM trips ORDER BY price DESC";
	
	public static final String select[] = { orderByIdAsc, orderByIdDesc, orderByMPGDesc, orderByMPGAsc, 
											orderByCostDesc, orderByCostAsc, orderByPriceDesc, orderByPriceAsc};
	
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
	
	public int deleteRow(int rowId) {
		return db.delete(TRIPS_TABLE, ID +  "=" + Integer.toString(rowId), null);
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
			String drop = "DROP TABLE IF EXISTS ";
			execSQL(db, drop + TRIPS_TABLE);
			onCreate(db);
		}
		
		private static void execSQL(SQLiteDatabase db, String sql) {
			db.execSQL(sql);
		}
	}
	
}
