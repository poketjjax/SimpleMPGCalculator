package com.jackson.simplempgcalculator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PastResults extends Fragment implements OnClickListener, PopupMenu.OnMenuItemClickListener  {
	
	/* VARIABLES */
	private Button delete;
	private ImageView cardMenu;
	public Boolean listIsEmpty = true;
	public Integer rowId;

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

	public void populateResultsList() {
		
		cardMenu = (ImageView) getActivity().findViewById(R.id.cardBtn);
		if(cardMenu == null) {
			Toast.makeText(getActivity(), "it's null", Toast.LENGTH_SHORT).show();
		} else {
			
		
		cardMenu.setOnClickListener(this);
		}
		DbAdapter adapter = new DbAdapter(getActivity());
		
		adapter.open();
		
		Cursor cursor = adapter.select(DbAdapter.selectAll);
		
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
		myCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.result_item, cursor, from, to);
		
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

	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(getActivity(), v);
	    popup.setOnMenuItemClickListener(this);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.card_menu, popup.getMenu());
	    View view = (View)v.getParent();
	    TextView cardId = (TextView) view.findViewById(R.id.rowId);
	    rowId = Integer.parseInt(cardId.getText().toString());
	    popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.card_delete:
				deleteCard(rowId);
				return true;
			default:
				return false;
		}
	}
	
	public void deleteCard(int rowId) {
		int count;
		
		DbAdapter adapter = new DbAdapter(getActivity());
		adapter.open();
		count = adapter.deleteRow(rowId);
		adapter.close();
		
		if(count > 0) {
			Toast.makeText(getActivity(), "Trip deleted!", Toast.LENGTH_SHORT).show();
//			PastResults pastResults = new PastResults();
//			pastResults.populateResultsList();
		} else {
			Toast.makeText(getActivity(), "Deleting failed, try again", Toast.LENGTH_SHORT).show();
		}
	}
	
}















