/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.t3hh4xx0r.god_mode.applications;

import com.android.internal.content.PackageHelper;
import com.t3hh4xx0r.god_mode.R;

import com.t3hh4xx0r.god_mode.applications.ApplicationsState.AppEntry;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.format.Formatter;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.view.View;
import android.widget.AppSecurityPermissions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity to display application information from Settings. This activity presents
 * extended information associated with a package like code, data, total size, permissions
 * used by the application and also the set of default launchable activities.
 * For system applications, an option to clear user data is displayed only if data size is > 0.
 * System applications that do not want clear user data do not have this option.
 * For non-system applications, there is no option to clear data. Instead there is an option to
 * uninstall the application.
 */
public class InstalledAppDetails extends Activity
        implements  ApplicationsState.Callbacks {
    private static final String TAG="InstalledAppDetails";
    static final boolean SUPPORT_DISABLE_APPS = false;
    private static final boolean localLOGV = false;
    
    private PackageManager mPm;
    private ApplicationsState mState;
    private ApplicationsState.AppEntry mAppEntry;
    private PackageInfo mPackageInfo;
    private boolean mMoveInProgress = false;
    private boolean mUpdatedSysApp = false;
    private boolean mCanClearData = true;

    private ClearUserDataObserver mClearDataObserver;
    // Views related to cache info

    private int mMoveErrorCode;
    
    
    private boolean mHaveSizes = false;
    private long mLastCodeSize = -1;
    private long mLastDataSize = -1;
    private long mLastCacheSize = -1;
    private long mLastTotalSize = -1;
    
    //internal constants used in Handler
    private static final int OP_SUCCESSFUL = 1;
    private static final int OP_FAILED = 2;
    private static final int CLEAR_USER_DATA = 1;
    private static final int CLEAR_CACHE = 3;
    private static final int PACKAGE_MOVE = 4;
    
    // invalid size value used initially and also when size retrieval through PackageManager
    // fails for whatever reason
    private static final int SIZE_INVALID = -1;
    
    // Resource strings
    private CharSequence mInvalidSizeStr;
    private CharSequence mComputingStr;
    
    // Dialog identifiers used in showDialog
    private static final int DLG_BASE = 0;
    private static final int DLG_CLEAR_DATA = DLG_BASE + 1;
    private static final int DLG_FACTORY_RESET = DLG_BASE + 2;
    private static final int DLG_APP_NOT_FOUND = DLG_BASE + 3;
    private static final int DLG_CANNOT_CLEAR_DATA = DLG_BASE + 4;
    private static final int DLG_FORCE_STOP = DLG_BASE + 5;
    private static final int DLG_MOVE_FAILED = DLG_BASE + 6;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // If the activity is gone, don't process any more messages.
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case CLEAR_USER_DATA:
                    processClearMsg(msg);
                    break;
                default:
                    break;
            }
        }
    };
    
    class ClearUserDataObserver extends IPackageDataObserver.Stub {
       public void onRemoveCompleted(final String packageName, final boolean succeeded) {
           final Message msg = mHandler.obtainMessage(CLEAR_USER_DATA);
           msg.arg1 = succeeded?OP_SUCCESSFUL:OP_FAILED;
           mHandler.sendMessage(msg);
        }
    }
    

    
    private String getSizeStr(long size) {
        if (size == SIZE_INVALID) {
            return mInvalidSizeStr.toString();
        }
        return Formatter.formatFileSize(this, size);
    }
    
  

  


    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        mState = ApplicationsState.getInstance(getApplication());
        mPm = getPackageManager();
        
        //mCanBeOnSdCardChecker = new CanBeOnSdCardChecker();
        
        //setContentView(R.layout.installed_app_details);
        
        //mComputingStr = getText(R.string.computing_size);
        
        // Set default values on sizes
        //mTotalSize = (TextView)findViewById(R.id.total_size_text);
        //mAppSize = (TextView)findViewById(R.id.application_size_text);
        // mDataSize = (TextView)findViewById(R.id.data_size_text);
        
        // Get Control button panel
        // View btnPanel = findViewById(R.id.control_buttons_panel);
        //mForceStopButton = (Button) btnPanel.findViewById(R.id.left_button);
        //mForceStopButton.setText(R.string.force_stop);
        // mUninstallButton = (Button)btnPanel.findViewById(R.id.right_button);
        // mForceStopButton.setEnabled(false);
        
        // Initialize clear data and move install location buttons
        //View data_buttons_panel = findViewById(R.id.data_buttons_panel);
        //mClearDataButton = (Button) data_buttons_panel.findViewById(R.id.left_button);
        //mMoveAppButton = (Button) data_buttons_panel.findViewById(R.id.right_button);
        
        // Cache section
        //mCacheSize = (TextView) findViewById(R.id.cache_size_text);
        //mClearCacheButton = (Button) findViewById(R.id.clear_cache_button);
        
       // mActivitiesButton = (Button)findViewById(R.id.clear_activities_button);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        mState.resume(this);
       // if (!refreshUi()) {
       //     setIntentAndFinish(true, true);
       // }
    }

    @Override
    public void onPause() {
        super.onPause();
        mState.pause();
    }

    @Override
    public void onAllSizesComputed() {
    }

    @Override
    public void onPackageIconChanged() {
    }

    @Override
    public void onPackageListChanged() {
        //refreshUi();
    }

    @Override
    public void onRebuildComplete(ArrayList<AppEntry> apps) {
    }

    @Override
    public void onPackageSizeChanged(String packageName) {
        if (packageName.equals(mAppEntry.info.packageName)) {
            //refreshSizeInfo();
        }
    }

    @Override
    public void onRunningStateChanged(boolean running) {
    }

    /*
     * Private method to handle clear message notification from observer when
     * the async operation from PackageManager is complete
     */
    private void processClearMsg(Message msg) {
        int result = msg.arg1;
        String packageName = mAppEntry.info.packageName;
        //mClearDataButton.setText(R.string.clear_user_data_text);
        if(result == OP_SUCCESSFUL) {
            Log.i(TAG, "Cleared user data for package : "+packageName);
            mState.requestSize(mAppEntry.info.packageName);
        } else {
            //mClearDataButton.setEnabled(true);
        }
        checkForceStop();
    }

    private void processMoveMsg(Message msg) {
        int result = msg.arg1;
        String packageName = mAppEntry.info.packageName;
        // Refresh the button attributes.
        mMoveInProgress = false;
        if (result == PackageManager.MOVE_SUCCEEDED) {
            Log.i(TAG, "Moved resources for " + packageName);
            // Refresh size information again.
            mState.requestSize(mAppEntry.info.packageName);
        } else {
            mMoveErrorCode = result;
            showDialogInner(DLG_MOVE_FAILED);
        }
        //refreshUi();
    }

    /*
     * Private method to initiate clearing user data when the user clicks the clear data 
     * button for a system package
     */
    private  void initiateClearUserData() {
        //mClearDataButton.setEnabled(false);
        // Invoke uninstall or clear user data based on sysPackage
        String packageName = mAppEntry.info.packageName;
        Log.i(TAG, "Clearing user data for package : " + packageName);
        if (mClearDataObserver == null) {
            mClearDataObserver = new ClearUserDataObserver();
        }
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(packageName, mClearDataObserver);
        if (!res) {
            // Clearing data failed for some obscure reason. Just log error for now
            Log.i(TAG, "Couldnt clear application user data for package:"+packageName);
            showDialogInner(DLG_CANNOT_CLEAR_DATA);
        } else {
            //mClearDataButton.setText(R.string.recompute_size);
        }
    }
    
    private void showDialogInner(int id) {
        //removeDialog(id);
        showDialog(id);
    }
    
    private void forceStopPackage(String pkgName) {
        ActivityManager am = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE);
        am.forceStopPackage(pkgName);
        checkForceStop();
    }

    private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //mForceStopButton.setEnabled(getResultCode() != RESULT_CANCELED);
            //mForceStopButton.setOnClickListener(InstalledAppDetails.this);
        }
    };
    
    private void checkForceStop() {
        Intent intent = new Intent(Intent.ACTION_QUERY_PACKAGE_RESTART,
                Uri.fromParts("package", mAppEntry.info.packageName, null));
        intent.putExtra(Intent.EXTRA_PACKAGES, new String[] { mAppEntry.info.packageName });
        intent.putExtra(Intent.EXTRA_UID, mAppEntry.info.uid);
        sendOrderedBroadcast(intent, null, mCheckKillProcessesReceiver, null,
                Activity.RESULT_CANCELED, null, null);
    }
    
    static class DisableChanger extends AsyncTask<Object, Object, Object> {
        final PackageManager mPm;
        final WeakReference<InstalledAppDetails> mActivity;
        final ApplicationInfo mInfo;
        final int mState;

        DisableChanger(InstalledAppDetails activity, ApplicationInfo info, int state) {
            mPm = activity.mPm;
            mActivity = new WeakReference<InstalledAppDetails>(activity);
            mInfo = info;
            mState = state;
        }

        @Override
        protected Object doInBackground(Object... params) {
            mPm.setApplicationEnabledSetting(mInfo.packageName, mState, 0);
            return null;
        }
    }

}

