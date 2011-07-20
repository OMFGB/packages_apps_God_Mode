package com.t3hh4xx0r.addons.utils;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceType {
	
	private static String TAG = "DeviceType";
	private static final boolean DBG = ( false || Constants.FULL_DBG);


public static String DEVICE_TYPE;
// Defualt to all densities
private static String DEVICE_DENSITY = "Unset";


// Device name constatnts

/*
 * HTC phones below this point
 */
public static final String INCREDIBLE = "inc";
public static final String INCREDIBLE_SCRIPT = "inc.js";

public static final String ERIS = "desirec";
public static final String ERIS_SCRIPT = "desirec.js";

public static final String EVO = "supersonic";
public static final String EVO_SCRIPT = "supersonic.js";

public static final String HERO = "heroc";
public static final String HERO_SCRIPT = "heroc.js";

public static final String THUNDERBOLT = "mecha" ;
public static final String THUNDERBOLT_SCRIPT = "mecha.js";

public static final String INCREDIBLE2 = "vivow";
public static final String INCREDIBLE2_SCRIPT = "vivow.js";

/*
 * Motorola devices below this point
 * 
 */

public static final String DROID = "Droid";
public static final String DROID_SCRIPT = "sholes.js";


/* 
 * Samsung devices go below this point
 * Some of samsungs devices use the same 
 * build model, so instead of determining
 * the device based upon the model we
 * determine the device by parsing the 
 * retreiving the device name from
 * ro.product.device which is done
 * with Build.DEVICE
 * 
 */
public static final String SAMMY_MODEL = "SCH-I500";

public static final String FASCINATEMTD = "fascinatemtd";
public static final String FASCINATEMTD_SCRIPT = "fascinatemtd.js";



@SuppressWarnings("unused")
public static boolean deviceModelEquals(String s){
		
	
	
		if(Build.MODEL.equals(s))
			return true;
		else 
			return false;
	
	

}

@SuppressWarnings("unused")
public static boolean deviceDeviceEquals(String s){
	
	if(Build.DEVICE.equals(s))
		return true;
	else 
		return false;

}

/**
 * 
 * 
 * @param density the density as a specified int from DisplayMetrics
 * @return true if the density was set, else return false
 */
@SuppressWarnings("unused")
public static boolean determineDeviceDensity(int density){
		
	switch(density)
	{
	
	
		case DisplayMetrics.DENSITY_XHIGH:
			if(DBG)Log.i(TAG, "Device density is xhdpi");
			setDensity("xhdpi");
			return true;
		case DisplayMetrics.DENSITY_HIGH:
			if(DBG)Log.i(TAG, "Device density is hdpi");
			setDensity("hdpi");
			return true;
		case DisplayMetrics.DENSITY_MEDIUM:
			if(DBG)Log.i(TAG, "Device density is mdpi");
			setDensity("mdpi");
			return true;
		case DisplayMetrics.DENSITY_LOW:
			if(DBG)Log.i(TAG, "Device density is ldpi");
			setDensity("ldpi");
			return true;
	}
	
	
		return false;
	
		
	}

private static void setDensity(String density) {
	DEVICE_DENSITY = density;
}
public static String getDensity() {
	return DEVICE_DENSITY;
}

}
