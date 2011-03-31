/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.utils.PowerWidgetUtil;
import com.cyanogenmod.cmparts.widgets.TouchInterceptor;

import android.app.ListActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PowerWidgetOrderActivity extends ListActivity
{
    private static final String TAG = "PowerWidgetOrderActivity";

    private ListView mButtonList;
    private ButtonAdapter mButtonAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.order_power_widget_buttons_activity);

        mButtonList = getListView();
        ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
        mButtonAdapter = new ButtonAdapter(this);
        setListAdapter(mButtonAdapter);
    }

    @Override
    public void onDestroy() {
        ((TouchInterceptor) mButtonList).setDropListener(null);
        setListAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // reload our buttons and invalidate the views for redraw
        mButtonAdapter.reloadButtons();
        mButtonList.invalidateViews();
    }

    private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
                // get the current button list
                ArrayList<String> buttons = PowerWidgetUtil.getButtonListFromString(
                        PowerWidgetUtil.getCurrentButtons(PowerWidgetOrderActivity.this));

                // move the button
                if(from < buttons.size()) {
                    String button = buttons.remove(from);

                    if(to <= buttons.size()) {
                        buttons.add(to, button);

                        // save our buttons
                        PowerWidgetUtil.saveCurrentButtons(PowerWidgetOrderActivity.this,
                                PowerWidgetUtil.getButtonStringFromList(buttons));

                        // tell our adapter/listview to reload
                        mButtonAdapter.reloadButtons();
                        mButtonList.invalidateViews();
                    }
                }
            }
        };

    private class ButtonAdapter extends BaseAdapter {
        private Context mContext;
        private Resources mSystemUIResources = null;
        private LayoutInflater mInflater;
        private ArrayList<PowerWidgetUtil.ButtonInfo> mButtons;

        public ButtonAdapter(Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);

            PackageManager pm = mContext.getPackageManager();
            if(pm != null) {
                try {
                    mSystemUIResources = pm.getResourcesForApplication("com.android.systemui");
                } catch(Exception e) {
                    mSystemUIResources = null;
                    Log.e(TAG, "Could not load SystemUI resources", e);
                }
            }

            reloadButtons();
        }

        public void reloadButtons() {
            ArrayList<String> buttons = PowerWidgetUtil.getButtonListFromString(
                    PowerWidgetUtil.getCurrentButtons(mContext));

            mButtons = new ArrayList<PowerWidgetUtil.ButtonInfo>();
            for(String button : buttons) {
                if(PowerWidgetUtil.BUTTONS.containsKey(button)) {
                    mButtons.add(PowerWidgetUtil.BUTTONS.get(button));
                }
            }
        }

        public int getCount() {
            return mButtons.size();
        }

        public Object getItem(int position) {
            return mButtons.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final View v;
            if(convertView == null) {
                v = mInflater.inflate(R.layout.order_power_widget_button_list_item, null);
            } else {
                v = convertView;
            }

            PowerWidgetUtil.ButtonInfo button = mButtons.get(position);

            final TextView name = (TextView)v.findViewById(R.id.name);
            final ImageView icon = (ImageView)v.findViewById(R.id.icon);

            name.setText(button.getTitleResId());

            // assume no icon first
            icon.setVisibility(View.GONE);

            // attempt to load the icon for this button
            if(mSystemUIResources != null) {
                int resId = mSystemUIResources.getIdentifier(button.getIcon(), null, null);
                if(resId > 0) {
                    try {
                        Drawable d = mSystemUIResources.getDrawable(resId);
                        icon.setVisibility(View.VISIBLE);
                        icon.setImageDrawable(d);
                    } catch(Exception e) {
                        Log.e(TAG, "Error retrieving icon drawable", e);
                    }
                }
            }

            return v;
        }
    }
}

