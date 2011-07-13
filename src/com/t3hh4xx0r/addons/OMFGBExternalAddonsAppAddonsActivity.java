package com.t3hh4xx0r.addons;

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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.t3hh4xx0r.R;

public class OMFGBExternalAddonsAppAddonsActivity extends PreferenceActivity {
	
	private boolean DBG = (false || Constants.FULL_DBG);
	private final String TAG = "OMFGB External Addons App Addons Activity";

	private RelativeLayout mPreferenceContainer;
	private ListView mPreferenceListView;
	private PreferenceScreen mRootPreference;

	private ProgressDialog mProgressDialog;
	

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
        
        
        mJSONRunnable = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mRootPreference = getAddons();
				mHandler.sendEmptyMessage(Constants.DOWNLOAD_COMPLETE);
				
				
			}     	
        };
        
        
        

        Thread Download = new Thread(mJSONRunnable);
        Download.start();

      

      mProgressDialog = ProgressDialog.show(OMFGBExternalAddonsAppAddonsActivity.this,    
              "Please wait...", "Retrieving data ...", true);
      
     
    }
	
    

    
    public void finishUIConstruction(){
    	
    	 
    	

        mRootPreference.bind(mPreferenceListView);
        if(DBG)Log.i(TAG, "mPreferenceListView: " + mPreferenceListView.getCount());
        
        mPreferenceContainer.addView(mPreferenceListView);
        
        
      setContentView(mPreferenceContainer);
      setPreferenceScreen(mRootPreference);
        
        
    	
    	
    	
    	
    }
    
    private PreferenceScreen getAddons(){

     	
   	 PreferenceScreen PreferenceRoot = getPreferenceManager().createPreferenceScreen(this);
   	 PreferenceCategory googlecat =  new PreferenceCategory(this);
   	 googlecat.setTitle("Flashable Google Apps");

   	 PreferenceCategory kernelcat =  new PreferenceCategory(this);
   	 kernelcat.setTitle("Additional Kernels");

  	 PreferenceCategory applicationcat =  new PreferenceCategory(this);
  	 applicationcat.setTitle("Additional applications");
   	
   	 
   	 PreferenceRoot.addPreference(googlecat);
   	 PreferenceRoot.addPreference(applicationcat);
   	 PreferenceRoot.addPreference(kernelcat);
   	
       	try
           {

       		
               String x = "";
               InputStream is;
               // Need to actually put our sript locatio here
               Log.i(TAG, "Begining json parsing");
               //is = this.getResources().openRawResource(R.raw.jsonomfgb);
               
               
               File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.ADDONS);
              	try{
              		Log.i(TAG, updateFile.toString());
              		
              		File f = new File(Constants.DOWNLOAD_DIR );
               		if(!f.exists()){
               			
               			f.mkdirs();
               			if(true)Log.d(TAG, "File diretory does not exist, creating it");
               		}
               		f = null;
               		f = new File(Constants.DOWNLOAD_DIR );
              		
              		// Needed because the manager does not handle https connections
              		DownloadFile.updateAppManifest(Constants.ADDONS);
              		
              		is = new FileInputStream(updateFile);
              	}
           	catch(FileNotFoundException e){
           		
           			e.printStackTrace();
           			if(DBG)Log.d(TAG, "Could not update app from file resource, the file was not found. Reverting to nothing");
                   	is = null;
           		
           	}
               
               if(is != null){
               	
               
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
   	                	n.setZipName(post.getString("name"));
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
   	                
   	                if(DeviceType.DEVICE_TYPE.equals(n.getDevice()) || n.getDevice().equals("all")){
   	                	if(DBG){
   	                	Log.i(TAG, "Adding screen now");
   	                	Log.i(TAG, "Category = " +  n.getCategory());
   	                	}
   	                if(n.getCategory().equals("google"))googlecat.addPreference(inscreen);
   	                if(n.getCategory().equals("application"))applicationcat.addPreference(inscreen);
   	                if(n.getCategory().equals("kernel"))kernelcat.addPreference(inscreen);
   	                Log.i(TAG, "Preference screen added with addon object");
   	                
   	                }else{
   	                	 
   	                	Log.i(TAG, "Device not compatible with package");
   	                	
   	                }
   	                
   	          
   	
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
               }
               else{
               	
               	// Tell the user here that the
               	// manifest is messed up
               	// and to contact us
               	//AlertBox("Warning","Please contact the rom developers");
               	
               }
               
           }
           catch (Exception je)
           {

               Log.e(TAG, je.getMessage());
                je.printStackTrace();
           }
           
           

           return PreferenceRoot;
         }
     
    
	

}
