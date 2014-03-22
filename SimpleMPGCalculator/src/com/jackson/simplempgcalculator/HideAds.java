package com.jackson.simplempgcalculator;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HideAds extends Fragment implements OnClickListener, OnIabPurchaseFinishedListener{
	
	/* VARIABLES */	
	private IabHelper mHelper;
	private Button purchase;
	private Integer requestCode = 11;
	private Boolean isPurchased; 
	private TextView textView;
	
	/* LIFECYCLE METHODS */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.hide_ads, container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		//hide the tabs on the actionbar by switching to standard mode
		ActionBar actionbar = getActivity().getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
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
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {	
    	setHasOptionsMenu(true);
    	
	    textView = (TextView) getActivity().findViewById(R.id.ads_message);
	    purchase = (Button) getActivity().findViewById(R.id.purchaseBtn);
	    purchase.setOnClickListener(this);
	    
		super.onActivityCreated(savedInstanceState);
	}

	/* Inherited methods */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.purchaseBtn:
			launchPurchase();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {		
	    // Pass on the activity result to the helper for handling
	    if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
	        // not handled, so handle it ourselves (here's where you'd
	        // perform any handling of activity results not related to in-app
	        // billing...
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		if(result.isFailure()) {
			Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
		} else if(purchase.getSku().equals(getActivity().getResources().getString(R.string.SKU_ADS))) {
			Toast.makeText(getActivity(), "Purchase Successfull!", Toast.LENGTH_SHORT).show();
        	changeText();
		}
	}
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {			
			if (result.isFailure()) {
				//this means there are no items to query, so the purchase has been made 
				if(result.getResponse() == -1003) {
					changeText();
		    	} else {
		    		Toast.makeText(getActivity(), "Couldn't connect to Google's Server. Try again later", Toast.LENGTH_LONG).show();
		    		createAd();
		    	}
		    } else {
			    // has the user paid to hide ads?
			    isPurchased = inv.hasPurchase(getActivity().getResources().getString(R.string.SKU_ADS));        
			    if(isPurchased) {
			    	changeText();
			    } else {
			    	createAd();
			    }
		    }
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.getItem(0).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* Custom methods */
	private void launchPurchase() {
		mHelper.launchPurchaseFlow(getActivity(), getActivity().getResources().getString(R.string.SKU_ADS), requestCode, this);
	}
	
	private void changeText() {
		purchase.setVisibility(View.GONE);
		textView.setText(getResources().getString(R.string.purchase_successful));
		Linkify.addLinks(textView, Linkify.ALL);
		hideAd();
	}
	
	private void createAd() {
		//set up the ad banner
	    AdView adView = (AdView) getActivity().findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("8A9DA1B236989CF0344431DAB1CF42FB").build();
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
	
}












