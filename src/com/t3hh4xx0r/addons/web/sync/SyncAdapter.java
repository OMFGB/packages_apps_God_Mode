/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t3hh4xx0r.addons.web.sync;

import com.example.jumpnote.allshared.JsonRpcClient;
import com.example.jumpnote.allshared.JsonRpcException;
import com.example.jumpnote.allshared.JumpNoteProtocol;
import com.example.jumpnote.android.ModelJava.DeviceRegistration;
import com.example.jumpnote.android.jsonrpc.AuthenticatedJsonRpcJavaClient;
import com.example.jumpnote.android.jsonrpc.AuthenticatedJsonRpcJavaClient.InvalidAuthTokenException;
import com.example.jumpnote.android.jsonrpc.AuthenticatedJsonRpcJavaClient.RequestedUserAuthenticationException;
import com.example.jumpnote.javashared.Util;
import com.google.android.c2dm.C2DMessaging;
import com.t3hh4xx0r.addons.utils.Constants;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.content.SyncStats;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JumpNote SyncAdapter implementation. The sync adapter does the following:
 * <ul>
 *   <li>Device registration/unregistration when auto-sync settings for the account
 *     (or global settings) have changed, via the <code>devices.register</code> (and similar)
 *     RPC method.</li>
 *   <li>Checking for locally modified notes since the last successful sync time.</li>
 *   <li>Synchronization with the server, via the <code>notes.sync</code> RPC method.</li>
 * </ul>
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    static final String TAG = Config.makeLogTag(SyncAdapter.class);

    public static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    public static final String[] GOOGLE_ACCOUNT_REQUIRED_SYNCABILITY_FEATURES =
            new String[]{ "service_ah" };

    public static final String DEVICE_TYPE = "android";
    public static final String LAST_SYNC = "last_sync";
    public static final String SERVER_LAST_SYNC = "server_last_sync";
    public static final String DM_REGISTERED = "dm_registered";

    private static final String[] PROJECTION = new String[] {
        JumpNoteContract.Notes._ID, // 0
        JumpNoteContract.Notes.SERVER_ID, // 1
        JumpNoteContract.Notes.TITLE, // 2
        JumpNoteContract.Notes.BODY, // 3
        JumpNoteContract.Notes.CREATED_DATE, // 4
        JumpNoteContract.Notes.MODIFIED_DATE, // 5
        JumpNoteContract.Notes.PENDING_DELETE, // 6
    };

    private final Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(final Account account, Bundle extras, String authority,
            final ContentProviderClient provider, final SyncResult syncResult) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        String clientDeviceId = tm.getDeviceId();

        final long newSyncTime = System.currentTimeMillis();

        final boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        final boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        final boolean initialize = extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);

        C2DMReceiver.refreshAppC2DMRegistrationState(mContext);

        Log.i(TAG, "Beginning " + (uploadOnly ? "upload-only" : "full") +
                " sync for account " + account.name);

        // Read this account's sync metadata
        final SharedPreferences syncMeta = mContext.getSharedPreferences("sync:" + account.name, 0);
        long lastSyncTime = syncMeta.getLong(LAST_SYNC, 0);
        long lastServerSyncTime = syncMeta.getLong(SERVER_LAST_SYNC, 0);

        // Check for changes in either app-wide auto sync registration information, or changes in
        // the user's preferences for auto sync on this account; if either changes, piggy back the
        // new registration information in this sync.
        long lastRegistrationChangeTime = C2DMessaging.getLastRegistrationChange(mContext);

        boolean autoSyncDesired = ContentResolver.getMasterSyncAutomatically() &&
                ContentResolver.getSyncAutomatically(account, Constants.AUTHORITY);
        boolean autoSyncEnabled = syncMeta.getBoolean(DM_REGISTERED, false);

        // Will be 0 for no change, -1 for unregister, 1 for register.
        final int deviceRegChange;
        JsonRpcClient.Call deviceRegCall = null;
        if (autoSyncDesired != autoSyncEnabled || lastRegistrationChangeTime > lastSyncTime ||
                initialize || manualSync) {

            String registrationId = C2DMessaging.getRegistrationId(mContext);
            deviceRegChange = (autoSyncDesired && registrationId != null) ? 1 : -1;

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Auto sync selection or registration information has changed, " +
                        (deviceRegChange == 1 ? "registering" : "unregistering") +
                        " messaging for this device, for account " + account.name);
            }

            try {
                if (deviceRegChange == 1) {
                    // Register device for auto sync on this account.
                    deviceRegCall = new JsonRpcClient.Call(JumpNoteProtocol.DevicesRegister.METHOD);
                    JSONObject params = new JSONObject();

                    DeviceRegistration device = new DeviceRegistration(clientDeviceId,
                            DEVICE_TYPE, registrationId);
                    params.put(JumpNoteProtocol.DevicesRegister.ARG_DEVICE, device.toJSON());
                    deviceRegCall.setParams(params);
                } else {
                    // Unregister device for auto sync on this account.
                    deviceRegCall = new JsonRpcClient.Call(JumpNoteProtocol.DevicesUnregister.METHOD);
                    JSONObject params = new JSONObject();
                    params.put(JumpNoteProtocol.DevicesUnregister.ARG_DEVICE_ID, clientDeviceId);
                    deviceRegCall.setParams(params);
                }
            } catch (JSONException e) {
                logErrorMessage("Error generating device registration remote RPC parameters.",
                        manualSync);
                e.printStackTrace();
                return;
            }
        } else {
            deviceRegChange = 0;
        }

        // Set up the RPC sync calls
      

        // Set up the notes sync call.
        

        
    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                .build();
    }

    public static void clearSyncData(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        for (Account account : accounts) {
            final SharedPreferences syncMeta = context.getSharedPreferences(
                    "sync:" + account.name, 0);
            syncMeta.edit().clear().commit();
        }
    }

    private void logErrorMessage(final String message, boolean showToast) {
        Log.e(TAG, message);

        // Note: in general, showing any form of UI from a service is bad. showToast should only
        // be true if this is a manual sync, i.e. the user has just invoked some UI that indicates
        // she wants to perform a sync.
        Looper mainLooper = mContext.getMainLooper();
        if (mainLooper != null) {
            new Handler(mainLooper).post(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
