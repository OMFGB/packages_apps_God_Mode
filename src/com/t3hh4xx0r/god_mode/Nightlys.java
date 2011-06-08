package com.t3hh4xx0r.god_mode;

import android.content.Intent;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import android.os.Bundle;
import android.os.Build;

import android.util.Slog;

public class Nightlys extends PreferenceActivity {
        private static final String TAG = "god_mode.Nightlys";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

	if (Build.MODEL == "Incredible"); {
	Intent intent = new Intent(Intent.ACTION_MAIN);
	intent.setClassName("com.t3hh4xx0r.god_mode", "com.t3hh4xx0r.god_mode.IncNightlys");
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	startActivity(intent);
	
	} if (Build.MODEL == "Eris"); {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.t3hh4xx0r.god_mode", "com.t3hh4xx0r.god_mode.DesirecNightlys");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        } if (Build.MODEL == "Evo"); {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.t3hh4xx0r.god_mode", "com.t3hh4xx0r.god_mode.SupersonicNightlys");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        } if (Build.MODEL == "Hero"); {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.t3hh4xx0r.god_mode", "com.t3hh4xx0r.god_mode.HerocNightlys");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

	}
    }
}

