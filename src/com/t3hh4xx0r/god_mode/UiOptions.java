package com.t3hh4xx0r.god_mode;


import com.t3hh4xx0r.R;
import com.t3hh4xx0r.R.xml;

import com.t3hh4xx0r.god_mode.ColorChangedListener;
import com.t3hh4xx0r.god_mode.ColorPickerDialog;
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

	private static final String ELECTRON_BEAM_ANIMATION_ON = "electron_beam_animation_on";
	private static final String ELECTRON_BEAM_ANIMATION_OFF = "electron_beam_animation_off";
	private static final String BATTERY_OPTION = "battery_option";
	private static final String ENABLE_VOL_MUSIC_CONTROLS = "enable_vol_music_controls";
        private static final String UI_EXP_WIDGET = "expanded_widget";
        private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";
        private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";
        private static final String UI_EXP_WIDGET_ORDER = "widget_order";
        private static final String UI_EXP_WIDGET_PICKER = "widget_picker";
        private static final String OVERSCROLL_PREF = "pref_overscroll_effect";
        private static final String OVERSCROLL_WEIGHT_PREF = "pref_overscroll_weight";	
	private static final String STATUSBAR_HIDE_BATTERY = "statusbar_hide_battery";
	private static final String STATUSBAR_BATTERY_PERCENT = "statusbar_battery_percent";
	private static final String HIDE_ADB_ICON = "hide_adb_icon";
	private static final String STATUSBAR_CLOCK_OPT = "statusbar_clock_opt"; 
	private static final String HIDE_SIGNAL_ICON = "hide_signal_icon";
	private static final String STATUSBAR_HIDE_ALARM = "statusbar_hide_alarm";
	private static final String STATUSBAR_DATECLOCK = "statusbar_dateclock";
	private static final String STATUSBAR_CLOCK_COLOR = "statusbar_clock_color"; 
	private static final String BATTERY_TEXT_OPTIONS = "battery_text_options";
	private static final String STATUSBAR_CARRIER_TEXT = "statusbar_carrier_text";
        
        // Rotation preferences
        private static final String ROTATION_90_PREF = "pref_rotation_90";
		private static final String ROTATION_180_PREF = "pref_rotation_180";
		private static final String ROTATION_270_PREF = "pref_rotation_270";
		
		private CheckBoxPreference mRotation90Pref;
		private CheckBoxPreference mRotation180Pref;
		private CheckBoxPreference mRotation270Pref;

	private CheckBoxPreference mUseScreenOnAnim;
	private CheckBoxPreference mUseScreenOffAnim;
	private ListPreference mBatteryOption;
	private CheckBoxPreference mEnableVolMusicControls;
        private CheckBoxPreference mPowerWidget;
        private CheckBoxPreference mPowerWidgetHideOnChange;

        private ListPreference mOverscrollPref;
        private ListPreference mOverscrollWeightPref;

        private Preference mPowerWidgetColor;
        private PreferenceScreen mPowerPicker;
        private PreferenceScreen mPowerOrder;

	private ListPreference mClockStyle;
	private Preference mClockColor;
	private CheckBoxPreference mHideSignal;
	private CheckBoxPreference mHideBattery;
	private CheckBoxPreference mBatteryPercent;
	private CheckBoxPreference mHideAlarm;
	private ListPreference mDateClock;
	private CheckBoxPreference mHideAdb;
	private PreferenceScreen mBatteryTextOptions;
	private EditTextPreference mCarrierText;

	private int clockStyleVal;

	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ui_options);
		PreferenceScreen prefSet = getPreferenceScreen();

		mUseScreenOnAnim = (CheckBoxPreference)prefSet.findPreference(ELECTRON_BEAM_ANIMATION_ON);
		mUseScreenOnAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ELECTRON_BEAM_ANIMATION_ON, 1) == 1);
		mUseScreenOffAnim = (CheckBoxPreference)prefSet.findPreference(ELECTRON_BEAM_ANIMATION_OFF);
		mUseScreenOffAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ELECTRON_BEAM_ANIMATION_OFF, 1) == 1);			
		mBatteryOption = (ListPreference) prefSet.findPreference(BATTERY_OPTION);
		mBatteryOption.setOnPreferenceChangeListener(this);
		
		mEnableVolMusicControls = (CheckBoxPreference) prefSet.findPreference(ENABLE_VOL_MUSIC_CONTROLS);
		mEnableVolMusicControls.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ENABLE_VOL_MUSIC_CONTROLS, 0) == 1);

	        mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
	        mPowerPicker = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_PICKER);
	        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);

	        /* Overscroll Effect */
	        mOverscrollPref = (ListPreference) prefSet.findPreference(OVERSCROLL_PREF);
	        int overscrollEffect = Settings.System.getInt(getContentResolver(),
	                Settings.System.OVERSCROLL_EFFECT, 1);
	        mOverscrollPref.setValue(String.valueOf(overscrollEffect));
	        mOverscrollPref.setOnPreferenceChangeListener(this);
	        mOverscrollWeightPref = (ListPreference) prefSet.findPreference(OVERSCROLL_WEIGHT_PREF);
	        int overscrollWeight = Settings.System.getInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT, 5);
        	mOverscrollWeightPref.setValue(String.valueOf(overscrollWeight));
	        mOverscrollWeightPref.setOnPreferenceChangeListener(this);
	        
	        
	        
	        // Rotation
	        /* Rotation */
	        mRotation90Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_90_PREF);
			mRotation180Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_180_PREF);
			mRotation270Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_270_PREF);
			int mode = Settings.System.getInt(getContentResolver(),
			Settings.System.ACCELEROMETER_ROTATION_MODE, 5);
			mRotation90Pref.setChecked((mode & 1) != 0);
			mRotation180Pref.setChecked((mode & 2) != 0);
			mRotation270Pref.setChecked((mode & 4) != 0);
	        
			
		mClockStyle = (ListPreference) prefSet.findPreference(STATUSBAR_CLOCK_OPT);
		clockStyleVal = Settings.System.getInt(getContentResolver(),Settings.System.STATUSBAR_CLOCK_OPT, 2);
		mClockStyle.setValue(String.valueOf(clockStyleVal));
		mClockStyle.setOnPreferenceChangeListener(this);

		mHideSignal = (CheckBoxPreference) prefSet.findPreference(HIDE_SIGNAL_ICON);
		mHideSignal.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_SIGNAL_ICON, 0) == 1);

		mBatteryPercent = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_BATTERY_PERCENT);
		mBatteryPercent.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_BATTERY_PERCENT, 0) == 1);

		mHideBattery = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_HIDE_BATTERY);
		mHideBattery.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_BATTERY, 0) == 1);
				
		mHideAlarm = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_HIDE_ALARM);
		mHideAlarm.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_ALARM, 0) == 1);

		mHideAdb = (CheckBoxPreference) prefSet.findPreference(HIDE_ADB_ICON);
		mHideAdb.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_ADB_ICON, 0) == 1);

		mClockColor = (Preference) prefSet.findPreference(STATUSBAR_CLOCK_COLOR);

		mDateClock = (ListPreference) prefSet.findPreference(STATUSBAR_DATECLOCK);
		mDateClock.setOnPreferenceChangeListener(this);

		mBatteryTextOptions = (PreferenceScreen) prefSet.findPreference(BATTERY_TEXT_OPTIONS);

		mCarrierText = (EditTextPreference) prefSet.findPreference(STATUSBAR_CARRIER_TEXT);
		mCarrierText.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
		    Settings.System.putString(getContentResolver(),Settings.System.STATUSBAR_CARRIER_TEXT, (String) newValue);
		    ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		    am.forceStopPackage("com.android.phone");
		    return true;
	  }
      });
      updateStylePrefs(clockStyleVal);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        
        if (preference == mUseScreenOnAnim) {
        	value = mUseScreenOnAnim.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.ELECTRON_BEAM_ANIMATION_ON, value ? 1 : 0);
        }
        
        if (preference == mUseScreenOffAnim) {
        	value = mUseScreenOffAnim.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.ELECTRON_BEAM_ANIMATION_OFF, value ? 1 : 0);
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
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.EXPANDED_VIEW_WIDGET_COLOR),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.EXPANDED_VIEW_WIDGET_COLOR,
                            getResources().getColor(com.android.internal.R.color.white))); 
		cp.show();
	}
        if (preference == mRotation90Pref || preference == mRotation180Pref || preference == mRotation270Pref) {
        	int mode = 0;
				if (mRotation90Pref.isChecked())  mode |= 1;
				if (mRotation180Pref.isChecked()) mode |= 2;
				if (mRotation270Pref.isChecked()) mode |= 4;
				Settings.System.putInt(getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION_MODE, mode);
        }
	if (preference == mHideSignal) {
             Settings.System.putInt(getContentResolver(), Settings.System.HIDE_SIGNAL_ICON, mHideSignal.isChecked() ? 1 : 0);
	}
	if (preference == mBatteryPercent) {
             Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_BATTERY_PERCENT, mBatteryPercent.isChecked() ? 1 : 0);
	}
	if (preference == mHideBattery) {
             Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_BATTERY, mHideBattery.isChecked() ? 1 : 0);
	}
	if (preference == mHideAlarm) {
             Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_ALARM, mHideAlarm.isChecked() ? 1 : 0);
	}
	if (preference == mHideAdb) {
             Settings.System.putInt(getContentResolver(), Settings.System.HIDE_ADB_ICON, mHideAdb.isChecked() ? 1 : 0);
	}
        if (preference == mClockColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.STATUSBAR_CLOCK_COLOR),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.STATUSBAR_CLOCK_COLOR,
                            getResources().getColor(com.android.internal.R.color.white))); 
		cp.show();
	}
	if (preference == mBatteryTextOptions) {
            startActivity(mBatteryTextOptions.getIntent());
        }
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mBatteryOption) {
        	Settings.System.putInt(getContentResolver(), Settings.System.BATTERY_OPTION, Integer.valueOf((String) objValue));
        } else if (preference == mOverscrollPref) {
            int overscrollEffect = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.OVERSCROLL_EFFECT, overscrollEffect);
            return true;
        } else if (preference == mOverscrollWeightPref) {
            int overscrollWeight = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT, overscrollWeight);
            return true;
        } else if (preference == mDateClock) {
	    Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_DATECLOCK, Integer.valueOf((String) objValue));
	    return true;
	}else if (preference == mClockStyle) {
	    clockStyleVal = Integer.valueOf((String) objValue);
	    Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_CLOCK_OPT, clockStyleVal );
	    updateStylePrefs(clockStyleVal);
	    return true;
	}
	return false;
   }

    private void updateStylePrefs(int mClockStyle) {
	if(mClockStyle == 3) {
	  mClockColor.setEnabled(false);
	} else {
	  mClockColor.setEnabled(true);
	}
    }
}
