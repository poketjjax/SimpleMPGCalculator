package com.jackson.simplempgcalculator;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.TextView;

public class resultsViewBinder implements ViewBinder, android.widget.SimpleCursorAdapter.ViewBinder {

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		
		switch(view.getId()){
		
		case R.id.price_item:
			Float price = cursor.getFloat(columnIndex);
			if(price == 0) {
				((TextView) view).setText(" -         ");
			}
			else {
				((TextView) view).setText("$" + Float.toString(cursor.getFloat(columnIndex)));
			}
			return true;
		
		case R.id.cost_item:
			Float cost = cursor.getFloat(columnIndex);
			if(cost == 0) {
				((TextView) view).setText(" -         ");
			}
			else {
				((TextView) view).setText("$" + Float.toString(cursor.getFloat(columnIndex)));
			}
			return true;	
		
	    default:
		return false;
		}
		
	}

}
