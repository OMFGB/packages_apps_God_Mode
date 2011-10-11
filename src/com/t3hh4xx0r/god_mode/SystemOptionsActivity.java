
package com.t3hh4xx0r.god_mode;

import com.t3hh4xx0r.R;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

import java.util.ArrayList;

public class SystemOptionsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.system_settings);

        ListPreference btpref = (ListPreference) findPreference("pref_gps_source");
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        for (String e : getResources().getStringArray(R.array.entries_gps_source)) {
            entries.add(e);
        }
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();
        for (String v : getResources().getStringArray(R.array.values_gps_source)) {
            values.add(v);
        }
        // add known bonded BT devices
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if ((mBluetoothAdapter != null) && (mBluetoothAdapter.isEnabled())) {
            for (BluetoothDevice d : mBluetoothAdapter.getBondedDevices()) {
                String dname = d.getName() + " - " + d.getAddress();
                entries.add(dname);
                values.add(d.getAddress());
            }
        }
        btpref.setEntries(entries.toArray(new CharSequence[entries.size()]));
        btpref.setEntryValues(values.toArray(new CharSequence[values.size()]));
        btpref.setDefaultValue("0");
        btpref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String oldPref = Settings.System.getString(getContentResolver(),
                Settings.System.EXTERNAL_GPS_BT_DEVICE);
        String newPref = newValue == null ? "0" : (String) newValue;
        // "0" represents the internal GPS.
        Settings.System.putString(getContentResolver(), Settings.System.EXTERNAL_GPS_BT_DEVICE,
                newPref);
        if (!oldPref.equals(newPref) && ("0".equals(oldPref) || "0".equals(newPref))) {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
            locationManager.setGPSSource(newPref);

            // Show dialog to inform user that source has been switched
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(R.string.pref_gps_source_notification_title);
            alertDialog.setMessage(getResources().getString(R.string.pref_gps_source_notification));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    getResources().getString(com.android.internal.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            alertDialog.show();
        }
        return true;
    }

}
