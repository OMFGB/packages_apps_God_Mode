package com.android.spare_parts;


import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class LauncherParts extends PreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener {
	
    private static final String TAG = "LauncherParts";
    private boolean DBG = true;

	private static final String RIGHT_AB = "rightaction_button";
	private static final String LEFT_AB = "leftaction_button";	
	
	// List Preferences
	private ListPreference mLAB;
	private ListPreference mRAB;	
	private ListPreference mScreenPreference;
	
	// Preference screens
	
	// Preference Categories
	PreferenceCategory mMappings;
	PreferenceCategory mUseSettings;
	
	// Preference Checkboxes
	CheckBoxPreference mMappingCheckBox;
	CheckBoxPreference mScreenCheckBox;
        private CheckBoxPreference mUse2dLaunchPref;
	

    private ActivityManager activityManager;
	
	// Strings	
	private String[] mAppNames;
        private static final String LAUNCHER_2D = "use_2d_launcher";
    private static final String THREE = "Three";
    private static final String FIVE = "Five";
    private static final String SEVEN = "Seven";
    private static final String SCREENSETTINGS = "NUM_SCREENS";
    // Activity Names 
    private static final String LAUNCHER = "com.android.launcher";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		// add the preferences and the default values
		addPreferencesFromResource(R.xml.launcher_prefs);
		PreferenceManager.setDefaultValues(LauncherParts.this, R.xml.launcher_prefs, false);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		PreferenceScreen prefSet = getPreferenceScreen();


		setPreferences();
		setInitialActionButtons();

                mUse2dLaunchPref = (CheckBoxPreference)prefSet.findPreference(LAUNCHER_2D);
                mUse2dLaunchPref.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.USE_2D_LAUNCHER   , 0) != 0); 

		PackageManager pm = getPackageManager();
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
		int max = packs.size();
		mAppNames = new String[max];
		for (int i = 0; i < max; i++) {  

		  	try {
				   if(DBG)Log.d("LauncherParts", "packageName: " + packs.get(i).packageName );
				   mAppNames[i] = packs.get(i).packageName;
			   }
			catch (NullPointerException e) {
			       Log.d("LauncherParts", "NullPointerException @: " + i);
			   }    		   
        	}  
		
		setLeftActionButton();
		setRightActionButton();


		activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		

		
	}
	public void setPreferences(){
		
		mMappings = (PreferenceCategory) findPreference("mappings");
		mUseSettings = (PreferenceCategory) findPreference("use_settings");		
		mMappingCheckBox = (CheckBoxPreference) findPreference("using_launcher");
		mScreenCheckBox = (CheckBoxPreference) findPreference("screen_changer");

		mLAB = (ListPreference) findPreference("launcher_lefttab");
		mRAB = (ListPreference) findPreference("launcher_righttab");
		mScreenPreference = (ListPreference) findPreference("num_screens");
		//mMappings.setDependency("using_launcher");

	
	}
	
	public void setInitialActionButtons(){

		//mLAB.setEnabled(true);
		mLAB.setSummary(Settings.System.getString(getContentResolver(), Settings.System.
LEFT_AB));
		
				
		mRAB.setEnabled(true);
		mRAB.setSummary(Settings.System.getString(getContentResolver(), Settings.System.RIGHT_AB));
		

	}
  
	public void setLeftActionButton(){


		mLAB.setDialogTitle("Left Action Button.");
		mLAB.setEntries(R.array.entries_leftbutton);
		mLAB.setEntryValues(R.array.entryvalues_leftbutton);
		
		mLAB.setEntries(mAppNames);
		mLAB.setEntryValues(mAppNames);
		//mLAB.setOnPreferenceChangeListener(this);
	}

	public void setRightActionButton(){
		

	        mRAB.setDialogTitle("Right Action Button.");
                mRAB.setEntries(R.array.entries_rightbutton);
                mRAB.setEntryValues(R.array.entryvalues_rightbutton);
                
                mRAB.setEntries(mAppNames);
                mRAB.setEntryValues(mAppNames);
                //mRAB.setOnPreferenceChangeListener(this);
	}

	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;  
        if (preference == mUse2dLaunchPref) {
            value = mUse2dLaunchPref.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.USE_2D_LAUNCHER, value ? 1 : 0);
            ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            am.forceStopPackage("com.android.launcher");
        }
        return false;
        
    }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	if(DBG)Log.v(TAG, "shared preference changed");    	
        	
	        if (key == mLAB.getKey()) {
	                Settings.System.putString(getContentResolver(), Settings.System.LEFT_AB, mLAB.getEntry().toString());
	        }
	        if (key == mRAB.getKey()) {
	                Settings.System.putString(getContentResolver(), Settings.System.RIGHT_AB, mRAB.getEntry().toString());
	        }
	        if (key ==  mMappingCheckBox.getKey())
	        {
	        	updateMappingsDependancies();
	        	
	        }
	        //This is the skeleton for the number of screen to change to
	        if(key == mScreenPreference.getKey()){
	          
	        		Log.v(TAG, "on shared preference change in God Mode");
	        		
	        		
	        		registerScreenChange(mScreenPreference.getEntry().toString());
	        		restartLauncher2(activityManager);
	        			
	        	
	        }
	        
        
        
        }
        public void updateMappingsDependancies(){
        	
        	if(mMappingCheckBox.isEnabled()){
        			//mMappings.setEnabled(true);
              		//mLAB.setEnabled(true);
              		//mRAB.setEnabled(true);
        	}
        	else{
    			//mMappings.setEnabled(false);
        		//mLAB.setEnabled(false);
          		//mRAB.setEnabled(false);
        	}
        		
        	
        }


     
         class Package {  
                private String Appname = "";
                private String PackagName = ""; 
                private Drawable icon;
           }  
         
         void registerScreenChange(String st){
             
        	    
         	if(compareStrings(st.compareTo(SEVEN)) )
         	{
         		st.compareTo(SEVEN);
         		
         	  Settings.System.putInt(getContentResolver(), SCREENSETTINGS, 7);
         	  Log.i(TAG, "The number of screens to register is " + st);
         	}
         	
         	if (compareStrings(st.compareTo(FIVE)))
         	{      	 
         	  Settings.System.putInt(getContentResolver(), SCREENSETTINGS, 5);
           	  Log.i(TAG, "The number of screens to register is " + st);
         	}
         	if (compareStrings(st.compareTo(THREE)))
           	{
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
         
         public void restartLauncher2(ActivityManager activity)
         {
     		if(DBG)
     			Log.d(TAG, "About to kill the launcher application");
     		
     	 	activity.killBackgroundProcesses(LAUNCHER);	
         }
         
         
}

 


