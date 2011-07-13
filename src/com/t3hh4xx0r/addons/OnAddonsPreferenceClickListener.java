package com.t3hh4xx0r.addons;

import java.io.File;

import com.t3hh4xx0r.addons.utils.Downloads;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;



public class OnAddonsPreferenceClickListener implements OnPreferenceClickListener {
	
	private final String TAG = "OnNightlyPreferenceClick";
	AddonsObject mAddons;
	int mPosition;
	Context mContext;
	private String externalStorageDir = "/mnt/sdcard/t3hh4xx0r/downloads";
	private String DOWNLOAD_DIR = externalStorageDir+ "/";
	
	
	public OnAddonsPreferenceClickListener(AddonsObject o, int position, Context context){
		
		mAddons = o;
		mPosition = position;
		mContext = context;
		
		
		
	}
	

	@Override
	public boolean onPreferenceClick(Preference v) {
		

 		Log.d(TAG, v.getSummary().toString()  );
 		Log.d(TAG, v.getTitle().toString()  );

 		File check =  new File(externalStorageDir+ "/" + mAddons.getZipName());
 		
 		
 		if(!check.exists()){
 		
 			DownloadManager dman = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
 		
 		File f = new File(externalStorageDir);
 		if(!f.exists()){
 			
 			f.mkdirs();
 			Log.i(TAG, "File diretory does not exist, creating it");
 		}
 		f = null;
 		f = new File(externalStorageDir+ "/" + mAddons.getZipName());
 		
 		Uri down = Uri.parse(mAddons.getURL());
 		
 		
 		DownloadManager.Request req = new DownloadManager.Request(down);
 		req.setShowRunningNotification(true);
 		req.setVisibleInDownloadsUi(false);
 		req.setDestinationUri(Uri.fromFile(f));
 		
 		dman.enqueue(req);
 		}
 		else{
 			
 			check = null;
 	 		FlashAlertBox("Warning:", "About to flash package", Boolean.parseBoolean(mAddons.getInstallable()), mAddons.getZipName());

 		}
 		
		// mNightly.get
		// TODO Auto-generated method stub
		return false;
	}
	
	
	protected void FlashAlertBox(String title, String mymessage, final boolean Installable, final String OUTPUT_NAME)
	   {
	   new AlertDialog.Builder(mContext)
	      .setMessage(mymessage)
	      .setTitle(title)
	      .setCancelable(false)
	      .setPositiveButton("OK",
	         new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton){
	        	 
	        	 Thread FlashThread = new Thread(){
	            		
	            		@Override
	            	    public void	run(){
	            			
	            			File f = new File (DOWNLOAD_DIR + OUTPUT_NAME);
	            			
	            			if(f.exists() ){
		  				  		Log.d(TAG, "User approved flashing, begining flash. Installable = " + String.valueOf(Installable));
		  				  		Log.i(TAG, "File location is: "+ f.toString());
		  						if (Installable) 
		  						{
		  						   Downloads.installPackage(OUTPUT_NAME );
		  						} else 
		  						{
		  							Downloads.flashPackage(OUTPUT_NAME);
		  							
		  						}
	  				  
	            			} 
	            			
	            	  }
	            };
	            
	            FlashThread.run();
	         }
	         })
	         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton){
	        	 // Do nothing
	        	 Log.d(TAG, "User did not approve flashing.");
	         }
	         })
	         
	         
	      .show();
	   		
		
	   }


	
   
}  