package com.jackson.simplempgcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	public float milesint;
	public float gallonsint;
	public float fuelpriceint;
	public float mpgRounded;
	public String mpgString;
	
	/* LIFECYCLE METHODS */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    // Set title
	    getActivity().setTitle(R.string.app_name);
	    //create an instance of preferences to read in the values
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    boolean showFuelPrice = sharedPref.getBoolean("show_price", true);
	    
	    //hide the fuel price based on the settings checkbox. also set onkeylistener based on checkbox
	    if(!showFuelPrice){
	    	fuelprice.setVisibility(View.GONE);
	    	fuelpricetxt.setVisibility(View.GONE);
	    	gallons.setOnKeyListener(this);
	    } else {
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
		
		
		//set the focus and pull up the keyboard for the first edit text field
		miles.requestFocus();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}	
	
	/* Custom class methods */
	@Override
	public void onClick(View v) {
			switch(v.getId()) {
			case R.id.reset:
				clearAllFields();
				break;
			case R.id.saveResults:
				
				break;
			}	
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
			
			
	        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	                (keyCode == KeyEvent.KEYCODE_ENTER))
	        {
	        	if(fuelprice.getText().toString().equalsIgnoreCase("")){
	        		fuelpriceint = 0;
	        	}
	        	else{
	        		fuelpriceint = Integer.parseInt(fuelprice.getText().toString());
	        	}
	        	
	        	switch (keyCode)
	            {
	                case KeyEvent.KEYCODE_DPAD_CENTER:
	                case KeyEvent.KEYCODE_ENTER:
	                	if( miles.getText().toString().trim().equals("")){
	                		
	                		Toast toast = Toast.makeText(getActivity(), "Miles is required!",
	                				   Toast.LENGTH_LONG);
	                		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	                		toast.show();
	                		miles.setError("Miles is required!");
	                	}
	                	else if(gallons.getText().toString().trim().equals("")){
	                		Toast toast = Toast.makeText(getActivity(), "Gallons is required!",
	                				   Toast.LENGTH_LONG);
	                		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	                		toast.show();
	                		gallons.setError("Gallons is required!");
	                	}
	                	else {
	        	        	milesint = Float.parseFloat(miles.getText().toString());
	        	        	gallonsint = Float.parseFloat(gallons.getText().toString());
	        	        	calculateMPG(milesint, gallonsint, fuelpriceint);
	                	}
	                    return true;
	                default:
	                    break;
	            }
	        }
		return false;
	}
	
	public void calculateMPG(float milesint, float gallonsint, float fuelpriceint) {
		float totalmpg;
		String mpgString;
		
		totalmpg = milesint/gallonsint;
		mpgString = String.format("%.2f", totalmpg);
		
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


}







