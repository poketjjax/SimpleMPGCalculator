package com.jackson.simplempgcalculator;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class PastResults extends Fragment {

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
		populateResultsList();
	}

	private void populateResultsList() {
		DbAdapter adapter = new DbAdapter(getActivity());
		
		adapter.open();
		
		String selectAll = "SELECT * FROM trips";
		Cursor cursor = adapter.select(selectAll);
		
		String from[] = new String[]
				{DbAdapter.GALLONS, DbAdapter.MILES, DbAdapter.MPG,
					DbAdapter.PRICE, DbAdapter.TOTAL_COST, DbAdapter.DATE};
		int[] to = new int[]
				{R.id.gallons_item, R.id.miles_item, R.id.mpg_item,
					R.id.price_item, R.id.cost_item, R.id.date};
		
		
		SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.result_item, cursor, from, to);
		
		ListView resultsList = (ListView) getView().findViewById(R.id.results_list);
		if(resultsList != null){
			resultsList.setAdapter(myCursorAdapter);
			myCursorAdapter.setViewBinder(new resultsViewBinder());
		}
	}

	
}
