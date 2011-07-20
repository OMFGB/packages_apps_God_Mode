package com.t3hh4xx0r.addons.nightlies;

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
import com.t3hh4xx0r.addons.web.JSON.JSONUtils;
import com.t3hh4xx0r.addons.web.JSON.JSONUtils.JSONParsingInterface;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.t3hh4xx0r.R;

public class OMFGBExternalAddonsAppNightlyActivity extends PreferenceActivity implements JSONParsingInterface {
	
	RelativeLayout mPreferenceContainer;
	private ListView mPreferenceListView;
	
	JSONUtils mJSONUtils;

	private boolean CREATE_ERROR = false;
	private ProgressDialog mProgressDialog;
	
	private static boolean mCreateUI = true;

	private static boolean mCreateBlankUIWithISerror = false;
	private static boolean mCreateBlankUIWithManifesterror = false;


	//private NightlyAdapter mAdapter;
	
	PreferenceScreen mRootPreference;
	private final String TAG = "OMFGB External Addons App Nightly Activity";
	NightlyReceiver mReceiver;
	private Runnable mJSONRunnable;
	final int ALERT_USER = 2;
	

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
  
        
        

        mPreferenceContainer = new RelativeLayout(this);
        mPreferenceContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        
        mPreferenceListView = new ListView(this);
        mPreferenceListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mPreferenceListView.setId(android.R.id.list);
        
        
        mJSONRunnable = new Runnable(){

			@Override
			public void run() {
			
				mJSONUtils = new JSONUtils();
				mJSONUtils.setJSONUtilsParsingInterface(OMFGBExternalAddonsAppNightlyActivity.this); 
				mRootPreference = mJSONUtils.ParseJSON(OMFGBExternalAddonsAppNightlyActivity.this, OMFGBExternalAddonsAppNightlyActivity.this,false);
			
				
				
				if(OMFGBExternalAddonsAppNightlyActivity.mCreateUI) {
				Log.i(TAG, "Finished retreiving nightlies, sending the ui construction message");
				 mHandler.sendEmptyMessage(Constants.DOWNLOAD_COMPLETE);
				}
				if(OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithISerror) {
					Log.i(TAG, "Finished retreiving nightlies, sending the blank ui construction message");
					 mHandler.sendEmptyMessage(Constants.CANNOT_RETREIVE_MANIFEST);
					}
				if(OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithManifesterror) {
					Log.i(TAG, "Finished retreiving nightlies, sending the blank ui construction message");
					 mHandler.sendEmptyMessage(Constants.MANIFEST_IS_WRONG);
					}
				
				
			}     	
        };
        
        
        

        Thread Download = new Thread(mJSONRunnable);
        Download.start();

      
     
      IntentFilter filter = new IntentFilter();
      filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
      filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
      
      if(mReceiver == null){
    	  Log.i(TAG, "Registering reciver");
    	  
    	  mReceiver = new NightlyReceiver();
    	  registerReceiver(mReceiver, filter);
      }
        
      

      mProgressDialog = ProgressDialog.show(OMFGBExternalAddonsAppNightlyActivity.this,    
              "Please wait...", "Retrieving data ...", true);
      
     
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_nightlies, menu);
        return true;
    }
    
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
        		AlertBox("Warning","The addons " +
        				"manifest cannot be retrieved because the inputstream is null.\n" +
        				"Do you have a data connection?");
        		
        		break;
        	 case Constants.MANIFEST_IS_WRONG: 
        		 finishEmptyUIConstruction();
        		 mProgressDialog.dismiss();
        		 // create thel alert box and warn user
        		AlertBox("Warning","The addons " +
        				"manifest cannot be parsed. Please contact the rom developers " +
        				"@r2doesinc, @xoomdev or @linuxmotion.");
        		break;
        		 
        	 
        	 }
              Log.d(TAG, "handleMessage:"+ msg.toString()); 
              
         } 
    }; 
    

    public void finishEmptyUIConstruction(){
    	
    	 
    	
    	// Do not bind the listview to the rootPreference
        mPreferenceContainer.addView(mPreferenceListView);
        
        
      setContentView(mPreferenceContainer);
      setPreferenceScreen(mRootPreference);
        
  	
    	
    }    
    
    
    public void finishUIConstruction(){
    	
    	 
    	

        mRootPreference.bind(mPreferenceListView);
        Log.i(TAG, "mPreferenceListView: " + mPreferenceListView.getCount());
        
        mPreferenceContainer.addView(mPreferenceListView);
        
        
      setContentView(mPreferenceContainer);
      setPreferenceScreen(mRootPreference);
        
        
    	
    	
    	
    	
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();

        Log.e(TAG, "OnDestroy Called");
    	unregisterReceiver(mReceiver);
    	
    }
    
   
    public class NightlyReceiver extends BroadcastReceiver{
    	
    	boolean flash = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			
			 Log.e(TAG, "I am receiver");
			
			if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){


				 Log.e(TAG, "Reciving " + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
				 
				 String ns = Context.NOTIFICATION_SERVICE;
				 NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
				
				 
				 
				 int icon = R.drawable.icon;        // icon from resources
				 CharSequence tickerText = "T3HHXX0R";              // ticker-text
				 long when = System.currentTimeMillis();         // notification time
				 CharSequence contentTitle = "OMFGB Nightlies";  // expanded message title
				 CharSequence contentText = "Download completed";      // expanded message text

				 Intent notificationIntent = new Intent(context, OMFGBExternalAddonsAppNightlyActivity.class);
				
				 
				 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

				 // the next two lines initialize the Notification, using the configurations above
				 Notification notification = new Notification(icon, tickerText, when);
				 notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				 final int HELLO_ID = 1;

				 mNotificationManager.notify(HELLO_ID, notification);
			}
			if(intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){


				 Log.e(TAG, "Reciving " + DownloadManager.ACTION_NOTIFICATION_CLICKED);
				 
			
				 Intent notificationIntent = new Intent(context, OMFGBExternalAddonsAppNightlyActivity.class);
				 
				 // Curently this does not work, use as reference as to what might
				 // http://www.java2s.com/Open-Source/Android/android-platform-apps
				 // /Browser/com/android/browser/OpenDownloadReceiver.java.htm
				 
				 startActivity(notificationIntent);
			}
			
			
			
		}
    	
    	
    	
    	
    	
    	
    } 
    
    
  
 
 
 protected void AlertBox(String title, String mymessage)
 {
 new AlertDialog.Builder(this)
    .setMessage(mymessage)
    .setTitle(title)
    .setCancelable(false)
    .setPositiveButton("OK",
       new DialogInterface.OnClickListener() {
       public void onClick(DialogInterface dialog, int whichButton){
    	   finish();
       }       
       })
       
       
    .show();
 		
	
 }

