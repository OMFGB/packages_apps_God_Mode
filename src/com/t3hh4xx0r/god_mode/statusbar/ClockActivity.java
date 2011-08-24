
package com.t3hh4xx0r.god_mode.statusbar;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.god_mode.ColorPickerDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class ClockActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    String pref;

    Context mContext;

    Dialog d;

    AlertDialog.Builder builder;

    private static final String PREF_CLOCK_DISPLAY_STYLE = "clock_am_pm";

    private static final String PREF_CLOCK_STYLE = "clock_style";

    private static final String PREF_CLOCK_COLOR = "clock_color_pref";

    ListPreference mAmPmStyle;

    ListPreference mClockStyle;

    Preference mColorPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        addPreferencesFromResource(R.xml.clock_prefs);
        PreferenceScreen prefs = getPreferenceScreen();

        mClockStyle = (ListPreference) prefs.findPreference(PREF_CLOCK_STYLE);
        mAmPmStyle = (ListPreference) prefs.findPreference(PREF_CLOCK_DISPLAY_STYLE);
        mColorPref = prefs.findPreference(PREF_CLOCK_COLOR);

        int styleValue = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM, 2);
        mAmPmStyle.setValueIndex(styleValue);
        mAmPmStyle.setOnPreferenceChangeListener(this);

        int clockVal = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1);
        mClockStyle.setValueIndex(clockVal);
        mClockStyle.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
        if (preference == mColorPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this, mColorChangeListener,
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.STATUS_BAR_CLOCK_COLOR, Color.WHITE));
            cp.show();
        }

        return false;
    }

    ColorPickerDialog.OnColorChangedListener mColorChangeListener = new ColorPickerDialog.OnColorChangedListener() {

        @Override
        public void colorChanged(int color) {
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CLOCK_COLOR,
                    color);

        }
    };

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAmPmStyle) {
            int statusBarAmPm = Integer.valueOf((String) newValue);

            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_AM_PM,
                    statusBarAmPm);
            return true;

        } else if (preference == mClockStyle) {
            int val = Integer.valueOf((String) newValue);

            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CLOCK, val);

            return true;
        }

        return false;
    }
}
