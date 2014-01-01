package com.jackson.simplempgcalculator;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EnterMileage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_mileage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_mileage, menu);
		return true;
	}

}
