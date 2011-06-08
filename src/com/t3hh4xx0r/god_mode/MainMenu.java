package com.t3hh4xx0r.god_mode;

import android.view.Menu;
import android.view.MenuItem;
import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;


import com.t3hh4xx0r.god_mode.R;

public class MainMenu extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

	public static String DATE = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());

        public static final String BACKUP_DIR = "/sdcard/clockworkmod/backup";
        public static final String EXTENDEDCMD = "/cache/recovery/extendedcommand";
        private static final String DOWNLOAD_DIR = "/sdcard/t3hh4xx0r/download/";


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.main_menu);

    File t3hh4xx0rDirectory = new File ("/sdcard/t3hh4xx0r");
                        if (!t3hh4xx0rDirectory.isDirectory()) {
                                t3hh4xx0rDirectory.mkdir();
                        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
       String key) {
    }

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Clear Download Cache");
		menu.add(0, 1, 0, "Create A Backup");

		return true;
	}

        public static boolean deleteDir (File dir) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i=0; i<children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
		    }
                }
	    }
            return dir.delete();
        }

	public void createBackup() {

                Thread cmdThread = new Thread(){
                        @Override
                        public void run() {

                        File updateDirectory = new File ("/cache/recovery");
                        if (!updateDirectory.isDirectory()) {
                                updateDirectory.mkdir();
                        }
                                Looper.prepare();

                                try{Thread.sleep(1000);}catch(InterruptedException e){ }

                                final Runtime run = Runtime.getRuntime();
                                DataOutputStream out = null;
                                Process p = null;

                                try {
                                        p = run.exec("su");
                                        out = new DataOutputStream(p.getOutputStream());
                                        out.writeBytes("busybox echo 'backup_rom(\"" + BACKUP_DIR + "/omfgb_" + DATE +"\");'  > " + EXTENDEDCMD + "\n"); 
                                        out.writeBytes("reboot recovery\n");
                                        out.flush();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        return;
                                }

                        }
                };
                cmdThread.start();
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 0:
                File dir = new File(DOWNLOAD_DIR);
		deleteDir(dir);
		break;

		case 1:		
		createBackup();
		break;

		}
	    	return(super.onOptionsItemSelected(item));
	}	
}
