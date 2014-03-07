package com.jackson.simplempgcalculator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;

public class MainActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
		
	/* VARIABLES */
	private static Context context;
	public Integer rowId;
	public Integer pos;
	private IabHelper mHelper;
	
	/* LIFECYCLE METHODS */	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		context = this;
		
		//start the in app purchase setup by making a connection to google play billing
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqgpj4Y6Aq2N3nuXTaEeFht/vow67vkeM1hV8mLjnt3BpVgM5ZgV3aotJoJhSfOgXO1hiNq1s++ZBs1God2bySYCxpn6JTlqxMMtuAJPDOoBE2xzf1gxok/21RZNDAmzB9WeSeh2NgHhXHTMoIFxQJL4YhtkyD5TtLfE63UJ30iI7Qy/zQZFunXTS2IF34RWW07IXtptTOSvLj6YwCThb9GvNp2DMTYRq/YDRPNSykNu2D6tzEgau9XH1G3lcB7px7bmHrzOXLkxRoJ2sTdlqmXAxuqEUsZStZijF515HOdB4CAe86HgFOvHkePxrxDeTCFZDDs+Pno1f/UFJ75jAdQIDAQAB";
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			   public void onIabSetupFinished(IabResult result) {
			      if (!result.isSuccess()) {
			         // print error message from in app billing
			         Log.e("In app billing result = ", "Problem setting up In-app Billing: " + result);
			      } else {          
			         Log.e("In app billing result = ", "Successful setup!!!!");
			      } 
			   }
			});
		
		//set up the action bar
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		EnterMileage Fragment1 = new EnterMileage();
		PastResults Fragment2 = new PastResults();
		
		Tab tab1 = actionbar.newTab().setText("Enter a Trip");
		tab1.setTabListener(new tabListener(Fragment1));
		actionbar.addTab(tab1, true);
		
		Tab tab2 = actionbar.newTab().setText("View Past Results");
		tab2.setTabListener(new tabListener(Fragment2));
		actionbar.addTab(tab2);
	}
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_bar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.about:
	        	About aboutFrag = new About();
	        	//used to test if the about fragment is being displayed. prevents from duplicate fragments being added to backstack
	        	TextView aboutTest = (TextView) findViewById(R.id.about_message);
	        	if(aboutTest != null) {
	        		//do nothing
	        	} else {
	        		FragmentManager fragmentManager = getFragmentManager();
	        		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        		fragmentTransaction.replace(R.id.container_layout, aboutFrag);
	        		fragmentTransaction.addToBackStack(null);
	        		fragmentTransaction.commit();
	        	}
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(this, v);
	    popup.setOnMenuItemClickListener(this);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.card_menu, popup.getMenu());
	    
	    View view = (View)v.getParent();
	    TextView cardId = (TextView) view.findViewById(R.id.rowId);
	    rowId = Integer.parseInt(cardId.getText().toString());
	    
	    Spinner spinner = (Spinner) findViewById(R.id.sort_spinner);
	    pos = spinner.getSelectedItemPosition();
	    
	    popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.card_delete:
				PastResults.deleteCard(rowId, pos, context);
				return true;
			default:
				return false;
		}
	}
	
}







