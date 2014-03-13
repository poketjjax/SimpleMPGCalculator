package com.jackson.simplempgcalculator;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnConsumeFinishedListener;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HideAds extends Fragment implements OnClickListener, OnIabPurchaseFinishedListener{
	
	/* VARIABLES */	
	private IabHelper mHelper;
	private final String SKU_ADS = "android.test.purchased";
	private String hidePrice;
	private Button purchase;
	private Integer requestCode = 11;
	private Boolean isPurchased; 
	
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
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqgpj4Y6Aq2N3nuXTaEeFht/vow67vkeM1hV8mLjnt3BpVgM5ZgV3aotJoJhSfOgXO1hiNq1s++ZBs1God2bySYCxpn6JTlqxMMtuAJPDOoBE2xzf1gxok/21RZNDAmzB9WeSeh2NgHhXHTMoIFxQJL4YhtkyD5TtLfE63UJ30iI7Qy/zQZFunXTS2IF34RWW07IXtptTOSvLj6YwCThb9GvNp2DMTYRq/YDRPNSykNu2D6tzEgau9XH1G3lcB7px7bmHrzOXLkxRoJ2sTdlqmXAxuqEUsZStZijF515HOdB4CAe86HgFOvHkePxrxDeTCFZDDs+Pno1f/UFJ75jAdQIDAQAB";
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
		mHelper.enableDebugLogging(true, "MPGtag");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// print error message from in app billing
					Log.e("In app billing result = ", "Problem setting up In-app Billing: " + result);
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
		//set up the ad banner
	    AdView adView = (AdView) getActivity().findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("8A9DA1B236989CF0344431DAB1CF42FB").build();
	    adView.loadAd(adRequest);
	    
	    purchase = (Button) getActivity().findViewById(R.id.purchaseBtn);
	    purchase.setOnClickListener(this);
	    
		super.onActivityCreated(savedInstanceState);
	}

	/* inherited methods */
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

	private void launchPurchase() {
		mHelper.launchPurchaseFlow(getActivity(), SKU_ADS, requestCode, this);
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		if(result.isFailure()) {
			Toast.makeText(getActivity(), "purchase failed!", Toast.LENGTH_SHORT).show();
		} else if(purchase.getSku().equals(SKU_ADS)) {
			Toast.makeText(getActivity(), "purchase successfull!!!", Toast.LENGTH_SHORT).show();
		}
	}

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.e(getTag(), "purchase consumed!!!!");
		}
	};

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {
			if (result.isFailure()) {
				Log.e("error querying inventory", "failed message==  " + result.getMessage());
		    } else {
		    	// has the user paid to hide ads?
		        isPurchased = inv.hasPurchase(SKU_ADS);        
		        Toast.makeText(getActivity(), Boolean.toString(isPurchased), Toast.LENGTH_SHORT).show();
		    }
		}
	};
	
}












