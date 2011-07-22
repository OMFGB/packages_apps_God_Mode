package com.t3hh4xx0r.addons;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Downloads;

public class SettingsMenu extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings_menu);
		
	}
public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.settings_menu, menu);
		
	
		return true;
	}	

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.clear_download_cache:
			Downloads.deleteDir();
			break;
		case R.id.refresh:
			Downloads.refreshAddonsAndNightlies();
			break;
		}

	    	return(super.onOptionsItemSelected(item));
	}	

	

}
