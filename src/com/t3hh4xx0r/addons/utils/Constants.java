package com.t3hh4xx0r.addons.utils;

import java.io.File;

import android.os.Environment;
import android.os.Build;

public final class Constants {
	
    public static final String GOOGLE_APPS = "GAPPS.zip";
    private static boolean DEVICEDETERMINED = false;
	
    public static final String BACKUP_DIR = "/sdcard/clockworkmod/backup/";

    public static String LOCAL_MD5;
    public static String REMOTE_MD5;	

    public static String OUTPUT_MD5;

    public static String LOCAL_FILE;

    public static int REFRESH_TIME = 3600;
	/**
	 *  Variable to turn debuging on across the app
	 */
	public static boolean FULL_DBG = true;
	/**
	 *  External storage mount point
	 *  @hide
	 * 
	 */
	private static File extStorageDirectory = Environment.getExternalStorageDirectory();
	
        public static String UPDATE_INTENT = "com.t3hh4xx0r.intent.ALERT_INTENT";

    /**
     * OMGB downloads directory
     */
    public static final String OMGB_DOWNLOAD_DIR = extStorageDirectory + "/t3hh4xx0r/downloads/omgb/";

	/**
	 * Basic OMFGB downloads directory
	 */
    public static final String DOWNLOAD_DIR = extStorageDirectory + "/t3hh4xx0r/downloads/";
    
    /**
     * ClockWorkMod Recovery flash directory 
     */
    public static final String CWR_FLASH_DIR = "/sdcard/t3hh4xx0r/downloads/";
    
    /**
     * System app location
     */
    public static final String SYSTEM_APP = "/system/app/";
    
    /**
     * ClockWorkMod Recovery extended command directory
     */
    public static final String CWR_EXTENDED_CMD = "/cache/recovery/extendedcommand";
    
    /**
     *  Base URL for the manifest files for the omgb releases.
     */
    public static final String OMGB_SCRIPT_URL = "https://raw.github.com/OMFGB/OMFGBManifests/master/omgb/";

    /**
     *  Base URL for the manifest files for the addons, nightlies, and eventually the release roms.
     */
    public static final String BASE_SCRIPT_URL = "https://raw.github.com/OMFGB/OMFGBManifests/master/";
 
    /*
     *  The associated download has been completed
     * 
     */
	public static final int DOWNLOAD_COMPLETE = 1;
	

	/**
	 * The manifest cannot be parsed
	 */
	public static final  int MANIFEST_IS_WRONG = 2;
	
	
	/**
	 * When the manifest was pulled from the web 
	 * there was a fatal error which resulted in
	 * in a null input stream with the associate
	 * manifest.
	 */
	public static final int CANNOT_RETREIVE_MANIFEST = 3;
    
	
	/**
	 * 
	 * 
	 */
	public static String ADDONS = "addons.js"; 

        public static String ALERTS = "alerts.js"; 
    /**
     * The script used for updating the app
     */
    private static String DEVICE_SCRIPT;
    
    public static boolean FIRST_LAUNCH = true;
    
    public static boolean FORCE_ADDONS_ACTIVITY_SYNC = false; 
    
    public static boolean FORCE_NIGHTLIES_ACTIVITY_SYNC = false; 

    public static boolean AUTOMATICALLY_UPDATE = false; 

    public static boolean AUTOMATICALLY_SYNC = true; 

    public static boolean shouldAutoUpdate(){

        if(AUTOMATICALLY_UPDATE){
                return true;
        }
                return false;
    }

    
    public static boolean shouldAutoSync(){
    	
    	if(AUTOMATICALLY_SYNC){
    		return true;
    	}
		return false;
    }

    public static boolean shouldForceAddonsSync(){
    	if(AUTOMATICALLY_SYNC && FORCE_ADDONS_ACTIVITY_SYNC){
    		return true;
    	}
		return false;	
    }

    public static boolean shouldForceNightliesSync(){
  	    if(AUTOMATICALLY_SYNC && FORCE_NIGHTLIES_ACTIVITY_SYNC){
  		    return true;
  	    }
		return false;
    }
    
    public static void setDeviceScript(String deviceScript) {
	DEVICE_SCRIPT = deviceScript;
	setDeviceDetermined(true);
    }

    public static String getDeviceScript() {
	return DEVICE_SCRIPT;
    }

    private static void setDeviceDetermined(boolean dEVICEDETERMINED) {
	DEVICEDETERMINED = dEVICEDETERMINED;
    }

    public static boolean isDeviceDetermined() {
	return DEVICEDETERMINED;
    }

}
