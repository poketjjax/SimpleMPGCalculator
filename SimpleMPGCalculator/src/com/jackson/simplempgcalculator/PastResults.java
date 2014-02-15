package com.jackson.simplempgcalculator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class PastResults extends Fragment implements OnClickListener {
	
	/* VARIABLES */
	private Button delete;
	public Boolean listIsEmpty = true;

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
		populateResultsList();
	}

	private void populateResultsList() {
		DbAdapter adapter = new DbAdapter(getActivity());
		
		adapter.open();
		
		String selectAll = "SELECT * FROM trips";
		Cursor cursor = adapter.select(selectAll);
		
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
		
		
		SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.result_item, cursor, from, to);
		
		ListView resultsList = (ListView) getView().findViewById(R.id.results_list);
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
		        		DbAdapter adapter = new DbAdapter(getActivity());
		        		
		        		adapter.open();
		        		
		        		int rows = adapter.delete();
		        		
		        		if(rows <= 0){
		        			Toast.makeText(getActivity(), "No rows were deleted", Toast.LENGTH_SHORT).show();
		        		} else {
		        			Toast.makeText(getActivity(), "All trips successfully deleted", Toast.LENGTH_SHORT).show();
		        		}
		        		
		        		populateResultsList();
			        }
			})
		    .setNegativeButton(R.string.deleteNo, null)
		    .show();
	}

}















