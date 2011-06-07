package com.t3hh4xx0r.god_mode.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.IPackageDataObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;		
import android.preference.PreferenceScreen;

import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



public class PackageUtils {
	
	private final String TAG = "PackageUtils";
	String mPackage;
	Context mContext;

    private static final int OP_SUCCESSFUL = 1;
    private static final int OP_FAILED = 2;
    private static final int CLEAR_USER_DATA = 1;
    
    public PackageUtils(Context context, String packageName) {
    	mPackage = packageName;
    	mContext = context;
    	
    }
    
	
	private static ClearUserDataObserver mClearDataObserver;
	
	
    public  void initiateClearUserData() {
        //mClearDataButton.setEnabled(false);
        // Invoke uninstall or clear user data based on sysPackage
        String packageName  = mPackage;
        Log.i(TAG, "Clearing user data for package : " + packageName);
        if (mClearDataObserver == null) {
            mClearDataObserver = new ClearUserDataObserver();
        }
       
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(packageName, mClearDataObserver);
        if (!res) {
            // Clearing data failed for some obscure reason. Just log error for now
            Log.i(TAG, "Couldnt clear application user data for package:"+packageName);
            //showDialogInner(DLG_CANNOT_CLEAR_DATA);
        } else {
            //mClearDataButton.setText(R.string.recompute_size);
        }
    }
    class ClearUserDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            final Message msg = mHandler.obtainMessage(CLEAR_USER_DATA);
            msg.arg1 = succeeded?OP_SUCCESSFUL:OP_FAILED;
            mHandler.sendMessage(msg);
         }
     }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // If the activity is gone, don't process any more messages.
            //if (isFinishing()) {
            //    return;
           // }
            switch (msg.what) {
                case CLEAR_USER_DATA:
                    processClearMsg(msg);
                    break;
               // case CLEAR_CACHE:
                    // Refresh size info
               //     mState.requestSize(mAppEntry.info.packageName);
               //     break;
               // case PACKAGE_MOVE:
               //     processMoveMsg(msg);
               //     break;
               // default:
               //     break;
            }
        }
    };
    /*
     * Private method to handle clear message notification from observer when
     * the async operation from PackageManager is complete
     */
    private void processClearMsg(Message msg) {
        int result = msg.arg1;
        String packageName = mPackage;
        //mClearDataButton.setText(R.string.clear_user_data_text);
        if(result == OP_SUCCESSFUL) {
            Log.i(TAG, "Cleared user data for package : "+packageName);
        } else {
        	Log.i(TAG, "Could not clear user data for package : "+packageName);
        }
        checkForceStop();
    }
    private void checkForceStop() {
    	Log.i(TAG, "About to stop the package: " + mPackage);

        String packageName = mPackage;
        Intent intent = new Intent(Intent.ACTION_QUERY_PACKAGE_RESTART,
                Uri.fromParts("package", packageName, null));
        intent.putExtra(Intent.EXTRA_PACKAGES, new String[] { packageName });
        //intent.putExtra(Intent.EXTRA_UID, mAppEntry.info.uid);
        mContext.sendOrderedBroadcast(intent, null, mCheckKillProcessesReceiver, null,
                Activity.RESULT_CANCELED, null, null);
    }
    private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //mForceStopButton.setEnabled(getResultCode() != RESULT_CANCELED);
            //mForceStopButton.setOnClickListener(InstalledAppDetails.this);
        }
    };
	
	
	

}
