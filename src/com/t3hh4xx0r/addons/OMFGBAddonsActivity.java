package com.t3hh4xx0r.addons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.DeviceType;
import com.t3hh4xx0r.addons.utils.DownloadFile;
import com.t3hh4xx0r.addons.utils.Downloads;
import com.t3hh4xx0r.addons.web.JSON.JSONUtils;
import com.t3hh4xx0r.addons.web.JSON.JSONUtils.JSONParsingInterface;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.t3hh4xx0r.R;

public class OMFGBAddonsActivity extends PreferenceActivity implements JSONParsingInterface {
	
	private boolean DBG = (false || Constants.FULL_DBG);
	private boolean CREATE_ERROR = false;
	private final String TAG = "OMFGB Addons App Addons Activity";

	AddonsReceiver mReceiver;

	JSONUtils mJSONUtils;
	
	private RelativeLayout mPreferenceContainer;
	private ListView mPreferenceListView;
	private PreferenceScreen mRootPreference;

	private ProgressDialog mProgressDialog;
	

	private static boolean mCreateUI = true;

	private static boolean mCreateBlankUIWithISerror = false;
	private static boolean mCreateBlankUIWithManifesterror = false;
	

	private Runnable mJSONRunnable;


	 // Define the Handler that receives messages from the thread and update the progress 
    final Handler mHandler = new Handler() 
    { 
         public void handleMessage(Message msg) 
         { 

        	 switch(msg.what){
        	 case Constants.DOWNLOAD_COMPLETE:
        		 finishUIConstruction();
        		 mProgressDialog.dismiss();
    			 break;
        	 case Constants.CANNOT_RETREIVE_MANIFEST:
        		 finishEmptyUIConstruction();
        		 mProgressDialog.dismiss();
        		 // create the alert box and warn user
        		AlertBox(getString(R.string.warning),getString(R.string.manifest_null));
        		
        		break;
        	 case Constants.MANIFEST_IS_WRONG: 
        		 finishEmptyUIConstruction();
        		 mProgressDialog.dismiss();
        		 // create the alert box and warn user
        		AlertBox(getString(R.string.warning),getString(R.string.cannot_parse_manifest));
        		break;
        	 
        	 }
              Log.d(TAG, "handleMessage:"+ msg.toString()); 
              
         } 
    }; 
    
    
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        mPreferenceContainer = new RelativeLayout(this);
        mPreferenceContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        
        mPreferenceListView = new ListView(this);
        mPreferenceListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mPreferenceListView.setId(android.R.id.list);
        
        
        startUIConstruction();
        

       mProgressDialog = ProgressDialog.show(OMFGBAddonsActivity.this,    
     		  getString(R.string.please_wait), getString(R.string.retreiving_data), true);

      IntentFilter filter = new IntentFilter();
      filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
      filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

