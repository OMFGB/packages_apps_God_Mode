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

import com.example.jumpnote.android.jsonrpc.AuthenticatedJsonRpcJavaClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity shows a list of JumpNote-syncable accounts. Eligible accounts include Google
 * accounts that have the 'ah' (app hosting) service bit set.
 */
public class AccountList extends ListActivity implements OnAccountsUpdateListener,
        SyncStatusObserver {
    private static final String TAG = Config.makeLogTag(AccountList.class);

    public static final int FLAG_SYNC_ACTIVE = 0x1;
    public static final int FLAG_SYNC_PENDING = 0x2;
    public static final int FLAG_SYNC_DISABLED = 0x4;
    public static final int FLAG_SYNC_ALL = FLAG_SYNC_ACTIVE | FLAG_SYNC_PENDING |
            FLAG_SYNC_DISABLED;

    // Menu item ids
    public static final int MENU_ACCOUNT_SETTINGS = Menu.FIRST;

    private AccountManager mAccountManager;
    private List<AccountListItem> mAccountListData;
    private AccountListAdapter mAccountListAdapter;

    private Object mSyncObserverHandle;

    private ContentObserver mNotesObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.account_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.account_list_title);

        mAccountManager = AccountManager.get(this);

        // Display the account list
        mAccountListData = new ArrayList<AccountListItem>();
        mAccountListAdapter = new AccountListAdapter(AccountList.this,
                mAccountListData);
        setListAdapter(mAccountListAdapter);

        // Will fire once immediately as an initialization step.
        mAccountManager.addOnAccountsUpdatedListener(this, null, true);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                AccountList.this.openNotesForAccount(
                        (Account) mAccountListData.get(position).account, false);
            }
        });
    }

    public void onAccountsUpdated(Account[] accounts) {
        updateAccountsList();
    }

    private void updateAccountsList() {
        final Account[] allGoogleAccounts = mAccountManager.getAccountsByType(
                SyncAdapter.GOOGLE_ACCOUNT_TYPE);
        mAccountManager.getAccountsByTypeAndFeatures(
                SyncAdapter.GOOGLE_ACCOUNT_TYPE,
                SyncAdapter.GOOGLE_ACCOUNT_REQUIRED_SYNCABILITY_FEATURES,
                new AccountManagerCallback<Account[]>() {
                    public void run(AccountManagerFuture<Account[]> syncableAccountsFuture) {
                        mAccountListData.clear();

                        // Local notes fake account
                        mAccountListData.add(new AccountListItem(null, 0));

                        try {
                            for (Account account : syncableAccountsFuture.getResult()) {
                                mAccountListData.add(new AccountListItem(account, 0));
                            }
                        } catch (AuthenticatorException e) {
                            Toast.makeText(AccountList.this,
                                    "Authenticator error, can't retrieve accounts",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "AuthenticatorException while accessing account list.");
                            e.printStackTrace();
                        } catch (IOException e) {
                            Toast.makeText(AccountList.this,
                                    "IO error, can't retrieve accounts",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "IOException while accessing account list.");
                            e.printStackTrace();
                        } catch (OperationCanceledException e) {
                            Log.i(TAG, "Access of accounts list canceled.");
                            e.printStackTrace();
                        }

                        // Notify the sync system about account syncability.
                        for (int i = 0; i < allGoogleAccounts.length; i++) {
                            boolean syncable = false;
                            for (int j = 1; j < mAccountListData.size(); j++) {
                                if (allGoogleAccounts[i].equals(mAccountListData.get(j).account)) {
                                    syncable = true;
                                    break;
                                }
                            }

                            ContentResolver.setIsSyncable(allGoogleAccounts[i],
                                    JumpNoteContract.AUTHORITY, syncable ? 1 : 0);
                        }

                        updateSyncStatuses();
                        mAccountListAdapter.notifyDataSetChanged();
                    }
                }, null);
    }

    @Override
    protected void onResume() {
        updateSyncStatuses();
        postNotesChanged();

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(
                mask, this);

        // Watch for notes changes
        mNotesObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                postNotesChanged();
            }
        };
        getContentResolver().registerContentObserver(JumpNoteContract.ROOT_URI, true,
                mNotesObserver);

        getListView().requestFocus();

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mNotesObserver != null) {
            getContentResolver().unregisterContentObserver(mNotesObserver);
            mNotesObserver = null;
        }

        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void postNotesChanged() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mAccountListAdapter != null)
                    mAccountListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Called when observed sync statuses change.
     */
    public void onStatusChanged(int which) {
        updateSyncStatuses();
    }

    private void updateSyncStatuses() {
        if (mAccountListData == null)
            return;

        boolean dirty = false;
        for (int i = 1; i < mAccountListData.size(); i++) {
            AccountListItem item = mAccountListData.get(i);
            int oldFlags = item.flags;
            item.flags &= ~FLAG_SYNC_ALL;

            if (!ContentResolver.getMasterSyncAutomatically() ||
                !ContentResolver.getSyncAutomatically(item.account, JumpNoteContract.AUTHORITY))
                item.flags |= FLAG_SYNC_DISABLED;

            if (ContentResolver.isSyncPending(item.account, JumpNoteContract.AUTHORITY))
                item.flags |= FLAG_SYNC_PENDING;
            else if (ContentResolver.isSyncActive(item.account, JumpNoteContract.AUTHORITY))
                item.flags |= FLAG_SYNC_ACTIVE;

            if (item.flags != oldFlags)
                dirty = true;
        }

        if (dirty)
            postNotesChanged();
    }

    public void openNotesForAccount(final Account account, final boolean bypassAuthCheck) {
        if (account == null || bypassAuthCheck) {
            Uri noteListUri = JumpNoteContract.buildNoteListUri(
                    account == null ? null : account.name);
            Intent intent = new Intent(Intent.ACTION_EDIT, noteListUri);
            startActivity(intent);
        } else {
            AuthenticatedJsonRpcJavaClient.ensureHasTokenWithUI(
                    this, account, new AuthenticatedJsonRpcJavaClient.EnsureHasTokenWithUICallback() {
               public void onHasToken(String authToken) {
                   openNotesForAccount(account, true);
               }

               public void onAuthDenied() {
               }

               public void onError(Throwable e) {
                   Toast.makeText(AccountList.this, "Auth error: " + e.getMessage(),
                           Toast.LENGTH_SHORT).show();
                   e.printStackTrace();
               }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ACCOUNT_SETTINGS, 0, R.string.menu_account_settings).setIcon(
                R.drawable.ic_menu_preferences);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ACCOUNT_SETTINGS:
                Intent intent = new Intent();
                intent.setAction("android.settings.SYNC_SETTINGS");
                intent.putExtra("authorities", new String[] { JumpNoteContract.AUTHORITY });
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AccountListItem {
        public Account account;
        public int flags;
        public Cursor noteCursor;

        public AccountListItem(Account account, int flags) {
            this.account = account;
            this.flags = flags;

            this.noteCursor = managedQuery(
                    JumpNoteContract.buildNoteListUri(account == null ? null : account.name),
                    new String[] { JumpNoteContract.Notes._ID },
                    JumpNoteContract.Notes.PENDING_DELETE + " = 0",
                    null, JumpNoteContract.Notes.DEFAULT_SORT_ORDER);
        }
    }

    private static class AccountListAdapter extends ArrayAdapter<AccountListItem> {
        private static final int mResource = R.layout.account_list_item;
        protected LayoutInflater mInflater;
        private String mLocalNotesLabel;
        private String mSyncDisabledLabel;
        private String mNoteCountTemplate;

        public AccountListAdapter(Context context, List<AccountListItem> items) {
            super(context, mResource, items);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLocalNotesLabel = context.getString(R.string.local_notes);
            mSyncDisabledLabel = context.getString(R.string.account_list_status_sync_disabled);
            mNoteCountTemplate = context.getString(R.string.account_list_status_notes);
        }

        static class ViewHolder {
            TextView text1;
            TextView text2;
            ImageView syncIndicator;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(mResource, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
                holder.syncIndicator = (ImageView) convertView.findViewById(R.id.sync_indicator);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final AccountListItem item = getItem(position);

            if (item.account != null)
                holder.text1.setText(getItem(position).account.name);
            else
                holder.text1.setText("(" + mLocalNotesLabel + ")");

            StringBuilder statusBuilder = new StringBuilder();
            Cursor noteCursor = getItem(position).noteCursor;
            noteCursor.requery();
            statusBuilder.append(String.format(mNoteCountTemplate, noteCursor.getCount()));

            if (Config.ENABLE_SYNC_UI && (item.flags & FLAG_SYNC_PENDING) != 0) {
                holder.syncIndicator.setVisibility(View.VISIBLE);
                holder.syncIndicator.setImageResource(R.drawable.ic_list_sync_anim0);
            } else if (Config.ENABLE_SYNC_UI && (item.flags & FLAG_SYNC_ACTIVE) != 0) {
                final AnimationDrawable syncingDrawable = (AnimationDrawable)
                        getContext().getResources().getDrawable(R.drawable.ic_list_sync_anim);
                holder.syncIndicator.setVisibility(View.VISIBLE);
                holder.syncIndicator.setImageDrawable(syncingDrawable);
                holder.syncIndicator.post(new Runnable() {
                    public void run() {
                        syncingDrawable.start();
                    }
                });
            } else {
                holder.syncIndicator.setVisibility(View.GONE);
            }

            if ((item.flags & FLAG_SYNC_DISABLED) != 0)
                statusBuilder.append(", " + mSyncDisabledLabel);

            holder.text2.setText(statusBuilder.toString());
            return convertView;
        }
    }
}
