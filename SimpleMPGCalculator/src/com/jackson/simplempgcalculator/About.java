package com.jackson.simplempgcalculator;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class About extends Fragment {
	/* VARIABLES */
	private TextView aboutText;
	private IabHelper mHelper;
	private Boolean isPurchased = false; 
	
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
		
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {	
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {			
			if (result.isFailure()) {
				//this means there are no items to query, so the purchase has been made 
				if(result.getResponse() == -1003) {
					hideAd();
		    	} else {
		    		createAd();
		    	}
		    } else {
			    // has the user paid to hide ads?
			    isPurchased = inv.hasPurchase(getActivity().getResources().getString(R.string.SKU_ADS));        
			    if(isPurchased) {
			    	hideAd();
			    } else {
			    	createAd();
			    }
		    }
		}
	};
	
	private void createAd() {
		//set up the ad banner
	    AdView adView = (AdView) getActivity().findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	}
	
	private void hideAd() {
		isPurchased = true;
		final AdView hideAdView = (AdView) getActivity().findViewById(R.id.adView);
		getActivity().runOnUiThread(new Runnable() {
			@Override
		    public void run() {
				if(hideAdView != null) {
					hideAdView.setEnabled(false);
			        hideAdView.setVisibility(View.GONE);
				}
		    }
		});
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(isPurchased) {
			MenuItem item = menu.getItem(0).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
	}

}