      if(mReceiver == null){
          Log.i(TAG, "Registering reciver");

          mReceiver = new AddonsReceiver();
          registerReceiver(mReceiver, filter);
      }

    }

    private void startUIConstruction() {


        mJSONRunnable = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mJSONUtils = new JSONUtils();
				mJSONUtils.setJSONUtilsParsingInterface(OMFGBAddonsActivity.this); 
				mRootPreference = mJSONUtils.ParseJSON(OMFGBAddonsActivity.this, OMFGBAddonsActivity.this, false);
			
				if(OMFGBAddonsActivity.mCreateUI) {
					Log.i(TAG, "Finished retreiving addons, sending the ui construction message");
					 mHandler.sendEmptyMessage(Constants.DOWNLOAD_COMPLETE);
					}
					if(OMFGBAddonsActivity.mCreateBlankUIWithISerror) {
						Log.i(TAG, "Finished retreiving addons, sending the blank ui construction message");
						 mHandler.sendEmptyMessage(Constants.CANNOT_RETREIVE_MANIFEST);
						}
					if(OMFGBAddonsActivity.mCreateBlankUIWithManifesterror) {
						Log.i(TAG, "Finished retreiving addons, sending the blank ui construction message");
						 mHandler.sendEmptyMessage(Constants.MANIFEST_IS_WRONG);
						}
			}     	
        };
        Thread Download = new Thread(mJSONRunnable);
        Download.start();
    	
	}

	public void finishEmptyUIConstruction(){
    	// Do not bind the listview to the rootPreference
    	mPreferenceContainer.removeAllViews();
        mPreferenceContainer.addView(mPreferenceListView);
        
       setContentView(mPreferenceContainer);
       setPreferenceScreen(mRootPreference);
    }    

    public void finishUIConstruction(){
        mRootPreference.bind(mPreferenceListView);
        if(DBG)Log.i(TAG, "mPreferenceListView: " + mPreferenceListView.getCount());
        
        mPreferenceContainer.removeAllViews();
        mPreferenceContainer.addView(mPreferenceListView);
        
      setContentView(mPreferenceContainer);
      setPreferenceScreen(mRootPreference);
    }
    
    protected void AlertBox(String title, String mymessage){
    
    new AlertDialog.Builder(this)
  	.setMessage(mymessage)
  	.setTitle(title)
  	.setCancelable(false)
  	.setPositiveButton("OK",new DialogInterface.OnClickListener()
  	{public void onClick(DialogInterface dialog, int whichButton){
  		finish();
  	}
  	})
  	.show();
}


	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.addons_menu, menu);
		
	
		return true;
	}	

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.clear_download_cache:
			Downloads.deleteDir();
			break;
		case R.id.refresh:
			startRefresh();
			break;
		case R.id.settings_menu:
			launchSettingMenu();
			break;
		}

	    	return(super.onOptionsItemSelected(item));
	}	

	
        private void startRefresh() {


		       mProgressDialog = ProgressDialog.show(OMFGBAddonsActivity.this,  
		     		  getString(R.string.please_wait), getString(R.string.retreiving_data), true);
		    mJSONRunnable = new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					mJSONUtils = new JSONUtils();
					mJSONUtils.setJSONUtilsParsingInterface(OMFGBAddonsActivity.this); 
					mRootPreference.removeAll();
					mRootPreference = mJSONUtils.ParseJSON(OMFGBAddonsActivity.this, OMFGBAddonsActivity.this, true);
				
					if(OMFGBAddonsActivity.mCreateUI) {
						Log.i(TAG, "Finished retreiving addons, sending the ui construction message");
						 mHandler.sendEmptyMessage(Constants.DOWNLOAD_COMPLETE);
						}
						if(OMFGBAddonsActivity.mCreateBlankUIWithISerror) {
							Log.i(TAG, "Finished retreiving addons, sending the blank ui construction message");
							 mHandler.sendEmptyMessage(Constants.CANNOT_RETREIVE_MANIFEST);
							}
						if(OMFGBAddonsActivity.mCreateBlankUIWithManifesterror) {
							Log.i(TAG, "Finished retreiving addons, sending the blank ui construction message");
							 mHandler.sendEmptyMessage(Constants.MANIFEST_IS_WRONG);
							}
				}     	
	        };
	        Thread Download = new Thread(mJSONRunnable);
	        Download.start();
		
	}

		private void launchSettingMenu() {
        	
        	Intent settings = new Intent(this, SettingsMenu.class);
        	startActivity(settings);
		
	}

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "OnDestroy Called");
        unregisterReceiver(mReceiver);
    }

    public class AddonsReceiver extends BroadcastReceiver{
    	
    	boolean flash = false;

		@Override
		public void onReceive(Context context, Intent intent) {

			 Log.e(TAG, "I am receiver");

			if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
				notificationDownloadCompleteClicked(context);

				
			}
		}
	}
    public void notificationDownloadCompleteClicked(Context context){
    	
    	 Log.e(TAG, "Reciving " + DownloadManager.ACTION_DOWNLOAD_COMPLETE);

		 String ns = Context.NOTIFICATION_SERVICE;
		 NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);



		 int icon = R.drawable.icon;        // icon from resources
		 CharSequence tickerText = "T3hh4xx0r";              // ticker-text
		 long when = System.currentTimeMillis();         // notification time
		 CharSequence contentTitle = "T3hh4xx0r Addons";  // expanded message title
		 CharSequence contentText = "Download completed";      // expanded message text

		 Intent notificationIntent = new Intent(context, OMFGBAddonsActivity.class);


		 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		 // the next two lines initialize the Notification, using the configurations above
		 Notification notification = new Notification(icon, tickerText, when);
		 notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		 final int HELLO_ID = 1;

		 mNotificationManager.notify(HELLO_ID, notification);
    	
    	
    }
    
    // Interface for the download and jsonparser

    /**
     * Create three categories for the addons preferncescreen then adds them.
     * The three categories will populate the list dynamicly from
     * a parsed JSON file.
     * 
     * If a category is empty it will be reomved before displaying
     * 
     * @return PreferenceScreen the screen preference that will be insflated
     */
    
	@Override
	public PreferenceScreen ParseJSONScript(PreferenceScreen PreferenceRoot,
			InputStream is) throws JSONException {

	   	 
	   	 PreferenceCategory googlecat =  new PreferenceCategory(this);
	   	 googlecat.setTitle("Flashable Google Apps");

	   	 PreferenceCategory kernelcat =  new PreferenceCategory(this);
	   	 kernelcat.setTitle("Additional Kernels");

	  	 PreferenceCategory applicationcat =  new PreferenceCategory(this);
	  	 applicationcat.setTitle("Additional applications");

                 PreferenceCategory themecat =  new PreferenceCategory(this);
                 themecat.setTitle("Additional themes");	   	
	   	 
	   	 PreferenceRoot.addPreference(googlecat);
	   	 PreferenceRoot.addPreference(applicationcat);
	   	 PreferenceRoot.addPreference(kernelcat);
	   	 PreferenceRoot.addPreference(themecat);
	      

	       		
	              
	    try{
	              String x = "";
	              
	               Log.i(TAG, "Begining json parsing");
	  
	               byte [] buffer = new byte[is.available()];
	             
	               
	               while (is.read(buffer) != -1);
	               
	               String jsontext = new String(buffer);
	               JSONArray entries = new JSONArray(jsontext);

	               Log.i(TAG, "Json parsing finished");

	               x = "JSON parsed.\nThere are [" + entries.length() + "] entries.\n";

	               int i;
	               Log.i(TAG, "The number of entries is: " + entries.length());
	               if(DBG){
	               Log.d(TAG, "Starting preference resolver for");
	               Log.d(TAG, "device type " + DeviceType.DEVICE_TYPE);
	               }
	               // Parse the JSON entries and add the to the approrite category
	   	            for (i=0;i<entries.length();i++)
	   	            {
	   	
	   	                if(DBG)Log.d(TAG, "Resolving addon " + (i+1));
	   	            	AddonsObject n = new AddonsObject();
	   	                JSONObject post = entries.getJSONObject(i);
	   	               
	   	                	n.setCategory(post.getString("category"));
		   	                n.setDevice(post.getString("device"));
		   	                n.setURL(post.getString("url"));
		   	                n.setName(post.getString("name"));
		   	                try{
		   	                	n.setDensity(post.getString("density"));
		   	                }catch(JSONException e)
		   	                {
		   	                	
		   	                	Log.d(TAG, "Density is not set");
		   	                	n.setDensity("all"); 
		   	                	
		   	                }
		   	                try{
		   	                	n.setZipName(post.getString("zipname"));
		   	                }catch(JSONException e){
		   	                	e.printStackTrace();
		   	                	n.setZipName("Addon Name");
		   	                }
		   	                n.setInstallable(post.getString("installable"));
		   	                try{
		   	                	n.setDescription(post.getString("description"));
		   	                }catch(JSONException e){
		   	                	n.setDescription("Addon Description");
		   	                	
		   	                	
		   	                }
		   	                
		   	                
	   	                if(DBG)Log.d(TAG, "Finished setting addons object");
	   	                
	   	                PreferenceScreen inscreen = getPreferenceManager().createPreferenceScreen(this);
	   	                inscreen.setSummary(n.getDescription());
	   	                inscreen.setTitle(n.getName());
	   	                
	   	                // Set the click listener for each preference
	   	             OnAddonsPreferenceClickListener listner = new OnAddonsPreferenceClickListener(n, i, this);
	   	                
	   	                inscreen.setOnPreferenceClickListener(listner);
	   	                
	   	                // Finally add the preference to the heirachy
	   	                Log.i(TAG,"Adding " + (String) inscreen.getTitle() + "to screen if compatible") ;
	   	                
	   	                /*
	   	                 * If the device type eqauls this device or all devices and the density equals all devices or this devices
	   	                 * density  set the category
	   	                 * 
	   	                 */
	   	                
	   	                if(DeviceType.DEVICE_TYPE.equals(n.getDevice()) || n.getDevice().equals("all")){
	   	                	// Debug
	   	                	if(DBG){
	   	                	Log.i(TAG, "Adding screen now");
	   	                	Log.i(TAG, "Category = " +  n.getCategory());
	   	                	}
	   	                // Add the addons the appropriate category
	   	                	if( n.getDensity().equals("all") || DeviceType.getDensity().equals(n.getDensity())){
	   	                		
	   	                	
			   	                if(n.getCategory().equals("google"))googlecat.addPreference(inscreen);
			   	                if(n.getCategory().equals("applications"))applicationcat.addPreference(inscreen);
			   	                if(n.getCategory().equals("kernel"))kernelcat.addPreference(inscreen);
                                                if(n.getCategory().equals("themes"))themecat.addPreference(inscreen);
			   	                Log.i(TAG, "Preference screen added with addon object category " + n.getCategory());
	   	                	}
	   	                	else{
	   	                		
	   	                		Log.i(TAG, "Device density is not compatible");
	   	                	}
	   	                }else{
	   	                	 
	   	                	Log.i(TAG, "Device not compatible with package");
	   	                	
	   	                }
	   	                
	   	          
	   	
	   	            }
	   	            if(themecat.getPreferenceCount() == 0){
                                 PreferenceRoot.removePreference(themecat);
                                 Log.i(TAG, "Removing themes category");
                                 }

	   	            if(googlecat.getPreferenceCount() == 0){
		           	 PreferenceRoot.removePreference(googlecat);
		           	 Log.i(TAG, "Removing gapps category");
		           	 }
		            if(applicationcat.getPreferenceCount() == 0){
		           	 PreferenceRoot.removePreference(applicationcat);
		
		           	 Log.i(TAG, "Removing applications category");
		            }
		            if(kernelcat.getPreferenceCount() == 0){
		           	 PreferenceRoot.removePreference(kernelcat);
		           	 Log.i(TAG, "Removing kernels category");
		            }
	   	            
	               Log.d(TAG, x);
	               
	       }catch(IOException e){
	          	   e.printStackTrace();
	       }
	         
	       	
	       	
		return PreferenceRoot;
	}



	@Override
	public PreferenceScreen unableToDownloadScript() {
	  
		// and to contact us
 	   Log.d(TAG, "The input stream is null. Does the user have a data connection or has the " +
 	   		"developer left CREATE_ERROR set to true");

 	   mCreateBlankUIWithISerror = true;
 	   mCreateUI = false;
 	   mCreateBlankUIWithManifesterror = false;
 	   // return a blank view to the user
 	   return getPreferenceManager().createPreferenceScreen(this);
	}



	@Override
	public PreferenceScreen unableToParseScript() {
        Log.d(TAG, "Cannot parse the JSON script correctly");

 	   mCreateBlankUIWithISerror = false;
 	   mCreateUI = false;
 	   mCreateBlankUIWithManifesterror = true;
 	   return getPreferenceManager().createPreferenceScreen(this);

	}



	@Override
	public PreferenceScreen ParsingCompletedSuccess() {


  	   mCreateBlankUIWithISerror = false;
  	   mCreateUI = true;
  	   mCreateBlankUIWithManifesterror = false;
		return null;
	}
     // end parser interface

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
	

}