@Override
public  PreferenceScreen ParseJSONScript(PreferenceScreen PreferenceRoot, InputStream is) {
	
	String x;
	JSONArray entries = null;
	
	try{
		
	    byte [] buffer = new byte[is.available()];
	    while (is.read(buffer) != -1);
		String jsontext = new String(buffer);
		entries = new JSONArray(jsontext);
		
	}catch(IOException e){
		
		e.printStackTrace();
	}
	catch(JSONException e){
		
		e.printStackTrace();
	}
	
    
    
    
    
    PreferenceCategory cat =  new PreferenceCategory(this);
	cat.setTitle("OMFGB Nightlies");
	 
	PreferenceRoot.addPreference(cat);
	 
	

    Log.d(TAG, "Json parsing started");

    x = "JSON parsed.\nThere are [" + entries.length() + "] entries.\n";

    int i;
    Log.i(TAG, "The number of entries is: " + entries.length());
    Log.d(TAG, "Starting preference resolver");
    
        for (i=0;i<entries.length();i++)
        {

            try{
            	
            	NightlyObject n = new NightlyObject();
            	JSONObject post = entries.getJSONObject(i);
            
	            n.setDate(post.getString("date"));
	            n.setBase(post.getString("base"));
	            n.setDevice(post.getString("device"));
	            n.setURL(post.getString("url"));
	            n.setVersion(post.getString("version"));
	            n.setZipName(post.getString("name"));
	            n.setInstallable(post.getString("installable"));
	            try{
	            	n.setDescription(post.getString("description"));
	            }catch(JSONException e){
	            	n.setDescription("Older Nightly, Please select a nwer one");
	            	
	            	
	            }
           
            
            PreferenceScreen inscreen = getPreferenceManager().createPreferenceScreen(this);
            inscreen.setSummary(n.getDescription());
            inscreen.setTitle(n.getDate());
            
            // Set the click listener for each preference
            OnNightlyPreferenceClickListener listner = new OnNightlyPreferenceClickListener(n, i, this);
            
            inscreen.setOnPreferenceClickListener(listner);
            
            // Finally add the preference to the heirachy
            Log.i(TAG, (String) inscreen.getTitle());
            cat.addPreference(inscreen);
            Log.i(TAG, "Preference screen added with nightly object");
            
            }
            catch(JSONException e){
            	
            	e.printStackTrace();
            	
            }
            

        	}
        
        Log.d(TAG, x);
    

		return  PreferenceRoot;
	}

	@Override
	public PreferenceScreen unableToDownloadScript() {
		  Log.d(TAG, "The input stream is null. Does the user have a data connection or has the " +
	 		"developer left CREATE_ERROR set to true");
	 OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithISerror = true;
	 OMFGBExternalAddonsAppNightlyActivity.mCreateUI = false;
	 OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithManifesterror = false;
	 // return a blank view to the user
	 return getPreferenceManager().createPreferenceScreen(this);	
	 
	}
	
	@Override
	public PreferenceScreen unableToParseScript() {
		
	     Log.d(TAG, "Cannot parse the JSON script correctly");
	     OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithISerror = false;
		   OMFGBExternalAddonsAppNightlyActivity.mCreateUI = false;
		   OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithManifesterror = true;
		   return getPreferenceManager().createPreferenceScreen(this);
		
	}
	
	@Override
	public PreferenceScreen ParsingCompletedSuccess() {
		OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithISerror = false;
		OMFGBExternalAddonsAppNightlyActivity.mCreateUI = true;
		OMFGBExternalAddonsAppNightlyActivity.mCreateBlankUIWithManifesterror = false;
		
		return null;
	}
    
}
