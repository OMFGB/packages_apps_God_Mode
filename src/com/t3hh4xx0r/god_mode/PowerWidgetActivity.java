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

package com.t3hh4xx0r.god_mode;

import com.android.internal.telephony.Phone;
import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Constants;
import com.t3hh4xx0r.god_mode.utils.PowerWidgetUtil;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PowerWidgetActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "PowerWidgetActivity";
	private boolean DBG = (false || Constants.FULL_DBG);

    private static final String BUTTONS_CATEGORY = "pref_buttons";
    private static final String SELECT_BUTTON_KEY_PREFIX = "pref_button_";

    private static final String EXP_BRIGHTNESS_MODE = "pref_brightness_mode";
    private static final String EXP_NETWORK_MODE = "pref_network_mode";
    private static final String EXP_SCREENTIMEOUT_MODE = "pref_screentimeout_mode";
    private static final String EXP_RING_MODE = "pref_ring_mode";
    private static final String EXP_FLASH_MODE = "pref_flash_mode";
    private static final String POWER_WIDGET_LOC = "pref_widget_loc";

    private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

    ListPreference mBrightnessMode;
    ListPreference mNetworkMode;
    ListPreference mScreentimeoutMode;
    ListPreference mRingMode;
    ListPreference mFlashMode;
    CheckBoxPreference mPowerWidgetLoc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_widget_buttons);
        addPreferencesFromResource(R.xml.power_widget);

        PreferenceScreen prefSet = getPreferenceScreen();

        mBrightnessMode = (ListPreference) prefSet.findPreference(EXP_BRIGHTNESS_MODE);
        mBrightnessMode.setOnPreferenceChangeListener(this);
        mNetworkMode = (ListPreference) prefSet.findPreference(EXP_NETWORK_MODE);
        mNetworkMode.setOnPreferenceChangeListener(this);
        mScreentimeoutMode = (ListPreference) prefSet.findPreference(EXP_SCREENTIMEOUT_MODE);
        mScreentimeoutMode.setOnPreferenceChangeListener(this);
        mRingMode = (ListPreference) prefSet.findPreference(EXP_RING_MODE);
        mRingMode.setOnPreferenceChangeListener(this);
        mFlashMode = (ListPreference) prefSet.findPreference(EXP_FLASH_MODE);
        mFlashMode.setOnPreferenceChangeListener(this);
        
        //power widget loc
        mPowerWidgetLoc = (CheckBoxPreference) prefSet.findPreference(POWER_WIDGET_LOC);
        int currentVal = Settings.System.getInt(getContentResolver(),
                Settings.System.EXPANDED_VIEW_WIDGET, 1);
        if (currentVal == 0) {
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET, 1);
        }

        if (currentVal == 1) {
            mPowerWidgetLoc.setChecked(false);
        } else if (currentVal == 2) {
            mPowerWidgetLoc.setChecked(true);
        }

        PreferenceCategory prefButtons = (PreferenceCategory) prefSet.findPreference(BUTTONS_CATEGORY);

        // empty our preference category and set it to order as added
        prefButtons.removeAll();
        prefButtons.setOrderingAsAdded(false);

        // emtpy our checkbox map
        mCheckBoxPrefs.clear();

        // get our list of buttons
        ArrayList<String> buttonList = PowerWidgetUtil.getButtonListFromString(PowerWidgetUtil.getCurrentButtons(this));

        // fill that checkbox map!
        for(PowerWidgetUtil.ButtonInfo button : PowerWidgetUtil.BUTTONS.values()) {
            // create a checkbox
            CheckBoxPreference cb = new CheckBoxPreference(this);

            // set a dynamic key based on button id
            cb.setKey(SELECT_BUTTON_KEY_PREFIX + button.getId());

            // set vanity info
            cb.setTitle(button.getTitleResId());

            // set our checked state
            if(buttonList.contains(button.getId())) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }

            // add to our prefs set
            mCheckBoxPrefs.put(cb, button.getId());

            // specific checks for availability on some platforms
            if (PowerWidgetUtil.BUTTON_FLASHLIGHT.equals(button.getId()) &&
                    !getResources().getBoolean(R.bool.has_led_flash)) { // disable flashlight if it's not supported
                cb.setEnabled(false);
                mFlashMode.setEnabled(false);
            } else if (PowerWidgetUtil.BUTTON_NETWORKMODE.equals(button.getId())) {
                // some phones run on networks not supported by this button, so disable it
                int network_state = -99;

                try {
                    network_state = Settings.Secure.getInt(getContentResolver(),
                            Settings.Secure.PREFERRED_NETWORK_MODE);
                } catch(Settings.SettingNotFoundException e) {
                    Log.e(TAG, "Unable to retrieve PREFERRED_NETWORK_MODE", e);
                }

                switch(network_state) {
                    // list of supported network modes
                    case Phone.NT_MODE_CDMA:
                    case Phone.NT_MODE_CDMA_AND_LTE_EVDO:
                    case Phone.NT_MODE_CDMA_NO_EVDO:
                    case Phone.NT_MODE_LTE_ONLY:
                        break;
                    default:
                        cb.setEnabled(false);
                        break;
                }
            }
            // add to the category
            prefButtons.addPreference(cb);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference == mPowerWidgetLoc) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            
            if(checked) {
                Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET, 2);
                
            } else {
                Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET, 1);
            }
            return true;
        } else {

            // we only modify the button list if it was one of our checks that
            // was clicked
            boolean buttonWasModified = false;
            ArrayList<String> buttonList = new ArrayList<String>();
            for (Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs.entrySet()) {
                if (entry.getKey().isChecked()) {
                    buttonList.add(entry.getValue());
                }

                if (preference == entry.getKey()) {
                    buttonWasModified = true;
                }
            }

            if (buttonWasModified) {
                // now we do some wizardry and reset the button list
                PowerWidgetUtil.saveCurrentButtons(
                        this,
                        PowerWidgetUtil.mergeInNewButtonString(
                                PowerWidgetUtil.getCurrentButtons(this),
                                PowerWidgetUtil.getButtonStringFromList(buttonList)));
                return true;
            }
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int value = Integer.valueOf((String)newValue);
        if(preference == mBrightnessMode) {
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_BRIGHTNESS_MODE, value);
        } else if(preference == mNetworkMode) {
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_NETWORK_MODE, value);
        } else if(preference == mScreentimeoutMode) {
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_SCREENTIMEOUT_MODE, value);
        } else if(preference == mRingMode) {
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_RING_MODE, value);
        } else if(preference == mFlashMode) {
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_FLASH_MODE, value);
        }
        return true;
    }
}
