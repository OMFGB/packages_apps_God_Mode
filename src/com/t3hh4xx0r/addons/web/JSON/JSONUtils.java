package com.t3hh4xx0r.addons.web.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.internal.widget.CircularSelector.OnCircularSelectorTriggerListener;
import com.t3hh4xx0r.addons.AddonsObject;
import com.t3hh4xx0r.addons.OMFGBExternalAddonsAppAddonsActivity;
import com.t3hh4xx0r.addons.OnAddonsPreferenceClickListener;
import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.DeviceType;
import com.t3hh4xx0r.addons.utils.DownloadFile;

public class JSONUtils {
	
	JSONParsingInterface mJSONParsingInterface;
	Context mContext;
	
	private String TAG = "JSONParsingInterface";
	
	public PreferenceScreen ParseJSON(PreferenceActivity activity, Context context, boolean isAddon){
		
		 mContext = context;
		 PreferenceScreen PreferenceRoot = activity.getPreferenceManager().createPreferenceScreen(mContext);
		 InputStream is;
		 if(isAddon){
			 is = downloadAddonJSONScript();
			 Log.d(TAG, "Setting addons parser");
		 }
		 else {
			 is = downloadNightlyJSONScript();
			 Log.d(TAG, "Setting nightlies parser");
		 }
		 
		 if(is != null){
			 
		
			 try {
				 
				 PreferenceRoot = mJSONParsingInterface.ParseJSONScript(PreferenceRoot,is);
				 mJSONParsingInterface.ParsingCompletedSuccess();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				PreferenceRoot = mJSONParsingInterface.unableToParseScript();
			}
		 
		 }
		 else{
			 PreferenceRoot = mJSONParsingInterface.unableToDownloadScript();
			 
		 }
		return PreferenceRoot;
	}
	
	/**
	 * Downloads the JSON script.
	 * Performs a check to make sure that the 
	 * file directory is present
	 * 
	 * @return is the inputstream for the json script
	 */
	private InputStream downloadNightlyJSONScript(){
		
		

    		
            InputStream is = null;
            Log.d(TAG, "Begining json download");
            
            if(checkDownloadDirectory()){
            	
            	File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.getDeviceScript());
	           	try{
	           		Log.i(TAG, "The update path and file is called: " + updateFile.toString());
	           		// Needed because the manager does not handle https connections
	           		DownloadFile.updateAppManifest(Constants.getDeviceScript());
	           		
	           		is = new FileInputStream(updateFile);
	           	
	           	}
	           	catch(FileNotFoundException e){
	           		
	           			e.printStackTrace();
	           			if(true)Log.d(TAG, "Could not update app from file resource," +
	           					" the file was not found. Reverting to nothing");
	           			is = null;
	                   	
	           	}
	           	
            	
            }// End checkdownloaddir if statement
           
           
            

            // If this point is reached then the input stream is null  
        return is;
        
        }
	/**
	 * Downloads the JSON script.
	 * Performs a check to make sure that the 
	 * file directory is present
	 * 
	 * @return is the inputstream for the json script
	 */
	private InputStream downloadAddonJSONScript(){
		
		

    		
            InputStream is = null;
            Log.d(TAG, "Begining json download");
            
            if(checkDownloadDirectory()){
            	
            	File updateFile = new File(Constants.DOWNLOAD_DIR + Constants.ADDONS);
	           	try{
	           		Log.i(TAG, "The update path and file is called: " + updateFile.toString());
	           		// Needed because the manager does not handle https connections
	           		DownloadFile.updateAppManifest(Constants.ADDONS);
	           		
	           		is = new FileInputStream(updateFile);
	           	
	           	}
	           	catch(FileNotFoundException e){
	           		
	           			e.printStackTrace();
	           			if(true)Log.d(TAG, "Could not update app from file resource," +
	           					" the file was not found. Reverting to nothing");
	           			is = null;
	                   	
	           	}
	           	
            	
            }// End checkdownloaddir if statement
           
           
            

            // If this point is reached then the input stream is null  
        return is;
        
        }
	/**
	 * Checks to see if the download directory
	 * for t3hh4xx0r is created. If is
	 * not created, it will create it.
	 * 
	 * 
	 * @return true if the directory exists or if the directory was created if needed, false otherwise.
	 */
	private Boolean checkDownloadDirectory(){
		
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

			Log.i(TAG, "File directory  exist.");
			
		}
		return success;
		
	}
		
	
	
	
	public interface JSONParsingInterface {
		
	
	/**
     * Create three categories for the addons preferncescreen then adds them.
     * The three categories will populate the list dynamicly from
     * a parsed JSON file.
     * 
     * If a category is empty it will be reomved before displaying
     * 
     * @return PreferenceScreen the screen preference that will be insflated
     */
	 PreferenceScreen ParseJSONScript(PreferenceScreen PreferenceRoot, InputStream is) throws JSONException;
	 /**
	  * Function that is executed if the JSON script
	  * is failed to be parsed correctly
	  * 
	  * @return the preferencescreen to create if the script cannot be parsed
	  */
	 PreferenceScreen unableToParseScript();
	 /**
	  * 
	  * @return the preferencescreen to create if the script cannot be downloaded
	  */
	 PreferenceScreen unableToDownloadScript();
	 /**
	  * Used to send message or do more work
	  * after the script is correctly parsed.
	  * 
	  * @return an unused preferencescreen
	  */
	 PreferenceScreen ParsingCompletedSuccess();
    
    
	}
	
	
	/**
     * Registers a callback to be invoked when the music controls
     * are "triggered" by sliding the view one way or the other
     * or pressing the music control buttons.
     *
     * @param l the OnMusicTriggerListener to attach to this view
     */
    public void setJSONUtilsParsingInterface(JSONParsingInterface l) {
    	
    	this.mJSONParsingInterface = l;
    }
	
	
	
	
}
