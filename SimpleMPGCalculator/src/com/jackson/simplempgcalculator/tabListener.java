package com.jackson.simplempgcalculator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

public class tabListener implements ActionBar.TabListener {
	
	private Fragment frag;
	
	public tabListener(Fragment newFrag){
		this.frag = newFrag;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.container_layout, frag);		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.container_layout, frag);
		}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(frag);
		
	}

}
