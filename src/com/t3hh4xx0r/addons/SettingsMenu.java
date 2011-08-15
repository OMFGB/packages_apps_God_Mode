package com.t3hh4xx0r.addons;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Slog;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.Downloads;
import com.t3hh4xx0r.addons.utils.BroadcastReceivers;

import java.util.Calendar;

public class SettingsMenu extends PreferenceActivity implements OnPreferenceChangeListener {
        public static String TAG = "SettingsMenu";

	private PendingIntent pendingIntent;

	private ListPreference mRefreshTime;
	private CheckBoxPreference mAutoSync;
        private CheckBoxPreference mAutoUpdate;
	private CheckBoxPreference mForceAddonsSync;
	private CheckBoxPreference mForceNightliesSync;
	private PreferenceCategory mNightlies;
	private PreferenceCategory mAddons;
	private PreferenceCategory mSync;
        private int refreshValue;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings_menu);
	
		InitializeUI();
		setPreferencesCheckValues();

	}

    private void InitializeUI() {
            mAutoUpdate = (CheckBoxPreference) findPreference("auto_update");
	    mAutoSync = (CheckBoxPreference) findPreference("auto_sync");
	    mForceAddonsSync = (CheckBoxPreference) findPreference("force_addons_sync");
	    mForceNightliesSync = (CheckBoxPreference) findPreference("force_nightlies_sync");
	    mSync = (PreferenceCategory) findPreference("auto_sync_cat");
	    mAddons = (PreferenceCategory) findPreference("addons_settings_cat");
            mNightlies = (PreferenceCategory) findPreference("nightlies_settings_cat");

            mRefreshTime = (ListPreference) findPreference("refresh_time");
       	    refreshValue = (Constants.REFRESH_TIME);
       	    mRefreshTime.setValue(String.valueOf(refreshValue));
	    mRefreshTime.setOnPreferenceChangeListener(this);
	}

    private void setPreferencesCheckValues() {
            mAutoUpdate.setChecked(Constants.AUTOMATICALLY_UPDATE);
	    mAutoSync.setChecked(Constants.AUTOMATICALLY_SYNC);
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
       if(preference == mAutoUpdate){
            value = mAutoUpdate.isChecked();
            if(value) {
                Constants.AUTOMATICALLY_UPDATE  = true;
		//start the update service
	        Toast.makeText(SettingsMenu.this, "Start service", Toast.LENGTH_LONG).show();
		Intent myIntent = new Intent(getBaseContext(), BroadcastReceivers.class);
		pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 5);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Constants.REFRESH_TIME*1000, pendingIntent);
	    } else {
                Constants.AUTOMATICALLY_UPDATE = false;
            }
            return true;
        } 
	if(preference == mAutoSync){
	    value = mAutoSync.isChecked();	
	    if(!value) {
	        Constants.AUTOMATICALLY_SYNC = false;
	        mAddons.setEnabled(false);
	        mNightlies.setEnabled(false);
	        mForceAddonsSync.setChecked(false);
	        mForceNightliesSync.setChecked(false);	
	    } else {
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
       	    if(value) {
		Constants.FORCE_ADDONS_ACTIVITY_SYNC = true;
	    } else {
	        Constants.FORCE_ADDONS_ACTIVITY_SYNC = false;	
	        }
	    return true;
	    }
	if(preference == mForceNightliesSync){
	    value = mForceNightliesSync.isChecked();
            if(value) {
	        Constants.FORCE_NIGHTLIES_ACTIVITY_SYNC = true;
	    } else {
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

	public boolean onPreferenceChange(Preference preference, Object objValue) {
           if (preference == mRefreshTime) {
                refreshValue = Integer.valueOf((String) objValue);
		Constants.REFRESH_TIME = Integer.valueOf((String) objValue);
		Slog.d(TAG, "New refresh time is " + Constants.REFRESH_TIME);
                return true;
	   }
	   return false;
	}
}
