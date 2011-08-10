/* //device/apps/Settings/src/com/android/settings/Keyguard.java
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.t3hh4xx0r.god_mode;

import com.t3hh4xx0r.god_mode.ColorChangedListener;
import com.t3hh4xx0r.god_mode.ColorPickerDialog;
import com.t3hh4xx0r.R;
import android.app.ActivityManagerNative;
import android.content.Intent.ShortcutIconResource;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.EditTextPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.IWindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

public class Battery extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "Battery";

	private static final String BATTERY_TEXT_COLOR = "battery_text_color";
	private static final String BATTERY_COLOR_AUTO_CHARGING = "battery_color_auto_charging"; 
	private static final String BATTERY_COLOR_AUTO_REGULAR = "battery_color_auto_regular";
	private static final String BATTERY_COLOR_AUTO_MEDIUM = "battery_color_auto_medium";
	private static final String BATTERY_COLOR_AUTO_LOW = "battery_color_auto_low";
	private static final String BATTERY_COLOR = "battery_color"; 

    private final Configuration mCurConfig = new Configuration();
    
	private CheckBoxPreference mTextColor;
	private Preference mAutoCharging;
	private Preference mAutoRegular;
	private Preference mAutoMedium;
	private Preference mAutoLow;
	private Preference mColor;

	private IWindowManager mWindowManager;
	
	private int mKeyNumber = 1;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
	    addPreferencesFromResource(R.xml.batterytext);
        PreferenceScreen prefSet = getPreferenceScreen();

		mTextColor = (CheckBoxPreference) prefSet.findPreference(BATTERY_TEXT_COLOR);
        mTextColor.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.BATTERY_TEXT_COLOR, 0) == 1);
		mAutoCharging = (Preference) prefSet.findPreference(BATTERY_COLOR_AUTO_CHARGING);
		mAutoRegular = (Preference) prefSet.findPreference(BATTERY_COLOR_AUTO_REGULAR);
		mAutoMedium = (Preference) prefSet.findPreference(BATTERY_COLOR_AUTO_MEDIUM);
		mAutoLow = (Preference) prefSet.findPreference(BATTERY_COLOR_AUTO_LOW);
		mColor = (Preference) prefSet.findPreference(BATTERY_COLOR);

        mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    }

    private void updateToggles() {
    }
    
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mTextColor) {
            int battColor = Integer.valueOf((String) objValue);
            android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.BATTERY_TEXT_COLOR,
                    battColor);
        }
        // always let the preference setting proceed.
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	    if (preference == mAutoCharging) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.BATTERY_COLOR_AUTO_CHARGING),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.BATTERY_COLOR_AUTO_CHARGING,
                            getResources().getColor(com.android.internal.R.color.white))); 
		    cp.show();
		    return true;
	    }else if (preference == mAutoMedium) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.BATTERY_COLOR_AUTO_MEDIUM),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.BATTERY_COLOR_AUTO_MEDIUM,
                            getResources().getColor(com.android.internal.R.color.white))); 
		    cp.show();
		    return true;
	    }else if (preference == mAutoRegular) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.BATTERY_COLOR_AUTO_REGULAR),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.BATTERY_COLOR_AUTO_REGULAR,
                            getResources().getColor(com.android.internal.R.color.white))); 
		    cp.show();
		    return true;
	    }else if (preference == mAutoLow) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.BATTERY_COLOR_AUTO_LOW),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.BATTERY_COLOR_AUTO_LOW,
                            getResources().getColor(com.android.internal.R.color.white))); 
		    cp.show();
		    return true;
	    }else if (preference == mColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.BATTERY_COLOR),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.BATTERY_COLOR,
                            getResources().getColor(com.android.internal.R.color.white))); 
		    cp.show();
		    return true;
	    } else if (preference == mTextColor) {
             Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_TEXT_COLOR,
					mTextColor.isChecked() ? 1 : 0);
            return true;
	    }
        return false;
    }
}   
