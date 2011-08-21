package com.t3hh4xx0r.god_mode;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.addons.utils.Constants;

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

public class StatusBar extends PreferenceActivity implements OnPreferenceChangeListener {

    private boolean DBG = (false || Constants.FULL_DBG);
    
    private static final String CARRIER_CAP = "carrier_caption";
    private static final String BATTERY_OPTION = "battery_option";
    private static final String STATUSBAR_HIDE_BATTERY = "statusbar_hide_battery";
    private static final String STATUSBAR_BATTERY_PERCENT = "statusbar_battery_percent";
    private static final String STATUSBAR_CLOCK_OPT = "statusbar_clock_opt";
    private static final String HIDE_SIGNAL_ICON = "hide_signal_icon";
    private static final String HIDE_DATE = "hide_date";
    private static final String HIDE_CLOCK = "hide_clock";
    private static final String HIDE_WIFI = "hide_wifi";
    private static final String STATUSBAR_HIDE_ALARM = "statusbar_hide_alarm";
    private static final String STATUSBAR_DATECLOCK = "statusbar_dateclock";
    private static final String STATUSBAR_CLOCK_COLOR = "statusbar_clock_color"; 
    private static final String ADB_NOTIFY = "adb_notify";
    private static final String BATTERY_TEXT_OPTIONS = "battery_text_options";
    private static final String MIUI_BATTERY_COLOR = "miui_battery_color";
    private static final String UI_EXP_WIDGET = "expanded_widget";
    private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";
    private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";
    private static final String UI_EXP_WIDGET_ORDER = "widget_order";
    private static final String UI_EXP_WIDGET_PICKER = "widget_picker";

