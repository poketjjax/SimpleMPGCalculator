package com.jackson.simplempgcalculator;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {



	/* CONSTANTS */
	private static final int DB_VERSION = 1;
	private static final String TRIPS_TABLE = "trips";
	private static final String ID = "id";
	private static final String MILES = "miles";
	private static final String GALLONS = "gallons";
	private static final String MPG = "mpg";
	private static final String PRICE = "price";
	private static final String TOTAL_COST = "total_cost";
	
	private static final String TRIPS_TABLE_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s (%s integer primary key autoincrement, %s real, %s real, %s real, %s real, %s real",
			TRIPS_TABLE, ID, MILES, GALLONS, MPG, PRICE, TOTAL_COST);

	
}
