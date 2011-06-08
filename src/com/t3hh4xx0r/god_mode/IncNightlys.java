package com.t3hh4xx0r.god_mode;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import android.os.Bundle;
import android.os.Build;

import android.util.Slog;

public class IncNightlys extends PreferenceActivity {
        private static final String TAG = "god_mode.IncNightlys";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.inc_nightlys);
    }

}