    private EditTextPreference mCarrierCaption;
    private ListPreference mBatteryOption;
    private CheckBoxPreference mPowerWidget;
    private CheckBoxPreference mAdbNotify;
    private CheckBoxPreference mPowerWidgetHideOnChange;
    private Preference mPowerWidgetColor;
    private PreferenceScreen mPowerPicker;
    private PreferenceScreen mPowerOrder;
	private ListPreference mClockStyle;
	private Preference mClockColor;
	private Preference mMiuiBatteryColor;
	private CheckBoxPreference mHideSignal;
	private CheckBoxPreference mHideBattery;
	private CheckBoxPreference mBatteryPercent;
	private CheckBoxPreference mHideAlarm;
        private CheckBoxPreference mHideDate;
        private CheckBoxPreference mHideWifi;
        private CheckBoxPreference mHideClock;
	private ListPreference mDateClock;
	private PreferenceScreen mBatteryTextOptions;
	private int clockStyleVal;
    private int dateStyleVal;
    private int batteryStyleVal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar);
        PreferenceScreen prefSet = getPreferenceScreen();

        /* Battery type */
        	mBatteryOption = (ListPreference) prefSet.findPreference(BATTERY_OPTION);
        	batteryStyleVal = Settings.System.getInt(getContentResolver(), Settings.System.BATTERY_OPTION, 1);
        	mBatteryOption.setValue(String.valueOf(batteryStyleVal));
		mBatteryOption.setOnPreferenceChangeListener(this);

        /* Clock style */
        	mClockStyle = (ListPreference) prefSet.findPreference(STATUSBAR_CLOCK_OPT);
		clockStyleVal = Settings.System.getInt(getContentResolver(),Settings.System.STATUSBAR_CLOCK_OPT, 0);
		mClockStyle.setValue(String.valueOf(clockStyleVal));
		mClockStyle.setOnPreferenceChangeListener(this);

        /* Show or hide the date */
                mHideDate = (CheckBoxPreference) prefSet.findPreference(HIDE_DATE);
                mHideDate.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_DATE, 0) == 1);

	/* Show or hide the wifi signal bars */
		mHideWifi = (CheckBoxPreference) prefSet.findPreference(HIDE_WIFI);
                mHideWifi.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_WIFI, 0) == 1);

        /* Show or hide the clock */
                mHideClock = (CheckBoxPreference) prefSet.findPreference(HIDE_CLOCK);
                mHideClock.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_CLOCK, 0) == 1);

        /* Show or hide signal icons */
        	mHideSignal = (CheckBoxPreference) prefSet.findPreference(HIDE_SIGNAL_ICON);
		mHideSignal.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_SIGNAL_ICON, 0) == 1);

        /* Show or hide battery percentages */
        	mBatteryPercent = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_BATTERY_PERCENT);
		mBatteryPercent.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_BATTERY_PERCENT, 0) == 1);

        /* Show or hide the battery icon */
		mHideBattery = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_HIDE_BATTERY);
		mHideBattery.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_BATTERY, 0) == 1);

        /* Show or hide the alarm icon */		
		mHideAlarm = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_HIDE_ALARM);
		mHideAlarm.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_ALARM, 0) == 1);

        /* Setting for clock color */
		mClockColor = (Preference) prefSet.findPreference(STATUSBAR_CLOCK_COLOR);

	/* Setting for hiding USB debugging icon */
		mAdbNotify = (CheckBoxPreference) findPreference(ADB_NOTIFY);
	        mAdbNotify.setChecked(Settings.Secure.getInt(getContentResolver(),Settings.Secure.ADB_NOTIFY, 0) == 1);

        /* Setting for miui battery color */
        	mMiuiBatteryColor = (Preference) prefSet.findPreference(MIUI_BATTERY_COLOR);            

        /* StatusBar date setting */
		mDateClock = (ListPreference) prefSet.findPreference(STATUSBAR_DATECLOCK);
        	dateStyleVal = Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_DATECLOCK, 1);
        	mDateClock.setValue(String.valueOf(dateStyleVal));
		mDateClock.setOnPreferenceChangeListener(this);

        /* Battery text setting */
        	mBatteryTextOptions = (PreferenceScreen) prefSet.findPreference(BATTERY_TEXT_OPTIONS);

        /* Carrier cap setting */
		mCarrierCaption = (EditTextPreference) prefSet.findPreference(CARRIER_CAP);
		mCarrierCaption.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
		    Settings.System.putString(getContentResolver(),Settings.System.CARRIER_CAP, (String) newValue);
		    ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		    am.forceStopPackage("com.android.phone");
		    return true;
	    }
        });
        updateStylePrefs();
        checkMiuiBattery(batteryStyleVal);

        /* Power widget settings */
        	mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
		mPowerPicker = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_PICKER);
	        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mHideSignal) {
             Settings.System.putInt(getContentResolver(), Settings.System.HIDE_SIGNAL_ICON, mHideSignal.isChecked() ? 1 : 0);
	    }
            if (preference == mHideWifi) {
             Settings.System.putInt(getContentResolver(), Settings.System.HIDE_WIFI, mHideWifi.isChecked() ? 0 : 1);
            }
            if (preference == mHideClock) {
             Settings.System.putInt(getContentResolver(), Settings.System.HIDE_CLOCK, mHideClock.isChecked() ? 0 : 1);
             updateStylePrefs();
            }
            if (preference == mHideDate) {
             Settings.System.putInt(getContentResolver(), Settings.System.HIDE_DATE, mHideDate.isChecked() ? 0 : 1);
             updateStylePrefs();
            }
            if (preference == mAdbNotify) {
		Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_NOTIFY, mAdbNotify.isChecked() ? 0 : 1);
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
        if (preference == mMiuiBatteryColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.MIUI_BATTERY_COLOR),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.MIUI_BATTERY_COLOR,
                            getResources().getColor(com.android.internal.R.color.android_green))); 
                cp.show();
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
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mBatteryOption) {
                batteryStyleVal = Integer.valueOf((String) objValue);
        	Settings.System.putInt(getContentResolver(), Settings.System.BATTERY_OPTION, Integer.valueOf((String) objValue));
		checkMiuiBattery(batteryStyleVal);
                return true;
            } else if (preference == mDateClock) {
                dateStyleVal = Integer.valueOf((String) objValue);
	        Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_DATECLOCK, Integer.valueOf((String) objValue));
                updateStylePrefs();
	        return true;
	    } else if (preference == mClockStyle) {
	        clockStyleVal = Integer.valueOf((String) objValue);
	        Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_CLOCK_OPT, clockStyleVal );
	        updateStylePrefs();
	        return true;
	    }
	    return false;
    }

    private void checkMiuiBattery(int mBatteryOption) {
        if (mBatteryOption == 2) {
                 mMiuiBatteryColor.setEnabled(true);
        } else {
                 mMiuiBatteryColor.setEnabled(false);
        }
    }

    private void updateStylePrefs() {
        if(mHideClock.isChecked()) {
            mClockColor.setEnabled(false);
            mClockStyle.setEnabled(false);
        } else {
            mClockColor.setEnabled(true);
            mClockStyle.setEnabled(true);

            }

        if(mHideDate.isChecked()) {
            mDateClock.setEnabled(false);
        } else {
            mDateClock.setEnabled(true);
	}
    }
}
        
    
