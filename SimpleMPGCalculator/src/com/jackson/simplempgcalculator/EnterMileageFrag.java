package com.jackson.simplempgcalculator;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.jackson.simplempgcalculator.DbAdapter;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EnterMileageFrag extends Fragment implements View.OnClickListener, View.OnKeyListener {

	/* VARIABLES */
    public EditText miles;
	public EditText gallons;
	public EditText fuelprice;
	public TextView mpg;
	public TextView mpgtext;
	public TextView fuelpricetxt;
	private Button reset;
	private Button submit;
	public float milesfloat;
	public float gallonsfloat;
	public float fuelpricefloat;
	public float totalmpg;
	public float totalPrice;
	public String mpgString;
	public String priceString;
	
	
	/* LIFECYCLE METHODS */
	@Override
	public void onResume() {
	    super.onResume();
	    // Set title
	    getActivity().setTitle(R.string.app_name);
	    
		//set the focus and pull up the keyboard for the first edit text field
		miles.requestFocus();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); 
		
	    //create an instance of preferences to read in the values
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    boolean showFuelPrice = sharedPref.getBoolean("show_price", false);
	    
	    //hide the fuel price based on the settings checkbox. also set onkeylistener based on checkbox
	    if(!showFuelPrice){
	    	fuelprice.setVisibility(View.GONE);
	    	fuelpricetxt.setVisibility(View.GONE);
	    	gallons.setOnKeyListener(this);
	    } else {
	    	fuelprice.setVisibility(View.VISIBLE);
	    	fuelpricetxt.setVisibility(View.VISIBLE);
	    	fuelprice.setOnKeyListener(this);
	    }
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		miles = (EditText) getView().findViewById(R.id.tankmiles);
		gallons = (EditText) getView().findViewById(R.id.gallons);
		fuelprice = (EditText) getView().findViewById(R.id.fuelprice);
		mpg = (TextView) getView().findViewById(R.id.mpg);
		mpgtext = (TextView) getView().findViewById(R.id.mpgtext);
		fuelpricetxt = (TextView) getView().findViewById(R.id.fuelpricetext);
		
		//hide the mpg text until after it is calculated
		mpgtext.setVisibility(View.GONE);
		mpg.setVisibility(View.GONE);
		
		//set onclicklistener for all buttons
		reset = (Button) getView().findViewById(R.id.reset);
		submit = (Button) getView().findViewById(R.id.saveResults);
		
		reset.setOnClickListener(this);
		submit.setOnClickListener(this);
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_enter_mileage, container, false);
	}
	
	
	@Override
	public void onPause() {
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		super.onPause();
	}
	
	/* Custom class methods */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.reset:
			clearAllFields();
			break;
		case R.id.saveResults:
			if(mpgtext.isShown()){
				saveResults();
			} else {
				Toast.makeText(getActivity(), "Fill out a Trip first!", Toast.LENGTH_LONG).show();
			}	
			break;
		}	
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
	
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
	        	
			if(fuelprice.getText().toString().equalsIgnoreCase("")){
	        	fuelpricefloat = 0;
	        }
	        else{
	        	fuelpricefloat = Float.parseFloat(fuelprice.getText().toString());
	        }
	        
	        switch (keyCode) {
	        	case KeyEvent.KEYCODE_DPAD_CENTER:
	            case KeyEvent.KEYCODE_ENTER:
	            	if( miles.getText().toString().trim().equals("")){
	            		Toast toast = Toast.makeText(getActivity(), "Miles is required!", Toast.LENGTH_LONG);
	                	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	                	toast.show();
	                	miles.setError("Miles is required!");
	                } else if(gallons.getText().toString().trim().equals("")){
	                	Toast toast = Toast.makeText(getActivity(), "Gallons is required!", Toast.LENGTH_LONG);
	                	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	                	toast.show();
	                	gallons.setError("Gallons is required!");
	                } else {
	                	milesfloat = Float.parseFloat(miles.getText().toString());
	        	        gallonsfloat = Float.parseFloat(gallons.getText().toString());
	        	        calculateMPG(milesfloat, gallonsfloat, fuelpricefloat);
	                }
	                return true;
	                
	            default:
	            break;
	        }
	    }
		return false;
	}
	
	public void calculateMPG(float milesint, float gallonsint, float fuelpriceint) {
		
		totalmpg = milesint/gallonsint;
		mpgString = String.format("%.2f", totalmpg);
		
		if(fuelpriceint != 0){
			totalPrice = (fuelpriceint * gallonsint);
		} else {
			priceString = "-";
		}
		
		mpg.setVisibility(View.VISIBLE);
		mpgtext.setVisibility(View.VISIBLE);
		mpg.setText(mpgString);
	}
	
	public void clearAllFields() {
		miles.setText("");
		gallons.setText(""); 
		fuelprice.setText("");	
		
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		miles.requestFocus();
		imm.showSoftInput(miles, 0);
		mpgtext.setVisibility(View.GONE);
		mpg.setVisibility(View.GONE);
		mpg.setText("");
	}


	private void saveResults() {
		DbAdapter adapter = new DbAdapter(getActivity());
		
		adapter.open();
		
		//create a date to store in the DB
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Date date = new Date();
		
		ContentValues values = new ContentValues();
		values.put(DbAdapter.MILES, milesfloat);
		values.put(DbAdapter.GALLONS, gallonsfloat);
		values.put(DbAdapter.MPG, Math.round(totalmpg*100.0)/100.0);
		values.put(DbAdapter.DATE, dateFormat.format(date));
		if(fuelpricetxt.toString().matches("")){
			values.put(DbAdapter.PRICE, 0.00);
			values.put(DbAdapter.TOTAL_COST, 0.00);
		} else {
			values.put(DbAdapter.PRICE, fuelpricefloat);
			values.put(DbAdapter.TOTAL_COST, Math.round(totalPrice*100.0)/100.0);
		}
		
		adapter.insert(DbAdapter.TRIPS_TABLE, values);
		
	}
}







