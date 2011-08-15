package com.t3hh4xx0r.addons.utils;

import com.t3hh4xx0r.addons.alerts.OMFGBAlertsActivity;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.net.Uri;
import android.widget.Toast;
import android.util.Slog;


public class BroadcastReceivers extends BroadcastReceiver {
	public static String TAG = "Receivers";
	private static String updateDirectory = "/sdcard/t3hh4xx0r/updates/";
	private String OUTPUT;

        final String strPref_Download_ID = "PREF_DOWNLOAD_ID";


	Context mContext;
	DownloadManager downloadManager;

	public void onReceive(Context context, Intent intent) {
   	   Slog.d(TAG, "Received");
	   //First lets start with the bulletin board posts
	   downloadAlerts();
	}

	private void downloadAlerts() {
	   Constants.REMOTE_FILE = updateDirectory + Constants.ALERTS;
           Constants.LOCAL_FILE = "/sdcard/t3hh4xx0r/downloads/" + Constants.ALERTS;
           File local = new File(Constants.LOCAL_FILE);
           if (!local.exists()) {
		refreshLocal();
                Slog.d(TAG, "Local file refreshed, skipping update check.");
           } else {
		File remote = new File(Constants.REMOTE_FILE);
		if (!remote.exists())  {
		    createDirs();
                    refreshRemote();
	                if (remote.exists())  {
                            diffChecks();
			}
		} else {
		cleanFiles();
		createDirs();
                refreshRemote();
		    if (remote.exists())  {
			diffChecks();
		    }
		}
	   }
	}

    public void refreshLocal() {
	Downloads.refreshAlerts();
   }

   public void refreshRemote() {
	//download the manifests file
    }

    public static void createDirs() {
	File f = new File(updateDirectory);
        if(!f.exists()) {
        	f.mkdirs();
                Slog.d(TAG, "File diretory does not exist, creating it");
        }
    }	

    public static void cleanFiles() {
        Thread cmdThread = new Thread(){
            @Override
            public void run() {
                File deletedlcache = new File(updateDirectory);
                if(deletedlcache.exists()){
                    File cachedfiles[] = deletedlcache.listFiles();     
                        for(int i = 0; i < cachedfiles.length ; i++){
                            if(!cachedfiles[i].delete()){
                                Slog.d(TAG, "File cannot be deleted");           
                            }

                        }
                        deletedlcache.delete();
                }
            }
        };
        cmdThread.start();
    }

    public void diffChecks() {
	Slog.d(TAG, "BEGINNING DIFFCHECKS");
	    Thread cmdThread = new Thread(){
		    @Override
		    public void run() {
			final Runtime run = Runtime.getRuntime();
			DataOutputStream out = null;
			Process p = null;
			    try {
        			Slog.d(TAG, "Executing su");
	                	p = run.exec("su");
				out = new DataOutputStream(p.getOutputStream());
                        	        out.writeBytes("if diff " + Constants.LOCAL_FILE + " " + Constants.REMOTE_FILE + " >/dev/null ; then touch " +
					updateDirectory + "same.txt ; fi\n");
					out.flush();
		        	        File f = new File (updateDirectory + "same.txt");
            				if (f.exists()) {   
                			    Constants.IS_DIFF = 0;
                			    Slog.d(TAG, "Up to date.");
            				} else {
                			    Constants.IS_DIFF = 1;
                			    Slog.d(TAG, "New something availabe");
					}
			    } catch (IOException e) {
					    e.printStackTrace();
					    return;
			    }
	
		    }
	    };
	    cmdThread.start();
   }
}
