package com.jackson.simplempgcalculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		EnterMileageFrag frag = new EnterMileageFrag();
		
		android.app.FragmentManager man = getFragmentManager();
		android.app.FragmentTransaction trans = man.beginTransaction();
		trans.add(R.id.main_activity_layout, frag, "enterMileageFragment");
		trans.commit();	
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_mileage, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		InputMethodManager input = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        
		// Create new fragment and transaction
		preferences newFragment = new preferences();
		android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.main_activity_layout, newFragment,	"preferencesScreen");
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	        return true;
	}
	
	
	
}







