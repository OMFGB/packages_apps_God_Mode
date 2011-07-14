package com.t3hh4xx0r.addons;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.DeviceType;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.t3hh4xx0r.R;

public class MainMenu extends PreferenceActivity  {

    private static String TAG = "MainMenu";
	PreferenceScreen mNightlies;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		determineDevice();
		
		
		
		addPreferencesFromResource(R.layout.main_menu);
  
		
		
	}
	
	

	private void determineDevice(){
		
		// We dont need to set the device script every time just once
		if(Constants.getDeviceScript() == null || !Constants.getDeviceScript().equals("")){
		
				if (DeviceType.deviceEquals(DeviceType.INCREDIBLE)) {
			    	Constants.setDeviceScript(DeviceType.INCREDIBLE_SCRIPT);
			    	DeviceType.DEVICE_TYPE = "inc";
			        	
				}else  if (DeviceType.deviceEquals(DeviceType.ERIS)) {

			    	Constants.setDeviceScript(DeviceType.ERIS_SCRIPT);

			    	DeviceType.DEVICE_TYPE = "desirec";
			    	
				}else  if (DeviceType.deviceEquals(DeviceType.DROID)) {

			    	Constants.setDeviceScript(DeviceType.DROID_SCRIPT);

			    	DeviceType.DEVICE_TYPE = "sholes";
					
				}else  if (DeviceType.deviceEquals(DeviceType.EVO)) {

			    	Constants.setDeviceScript(DeviceType.EVO_SCRIPT);

			    	DeviceType.DEVICE_TYPE = "supersonic";
			        	
				}else  if (DeviceType.deviceEquals(DeviceType.HERO)) {

			    	Constants.setDeviceScript(DeviceType.HERO_SCRIPT);

			    	DeviceType.DEVICE_TYPE = "heroc";
			        	
				}else  if (DeviceType.deviceEquals(DeviceType.THUNDERBOLT)) {

			    	Constants.setDeviceScript(DeviceType.THUNDERBOLT_SCRIPT);

			    	DeviceType.DEVICE_TYPE = "mecha";
			        	
				}else  if (DeviceType.deviceEquals(DeviceType.INCREDIBLE2)) {

                                Constants.setDeviceScript(DeviceType.INCREDIBLE2_SCRIPT);

                                DeviceType.DEVICE_TYPE = "vivow";

				}else  if (DeviceType.deviceEquals(DeviceType.FASCINATEMTD)) {

                                Constants.setDeviceScript(DeviceType.FASCINATEMTD_SCRIPT);

                                DeviceType.DEVICE_TYPE = "fascinatemtd";
				
				}
		}
		
	}
	
}


