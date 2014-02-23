package com.jackson.simplempgcalculator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
		
	/* VARIABLES */
	private static Context context;
	public Integer rowId;
	public Integer pos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		context = this;
		
		//set up the action bar
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		EnterMileageFrag Fragment1 = new EnterMileageFrag();
		PastResults Fragment2 = new PastResults();
		
		Tab tab1 = actionbar.newTab().setText("Enter a Trip");
		tab1.setTabListener(new tabListener(Fragment1));
		actionbar.addTab(tab1, true);
		
		Tab tab2 = actionbar.newTab().setText("View Past Results");
		tab2.setTabListener(new tabListener(Fragment2));
		actionbar.addTab(tab2);
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
	        case R.id.action_settings:
	        	//launch the settings activity
	        	Intent intent = new Intent(this, preferences.class);
	        	startActivity(intent);
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







