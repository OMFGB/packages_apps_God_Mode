package com.t3hh4xx0r.addons.nightlies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.DeviceType;
import com.t3hh4xx0r.addons.utils.DownloadFile;

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


import com.t3hh4xx0r.god_mode.R;

public class OMFGBExternalAddonsAppNightlyActivity extends PreferenceActivity {
	
	RelativeLayout mPreferenceContainer;
	private ListView mPreferenceListView;
	
	
	private ProgressDialog mProgressDialog;



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
				// TODO Auto-generated method stub
				mRootPreference = createPreferenceHierarchy();
				mHandler.sendEmptyMessage(Constants.DOWNLOAD_COMPLETE);
				
				
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
        	 case ALERT_USER:
        		 AlertBox("USER", "Please contact rom devlopers");
        		 break;
        		 
        	 
        	 }
              Log.d(TAG, "handleMessage:"+ msg.toString()); 
              
         } 
    }; 
    
    
    
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
    
    
    private PreferenceScreen createPreferenceHierarchy(){
    	
    	
    	// The root preference
    	PreferenceScreen PreferenceRoot = getNightlies();
 	
    	
		return PreferenceRoot;
    	
    	
    	
    	
    }
    
    
 private PreferenceScreen getNightlies(){

 	
	 PreferenceScreen PreferenceRoot = getPreferenceManager().createPreferenceScreen(this);
	 PreferenceCategory cat =  new PreferenceCategory(this);
	 cat.setTitle("OMFGB Nightlies");

	 PreferenceRoot.addPreference(cat);
    	try
        {

    		
            String x = "";
            InputStream is = null;
            // Need to actually put our sript locatio here
            Log.d(TAG, "Begining json parsing");
            //is = this.getResources().openRawResource(R.raw.jsonomfgb);
            
            
           
           
            File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.getDeviceScript());
           	try{
           		Log.i(TAG, updateFile.toString());
           		
           		File f = new File(Constants.DOWNLOAD_DIR );
            		if(!f.exists()){
            			
            			f.mkdirs();
            			Log.i(TAG, "File diretory does not exist, creating it");
            		}
            		f = null;
           		
           		// Needed because the manager does not handle https connections
           		DownloadFile.updateAppManifest(Constants.getDeviceScript());
           		
           		is = new FileInputStream(updateFile);
           	}
           	catch(FileNotFoundException e){
           		
           			e.printStackTrace();
           			if(true)Log.d(TAG, "Could not update app from file resource, the file was not found. Reverting to nothing");
                   	is = null;
           		
           	}
            
            if(is != null){
            	
            
            byte [] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            
            String jsontext = new String(buffer);
            JSONArray entries = new JSONArray(jsontext);

            Log.d(TAG, "Json parsing finished");

            x = "JSON parsed.\nThere are [" + entries.length() + "] entries.\n";

            int i;
            Log.i(TAG, "The number of entries is: " + entries.length());
            Log.d(TAG, "Starting preference resolver");
            
	            for (i=0;i<entries.length();i++)
	            {
	
	                
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
            Log.d(TAG, x);
            }
            else{
            	
            	// Tell the user here that the
            	// manifest is messed up
            	// and to contact us
            	mHandler.sendEmptyMessage(ALERT_USER);
            	
            }
            
        }
        catch (Exception je)
        {

            Log.e(TAG, je.getMessage());
             je.printStackTrace();
        }
        
        

        return PreferenceRoot;
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
       }
       })
       
       
    .show();
 		
	
 }
    
}