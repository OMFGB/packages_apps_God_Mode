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
import android.os.Environment;

import android.util.Slog;

import com.t3hh4xx0r.god_mode.R;

public class MainMenu extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "god_mode.MainMenu";

	public static String DATE = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());
        public static String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

        public static final String BACKUP_DIR = extStorageDirectory + "/clockworkmod/backup";
        public static final String EXTENDEDCMD = "/cache/recovery/extendedcommand";
        private static final String DOWNLOAD_DIR = extStorageDirectory + "/t3hh4xx0r/downloads";

    @Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_menu);

	setupFolders();

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

        public void deleteDir() {
               Thread cmdThread = new Thread(){
                        @Override
                        public void run() {

                                Looper.prepare();

                                try{Thread.sleep(1000);}catch(InterruptedException e){ }

                                final Runtime run = Runtime.getRuntime();
                                DataOutputStream out = null;
                                Process p = null;
                                try {
                                        p = run.exec("su");
                                        out = new DataOutputStream(p.getOutputStream());
                                        out.writeBytes("busybox rm " + DOWNLOAD_DIR + "/" + "*\n");
                                        out.flush();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        return;
                                }

                        }
                };
                cmdThread.start();
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
		deleteDir();
		break;

		case 1:		
		createBackup();
		break;

		}
	    	return(super.onOptionsItemSelected(item));
	}	

	public void setupFolders() {

		Thread cmdThread = new Thread(){
                        @Override
                        public void run() {

                                Looper.prepare();

                                try{Thread.sleep(1000);}catch(InterruptedException e){ }

                                final Runtime run = Runtime.getRuntime();
                                DataOutputStream out = null;
                                Process p = null;
                                try {
                                        p = run.exec("su");
                                        out = new DataOutputStream(p.getOutputStream());
                                        out.writeBytes("busybox mkdir " + extStorageDirectory + "/t3hh4xx0r\n");
                                        out.writeBytes("busybox mkdir " + extStorageDirectory + "/t3hh4xx0r/downloads/\n");
                                        out.flush();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        return;
                                }

                        }
                };
                cmdThread.start();
	}

}

