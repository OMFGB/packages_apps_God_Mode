package com.t3hh4xx0r.addons.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import com.t3hh4xx0r.addons.web.JSON.JSONUtils;
import com.t3hh4xx0r.addons.web.JSON.JSONUtils.JSONParsingInterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceScreen;
import android.util.Log;

public class Downloads {
	
    public static String DATE = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());
    public static boolean DBG = (false || Constants.FULL_DBG);
    public static String PREF_LOCATION;
    private static String updateFilePath;

    private static String TAG = "Downloads";


    public static void installPackage(String outputzip, final Context context) {
		final String OUTPUT_NAME = outputzip;
	    Log.d(TAG,OUTPUT_NAME);

		Log.i(TAG, "Packaging installer thread starting");
	    
        Thread cmdThread = new Thread(){
            @Override
            public void run() {
            // http://paulononaka.wordpress.com/2011/07/02/how-to-install-a-application-in-background-on-android/
            // http://apachejava.blogspot.com/2011/04/install-and-uninstall-android.html
            Log.i(TAG, "Packaging installer thread started");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Constants.DOWNLOAD_DIR + OUTPUT_NAME)), "application/vnd.android.package-archive");
            context.startActivity(intent);
            }  		
        };
        cmdThread.start();
    }

    public static void prepareFlash(Context context) {

	    cwmWarningAlertBox("We Apologize", 
            "Due to limitations of CWM 5+, in app flashing is no longer available." + 
            "\nPress OK to reboot to recovery and manually continue flashing.", context);
    }

    protected static void cwmWarningAlertBox(String title, String mymessage, Context context) {
       new AlertDialog.Builder(context)
          .setMessage(mymessage)
          .setTitle(title)
          .setCancelable(false)
          .setPositiveButton("OK",
          new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
    	        Thread cmdThread = new Thread(){
                   @Override
                   public void run() {
   		       final Runtime run = Runtime.getRuntime();
		       DataOutputStream out = null;
  		       Process p = null;
		       try {
        	           Log.i(TAG, "Executing su");
                           p = run.exec("su");
		           out = new DataOutputStream(p.getOutputStream());
                           out.writeBytes("reboot recovery\n");
		           out.flush();
	               } catch (IOException e) {
		           e.printStackTrace();
		           return;
	               }
	            }
	        };
	        cmdThread.start();
             }
           })
           .setNegativeButton("Cancel", 
           new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing
              }
           })
       .show();
    }

    public static void deleteDir() {
        Thread cmdThread = new Thread(){
            @Override
            public void run() {
                File deletedlcache = new File(Constants.DOWNLOAD_DIR);
                if(deletedlcache.exists()){
                    File cachedfiles[] = deletedlcache.listFiles();     
	                for(int i = 0; i < cachedfiles.length ; i++){
	                    if(!cachedfiles[i].delete()){
	                        Log.d(TAG, "File cannot be deleted");		
	                    }
	
	                }
	                deletedlcache.delete();
                }
                refreshAddonsAndNightlies();     
            }
        };
        cmdThread.start();
    }


    public static void refreshAddonsAndNightlies(){
	    Thread refreshthread = new Thread(){
		    @Override
            public void run() {
		if (!Constants.isDeviceDetermined()) {
                   updateFilePath = (Constants.DOWNLOAD_DIR + "/fascinatemtd");
		} else {
                   updateFilePath = (Constants.DOWNLOAD_DIR + Constants.getDeviceScript());
		}
		   refreshAddons();
		   refreshNightlies();
		   refreshOMGB();
		   refreshAlerts();
		}
	    };
	    refreshthread.start();
    }

    public static void refreshAddons(){

        JSONUtils addons = new JSONUtils();
        addons.setJSONUtilsParsingInterface(new JSONParsingInterface(){

    		@Override
    		public InputStream DownloadJSONScript(boolean refresh) {
			    InputStream is = null;
		        Log.d(TAG, "Begining json download");
		        if(Downloads.checkDownloadDirectory()){
			        File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.ADDONS);
			        try{
			            Log.i(TAG, "The update path and file is called: " + updateFile.toString());
			            // Needed because the manager does not handle https connections
			            if(Constants.shouldForceAddonsSync() || 
			           				Constants.FIRST_LAUNCH || refresh)DownloadFile.updateAppManifest(Constants.ADDONS);
			           		
			                is = new FileInputStream(updateFile);
			           	}
			           	catch(FileNotFoundException e) {
			           	    e.printStackTrace();
			           		if(true)Log.d(TAG, "Could not update app from file resource," +
			           					" the file was not found. Reverting to nothing");
			           		is = null;
			                   	
			            }
			           	
					
				    }
		            return is;
		    }

		    @Override
		    public PreferenceScreen ParseJSONScript(
				PreferenceScreen PreferenceRoot, InputStream is)
				throws JSONException {
			        // TODO Auto-generated method stub
			        return null;
		    }

		    @Override
		    public PreferenceScreen ParsingCompletedSuccess() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToDownloadScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToParseScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }
    	
    	
        });
        addons.downloadJSONFile(true);
	
    }

    public static void refreshNightlies(){

    JSONUtils nightlies = new JSONUtils();
    nightlies.setJSONUtilsParsingInterface(new JSONParsingInterface(){

		@Override
		public InputStream DownloadJSONScript(boolean refresh) {
	        InputStream is = null;
	        Log.d(TAG, "Begining json download");
		    
            if(Downloads.checkDownloadDirectory()){
                   File updateFile = new File(updateFilePath);
		        try{
		            Log.i(TAG, "The update path and file is called: " + updateFile.toString());
		            // Needed because the manager does not handle https connections
		            if(Constants.shouldForceNightliesSync() || 
		           				Constants.FIRST_LAUNCH || refresh)DownloadFile.updateAppManifest(Constants.getDeviceScript());
		           		
		           	    is = new FileInputStream(updateFile);
		           	
		           	}
		           	catch(FileNotFoundException e){	
		           	    e.printStackTrace();
		           		if(true)Log.d(TAG, "Could not update app from file resource," +
		           					" the file was not found. Reverting to nothing");
		           		is = null;
		                   	
		           	}
		           	
				
			    }
	            return is;
			
		    }

		    @Override
		    public PreferenceScreen ParseJSONScript(
				PreferenceScreen PreferenceRoot, InputStream is)
				throws JSONException {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen ParsingCompletedSuccess() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToDownloadScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToParseScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }
        });
        nightlies.downloadJSONFile(true);	
    }

    public static void refreshOMGB(){
        JSONUtils omgb = new JSONUtils();
        omgb.setJSONUtilsParsingInterface(new JSONParsingInterface(){

		    @Override
		    public InputStream DownloadJSONScript(boolean refresh) {

	            InputStream is = null;
	            Log.d(TAG, "Begining json download");

		    if(Downloads.checkOMGBDownloadDirectory()){
                        File updateFile = new File(updateFilePath);
		           	try{
		           		Log.i(TAG, "The update path and file is called: " + updateFile.toString());
		           		// Needed because the manager does not handle https connections
		           		if(Constants.shouldForceNightliesSync() || 
		           				Constants.FIRST_LAUNCH || refresh)DownloadFile.updateAppManifest("omgb/" + Constants.getDeviceScript());

		           		    is = new FileInputStream(updateFile);

		           	    }
		           	    catch(FileNotFoundException e){
                            e.printStackTrace();
		           			if(true)Log.d(TAG, "Could not update app from file resource," +
		           					" the file was not found. Reverting to nothing");
		           			    is = null;

		           	    }


			    }
	            return is;

		    }

		    @Override
		    public PreferenceScreen ParseJSONScript(
				PreferenceScreen PreferenceRoot, InputStream is)
				throws JSONException {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen ParsingCompletedSuccess() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToDownloadScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToParseScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }	
        });
        omgb.downloadJSONFile(true);
    }

    public static void refreshAlerts(){

        JSONUtils alerts = new JSONUtils();
        alerts.setJSONUtilsParsingInterface(new JSONParsingInterface(){

    		@Override
    		public InputStream DownloadJSONScript(boolean refresh) {
			    InputStream is = null;
		        Log.d(TAG, "Begining json download");
		        if(Downloads.checkDownloadDirectory()){
			        File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.ALERTS);
			        try{
			            Log.i(TAG, "The update path and file is called: " + updateFile.toString());
			            // Needed because the manager does not handle https connections
			            if(Constants.AUTOMATICALLY_UPDATE || 
			           				Constants.FIRST_LAUNCH || refresh)DownloadFile.updateAppManifest(Constants.ALERTS);

			                is = new FileInputStream(updateFile);
			           	}
			           	catch(FileNotFoundException e) {
			           	    e.printStackTrace();
			           		if(true)Log.d(TAG, "Could not update app from file resource," +
			           					" the file was not found. Reverting to nothing");
			           		is = null;

			            }


				    }
		            return is;
		    }

		    @Override
		    public PreferenceScreen ParseJSONScript(
				PreferenceScreen PreferenceRoot, InputStream is)
				throws JSONException {
			        // TODO Auto-generated method stub
			        return null;
		    }

		    @Override
		    public PreferenceScreen ParsingCompletedSuccess() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToDownloadScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }

		    @Override
		    public PreferenceScreen unableToParseScript() {
			    // TODO Auto-generated method stub
			    return null;
		    }
    	
    	
        });
        alerts.downloadJSONFile(true);

    }

	/**
	* Checks to see if the download directory
	* for t3hh4xx0r is created. If is
	* not created, it will create it.
	*
	*
	* @return true if the directory exists or if the directory was created if needed, false otherwise.
	*/
	public static Boolean checkDownloadDirectory(){

		File f = new File(Constants.DOWNLOAD_DIR );
		boolean success;
		if(!f.exists()){
			Log.i(TAG, "File diretory does not exist, creating it");
			success = f.mkdirs();
			if(!success)Log.d(TAG, "Directory creation failed");
		} else {
			f = null;
			success = true;
			Log.i(TAG, "File directory exist.");
		}
	    return success;
	}

    public static Boolean checkOMGBDownloadDirectory(){
        File  f = new File(Constants.OMGB_DOWNLOAD_DIR );
        boolean success;
        if(!f.exists()){
            Log.i(TAG, "File diretory does not exist, creating it");
            success = f.mkdirs();
            if(!success)Log.d(TAG, "Directory creation failed");
        } else {
            f = null;
            success = true;
            Log.i(TAG, "File directory exist.");
        }
        return success;
    }
}
