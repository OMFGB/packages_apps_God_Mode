package com.t3hh4xx0r.god_mode;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;		
import android.preference.PreferenceScreen;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class LauncherMode extends PreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener {
	
    private static final String TAG = "LauncherMode";
    private boolean DBG = true;

	private ListPreference mScreenPreference;

	CheckBoxPreference mScreenCheckBox;
	CheckBoxPreference mLauncherEndlessLoop;

    private ActivityManager activityManager;
	
    private String[] mAppNames;

    private static final String THREE = "Three";
    private static final String FIVE = "Five";
    private static final String SEVEN = "Seven";
    private static final String SCREENSETTINGS = "NUM_SCREENS";
    private static final String LAUNCHER_ENDLESS_LOOP = "launcher_endless_loop";

    private static final String LAUNCHER = "com.android.launcher";
    private static final int OP_SUCCESSFUL = 1;
    private static final int OP_FAILED = 2;
    private static final int CLEAR_USER_DATA = 1;
    
  

	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.launcher_prefs);
		PreferenceManager.setDefaultValues(LauncherMode.this, R.xml.launcher_prefs, false);
		
		// All preferences are call from the fallowing function
		// If you add preferences add the to the function below and
		// not here, non-initializtion here leads to null pointer exception
		setPreferences();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

   public void setPreferences(){
	PreferenceScreen prefSet = getPreferenceScreen();
	mScreenCheckBox = (CheckBoxPreference) findPreference("screen_changer");
	mLauncherEndlessLoop = (CheckBoxPreference) prefSet.findPreference(LAUNCHER_ENDLESS_LOOP);
	mLauncherEndlessLoop.setChecked(Settings.System.getInt(getContentResolver(),
			Settings.System.LAUNCHER_ENDLESS_LOOP, 0) == 1);
	mScreenPreference = (ListPreference) findPreference("num_screens");
	activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);

	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	    boolean value;

	    if (preference == mLauncherEndlessLoop) {
		value = mLauncherEndlessLoop.isChecked();
		Settings.System.putInt(getContentResolver(),
			Settings.System.LAUNCHER_ENDLESS_LOOP, value ? 1 : 0);
	        restartLauncher2(activityManager);
	    }
	    return true;
	}

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	if(DBG)Log.v(TAG, "shared preference changed");    	
	        //This is the skeleton for the number of screen to change to
	        if(key == mScreenPreference.getKey()){
	        if(DBG)Log.v(TAG, "on shared screen preference change in God Mode");
	        	registerScreenChange(mScreenPreference.getEntry().toString());
	        	restartLauncher2(activityManager);
	        }
        }
     
         void registerScreenChange(String st){
         	if(compareStrings(st.compareTo(SEVEN))) {
         		st.compareTo(SEVEN);
         	  Settings.System.putInt(getContentResolver(), SCREENSETTINGS, 7);
         	  Log.i(TAG, "The number of screens to register is " + st);
         	}
         	if (compareStrings(st.compareTo(FIVE))) {
         	  Settings.System.putInt(getContentResolver(), SCREENSETTINGS, 5);
           	  Log.i(TAG, "The number of screens to register is " + st);
         	}
         	if (compareStrings(st.compareTo(THREE))) {
               Settings.System.putInt(getContentResolver(), SCREENSETTINGS, 3);
           	  Log.i(TAG, "The number of screens to register is " + st);
         	}
         }

         @Override
         protected void onResume() {
             super.onResume();
             // Set up a listener whenever a key changes
             getPreferenceScreen().getSharedPreferences()
                     .registerOnSharedPreferenceChangeListener(this);
         }

         @Override
         protected void onPause() {
             super.onPause();
             // Unregister the listener whenever a key changes
             getPreferenceScreen().getSharedPreferences()
                     .unregisterOnSharedPreferenceChangeListener(this);
         }
         
         boolean compareStrings(int i){
        		if (i == 0)
        			return true;
        		else return false;
        	}

         void toastMsg(String msg){
         	Context context = getApplicationContext();
          	int duration = Toast.LENGTH_SHORT;
         	Toast toast = Toast.makeText(context, msg, duration);
         	toast.show();
         }
         
         public void restartLauncher2(ActivityManager activity) {
     		if(DBG)
     			Log.d(TAG, "About to kill the launcher application");
     	 	activity.killBackgroundProcesses(LAUNCHER);	
         }
}


 


