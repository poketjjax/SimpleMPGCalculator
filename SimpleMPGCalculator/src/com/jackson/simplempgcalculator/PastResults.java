package com.jackson.simplempgcalculator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PastResults extends Fragment implements OnClickListener, OnItemSelectedListener  {
	
	/* VARIABLES */
	private Button delete;
	private static DbAdapter adapter;
	private static ListView resultsList;
	private static Context context;
	public static Boolean listIsEmpty = true;
	private static TextView emptyView;
	private IabHelper mHelper;
	private Boolean isPurchased = false; 

	/* LIFECYCLE METHODS */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.past_results, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		delete = (Button) getActivity().findViewById(R.id.delete_all);
		delete.setOnClickListener(this);
		Spinner spinner = (Spinner) getActivity().findViewById(R.id.sort_spinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(this);

	    resultsList = (ListView) getView().findViewById(R.id.results_list);
	    emptyView = (TextView) getView().findViewById(R.id.empty_view);
	    resultsList.setEmptyView(emptyView);
		context = getActivity();
		adapter = new DbAdapter(getActivity());
		adapter.open();
		populateResultsList(context, spinner.getSelectedItemPosition());
	}

	@Override
	public void onDestroyView() {
	    adapter.close();
		super.onDestroyView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//stop the keyboard from showing when re-opening app
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		//restore the tabs to the actionbar by entering navigation mode 
		ActionBar actionBar = getActivity().getActionBar();
		if(actionBar.getNavigationMode() == 0) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
		
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

	/* Inherited methods */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.delete_all:
			//check to see if there are any records in the listview before proceeding
			if(listIsEmpty) {
				Toast.makeText(getActivity(), "There are no records to delete", Toast.LENGTH_SHORT).show();
			} else {
				deleteAllRecords();
			}
			break;
		}	
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		populateResultsList(getActivity(), pos);		
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
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
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(isPurchased) {
			MenuItem item = menu.getItem(0).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	/* Custom methods */
	public static void populateResultsList(Context context, int pos) {
		Cursor cursor = adapter.select(DbAdapter.select[pos]);
		
		if(cursor.moveToFirst()) {
			listIsEmpty = false;
		} else {
			listIsEmpty = true;
		}
		
		String from[] = new String[]
				{DbAdapter.GALLONS, DbAdapter.MILES, DbAdapter.MPG,
					DbAdapter.PRICE, DbAdapter.TOTAL_COST, DbAdapter.DATE, DbAdapter.ID};
		int[] to = new int[]
				{R.id.gallons_item, R.id.miles_item, R.id.mpg_item,
					R.id.price_item, R.id.cost_item, R.id.date, R.id.rowId};
		
		
		SimpleCursorAdapter myCursorAdapter;
		myCursorAdapter = new SimpleCursorAdapter(context, R.layout.result_item, cursor, from, to);
		
		
		if(resultsList != null){
			resultsList.setAdapter(myCursorAdapter);
			myCursorAdapter.setViewBinder(new resultsViewBinder());
		}
	}

	private void deleteAllRecords() {		
		new AlertDialog.Builder(getActivity())
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.deleteTitle)
	        .setMessage(R.string.deleteMessage)
	        .setPositiveButton(R.string.deleteYes, new DialogInterface.OnClickListener() {
		        	@Override
				    public void onClick(DialogInterface dialog, int which) {
		        		int rows = adapter.delete();

		        		if(rows <= 0){
		        			Toast.makeText(getActivity(), "No rows were deleted", Toast.LENGTH_SHORT).show();
		        		} else {
		        			Toast.makeText(getActivity(), "All trips successfully deleted", Toast.LENGTH_SHORT).show();
		        		}
		        		populateResultsList(context, 0);
			        }
			})
		    .setNegativeButton(R.string.deleteNo, null)
		    .show();
	}
	
	public static void deleteCard(int rowId, int pos, Context context) {
		int count;
		count = adapter.deleteRow(rowId);
		if(count > 0) {
			Toast.makeText(context, "Trip deleted!", Toast.LENGTH_SHORT).show();
			populateResultsList(context, pos);
		} else {
			Toast.makeText(context, "Deleting failed, try again", Toast.LENGTH_SHORT).show();
		}
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















