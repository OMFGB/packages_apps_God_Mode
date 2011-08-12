package com.t3hh4xx0r.addons.alerts;

import java.io.File;

import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.addons.utils.Downloads;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.t3hh4xx0r.R;

public class OnAlertsPreferenceClickListener implements OnPreferenceClickListener {

	private boolean DBG = (false || Constants.FULL_DBG);
	private final String TAG = "OnNightlyPreferenceClick";
	AlertsObject mAlerts;
	int mPosition;
	Context mContext;
	private String externalStorageDir = "/mnt/sdcard/t3hh4xx0r/downloads";
	private String DOWNLOAD_DIR = externalStorageDir+ "/";
	
	public OnAlertsPreferenceClickListener(AlertsObject o, int position, Context context) {
		mAlerts = o;
		mPosition = position;
		mContext = context;
	}
	
	@Override
	public boolean onPreferenceClick(Preference v) {
 		Log.d(TAG, v.getSummary().toString()  );
 		Log.d(TAG, v.getTitle().toString()  );
		//Gmail intent to reply to the dev
	return true;
	}
	
	
    private void log(String message){		   
        if(DBG)Log.d(TAG, message);		  
	}
}  
