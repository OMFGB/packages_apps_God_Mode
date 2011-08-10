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


public class UiOptions extends PreferenceActivity implements OnPreferenceChangeListener {

	private boolean DBG = (false || Constants.FULL_DBG);

	private static final String ELECTRON_BEAM_ANIMATION_ON = "electron_beam_animation_on";
	private static final String ELECTRON_BEAM_ANIMATION_OFF = "electron_beam_animation_off";
	private static final String ENABLE_VOL_MUSIC_CONTROLS = "enable_vol_music_controls";
    private static final String OVERSCROLL_PREF = "pref_overscroll_effect";
    private static final String OVERSCROLL_WEIGHT_PREF = "pref_overscroll_weight";	
    private static final String ROTATION_90_PREF = "pref_rotation_90";
    private static final String ROTATION_180_PREF = "pref_rotation_180";
	private static final String ROTATION_270_PREF = "pref_rotation_270";
		
    private CheckBoxPreference mRotation90Pref;
	private CheckBoxPreference mRotation180Pref;
	private CheckBoxPreference mRotation270Pref;
	private CheckBoxPreference mUseScreenOnAnim;
	private CheckBoxPreference mUseScreenOffAnim;
	private CheckBoxPreference mEnableVolMusicControls;
    private ListPreference mOverscrollPref;
    private ListPreference mOverscrollWeightPref;

	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ui_options);
		PreferenceScreen prefSet = getPreferenceScreen();

        /* Electron beam animation settings */
		mUseScreenOnAnim = (CheckBoxPreference)prefSet.findPreference(ELECTRON_BEAM_ANIMATION_ON);
		mUseScreenOnAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ELECTRON_BEAM_ANIMATION_ON, 1) == 1);
		mUseScreenOffAnim = (CheckBoxPreference)prefSet.findPreference(ELECTRON_BEAM_ANIMATION_OFF);
		mUseScreenOffAnim.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ELECTRON_BEAM_ANIMATION_OFF, 1) == 1);			
	
		/* Volume button music controls */
		mEnableVolMusicControls = (CheckBoxPreference) prefSet.findPreference(ENABLE_VOL_MUSIC_CONTROLS);
		mEnableVolMusicControls.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.ENABLE_VOL_MUSIC_CONTROLS, 0) == 1);

	    /* Overscroll effect */
	    mOverscrollPref = (ListPreference) prefSet.findPreference(OVERSCROLL_PREF);
	    int overscrollEffect = Settings.System.getInt(getContentResolver(),
	                Settings.System.OVERSCROLL_EFFECT, 1);
	    mOverscrollPref.setValue(String.valueOf(overscrollEffect));
	    mOverscrollPref.setOnPreferenceChangeListener(this);
	    mOverscrollWeightPref = (ListPreference) prefSet.findPreference(OVERSCROLL_WEIGHT_PREF);
	    int overscrollWeight = Settings.System.getInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT, 5);
        mOverscrollWeightPref.setValue(String.valueOf(overscrollWeight));
	    mOverscrollWeightPref.setOnPreferenceChangeListener(this);
	        
	    /* Rotation */
	    mRotation90Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_90_PREF);
	    mRotation180Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_180_PREF);
	    mRotation270Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_270_PREF);
	    int mode = Settings.System.getInt(getContentResolver(),
			Settings.System.ACCELEROMETER_ROTATION_MODE, 5);
	    mRotation90Pref.setChecked((mode & 1) != 0);
		mRotation180Pref.setChecked((mode & 2) != 0);
		mRotation270Pref.setChecked((mode & 4) != 0);
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
        if (preference == mRotation90Pref || preference == mRotation180Pref || preference == mRotation270Pref) {
        	int mode = 0;
				if (mRotation90Pref.isChecked())  mode |= 1;
				if (mRotation180Pref.isChecked()) mode |= 2;
				if (mRotation270Pref.isChecked()) mode |= 4;
				Settings.System.putInt(getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION_MODE, mode);
        }   
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        
        if (preference == mOverscrollPref) {
            int overscrollEffect = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.OVERSCROLL_EFFECT, overscrollEffect);
            return true;
        } else if (preference == mOverscrollWeightPref) {
            int overscrollWeight = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT, overscrollWeight);
            return true;
        }
        return false;
    }
}
