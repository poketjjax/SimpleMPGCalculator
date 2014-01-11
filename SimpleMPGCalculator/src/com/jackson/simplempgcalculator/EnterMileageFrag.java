package com.jackson.simplempgcalculator;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
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

public class EnterMileageFrag extends Fragment implements View.OnClickListener, View.OnKeyListener {

	/* VARIABLES */
    public EditText miles;
	public EditText gallons;
	public EditText fuelprice;
	public TextView mpg;
	public TextView mpgtext;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("made it here =====", "yes");
		miles = (EditText) getView().findViewById(R.id.tankmiles);
		gallons = (EditText) getView().findViewById(R.id.gallons);
		fuelprice = (EditText) getView().findViewById(R.id.fuelprice);
		mpg = (TextView) getView().findViewById(R.id.mpg);
		mpgtext = (TextView) getView().findViewById(R.id.mpgtext);
		

		//hide the mpg text until after it is calculated
		mpgtext.setVisibility(View.GONE);
		
		//set the focus and pull up the keyboard for the first edit text field
		miles.requestFocus();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		//set onclicklistener for all buttons
		reset = (Button) getView().findViewById(R.id.reset);
		submit = (Button) getView().findViewById(R.id.saveResults);
		
		reset.setOnClickListener(this);
		submit.setOnClickListener(this);
	
		//set onkeylistener so that enter on the last text field will calculate MPG
		fuelprice.setOnKeyListener(this);
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
	
	public void calculateMPG(float milesint, float gallonsint, float fuelpriceint) {
		float totalmpg;
		String mpgString;
		
		totalmpg = milesint/gallonsint;
		mpgString = String.format("%.2f", totalmpg);
		
		
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
		mpg.setText("");
	}


}







