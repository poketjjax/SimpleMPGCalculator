package com.jackson.simplempgcalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class preferences extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}	
	
	@Override
	public void onResume() {
	    super.onResume();
	    // Set title
	    getActivity().setTitle(R.string.settings);
	    

	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    boolean showFuelPrice = sharedPref.getBoolean("show_price", true);
	}

}