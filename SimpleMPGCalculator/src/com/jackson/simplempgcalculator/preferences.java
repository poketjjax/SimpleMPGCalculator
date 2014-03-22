package com.jackson.simplempgcalculator;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class preferences extends PreferenceActivity {

	/* LIFECYCLE METHODS */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setBackgroundColor(Color.parseColor("#D4D4D4"));
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
    }
	
	@Override
	public void onResume() {
	    super.onResume();
	    // Set title
	    setTitle(R.string.settings);	    
	}

	/* NESTED CLASS */
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}