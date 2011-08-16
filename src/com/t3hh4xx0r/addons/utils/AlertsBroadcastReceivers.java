package com.t3hh4xx0r.addons.utils;

import com.t3hh4xx0r.R;
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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

        Constants.LOCAL_FILE = "/sdcard/t3hh4xx0r/downloads/" + Constants.ALERTS;

	Context mContext;
	DownloadManager downloadManager;

	public void onReceive(Context context, Intent intent) {
   	   Slog.d(TAG, "Received");
	   //First lets start with the bulletin board posts
	   downloadAlerts(context);
	}

	private void downloadAlerts(Context context) {
           File local = new File(Constants.LOCAL_FILE);
           if (!local.exists()) {
		refreshLocal();
                Slog.d(TAG, "Local file refreshed, skipping update check.");
           } else {
                getAlertsValues();
     	        diffChecks(context);
	   }
	}

    public void refreshLocal() {
	Downloads.refreshAlerts();
   }

   public void getAlertsValues() {
        Constants.LOCAL_MD5 = checkMd5(Constants.LOCAL_FILE);
	Slog.d(TAG, "Local md5: " + checkMd5(Constants.LOCAL_FILE));

	Downloads.refreshAlerts();
        Constants.REMOTE_MD5 = checkMd5(Constants.LOCAL_FILE);
	Slog.d(TAG, "remote md5: " + checkMd5(Constants.LOCAL_FILE));
    }

    public void diffChecks(Context context) {
	Slog.d(TAG, "BEGINNING DIFFCHECKS");
	if (!Constants.LOCAL_MD5.equals(Constants.REMOTE_MD5)) {
	    alertUser(context);
	} else {
	    Slog.d(TAG, "Your Alerts are up to date.");
	}
        Constants.REMOTE_MD5 = null;
        Constants.LOCAL_MD5 = null;
    }

    public void alertUser(Context context) {
        Slog.d(TAG, "You have new alerts.");

		 String ns = Context.NOTIFICATION_SERVICE;
		 NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		 int icon = R.drawable.icon;        // icon from resources
		 CharSequence tickerText = "T3hh4xx0r";              // ticker-text
		 long when = System.currentTimeMillis();         // notification time
		 CharSequence contentTitle = "T3hh4xx0r Addons";  // expanded message title
		 CharSequence contentText = "Download completed";      // expanded message text

		 Intent notificationIntent = new Intent(context, AlertsBroadcastReceivers.class);

		 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		 // the next two lines initialize the Notification, using the configurations above
		 Notification notification = new Notification(icon, tickerText, when);
		 notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		 final int HELLO_ID = 1;

		 mNotificationManager.notify(HELLO_ID, notification);
    }

    public String checkMd5(String s) {
    try {
        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();
        //Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();
        
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
    return "";
}
}
