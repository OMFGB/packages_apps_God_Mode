package com.t3hh4xx0r.god_mode;

import com.t3hh4xx0r.god_mode.ColorPickerDialog;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class ColorChangedListener implements ColorPickerDialog.OnColorChangedListener {
    private PreferenceActivity mActivity;
    private String mSetting;

    public ColorChangedListener(PreferenceActivity activity, String setting) {
        mActivity = activity;
        mSetting = setting;
    }

    public void colorChanged(int color) {
        Settings.System.putInt(mActivity.getContentResolver(), mSetting, color);
    }
}
