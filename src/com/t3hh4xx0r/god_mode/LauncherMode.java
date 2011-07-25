package com.t3hh4xx0r.god_mode;

import java.util.List;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.god_mode.utils.PackageUtils;
import com.t3hh4xx0r.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.IPackageDataObserver;
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

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class LauncherMode extends PreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener {
	
    private static final String TAG = "LauncherMode";

	private boolean DBG = (false || Constants.FULL_DBG);

	private ListPreference mScreenPreference;
	
	private static boolean mIsScreenChangerOn = false;

	CheckBoxPreference mScreenCheckBox;
	CheckBoxPreference mLauncherEndlessLoop;
	CheckBoxPreference mWallpaperLoop;
        CheckBoxPreference mLauncherOrientationPref;
        CheckBoxPreference mFourHotseats;

    private ActivityManager activityManager;
	
    private String[] mAppNames;

    private static final String THREE = "Three";
    private static final String FIVE = "Five";
    private static final String SEVEN = "Seven";
    private static final String SCREENSETTINGS = "NUM_SCREENS";
    private static final String LAUNCHER_ENDLESS_LOOP = "launcher_endless_loop";
    private static final String WALLPAPER_LOOP = "wallpaper_loop";
    private static final String LAUNCHER_ORIENTATION_PREF = "launcher_orientation";
    private static final String FOUR_HOTSEATS = "four_hotseats"; 

    private static final String LAUNCHER = "com.android.launcher";

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
	mScreenCheckBox.setChecked(mIsScreenChangerOn);

	mWallpaperLoop = (CheckBoxPreference) prefSet.findPreference(WALLPAPER_LOOP);
	mWallpaperLoop.setChecked(Settings.System.getInt(getContentResolver(),
			Settings.System.WALLPAPER_LOOP, 1) == 1);

	mLauncherEndlessLoop = (CheckBoxPreference) prefSet.findPreference(LAUNCHER_ENDLESS_LOOP);
	mLauncherEndlessLoop.setChecked(Settings.System.getInt(getContentResolver(),
			Settings.System.LAUNCHER_ENDLESS_LOOP, 1) == 1);
        
        mLauncherOrientationPref = (CheckBoxPreference) prefSet.findPreference(LAUNCHER_ORIENTATION_PREF);
        mLauncherOrientationPref.setChecked(Settings.System.getInt(getContentResolver(),
			Settings.System.LAUNCHER_ORIENTATION, 0) != 0);

	mFourHotseats = (CheckBoxPreference) prefSet.findPreference(FOUR_HOTSEATS);
        mFourHotseats.setChecked(Settings.System.getInt(getContentResolver(),
			Settings.System.FOUR_HOTSEATS, 1) == 1);

	mScreenPreference = (ListPreference) findPreference("num_screens");
	
	activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);

	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	    boolean value;

	    if (preference == mWallpaperLoop) {
		value = mWallpaperLoop.isChecked();
		Settings.System.putInt(getContentResolver(),
			Settings.System.WALLPAPER_LOOP, value ? 1 : 0);
		restartLauncher2(activityManager);
	    }

            if (preference == mLauncherOrientationPref) {
                value = mLauncherOrientationPref.isChecked ();
                 Settings.System.putInt(getContentResolver(),
                    Settings.System.LAUNCHER_ORIENTATION, value ? 1 : 0);
            }

	    if (preference == mFourHotseats) {
		value = mFourHotseats.isChecked();
		 Settings.System.putInt(getContentResolver(),
			Settings.System.FOUR_HOTSEATS, value ? 1 : 0);
	    }

	    if (preference == mLauncherEndlessLoop) {
		value = mLauncherEndlessLoop.isChecked();
		Settings.System.putInt(getContentResolver(),
			Settings.System.LAUNCHER_ENDLESS_LOOP, value ? 1 : 0);
	        restartLauncher2(activityManager);
	    }
	    
	    if (preference == mScreenCheckBox && mIsScreenChangerOn == false) {
		// Ask the user if they are sure they whant to proceed/
	    	// If they do let them know that the homescreen widgets/apps 
	    	// will be reset
	    	alertbox("Warning", "Selecting a new homescreen configuration will remove the current configuration. " +
	    			"If you do not wish to set up your homescreen again, don't change the configuration.");
	    	
	    	mIsScreenChangerOn = true;
	    	
		    }
	    else if (preference == mScreenCheckBox && mIsScreenChangerOn == true){
	    	
	    	mIsScreenChangerOn = false;
	    	
	    }
	    
	    return true;
	}
	
	//brough to you buy http://www.androidsnippets.com/display-an-alert-box
	
	protected void alertbox(String title, String mymessage)
	   {
	   new AlertDialog.Builder(this)
	      .setMessage(mymessage)
	      .setTitle(title)
	      .setCancelable(true)
	      .setNeutralButton("OK",
	         new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton){}
	         })
	      .show();
	   }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	if(DBG)Log.v(TAG, "shared preference changed");    	
        	
	        if(key == mScreenPreference.getKey()){
	        if(DBG)Log.v(TAG, "on shared screen preference change in God Mode");
	        	registerScreenChange(mScreenPreference.getEntry().toString());
	        	PackageUtils packageUtility = new PackageUtils(this , LAUNCHER);
	        	packageUtility.initiateClearUserData();
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


 


