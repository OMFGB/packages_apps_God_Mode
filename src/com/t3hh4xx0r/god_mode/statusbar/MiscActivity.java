
package com.t3hh4xx0r.god_mode.statusbar;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.god_mode.ColorChangedListener;
import com.t3hh4xx0r.god_mode.ColorPickerDialog;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class MiscActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String CARRIER_CAP = "carrier_caption";

    private static final String HIDE_DATE = "hide_date";

    private static final String HIDE_BLUETOOTH = "hide_bluetooth";

    private static final String HIDE_WIFI = "hide_wifi";

    private static final String HIDE_DATA = "hide_data";

    private static final String HIDE_SYNC = "hide_sync";

    private static final String STATUSBAR_HIDE_ALARM = "statusbar_hide_alarm";

    private static final String STATUSBAR_DATECLOCK = "statusbar_dateclock";

    private static final String ADB_NOTIFY = "adb_notify";

    private static final String UI_EXP_WIDGET = "expanded_widget";

    private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";

    private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";

    private static final String UI_EXP_WIDGET_ORDER = "widget_order";

    private static final String UI_EXP_WIDGET_PICKER = "widget_picker";

    private EditTextPreference mCarrierCaption;

    private CheckBoxPreference mPowerWidget;

    private CheckBoxPreference mAdbNotify;

    private CheckBoxPreference mPowerWidgetHideOnChange;

    private Preference mPowerWidgetColor;

    private PreferenceScreen mPowerPicker;

    private PreferenceScreen mPowerOrder;

    private CheckBoxPreference mHideAlarm;

    private CheckBoxPreference mHideDate;

    private CheckBoxPreference mHideWifi;

    private CheckBoxPreference mHideData;

    private CheckBoxPreference mHideSync;

    private CheckBoxPreference mHideBluetooth;

    private ListPreference mDateClock;

    private int dateStyleVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_misc_tweaks);
        PreferenceScreen prefSet = getPreferenceScreen();

        /* Show or hide the date */
        mHideDate = (CheckBoxPreference) prefSet.findPreference(HIDE_DATE);
        mHideDate.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.HIDE_DATE, 0) == 1);

        /* Show or hide the wifi signal bars */
        mHideWifi = (CheckBoxPreference) prefSet.findPreference(HIDE_WIFI);
        mHideWifi.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.HIDE_WIFI, 0) == 1);


        /* Show or hide the sync icon */
                mHideSync = (CheckBoxPreference) prefSet.findPreference(HIDE_SYNC);
                mHideSync.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_SYNC, 0) == 1);

        /* Show or hide the data icon */
                mHideData = (CheckBoxPreference) prefSet.findPreference(HIDE_DATA);
                mHideData.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIDE_DATA, 0) == 1);

        /* Show or hide the BT icon */
        mHideBluetooth = (CheckBoxPreference) prefSet.findPreference(HIDE_BLUETOOTH);
        mHideBluetooth.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.HIDE_BLUETOOTH, 0) == 1);

        /* Show or hide the alarm icon */
        mHideAlarm = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_HIDE_ALARM);
        mHideAlarm.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_HIDE_ALARM, 0) == 1);

        /* Setting for hiding USB debugging icon */
        mAdbNotify = (CheckBoxPreference) findPreference(ADB_NOTIFY);
        mAdbNotify.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.ADB_NOTIFY, 0) == 1);

        /* StatusBar date setting */
        mDateClock = (ListPreference) prefSet.findPreference(STATUSBAR_DATECLOCK);
        dateStyleVal = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_DATECLOCK, 1);
        mDateClock.setValue(String.valueOf(dateStyleVal));
        mDateClock.setOnPreferenceChangeListener(this);

        /* Carrier cap setting */
        mCarrierCaption = (EditTextPreference) prefSet.findPreference(CARRIER_CAP);
        mCarrierCaption.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putString(getContentResolver(), Settings.System.CARRIER_CAP,
                        (String) newValue);
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                am.forceStopPackage("com.android.phone");
                return true;
            }
        });
        updateStylePrefs();

        /* Power widget settings */
        mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
        mPowerPicker = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_PICKER);
        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mHideBluetooth) {
            Settings.System.putInt(getContentResolver(), Settings.System.HIDE_BLUETOOTH,
                    mHideBluetooth.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mHideWifi) {
            Settings.System.putInt(getContentResolver(), Settings.System.HIDE_WIFI,
                    mHideWifi.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mHideSync) {
            Settings.System.putInt(getContentResolver(), Settings.System.HIDE_SYNC, 
		    mHideSync.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mHideData) {
            Settings.System.putInt(getContentResolver(), Settings.System.HIDE_DATA, 
	    	    mHideData.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mHideDate) {
            Settings.System.putInt(getContentResolver(), Settings.System.HIDE_DATE,
                    mHideDate.isChecked() ? 0 : 1);
            updateStylePrefs();
            return true;
        } else if (preference == mAdbNotify) {
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_NOTIFY,
                    mAdbNotify.isChecked() ? 0 : 1);
            return true;
        } else if (preference == mHideAlarm) {
            Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_HIDE_ALARM,
                    mHideAlarm.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mPowerPicker) {
            startActivity(mPowerPicker.getIntent());
            return true;
        } else if (preference == mPowerOrder) {
            startActivity(mPowerOrder.getIntent());
            return true;
        } else if (preference == mPowerWidgetColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this, new ColorChangedListener(this,
                    Settings.System.EXPANDED_VIEW_WIDGET_COLOR), Settings.System.getInt(
                    getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET_COLOR,
                    getResources().getColor(com.android.internal.R.color.white)));
            cp.show();
            return true;
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mDateClock) {
            dateStyleVal = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_DATECLOCK,
                    Integer.valueOf((String) objValue));
            updateStylePrefs();
            return true;
        }
        return false;
    }

    private void updateStylePrefs() {

        if (mHideDate.isChecked()) {
            mDateClock.setEnabled(false);
        } else {
            mDateClock.setEnabled(true);
        }
    }
}
