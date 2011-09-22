package com.t3hh4xx0r.god_mode;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.god_mode.widgets.ShortcutsSelectionPreference;
import com.t3hh4xx0r.god_mode.widgets.ShortcutsSelectionPreference.onSelectionListener;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.view.View;


public class LockscreenMode extends PreferenceActivity
implements OnPreferenceChangeListener, ShortcutsSelectionPreference.onSelectionListener {
	
	private static String TAG = "LockscreenMode";
	private boolean DBG = (false || Constants.FULL_DBG);

	private static final String TRACKPAD_WAKE_SCREEN = "trackpad_wake_screen";
	private static final String VOLUME_WAKE_SCREEN = "volume_wake_screen";
	private static final String TRACKPAD_UNLOCK_SCREEN = "trackpad_unlock_screen";
	private static final String MENU_UNLOCK_SCREEN = "menu_unlock_screen";
	private static final String LOCKSCREEN_SHORTCUTS = "lockscreen_shortcuts";
	private static final String LOCKSCREEN_ALWAYS_BATTERY = "lockscreen_always_battery";
	private static final String LOCKSCREEN_TYPE = "lockscreen_type";
	private static final String CUSTOM_LOCKSCREEN_TIMEOUT = "custom_lockscreen_timeout";

	private int lockScreenTypeValue;
	
	private ListPreference mLockScreenTypeList;
	private CheckBoxPreference mTrackpadWakeScreen;
	private CheckBoxPreference mVolumeWakeScreen;
	private CheckBoxPreference mTrackpadUnlockScreen;
	private CheckBoxPreference mMenuUnlockScreen;
	private CheckBoxPreference mLockscreenShortcuts;
	private CheckBoxPreference mLockscreenAlwaysBattery;
	private ShortcutsSelectionPreference mShortcutsSelectionPreference;
	private Preference mLockscreenTimeout;
	
	Context context;
	Dialog d;
	AlertDialog.Builder builder;

	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.lockscreen_prefs);
		PreferenceScreen prefSet = getPreferenceScreen();

		d = new Dialog(this);
		builder = new AlertDialog.Builder(this);
		context = this.getApplicationContext();
		
		// Track pad wake preference
		mTrackpadWakeScreen = (CheckBoxPreference) prefSet.findPreference(TRACKPAD_WAKE_SCREEN);
		mTrackpadWakeScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.TRACKPAD_WAKE_SCREEN, 0) == 1);
		// Volume wake preference
		mVolumeWakeScreen = (CheckBoxPreference) prefSet.findPreference(VOLUME_WAKE_SCREEN);
		mVolumeWakeScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
		// Track Pad unlock preference
		mTrackpadUnlockScreen = (CheckBoxPreference) prefSet.findPreference(TRACKPAD_UNLOCK_SCREEN);
		mTrackpadUnlockScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.TRACKPAD_UNLOCK_SCREEN, 0) == 1);
		// Menu unlock preference
		mMenuUnlockScreen = (CheckBoxPreference) prefSet.findPreference(MENU_UNLOCK_SCREEN);
		mMenuUnlockScreen.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.MENU_UNLOCK_SCREEN, 0) == 1);
		// Shortcuts preference
		mLockscreenShortcuts = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_SHORTCUTS);
		mLockscreenShortcuts.setChecked (Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_SHORTCUTS, 0) == 1);
		// Battery preference
		mLockscreenAlwaysBattery = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_BATTERY);
		mLockscreenAlwaysBattery.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_ALWAYS_BATTERY, 0) == 1);
		// Lockscreen type preference
		mLockScreenTypeList = (ListPreference) findPreference(LOCKSCREEN_TYPE);
		lockScreenTypeValue = Settings.System.getInt(getContentResolver(),Settings.System.LOCKSCREEN_TYPE, 4);
		mLockScreenTypeList.setValue(String.valueOf(lockScreenTypeValue));
		mLockScreenTypeList.setOnPreferenceChangeListener(this);
		// Custom Lockscreen timeout
		mLockscreenTimeout = (Preference) prefSet.findPreference(CUSTOM_LOCKSCREEN_TIMEOUT);
		mLockscreenTimeout.setOnPreferenceClickListener(new OnPreferenceClickListener() {

        public boolean onPreferenceClick(Preference preference) {

	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

	        View layout = inflater.inflate(R.layout.number_picker_layout, null);
	        final EditText edit = (EditText) layout.findViewById(R.id.timepicker_input);
	        int seconds = Settings.System.getInt(getContentResolver(), CUSTOM_LOCKSCREEN_TIMEOUT, 5) / 1000;

	        edit.setText(Integer.toString(seconds));

	        builder.setView(layout);
	        builder.setMessage("Lockscreen Timeout").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
	      
		    public void onClick(DialogInterface dialog,int id) {
			    int result = Integer.parseInt(edit.getText().toString());
			    Settings.System.putInt(getContentResolver(), CUSTOM_LOCKSCREEN_TIMEOUT,result * 1000);
		    }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    
		    public void onClick(DialogInterface dialog,int id) {
		        dialog.cancel();
		    }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
	        return true;
        }
    });
		
		
		mShortcutsSelectionPreference = (ShortcutsSelectionPreference) prefSet.findPreference("lockscreen_app_selection");
		mShortcutsSelectionPreference.setSelectionListener(this);
		
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
		}
        return true;
    }
	
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mLockScreenTypeList) {
                lockScreenTypeValue = Integer.valueOf((String) objValue);
                Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_TYPE, Integer.valueOf((String) objValue));
        if(DBG){
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_HCC_LOCKSCREEN)Log.d(TAG, "Concept used");
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_TAB_LOCKSCREEN)Log.d(TAG, "Tab used");
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_ROTARY_LOCKSCREEN)Log.d(TAG, "Rotary used");
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_HONEYCOMB_LOCKSCREEN)Log.d(TAG, "Honey used");
			if(Integer.parseInt(mLockScreenTypeList.getValue()) == Settings.System.USE_SENSELIKE_LOCKSCREEN)Log.d(TAG, "Sense used");
        }
        
		return true;
	}
	return false;
    }
     
    private final int REQUEST_SHORTCUT_APPLICATION = 100;
    private int mLockscreenShortcut;
    
    
    
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       
    	Log.d(TAG,"On activity result");

        // The pattern used here is that a user PICKs a specific application,
        // which, depending on the target, might need to CREATE the actual target.

        // For example, the user would PICK_SHORTCUT for "Music playlist", and we
        // launch over to the Music app to actually CREATE_SHORTCUT.
	        if (resultCode == RESULT_OK ) {
	        	switch(requestCode){
	        	case REQUEST_SHORTCUT_APPLICATION:
	        	{
	        		
	        		switch(mLockscreenShortcut){
	        		
	        		case ShortcutsSelectionPreference.onSelectionListener.SELECTION_ONE:
	        			Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_1, data.toUri(0));
	        			break;
	        		case ShortcutsSelectionPreference.onSelectionListener.SELECTION_TWO:
	        			Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_2, data.toUri(0));
	        			break;
	        		case ShortcutsSelectionPreference.onSelectionListener.SELECTION_THREE:
	        			Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_3, data.toUri(0));
	        			break;
	        		case ShortcutsSelectionPreference.onSelectionListener.SELECTION_FOUR:
	        			Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_4, data.toUri(0));
	        			break;
	        		
	        		}
	        		
	        	}
	        	// end REQUEST_SHORTCUT_APPLICATION:
	        	
	        }
	    }
    
    }

	@Override
	public void startSelection(int selection) {
		// TODO Auto-generated method stub
		mLockscreenShortcut = selection;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
        startActivityForResult(pickIntent,  REQUEST_SHORTCUT_APPLICATION);
	}
    
}
