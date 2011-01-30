package com.t3hh4xx0r.god_mode;


import com.android.spare_parts.R;
import com.android.spare_parts.R.xml;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import android.util.Log;
import android.provider.Settings;


public class LockscreenMode extends PreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String CARRIER_CAP = "carrier_caption";
	private static final String LOCKSCREEN_ROTARY_LOCK = "use_rotary_lockscreen";
	
	private EditTextPreference mCarrierCaption;
	private CheckBoxPreference mUseRotaryLockPref;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.lockscreen_prefs);
		PreferenceScreen prefSet = getPreferenceScreen();
		
		
		mUseRotaryLockPref = (CheckBoxPreference)prefSet.findPreference(LOCKSCREEN_ROTARY_LOCK);
		mUseRotaryLockPref.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.USE_ROTARY_LOCKSCREEN, 0) != 0);		

		mCarrierCaption = (EditTextPreference)prefSet.findPreference(CARRIER_CAP);

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }	
    
	
	public boolean onDialogClosed() {

		return false;
	}


    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;	
        if (preference == mUseRotaryLockPref) {
            value = mUseRotaryLockPref.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.USE_ROTARY_LOCKSCREEN, value ? 1 : 0);
            //Temporary hack to fix Phone FC's when swapping styles.
            ActivityManager am = (ActivityManager)getSystemService(
                    Context.ACTIVITY_SERVICE);
            am.forceStopPackage("com.android.phone");
        }
        return true;
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (CARRIER_CAP.equals(key)) {
			Settings.System.putString(getContentResolver(),CARRIER_CAP, sharedPreferences.getString(CARRIER_CAP, ""));
			//Didn't i say i was learning?
            ActivityManager am = (ActivityManager)getSystemService(
                    Context.ACTIVITY_SERVICE);
            am.forceStopPackage("com.android.phone");
		}
		
	}
	
}
