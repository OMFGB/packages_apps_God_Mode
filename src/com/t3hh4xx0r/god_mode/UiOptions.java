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

	private static final String USE_SCREENON_ANIM = "use_screenon_anim";
	private static final String USE_SCREENOFF_ANIM = "use_screenoff_anim";
	private static final String BATTERY_OPTION = "battery_option";
	private static final String ENABLE_VOL_MUSIC_CONTROLS = "enable_vol_music_controls";
        private static final String UI_EXP_WIDGET = "expanded_widget";
        private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";
        private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";
        private static final String UI_EXP_WIDGET_ORDER = "widget_order";
        private static final String UI_EXP_WIDGET_PICKER = "widget_picker";
	
	private CheckBoxPreference mUseScreenOnAnim;
	private CheckBoxPreference mUseScreenOffAnim;
	private ListPreference mBatteryOption;
	private CheckBoxPreference mEnableVolMusicControls;
        private CheckBoxPreference mPowerWidget;
        private CheckBoxPreference mPowerWidgetHideOnChange;

        private Preference mPowerWidgetColor;
        private PreferenceScreen mPowerPicker;
        private PreferenceScreen mPowerOrder;


	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ui_options);
		PreferenceScreen prefSet = getPreferenceScreen();

		mUseScreenOnAnim = (CheckBoxPreference)prefSet.findPreference(USE_SCREENON_ANIM);
		mUseScreenOnAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.USE_SCREENON_ANIM, 1) == 1);
		mUseScreenOffAnim = (CheckBoxPreference)prefSet.findPreference(USE_SCREENOFF_ANIM);
		mUseScreenOffAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.USE_SCREENOFF_ANIM, 1) == 1);		
		
		mBatteryOption = (ListPreference) prefSet.findPreference(BATTERY_OPTION);
		mBatteryOption.setOnPreferenceChangeListener(this);
		mEnableVolMusicControls = (CheckBoxPreference) prefSet.findPreference(ENABLE_VOL_MUSIC_CONTROLS);
		mEnableVolMusicControls.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ENABLE_VOL_MUSIC_CONTROLS, 1) == 1);

	        mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
	        mPowerPicker = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_PICKER);
	        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);

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

	if (preference == mEnableVolMusicControls) {
		value = mEnableVolMusicControls.isChecked();
	    Settings.System.putInt(getContentResolver(), Settings.System.ENABLE_VOL_MUSIC_CONTROLS, value ? 1 : 0);
	}

        if (preference == mPowerPicker) {
            startActivity(mPowerPicker.getIntent());
        }

        if (preference == mPowerOrder) {
            startActivity(mPowerOrder.getIntent());
        }

        if (preference == mPowerWidgetColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this, mWidgetColorListener,
                    readWidgetColor());
            cp.show();
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
            return Settings.System.getInt(getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET_COLOR);
        } catch (SettingNotFoundException e) {
            return -16777216;
        }
    }

    ColorPickerDialog.OnColorChangedListener mWidgetColorListener = new ColorPickerDialog.OnColorChangedListener() {
        public void colorChanged(int color) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET_COLOR, color);
        }

        public void colorUpdate(int color) {
        }
    };
}
