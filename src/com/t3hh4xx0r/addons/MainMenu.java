package com.t3hh4xx0r.addons;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.DeviceType;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.DataOutputStream;
import java.io.IOException;

import com.t3hh4xx0r.R;

public class MainMenu extends PreferenceActivity  {
	PreferenceCategory mAddonsCat;

    private static String TAG = "MainMenu";
	PreferenceScreen mNightlies;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		determineDevice();
		
		addPreferencesFromResource(R.layout.main_menu);
		
		mAddonsCat = (PreferenceCategory) findPreference("addons_category");
		
		
		
		if( !hasStorage(true)){
			
			mAddonsCat.setEnabled(false);
			AlertBox("Warining","Sdard is not present or mounted. The addons portion requires an sdcard to use. " +
					"Please insert or mount the sdcard to use the addons portion of the app.");
		}
		else{
			mAddonsCat.setEnabled(true);
			
		}
  
		
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Clear Download Cache");
		return true;
	}	

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 0:
		deleteDir();
		break;
		}

	    	return(super.onOptionsItemSelected(item));
	}	

        public void deleteDir() {
               Thread cmdThread = new Thread(){
                        @Override
                        public void run() {

                                Looper.prepare();

                                try{Thread.sleep(1000);}catch(InterruptedException e){ }

                                final Runtime run = Runtime.getRuntime();
                                DataOutputStream out = null;
                                Process p = null;
                                try {
                                        p = run.exec("su");
                                        out = new DataOutputStream(p.getOutputStream());
                                        out.writeBytes("busybox rm -r " + Constants.DOWNLOAD_DIR + "\n");
                                        out.flush();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        return;
                                }

                        }
                };
                cmdThread.start();
        }

	static public boolean hasStorage(boolean requireWriteAccess) {
		
	    String state = Environment.getExternalStorageState();
	    Log.v(TAG, "storage state is " + state);

	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        if (requireWriteAccess) {
	            return true;
	        } else {
	            return false;
	        }
	    } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}



	private void determineDevice(){
		
		if(!DeviceType.determineDeviceDensity(getResources().getDisplayMetrics().densityDpi)){
			Log.d(TAG, "Cannot determine device density, defaultingg to Unset");
			}
		
		// We dont need to set the device script every time just once
		if(Constants.getDeviceScript() == null || !Constants.getDeviceScript().equals("")){
		
				if (DeviceType.deviceDeviceEquals(DeviceType.INCREDIBLE)) {
			    	Constants.setDeviceScript(DeviceType.INCREDIBLE_SCRIPT);
			    	DeviceType.DEVICE_TYPE = DeviceType.INCREDIBLE;
			        	
				}else  if (DeviceType.deviceDeviceEquals(DeviceType.ERIS)) {
			    	Constants.setDeviceScript(DeviceType.ERIS_SCRIPT);
			    	DeviceType.DEVICE_TYPE = DeviceType.ERIS;
			    	
				}else  if (DeviceType.deviceDeviceEquals(DeviceType.DROID)) {
			    	Constants.setDeviceScript(DeviceType.DROID_SCRIPT);
			    	DeviceType.DEVICE_TYPE = DeviceType.DROID;
					
				}else  if (DeviceType.deviceDeviceEquals(DeviceType.EVO)) {
			    	Constants.setDeviceScript(DeviceType.EVO_SCRIPT);
			    	DeviceType.DEVICE_TYPE = DeviceType.EVO;
			        	
				}else  if (DeviceType.deviceDeviceEquals(DeviceType.HERO)) {
			    	Constants.setDeviceScript(DeviceType.HERO_SCRIPT);
			    	DeviceType.DEVICE_TYPE = DeviceType.HERO;
			        	
				}else  if (DeviceType.deviceDeviceEquals(DeviceType.THUNDERBOLT)) {
			    	Constants.setDeviceScript(DeviceType.THUNDERBOLT_SCRIPT);
			    	DeviceType.DEVICE_TYPE = DeviceType.THUNDERBOLT;
			        	
				}else  if (DeviceType.deviceDeviceEquals(DeviceType.INCREDIBLE2)) {
                                Constants.setDeviceScript(DeviceType.INCREDIBLE2_SCRIPT);
                                DeviceType.DEVICE_TYPE = DeviceType.INCREDIBLE2;

				}else  if (DeviceType.deviceDeviceEquals(DeviceType.FASCINATEMTD)) {
				Constants.setDeviceScript(DeviceType.FASCINATEMTD_SCRIPT);
				DeviceType.DEVICE_TYPE = DeviceType.FASCINATEMTD;

                                }else  if (DeviceType.deviceDeviceEquals(DeviceType.SHOWCASEMTD)) {
                                Constants.setDeviceScript(DeviceType.SHOWCASEMTD_SCRIPT);
                                DeviceType.DEVICE_TYPE = DeviceType.SHOWCASEMTD;

                                }else  if (DeviceType.deviceDeviceEquals(DeviceType.MESMERIZEMTD)) {
                                Constants.setDeviceScript(DeviceType.MESMERIZEMTD_SCRIPT);
                                DeviceType.DEVICE_TYPE = DeviceType.MESMERIZEMTD;

                                }
		}
		
	}
	
	   protected void AlertBox(String title, String mymessage){
	    	
		    
		   new AlertDialog.Builder(this)
		     	.setMessage(mymessage)
		     	.setTitle(title)
		     	.setCancelable(false)
		     	.setPositiveButton("OK",new DialogInterface.OnClickListener()
		     	{public void onClick(DialogInterface dialog, int whichButton){
		     		// Do nothing, the warning is enough.
		     	}
		     	})
		     	.show();
		   }
	
}


