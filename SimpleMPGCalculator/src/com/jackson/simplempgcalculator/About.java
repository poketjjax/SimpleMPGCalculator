package com.jackson.simplempgcalculator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.R.integer;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class About extends Fragment {
	/* VARIABLES */
	private TextView aboutText;
	
	/* LIFECYCLE METHODS */	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.about, container, false);
    }

	@Override
	public void onResume() {
		//hide the tabs on the actionbar by switching to standard mode
		ActionBar actionbar = getActivity().getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		//make all the links in the textview clickable 
		aboutText = (TextView) getActivity().findViewById(R.id.about_message);
		Linkify.addLinks(aboutText, Linkify.ALL);
	    
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {	
		//set up the ad banner
	    AdView adView = (AdView) getActivity().findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("8A9DA1B236989CF0344431DAB1CF42FB").build();
	    adView.loadAd(adRequest);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		//restore the tabs to the actionbar by entering navigation mode 
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		super.onDestroyView();
	}

}
