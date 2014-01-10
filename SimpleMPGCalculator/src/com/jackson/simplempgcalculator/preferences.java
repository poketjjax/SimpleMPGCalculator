package com.jackson.simplempgcalculator;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class preferences extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}	

}