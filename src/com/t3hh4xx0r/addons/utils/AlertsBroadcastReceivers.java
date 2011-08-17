package com.t3hh4xx0r.addons.utils;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.alerts.OMFGBAlertsActivity;

import java.io.*;
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


	Context mContext;
	DownloadManager downloadManager;

	public void onReceive(Context context, Intent intent) {
           Constants.LOCAL_FILE = "/sdcard/t3hh4xx0r/downloads/" + Constants.ALERTS;
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
	File local = new File (Constants.LOCAL_FILE);
        Constants.LOCAL_MD5 = checkMd5(local);
	Slog.d(TAG, "Local md5: " + Constants.LOCAL_MD5);

	Downloads.refreshAlerts();
        File remote = new File (Constants.LOCAL_FILE);
        Constants.REMOTE_MD5 = checkMd5(remote);
	Slog.d(TAG, "remote md5: " + Constants.REMOTE_MD5);
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
		 CharSequence tickerText = "God Mode";              // ticker-text
		 long when = System.currentTimeMillis();         // notification time
		 CharSequence contentTitle = "Bulletin Board";  // expanded message title
		 CharSequence contentText = "New Bulletin Board post available.";      // expanded message text

		 Intent notificationIntent = new Intent(context, com.t3hh4xx0r.addons.alerts.OMFGBAlertsActivity.class);

		 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		 // the next two lines initialize the Notification, using the configurations above
		 Notification notification = new Notification(icon, tickerText, when);
		 notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		 final int HELLO_ID = 1;

		 mNotificationManager.notify(HELLO_ID, notification);
    }

    public String checkMd5(File updatefile) {
                MessageDigest digest;
                try {
                    digest = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    Slog.d(TAG, "Exception while getting Digest", e);
                    return null;
                }
                InputStream is;
                try {
                    is = new FileInputStream(updatefile);
                } catch (FileNotFoundException e) {
                    Slog.d(TAG, "Exception while getting FileInputStream", e);
                    return null;
                }
                byte[] buffer = new byte[8192];
                int read;
                try {
                    while ((read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }
                    byte[] md5sum = digest.digest();
                    BigInteger bigInt = new BigInteger(1, md5sum);
                    String output = bigInt.toString(16);
                    //Fill to 32 chars
                    output = String.format("%32s", output).replace(' ', '0');
                    return output;
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Unable to process file for MD5", e);
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Slog.d(TAG, "Exception on closing MD5 input stream", e);
                    }
                }
            }
}
