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

package com.example.jumpnote.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMessaging;

/**
 * Broadcast receiver that handles Android Cloud to Data Messaging (AC2DM) messages, initiated
 * by the JumpNote App Engine server and routed/delivered by Google AC2DM servers. The
 * only currently defined message is 'sync'.
 */
public class C2DMReceiver extends C2DMBaseReceiver {
    static final String TAG = Config.makeLogTag(C2DMReceiver.class);

    public C2DMReceiver() {
        super(Config.C2DM_SENDER);
    }

    @Override
    public void onError(Context context, String errorId) {
        Toast.makeText(context, "Messaging registration error: " + errorId,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String accountName = intent.getExtras().getString(Config.C2DM_ACCOUNT_EXTRA);
        String message = intent.getExtras().getString(Config.C2DM_MESSAGE_EXTRA);
        if (Config.C2DM_MESSAGE_SYNC.equals(message)) {
            if (accountName != null) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Messaging request received for account " + accountName);
                }

                ContentResolver.requestSync(
                        new Account(accountName, SyncAdapter.GOOGLE_ACCOUNT_TYPE),
                        JumpNoteContract.AUTHORITY, new Bundle());
            }
        }
    }

    /**
     * Register or unregister based on phone sync settings.
     * Called on each performSync by the SyncAdapter.
     */
    public static void refreshAppC2DMRegistrationState(Context context) {
        // Determine if there are any auto-syncable accounts. If there are, make sure we are
        // registered with the C2DM servers. If not, unregister the application.
        boolean autoSyncDesired = false;
        if (ContentResolver.getMasterSyncAutomatically()) {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccountsByType(SyncAdapter.GOOGLE_ACCOUNT_TYPE);
            for (Account account : accounts) {
                if (ContentResolver.getIsSyncable(account, JumpNoteContract.AUTHORITY) > 0 &&
                        ContentResolver.getSyncAutomatically(account, JumpNoteContract.AUTHORITY)) {
                    autoSyncDesired = true;
                    break;
                }
            }
        }

        boolean autoSyncEnabled = !C2DMessaging.getRegistrationId(context).equals("");

        if (autoSyncEnabled != autoSyncDesired) {
            Log.i(TAG, "System-wide desirability for JumpNote auto sync has changed; " +
                    (autoSyncDesired ? "registering" : "unregistering") +
                    " application with C2DM servers.");

            if (autoSyncDesired == true) {
                C2DMessaging.register(context, Config.C2DM_SENDER);
            } else {
                C2DMessaging.unregister(context);
            }
        }
    }
}
