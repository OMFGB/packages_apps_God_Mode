package com.t3hh4xx0r.god_mode;


import com.t3hh4xx0r.god_mode.R;
import com.t3hh4xx0r.god_mode.R.xml;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;
import android.util.Log;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;


public class UiOptions extends PreferenceActivity implements OnPreferenceChangeListener {

	private static final String USE_SCREENOFF_ANIM = "use_screenoff_anim";
	private static final String USE_SCREENON_ANIM = "use_screenon_anim";
	private static final String BATTERY_OPTION = "battery_option";

        private static final String UI_EXP_WIDGET = "expanded_widget";
        private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";
        private static final String UI_EXP_WIDGET_PICKER = "widget_picker";
	
	private CheckBoxPreference mUseScreenOnAnim;
	private CheckBoxPreference mUseScreenOffAnim;
	private ListPreference mBatteryOption;
	
        private CheckBoxPreference mPowerWidget;
        private Preference mPowerWidgetColor;
        private PreferenceScreen mPowerPicker;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ui_options);
		PreferenceScreen prefSet = getPreferenceScreen();

		mUseScreenOnAnim = (CheckBoxPreference)prefSet.findPreference(USE_SCREENON_ANIM);
		mUseScreenOnAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.USE_SCREENON_ANIM, 1) == 0);
		mUseScreenOffAnim = (CheckBoxPreference)prefSet.findPreference(USE_SCREENOFF_ANIM);
		mUseScreenOffAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.USE_SCREENOFF_ANIM, 1) == 1);		
		
		mBatteryOption = (ListPreference) prefSet.findPreference(BATTERY_OPTION);
		mBatteryOption.setOnPreferenceChangeListener(this);
/* Expanded View Power Widget */
                mPowerWidget = (CheckBoxPreference) prefSet.findPreference(UI_EXP_WIDGET);
      	        mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
                mPowerPicker = (PreferenceScreen)prefSet.findPreference(UI_EXP_WIDGET_PICKER);
		mPowerWidget.setChecked((Settings.System.getInt(getContentResolver(),
		Settings.System.EXPANDED_VIEW_WIDGET, 1) == 1));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        
        if (preference == mUseScreenOnAnim) {
        	value = mUseScreenOnAnim.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.USE_SCREENON_ANIM, value ? 1 : 0);
        }
        
        if (preference == mUseScreenOffAnim) {
        	value = mUseScreenOffAnim.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.USE_SCREENOFF_ANIM, value ? 1 : 0);
        }

        if(preference == mPowerPicker) {
            startActivity(mPowerPicker.getIntent());
        }

	if(preference == mPowerWidget) {
            value = mPowerWidget.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET, value ? 1 : 0);
	}
 
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mBatteryOption) {;
        	Settings.System.putInt(getContentResolver(), Settings.System.BATTERY_OPTION, Integer.valueOf((String) objValue));
        }

        return true;
    }
    
 private int readWidgetColor() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET_COLOR);
        }
        catch (SettingNotFoundException e) {
            return -16777216;
        }
    }
}