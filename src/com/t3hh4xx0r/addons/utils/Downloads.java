package com.t3hh4xx0r.addons.utils;

import com.t3hh4xx0r.addons.nightlies.OnNightlyPreferenceClickListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

public class Downloads {
	
public static String DATE = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());
public static boolean DBG = (false || Constants.FULL_DBG);
public static String PREF_LOCATION;

private static String TAG = "Downloads";
private static String DOWNLOAD_URL;

private int DOWNLOAD_PROGRESS = 0;

private static final int FLASH_ADDON = 0;
private static final int FLASH_COMPLETE = 1;
private static final int INSTALL_ADDON = 2;

private ProgressDialog pbarDialog;

private boolean mAddonIsFlashable;

private boolean isSdCardPresent(){
	return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
}

private boolean isSdCardWriteable(){
	return !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
}

public static void installPackage(String outputzip) {


		final String OUTPUT_NAME = outputzip;
	    Log.d(TAG,OUTPUT_NAME);

		Log.i(TAG, "Packaging installer thread starting");
	    
            Thread cmdThread = new Thread(){
                    @Override
                    public void run() {
                    	// http://paulononaka.wordpress.com/2011/07/02/how-to-install-a-application-in-background-on-android/
                    	// http://apachejava.blogspot.com/2011/04/install-and-uninstall-android.html
                		Log.i(TAG, "Packaging installer thread started");

                            try{Thread.sleep(1000);}catch(InterruptedException e){ }

                            final Runtime run = Runtime.getRuntime();
                            DataOutputStream out = null;
                            Process p = null;

                            try {

                        		Log.i(TAG, "Executing su");
                                    p = run.exec("su");
                                    out = new DataOutputStream(p.getOutputStream());
                                    out.writeBytes("busybox mount -o rw,remount /system\n");
                                    out.writeBytes("busybox cp " + Constants.CWR_FLASH_DIR + OUTPUT_NAME + Constants.SYSTEM_APP + "\n");
                                    out.writeBytes("busybox mount -o ro,remount /system\n");
                                    out.flush();
                            } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                            }

                    }
            };
            cmdThread.start();
    }

public static void flashPackage(String outputzip, boolean backuprom, boolean wipecache, boolean wipedata) {

	final boolean mBackupRom = backuprom;
	final boolean mWipeCache = wipecache;
	final boolean mWipeData = wipedata;
	
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
				    out.writeBytes("busybox echo 'backup_rom(\"" + Constants.BACKUP_DIR + "/omfgb_" + DATE +"\");'  >> " + Constants.CWR_EXTENDED_CMD + "\n");				
				}
				if (mWipeCache) {
				    out.writeBytes("busybox echo 'rm -r /data/cache' " );
				    out.writeBytes("busybox echo 'rm -r /cache' " );	
				}
				if (mWipeCache) {
				    out.writeBytes("busybox echo 'rm -r /data' " );
				    out.writeBytes("busybox echo 'rm -r /cache' " );	
				}
				out.writeBytes("busybox echo 'install_zip(\"" + Constants.CWR_FLASH_DIR + OUTPUT_NAME +"\");' >> " + Constants.CWR_EXTENDED_CMD + "\n");
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

}
