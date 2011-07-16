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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	private boolean CREATE_ERROR = false;
	private final String TAG = "OMFGB Addons App Addons Activity";

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
        		 // create the alert box and warn user
        		AlertBox("Warning","The addons " +
        				"manifest cannot be parsed. Please contact the rom developers " +
        				"@r2doesinc, @xoomdev or @linuxmotion.");
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
				
				
			}     	
        };
        
        
        

        Thread Download = new Thread(mJSONRunnable);
        Download.start();

      

      mProgressDialog = ProgressDialog.show(OMFGBExternalAddonsAppAddonsActivity.this,    
              "Please wait...", "Retrieving data ...", true);
      
     
    }
	
    

    public void finishEmptyUIConstruction(){
    	
    	 
    	
    	// Do not bind the listview to the rootPreference
        mPreferenceContainer.addView(mPreferenceListView);
        
        
      setContentView(mPreferenceContainer);
      setPreferenceScreen(mRootPreference);
        
  	
    	
    }    
    public void finishUIConstruction(){
    	
    	 
    	

        mRootPreference.bind(mPreferenceListView);
        if(DBG)Log.i(TAG, "mPreferenceListView: " + mPreferenceListView.getCount());
        
        mPreferenceContainer.addView(mPreferenceListView);
        
        
      setContentView(mPreferenceContainer);
      setPreferenceScreen(mRootPreference);
        
  	
    	
    }
    
    /**
     * Create three categories for the addons preferncescreen then adds them.
     * The three categories will populate the list dynamicly from
     * a parsed JSON file.
     * 
     * If a category is empty it will be reomved before displaying
     * 
     * @return PreferenceScreen the screen preference that will be insflated
     */
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
               
               
               File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.ADDONS);
               // Try and update the addons mainfest
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
              		if(CREATE_ERROR){
              			is = null;
              			Log.d(TAG, "Creating an error in the input stream");
              		}
              	}
           	catch(FileNotFoundException e){
           		// Could not update the addons manifest file
           			e.printStackTrace();
           			if(DBG)Log.d(TAG, "Could not update app from file resource, the file was not found. Reverting to nothing");
                   	is = null;
           		
           	}
               
           	// Only continue if the app could update the manifest
           	// This may be cahanged to use a cached version 
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
		   	                Log.i(TAG, "Preference screen added with addon object category " + n.getCategory());
   	                	}
   	                	else{
   	                		
   	                		Log.i(TAG, "Device density is not compatible");
   	                	}
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
            	   Log.d(TAG, "The input stream is null. Does the user have a data connection or has the " +
            	   		"developer left CREATE_ERROR set to true");
            	   mHandler.sendEmptyMessage(Constants.CANNOT_RETREIVE_MANIFEST);
            	   // return a blank view to the user
            	   return getPreferenceManager().createPreferenceScreen(this);
               	
               }
               
           }
           catch (Exception je)
           {

               Log.e(TAG, je.getMessage());
                je.printStackTrace();
                Log.d(TAG, "Cannot parse the JSON script correctly");
                mHandler.sendEmptyMessage(Constants.MANIFEST_IS_WRONG);
         	   return getPreferenceManager().createPreferenceScreen(this);
           }
           
           

	        	
           Log.i(TAG, "Finished retreiving addons, sending the ui construction message");

			mHandler.sendEmptyMessage(Constants.DOWNLOAD_COMPLETE);
           
           return PreferenceRoot;
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
     
    
	

}
