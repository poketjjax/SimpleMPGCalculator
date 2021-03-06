package com.jackson.simplempgcalculator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;

public class EnterMileage extends Fragment implements View.OnClickListener, View.OnKeyListener {

	/* VARIABLES */
	private EditText miles;
	private EditText gallons;
	private EditText fuelprice;
	private TextView mpg;
	private TextView mpgtext;
	private TextView fuelpricetxt;
	private Button reset;
	private Button submit;
	private Button calculate;
	private float milesfloat;
	private float gallonsfloat;
	private float fuelpricefloat;
	private float totalmpg;
	private float totalPrice;
	private String mpgString;
	private static DbAdapter adapter;
	private IabHelper mHelper;
	private Boolean isPurchased = false; 
	
	/* LIFECYCLE METHODS */
	@Override
	public void onResume() {
		super.onResume();
	    // Set title
	    getActivity().setTitle(R.string.app_name);
	    
		//restore the tabs to the actionbar by entering navigation mode 
		ActionBar actionBar = getActivity().getActionBar();
		if(actionBar.getNavigationMode() == 0) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
		
		//set the focus and pull up the keyboard for the first edit text field
	    miles.requestFocus();
	    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(miles, InputMethodManager.SHOW_IMPLICIT);

	    //set onkey listener to calculate MPG when enter is pressed on the last text field
	    fuelprice.setOnKeyListener(this);
	    
		//start the in app purchase setup by making a connection to google play billing
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(getActivity(), getActivity().getResources().getString(R.string.base64PublicKey));
		mHelper.enableDebugLogging(true, "MPGtag");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Do nothing
			    } else {
			    	mHelper.queryInventoryAsync(mGotInventoryListener);
			    } 
			}
		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
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
		calculate = (Button) getView().findViewById(R.id.calculateResult);
		
		reset.setOnClickListener(this);
		submit.setOnClickListener(this);
		calculate.setOnClickListener(this);
		
		adapter = new DbAdapter(getActivity());
		adapter.open();	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.enter_mileage, container, false);
	}
	
	@Override
	public void onPause() {
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
	    adapter.close();
		super.onDestroyView();
	}

	/* Inherited methods */
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
		case R.id.calculateResult:
			verifyInput();
			break;
		}	
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
	        	
	        switch (keyCode) {
	        case KeyEvent.KEYCODE_DPAD_CENTER:
	        case KeyEvent.KEYCODE_ENTER:
	            verifyInput();
	            return true;
	          
	        default:
	        	break;
	        }
	    }
		return false;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(isPurchased) {
			MenuItem item = menu.getItem(0).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	/* Custom methods */
	public void calculateMPG(float milesint, float gallonsint, float fuelpriceint) {
		totalmpg = milesint/gallonsint;
		mpgString = String.format("%.2f", totalmpg);
		
		if(fuelpriceint != 0){
			fuelpriceint = truncate(fuelpriceint);
			fuelpriceint += 0.009;
			totalPrice = (fuelpriceint * gallonsint);
		} else {
			totalPrice = 0;
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
			values.put(DbAdapter.PRICE, truncate(fuelpricefloat));
			values.put(DbAdapter.TOTAL_COST, Math.round(totalPrice*100.0)/100.0);
		}
		long insertResult = adapter.insert(DbAdapter.TRIPS_TABLE, values);	
		if(insertResult != -1) {
			Toast.makeText(getActivity(), "Trip saved successfully!", Toast.LENGTH_SHORT).show();
			miles.setText("");
			gallons.setText(""); 
			fuelprice.setText("");	
			miles.requestFocus();
			mpgtext.setVisibility(View.GONE);
			mpg.setVisibility(View.GONE);
			mpg.setText("");
		} else {
			Toast.makeText(getActivity(), "Trip failed to save, try again!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void verifyInput() {
		//hide soft keyboard right away
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	    
		if(fuelprice.getText().toString().equalsIgnoreCase("")){
        	fuelpricefloat = 0;
        }
        else{
        	fuelpricefloat = Float.parseFloat(fuelprice.getText().toString());
        }
        
    	if(miles.getText().toString().trim().equals("")){
    		Toast toast = Toast.makeText(getActivity(), "Miles is required!", Toast.LENGTH_SHORT);
        	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        	toast.show();
        	miles.setError("Miles is required!");
        } else if(gallons.getText().toString().trim().equals("")){
        	Toast toast = Toast.makeText(getActivity(), "Gallons is required!", Toast.LENGTH_SHORT);
        	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        	toast.show();
        	gallons.setError("Gallons is required!");
        } else {
        	milesfloat = Float.parseFloat(miles.getText().toString());
	        gallonsfloat = Float.parseFloat(gallons.getText().toString());
	        calculateMPG(milesfloat, gallonsfloat, fuelpricefloat);
        }
	}
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {			
			if (result.isFailure()) {
				//this means there are no items to query, so the purchase has been made 
				if(result.getResponse() == -1003) {
					isPurchased = true;
		    	} else {
		    		isPurchased = false;
		    	}
		    } else {
			    // has the user paid to hide ads?
			    isPurchased = inv.hasPurchase(getActivity().getResources().getString(R.string.SKU_ADS));        
			    if(isPurchased) {
					isPurchased = true;
			    } else {
					isPurchased = false;
			    }
		    }
		}
	};
	
	private float truncate(Float fullPriceFloat) {
		String tempPrice = fullPriceFloat.toString();
		Float truncatedFuelPrice;
		String endPortion = tempPrice.substring(tempPrice.lastIndexOf("."));
		
		if(endPortion.length() > 3) {	
			Integer decimalSpot = tempPrice.indexOf(".");
			tempPrice = tempPrice.substring(0, decimalSpot + 3);
			truncatedFuelPrice = Float.parseFloat(tempPrice);
		} else {
			return fuelpricefloat;
		}
		return truncatedFuelPrice;
	}
	
}







