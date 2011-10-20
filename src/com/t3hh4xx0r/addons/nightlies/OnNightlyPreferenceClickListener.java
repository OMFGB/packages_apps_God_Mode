package com.t3hh4xx0r.addons.nightlies;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.net.Uri;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.Downloads;

import com.t3hh4xx0r.R;

public class OnNightlyPreferenceClickListener implements OnPreferenceClickListener {
	
	private final String TAG = "OnNightlyPreferenceClickListener";

	private boolean DBG = (false || Constants.FULL_DBG);
	NightlyObject mNightly;
	int mPosition;
	Context mContext;
	private String externalStorageDir = "/mnt/sdcard/t3hh4xx0r/downloads";
	private String DOWNLOAD_DIR = externalStorageDir+ "/";
	public static String DATE = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());


	public OnNightlyPreferenceClickListener(NightlyObject o, int position, Context context){
		mNightly = o;
		mPosition = position;
		mContext = context;	
	}
	

	@Override
	public boolean onPreferenceClick(Preference v) {	
 		Log.d(TAG, v.getSummary().toString()  );
 		Log.d(TAG, v.getTitle().toString()  );

 		File check =  new File(externalStorageDir+ "/" + mNightly.getZipName());

 		if(!check.exists()){
 			DownloadManager dman = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
 		    File f = new File(externalStorageDir);
 		    if(!f.exists()){
 			    f.mkdirs();
 			    Log.i(TAG, "File diretory does not exist, creating it");
 		    }
 		    f = null;
 		    f = new File(externalStorageDir+ "/" + mNightly.getZipName());
 		
 		    Uri down = Uri.parse(mNightly.getURL());
 		    DownloadManager.Request req = new DownloadManager.Request(down);
 		    req.setShowRunningNotification(true);
 		    req.setVisibleInDownloadsUi(false);
 		    req.setDestinationUri(Uri.fromFile(f));
 		
 		    dman.enqueue(req);
        }
 		else{ 			
 		    check = null;
 	 	    checkInstallable(Boolean.parseBoolean(mNightly.getInstallable()), mNightly.getZipName());
 		}
		return false;
	}
	
	protected void checkInstallable(final boolean Installable, final String OUTPUT_NAME) {
	             Thread FlashThread = new Thread(){
	                 @Override
	            	 public void run() {
		  		if (Installable) {
		  		   Downloads.installPackage(OUTPUT_NAME, mContext );
		 		} else {
		     		   Downloads.prepareFlash(mContext);
		  		}
              	        }
	            };
	            FlashThread.run();
	 }
	
	 private void log(String message){	   
		   if(DBG)Log.d(TAG, message);
	 }
}  
