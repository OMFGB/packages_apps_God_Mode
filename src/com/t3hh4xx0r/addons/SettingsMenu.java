package com.t3hh4xx0r.addons;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.Downloads;





public class SettingsMenu extends PreferenceActivity {

	private CheckBoxPreference mAutoSync;
	private CheckBoxPreference mForceAddonsSync;
	private CheckBoxPreference mForceNightliesSync;
	private PreferenceCategory mNightlies;
	private PreferenceCategory mAddons;
	private PreferenceCategory mSync;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings_menu);
		
		
		
		InitializeUI();
		setPreferencesCheckValues();
		
		
		
		
	}
private void InitializeUI() {

	mAutoSync = (CheckBoxPreference) findPreference("auto_sync");
	mAutoSync.setChecked(Constants.AUTOMATICALLY_SYNC);

	
	

	mForceAddonsSync = (CheckBoxPreference) findPreference("force_addons_sync");
	
	
	mForceNightliesSync = (CheckBoxPreference) findPreference("force_nightlies_sync");
	
	mSync = (PreferenceCategory) findPreference("auto_sync_cat");
	
	mAddons = (PreferenceCategory) findPreference("addons_settings_cat");
	
	mNightlies	= (PreferenceCategory) findPreference("nightlies_settings_cat");
	
	
	
	
	}

private void setPreferencesCheckValues(){
	mForceNightliesSync.setChecked(Constants.FORCE_NIGHTLIES_ACTIVITY_SYNC);
	mForceAddonsSync.setChecked(Constants.FORCE_ADDONS_ACTIVITY_SYNC);
	
	if(!mAutoSync.isChecked()){
		Constants.AUTOMATICALLY_SYNC = false;
		mAddons.setEnabled(false);
		mNightlies.setEnabled(false);
		 mForceAddonsSync.setChecked(false);
		 mForceNightliesSync.setChecked(false);
	}
	
}
@Override
public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	    boolean value;
	    
	    
	    if(preference == mAutoSync){
	    	value = mAutoSync.isChecked();
	    	
	    	if(!value){
	    		Constants.AUTOMATICALLY_SYNC = false;
	    		mAddons.setEnabled(false);
	    		mNightlies.setEnabled(false);
	    		 mForceAddonsSync.setChecked(false);
	    		 mForceNightliesSync.setChecked(false);
	    		
	    	}else{
	    		Constants.AUTOMATICALLY_SYNC = true;
	    		mAddons.setEnabled(true);
	    		mNightlies.setEnabled(true);
	    		mForceAddonsSync.setChecked(Constants.shouldForceAddonsSync());
	    		mForceNightliesSync.setChecked(Constants.shouldForceNightliesSync());
	    		
	    	}
	    	return true;
	    }
		if(preference == mForceAddonsSync){
			value = mForceAddonsSync.isChecked();
			if(value){
				Constants.FORCE_ADDONS_ACTIVITY_SYNC = true;
			}else{
				Constants.FORCE_ADDONS_ACTIVITY_SYNC = false;
				
			}
			return true;
		}
		if(preference == mForceNightliesSync){
			value = mForceNightliesSync.isChecked();
			if(value){
				Constants.FORCE_NIGHTLIES_ACTIVITY_SYNC = true;
			}else{
				Constants.FORCE_NIGHTLIES_ACTIVITY_SYNC = false;
				
			}
			return true;
		}
	    
		return false;
	}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.settings_menu, menu);
		
	
		return true;
	}	

@Override
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
