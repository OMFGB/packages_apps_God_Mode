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

public static void flashPackage(String outputzip, final boolean backuprom, final  boolean wipedata, final boolean wipecache, final boolean installgoogle) {

	final boolean mBackupRom = backuprom;
	final boolean mWipeCache = wipecache;
	final boolean mWipeData = wipedata;
	final boolean mInstallGoogle = installgoogle;

	final String OUTPUT_NAME = outputzip;
	Log.d(TAG,OUTPUT_NAME);
	
	Thread cmdThread = new Thread(){
		@Override
		public void run() {
			
			Log.i(TAG, "Packaging flashing thread started");

                    File updateDirectory = new File ("/cache/recovery/");
                    if (!updateDirectory.isDirectory()) {
                        Log.i(TAG,"Creating cache dir");    
                    	updateDirectory.mkdir();
                            
                    }
                    

			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{ 
				
			}

			final Runtime run = Runtime.getRuntime();
			DataOutputStream out = null;
			Process p = null;

			try {

            	Log.i(TAG,"About to flash package");

        		Log.i(TAG, "Executing su");
                                p = run.exec("su");
				out = new DataOutputStream(p.getOutputStream());
				if (mBackupRom) {
				    out.writeBytes("busybox echo 'backup_rom(\"" + Constants.BACKUP_DIR +"omfgb_" + DATE +"\");'  >> " + Constants.CWR_EXTENDED_CMD + "\n");				
				}
				if (mWipeCache) {
				    out.writeBytes("busybox echo 'format(\"/cache\");' >> " + Constants.CWR_EXTENDED_CMD + "\n");
				}
				if (mWipeData) {
                                    out.writeBytes("busybox echo 'format(\"/cache\");' >> " + Constants.CWR_EXTENDED_CMD + "\n");
                                    out.writeBytes("busybox echo 'format(\"/data\");' >> " + Constants.CWR_EXTENDED_CMD + "\n");
				}
				// Install the rom
				out.writeBytes("busybox echo 'install_zip(\"" + Constants.CWR_FLASH_DIR + OUTPUT_NAME +"\");' >> " + Constants.CWR_EXTENDED_CMD + "\n");
				// Install google apps
				if (mInstallGoogle) {
				out.writeBytes("busybox echo 'install_zip(\"" + Constants.CWR_FLASH_DIR + "GAPPS.zip" +"\");' >> " + Constants.CWR_EXTENDED_CMD + "\n");
				}
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
			  
			  refreshAddons();
			  refreshNightlies();
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
		        	
		        	File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.getDeviceScript());
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
					
				
				}
				else {
				f = null;
				success = true;
				
				Log.i(TAG, "File directory exist.");
				
			}
	return success;
	
	}

}
