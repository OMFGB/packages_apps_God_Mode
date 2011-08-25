
package com.t3hh4xx0r.god_mode.statusbar;

import com.t3hh4xx0r.R;
import com.t3hh4xx0r.god_mode.ColorPickerDialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

public class SignalActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private String signalTextColorPickerFlag;

    // color preference constants
    private static final String PREF_SIGNAL_COLOR_0 = "signal_color_0";

    private static final String PREF_SIGNAL_COLOR_1 = "signal_color_1";

    private static final String PREF_SIGNAL_COLOR_2 = "signal_color_2";

    private static final String PREF_SIGNAL_COLOR_3 = "signal_color_3";

    private static final String PREF_SIGNAL_COLOR_4 = "signal_color_4";

    private static final String PREF_SIGNAL_COLOR_STATIC = "signal_color_static";

    private static final String PREF_AUTO_COLOR = "signal_automatically_color_pref";

    private static final String PREF_SIGNAL_TEXT_STYLE = "signal_text_style_pref";

    private static final String PREF_TOGGLE_4G_ICON = "show_4g_icon";

    private static final String PREF_SHOW_SIGNAL_BARS = "show_signal_bars";

    //
    Preference mSignalColor0;

    Preference mSignalColor1;

    Preference mSignalColor2;

    Preference mSignalColor3;

    Preference mSignalColor4;

    Preference mSignalColorStatic;

    CheckBoxPreference mSignalAutoColor;

    CheckBoxPreference mShow4GIcon;

    CheckBoxPreference mShowSignalBars;

    ListPreference mSignalTextStyle;

    public void onCreate(Bundle ofLove) {
        super.onCreate(ofLove);
        addPreferencesFromResource(R.xml.signal_prefs);

        // assign
        PreferenceScreen prefs = getPreferenceScreen();
        mSignalAutoColor = (CheckBoxPreference) prefs.findPreference(PREF_AUTO_COLOR);
        mSignalTextStyle = (ListPreference) prefs.findPreference(PREF_SIGNAL_TEXT_STYLE);
        mSignalColor0 = prefs.findPreference(PREF_SIGNAL_COLOR_0);
        mSignalColor1 = prefs.findPreference(PREF_SIGNAL_COLOR_1);
        mSignalColor2 = prefs.findPreference(PREF_SIGNAL_COLOR_2);
        mSignalColor3 = prefs.findPreference(PREF_SIGNAL_COLOR_3);
        mSignalColor4 = prefs.findPreference(PREF_SIGNAL_COLOR_4);
        mSignalColorStatic = prefs.findPreference(PREF_SIGNAL_COLOR_STATIC);
        mShow4GIcon = (CheckBoxPreference) prefs.findPreference(PREF_TOGGLE_4G_ICON);
        mShowSignalBars = (CheckBoxPreference) prefs.findPreference(PREF_SHOW_SIGNAL_BARS);

        // check enabled settings
        mShowSignalBars.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_SHOW_SIGNAL_ICON, 1) == 1));
        mSignalAutoColor.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT_ENABLE_AUTOCOLOR, 0) == 1));
        mShow4GIcon.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_SHOW_4G_ICON, 0) == 1));

        mSignalTextStyle.setOnPreferenceChangeListener(this);
        mSignalTextStyle.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT_STYLE, 0));

        refreshOptions();
    }

    public void onResume(Bundle ofLove) {
        refreshOptions();
    }

    public ColorPickerDialog generateDialog(String preference) {
        ColorPickerDialog cp = new ColorPickerDialog(this, mColorChangeListener,
                Settings.System.getInt(getContentResolver(), preference,
                        Settings.System.getInt(getContentResolver(), preference, Color.WHITE)));
        return cp;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {

        if (preference == mSignalColor0) {

            signalTextColorPickerFlag = Settings.System.STATUS_BAR_SIGNAL_TEXT_0_BARS;

            generateDialog(signalTextColorPickerFlag).show();
            return true;

        } else if (preference == mSignalColor1) {
            signalTextColorPickerFlag = Settings.System.STATUS_BAR_SIGNAL_TEXT_1_BARS;

            generateDialog(signalTextColorPickerFlag).show();
            return true;

        } else if (preference == mSignalColor2) {

            signalTextColorPickerFlag = Settings.System.STATUS_BAR_SIGNAL_TEXT_2_BARS;
            generateDialog(signalTextColorPickerFlag).show();
            return true;

        } else if (preference == mSignalColor3) {

            signalTextColorPickerFlag = Settings.System.STATUS_BAR_SIGNAL_TEXT_3_BARS;
            generateDialog(signalTextColorPickerFlag).show();
            return true;

        } else if (preference == mSignalColor4) {

            signalTextColorPickerFlag = Settings.System.STATUS_BAR_SIGNAL_TEXT_4_BARS;
            generateDialog(signalTextColorPickerFlag).show();
            return true;

        } else if (preference == mSignalColorStatic) {

            signalTextColorPickerFlag = Settings.System.STATUS_BAR_SIGNAL_TEXT_STATIC;
            generateDialog(signalTextColorPickerFlag).show();
            return true;

        } else if (preference == mSignalAutoColor) {
            boolean enable = mSignalAutoColor.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT_ENABLE_AUTOCOLOR, (enable ? 1 : 0));
            refreshOptions();
            return true;

        } else if (preference == mShow4GIcon) {
            boolean enable = mShow4GIcon.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_SHOW_4G_ICON,
                    (enable ? 1 : 0));
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SIGNAL_DBM_CHANGED);
            sendBroadcast(i);
            return true;

        } else if (preference == mShowSignalBars) {
            boolean enable = mShowSignalBars.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_SHOW_SIGNAL_ICON, (enable ? 1 : 0));

            return true;

        } else

            return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("TWEAKS", preference.getKey());

        if (preference == mSignalTextStyle) {
            preference = (ListPreference) preference;

            int val = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT_STYLE, val);
            return true;
        }

        return false;
    }

    ColorPickerDialog.OnColorChangedListener mColorChangeListener = new ColorPickerDialog.OnColorChangedListener() {

        @Override
        public void colorChanged(int color) {
            Settings.System.putInt(getContentResolver(), signalTextColorPickerFlag, color);

        }
    };

    public void refreshOptions() {
        if (((CheckBoxPreference) findPreference("signal_automatically_color_pref")).isChecked()) {
            findPreference("signal_color_static").setEnabled(false);
            findPreference("signal_color_0").setEnabled(true);
            findPreference("signal_color_1").setEnabled(true);
            findPreference("signal_color_2").setEnabled(true);
            findPreference("signal_color_3").setEnabled(true);
            findPreference("signal_color_4").setEnabled(true);
        } else {
            findPreference("signal_color_static").setEnabled(true);
            findPreference("signal_color_0").setEnabled(false);
            findPreference("signal_color_1").setEnabled(false);
            findPreference("signal_color_2").setEnabled(false);
            findPreference("signal_color_3").setEnabled(false);
            findPreference("signal_color_4").setEnabled(false);
        }
    }

}
