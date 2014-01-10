package com.jackson.simplempgcalculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Service;
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

public class EnterMileage extends Activity implements OnClickListener {
	
	//Decalre variables
    public EditText miles;
	public EditText gallons;
	public EditText fuelprice;
	public TextView mpg;
	public TextView mpgtext;
	public float milesint;
	public float gallonsint;
	public float fuelpriceint;
	public float totalmpg;
	public float mpgRounded;
	private Button reset;
	private Button submit;
	public String mpgString;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_mileage);
		
		miles = (EditText) findViewById(R.id.tankmiles);
		gallons = (EditText) findViewById(R.id.gallons);
		fuelprice = (EditText) findViewById(R.id.fuelprice);
		mpg = (TextView) findViewById(R.id.mpg);
		mpgtext = (TextView) findViewById(R.id.mpgtext);
		

		//hide the mpg text until after it is calculated
		mpgtext.setVisibility(View.GONE);
		
		//set the focus and pull up the keyboard for the first edit text field
		miles.requestFocus();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		//set onclicklistener for all buttons
		reset = (Button) findViewById(R.id.reset);
		submit = (Button) findViewById(R.id.saveResults);
		
		reset.setOnClickListener(this);
		submit.setOnClickListener(this);
	
		//set onkeylistener so that enter on the last text field will calculate MPG
		fuelprice.setOnKeyListener(new OnKeyListener()
		{
		    public boolean onKey(View v, int keyCode, KeyEvent event)
		    {
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		                (keyCode == KeyEvent.KEYCODE_ENTER))
		        {
		        	milesint = Float.parseFloat(miles.getText().toString());
		        	gallonsint = Float.parseFloat(gallons.getText().toString());
		        	if(fuelprice.getText().toString().equalsIgnoreCase(""))
		        	{
		        		fuelpriceint = 0;
		        	}
		        	else
		        	{
		        		fuelpriceint = Integer.parseInt(fuelprice.getText().toString());
		        	}
		        	switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    calculateMPG(milesint, gallonsint, fuelpriceint);
		                    return true;
		                default:
		                    break;
		            }
		        }
		        return false;
		    }
		});
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_mileage, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Create new fragment and transaction
		Fragment newFragment = new preferences();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(android.R.id.content, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	        return true;
	        }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.reset:
			clearAllFields();
			break;
		case R.id.saveResults:
			saveTheResults();
			break;
		}	
	}
	
	public void clearAllFields() {
		miles.setText("");
		gallons.setText(""); 
		fuelprice.setText("");	
		
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
		
		miles.requestFocus();
		imm.showSoftInput(miles, 0);
		mpgtext.setVisibility(View.GONE);
		mpg.setText("");
	}
	
	public void saveTheResults() {
		
	}
	
	public void calculateMPG(float milesint, float gallonsint, float fuelpriceint) {
		totalmpg = milesint/gallonsint;
		mpgString = String.format("%.2f", totalmpg);
		
		mpgtext.setVisibility(View.VISIBLE);
		mpg.setText(mpgString);
	}
	
}







