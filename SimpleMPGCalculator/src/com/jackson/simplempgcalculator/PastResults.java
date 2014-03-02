package com.jackson.simplempgcalculator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class PastResults extends Fragment implements OnClickListener, OnItemSelectedListener  {
	
	/* VARIABLES */
	private Button delete;
	private static DbAdapter adapter;
	private static ListView resultsList;
	private static Context context;
	public static Boolean listIsEmpty = true;
	private static TextView emptyView;

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

		delete = (Button) getActivity().findViewById(R.id.delete_all);
		delete.setOnClickListener(this);
		Spinner spinner = (Spinner) getActivity().findViewById(R.id.sort_spinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(this);
		
		//set up the ad banner
	    AdView adView = (AdView) getActivity().findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("8A9DA1B236989CF0344431DAB1CF42FB").build();
	    adView.loadAd(adRequest);

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
	}

	/* Custom class methods */
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		populateResultsList(getActivity(), pos);		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
}















