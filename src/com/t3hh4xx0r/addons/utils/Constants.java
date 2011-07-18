package com.t3hh4xx0r.addons.utils;

import java.io.File;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;

public final class Constants {
	
        public static final String BACKUP_DIR = "/sdcard/clockworkmod/backup";
	
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
    
    /**
     * The script ised for updating the app
     */
    private static String DEVICE_SCRIPT;




	public static void setDeviceScript(String deviceScript) {
		DEVICE_SCRIPT = deviceScript;
	}




	public static String getDeviceScript() {
		return DEVICE_SCRIPT;
	} 
    
	

}
