package com.t3hh4xx0r.addons.utils;

import com.t3hh4xx0r.addons.alerts.OMFGBAlertsActivity;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.security.*;
import java.math.*;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.net.Uri;
import android.widget.Toast;
import android.util.Slog;


public class AlertsBroadcastReceivers extends BroadcastReceiver {
	public static String TAG = "Receivers";
	private static String updateDirectory = "/sdcard/t3hh4xx0r/updates/";
	private String OUTPUT;

	Context mContext;
	DownloadManager downloadManager;

	public void onReceive(Context context, Intent intent) {
   	   Slog.d(TAG, "Received");
	   //First lets start with the bulletin board posts
	   downloadAlerts();
	}

	private void downloadAlerts() {
           Constants.LOCAL_FILE = "/sdcard/t3hh4xx0r/downloads/" + Constants.ALERTS;
           File local = new File(Constants.LOCAL_FILE);
           if (!local.exists()) {
		refreshLocal();
                Slog.d(TAG, "Local file refreshed, skipping update check.");
           } else {
                getAlertsValues();
     	        diffChecks();
	   }
	}

    public void refreshLocal() {
	Downloads.refreshAlerts();
   }

   public void getAlertsValues() {
	Slog.d(TAG, "local file is " + Constants.LOCAL_FILE); 
	checkMd5(Constants.LOCAL_FILE);
	Constants.LOCAL_MD5 = Constants.OUTPUT_MD5;
	Slog.d(TAG, "Local md5: " + Constants.LOCAL_MD5);
	Constants.OUTPUT_MD5 = null;

	Downloads.refreshAlerts();
	checkMd5(Constants.LOCAL_FILE);
	Constants.REMOTE_MD5 = Constants.OUTPUT_MD5;
	Slog.d(TAG, "remote md5: " + Constants.REMOTE_MD5);
 	Constants.OUTPUT_MD5 = null;
    }

    public void diffChecks() {
	Slog.d(TAG, "BEGINNING DIFFCHECKS");
	if (Constants.LOCAL_MD5 != Constants.REMOTE_MD5) {
	    alertUser();
	} else {
	    Slog.d(TAG, "Your Alerts are up to date.");
	}
    }

    public void alertUser() {
        Slog.d(TAG, "You have new alerts.");
        Slog.d(TAG, Constants.LOCAL_MD5 + " != " + Constants.REMOTE_MD5);
    }

    public static String checkMd5(String string) {
    	    byte[] hash;

	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
        	throw new RuntimeException("Huh, UTF-8 should be supported?", e);	    
	    }	
	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
            Constants.OUTPUT_MD5 = hex.toString();
	    return hex.toString();
      }
}
