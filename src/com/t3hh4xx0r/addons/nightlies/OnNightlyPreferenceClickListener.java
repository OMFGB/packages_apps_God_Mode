package com.t3hh4xx0r.addons.nightlies;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.t3hh4xx0r.addons.utils.Downloads;
import com.t3hh4xx0r.addons.utils.Constants;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.net.Uri;
import android.os.Looper;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.util.Slog;


public class OnNightlyPreferenceClickListener implements OnPreferenceClickListener {
	
	private final String TAG = "OnNightlyPreferenceClick";
	NightlyObject mNightly;
	int mPosition;
	Context mContext;
	private String externalStorageDir = "/mnt/sdcard/t3hh4xx0r/downloads";
	private String DOWNLOAD_DIR = externalStorageDir+ "/";
	public static String DATE = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());

	public static boolean mBackupRom = false;

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
 	 		FlashAlertBox("Choose flashing options.", Boolean.parseBoolean(mNightly.getInstallable()), mNightly.getZipName());

 		}
 		
		// mNightly.get
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void FlashAlertBox(String title, final boolean Installable, final String OUTPUT_NAME) {
	final CharSequence[] items = {"Backup before install"};
        final boolean checked[] = new boolean[]{false};

	   new AlertDialog.Builder(mContext)
	      //.setMessage(mymessage)
	      .setTitle(title)
	      .setCancelable(true)
              .setMultiChoiceItems(items, checked, new OnMultiChoiceClickListener() {
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
     		CharSequence text = "Item number " + which;
		}
		})
	      .setPositiveButton("OK",
	         new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton){
	        	 Thread FlashThread = new Thread(){
	            		@Override
	            	    public void	run(){
	            			File f = new File (DOWNLOAD_DIR + OUTPUT_NAME);
	            			if(f.exists()){
		  				  		Log.d(TAG, "User approved flashing, begining flash. Installable = " + String.valueOf(Installable));
		  				  		Log.i(TAG, "File location is: "+ f.toString());
		  						if (Installable) 
		  						{
		  						   Downloads.installPackage(OUTPUT_NAME );
		  						} else 
		  						{
								   for( int i = 0; i < items.length; i++ ){
									//This just checks if its an option. Needs an if (setchecked) or something, idk
								       if (items[i] == "Backup before install") {
									  Slog.d(TAG, "Backing up first");
									  mBackupRom = true;
								       } 
		  							Downloads.flashPackage(OUTPUT_NAME);
		  						    }
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
