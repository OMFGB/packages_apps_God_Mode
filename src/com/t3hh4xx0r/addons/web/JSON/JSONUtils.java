package com.t3hh4xx0r.addons.web.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.DownloadFile;

public class JSONUtils {
	

	private boolean DBG = (false || Constants.FULL_DBG);
	JSONParsingInterface mJSONParsingInterface;
	Context mContext;
	
	private String TAG = "JSONParsingInterface";
	
	public PreferenceScreen ParseJSON(PreferenceActivity activity, Context context, boolean refresh){
		
		 mContext = context;
		 PreferenceScreen PreferenceRoot = activity.getPreferenceManager().createPreferenceScreen(mContext);
		 InputStream is;
		 
		if(mJSONParsingInterface != null){
			
			 is = mJSONParsingInterface.DownloadJSONScript(refresh);
			 
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
			 
		}
		return PreferenceRoot;
	}
	
	public void downloadJSONFile(boolean refresh){
		
		if(mJSONParsingInterface != null){
			
			mJSONParsingInterface.DownloadJSONScript(refresh);
			
		}
		
		
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
	  * @return an || refreshunused preferencescreen
	  */
	 PreferenceScreen ParsingCompletedSuccess();
	 
	 
	 /**
	  * Downloads the json script from its server.
	  * 
	  * 
	  * @param refresh Manually refresh the manifest.
	  * @return The input stream of the manifest to download.
	  */
	 InputStream DownloadJSONScript(boolean refresh);
    
    
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
