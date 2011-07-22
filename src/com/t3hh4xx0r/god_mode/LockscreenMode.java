package com.t3hh4xx0r.god_mode;


import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Constants;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import android.util.Log;
import android.provider.Settings;


public class LockscreenMode extends PreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private static String TAG = "LockscreenMode";
	private boolean DBG = (false || Constants.FULL_DBG);

	private static final String CARRIER_CAP = "carrier_caption";
	private static final String TRACKPAD_WAKE_SCREEN = "trackpad_wake_screen";
	private static final String VOLUME_WAKE_SCREEN = "volume_wake_screen";
	private static final String TRACKPAD_UNLOCK_SCREEN = "trackpad_unlock_screen";
	private static final String MENU_UNLOCK_SCREEN = "menu_unlock_screen";
	private static final String LOCKSCREEN_SHORTCUTS = "lockscreen_shortcuts";
	private static final String LOCKSCREEN_ALWAYS_BATTERY = "lockscreen_always_battery";
	private static final String LOCKSCREEN_TYPE = "lockscreen_type";
	private static final String LOCKSCREEN_ORIENTATION = "lockscreen_orientation";
	
	private ListPreference mLockScreenTypeList;
	private EditTextPreference mCarrierCaption;
	private CheckBoxPreference mTrackpadWakeScreen;
	private CheckBoxPreference mVolumeWakeScreen;
	private CheckBoxPreference mTrackpadUnlockScreen;
	private CheckBoxPreference mMenuUnlockScreen;
	private CheckBoxPreference mLockscreenShortcuts;
	private CheckBoxPreference mLockscreenOrientation;
	private CheckBoxPreference mLockscreenAlwaysBattery;
	
	

	

	
	@Override
        public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.lockscreen_prefs);
		PreferenceScreen prefSet = getPreferenceScreen();
		
		// Carrier caption preference
		mCarrierCaption = (EditTextPreference)prefSet.findPreference(CARRIER_CAP);
		// Track pad wake preference
		mTrackpadWakeScreen = (CheckBoxPreference) prefSet.findPreference(TRACKPAD_WAKE_SCREEN);
		mTrackpadWakeScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.TRACKPAD_WAKE_SCREEN, 0) != 0);
		// Volume wake preference
		mVolumeWakeScreen = (CheckBoxPreference) prefSet.findPreference(VOLUME_WAKE_SCREEN);
		mVolumeWakeScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN, 0) != 0);
		// Track Pad unlock preference
		mTrackpadUnlockScreen = (CheckBoxPreference) prefSet.findPreference(TRACKPAD_UNLOCK_SCREEN);
		mTrackpadUnlockScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.TRACKPAD_UNLOCK_SCREEN, 0) != 0);
		// Menu unlock preference
		mMenuUnlockScreen = (CheckBoxPreference) prefSet.findPreference(MENU_UNLOCK_SCREEN);
		mMenuUnlockScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.MENU_UNLOCK_SCREEN, 0) != 0);
		// Shortcuts preference
		mLockscreenShortcuts = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_SHORTCUTS);
		mLockscreenShortcuts.setChecked (Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_SHORTCUTS, 0) != 0);
		// Battery preference
		mLockscreenAlwaysBattery = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_BATTERY);
		mLockscreenAlwaysBattery.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_ALWAYS_BATTERY, 0) != 0);
		// Lockscreen type preference
		mLockScreenTypeList  = (ListPreference) findPreference(LOCKSCREEN_TYPE);
		mLockScreenTypeList.setValueIndex(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_TYPE, 1)-1);
		// Lockscreen oreientation preference & default to portrait
		mLockscreenOrientation = (CheckBoxPreference) this.findPreference(LOCKSCREEN_ORIENTATION);
		mLockscreenOrientation.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_ORIENTATION, Configuration.ORIENTATION_PORTRAIT) != Configuration.ORIENTATION_LANDSCAPE);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }	
	
	public boolean onDialogClosed() {
		return false;
	}

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        
		if (preference == mLockscreenAlwaysBattery) {
			    value = mLockscreenAlwaysBattery.isChecked();
			    Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_ALWAYS_BATTERY, value ? 1 : 0);
		} else if (preference == mLockscreenShortcuts) {
			    value = mLockscreenShortcuts.isChecked();
			    Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_SHORTCUTS, value ? 1 : 0);
		} else if (preference == mTrackpadWakeScreen) {
			    value = mTrackpadWakeScreen.isChecked();
			    Settings.System.putInt(getContentResolver(), Settings.System.TRACKPAD_WAKE_SCREEN, value ? 1 : 0);
		} else if (preference == mVolumeWakeScreen) {
			    value = mVolumeWakeScreen.isChecked();
			    Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN, value ? 1 : 0);
		} else if (preference == mTrackpadUnlockScreen) {
			    value = mTrackpadUnlockScreen.isChecked();
			    Settings.System.putInt(getContentResolver(), Settings.System.TRACKPAD_UNLOCK_SCREEN, value ? 1 : 0);
		} else if (preference == mMenuUnlockScreen) {
			    value = mMenuUnlockScreen.isChecked();
			    Settings.System.putInt(getContentResolver(), Settings.System.MENU_UNLOCK_SCREEN, value ? 1 : 0);
		}else if ( preference == mLockScreenTypeList){
			// Do Nothing
			
		}else if ( preference ==  mLockscreenOrientation){
			    value = mLockscreenOrientation.isChecked();
			    Log.d(TAG, "Preference is checked =  " + mLockscreenOrientation.isChecked());
			    Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_ORIENTATION, value ? Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE );
			    Log.d(TAG, "Setting lockscreen to portrait orientation  =  " + (Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_ORIENTATION, Configuration.ORIENTATION_PORTRAIT) != Configuration.ORIENTATION_LANDSCAPE) );
		}
        return true;
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (CARRIER_CAP.equals(key)) {
			Settings.System.putString(getContentResolver(),CARRIER_CAP, sharedPreferences.getString(CARRIER_CAP, ""));
            ActivityManager am = (ActivityManager)getSystemService(
                    Context.ACTIVITY_SERVICE);
            am.forceStopPackage("com.android.phone");
		}
		
		
		
		if(mLockScreenTypeList.getKey().equals(key)){
			
			Settings.System.putInt(getContentResolver(),Settings.System.LOCKSCREEN_TYPE,Integer.parseInt(mLockScreenTypeList.getValue()));
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_HC_LOCKSCREEN)Log.d(TAG, "Concept used");
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_TAB_LOCKSCREEN)Log.d(TAG, "Tab used");
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_ROTARY_LOCKSCREEN)Log.d(TAG, "Rotary used");
			
		}
		
	}
}
