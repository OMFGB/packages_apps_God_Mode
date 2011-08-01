package com.t3hh4xx0r.addons.web.sync;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.c2dm.C2DMessaging;

import com.t3hh4xx0r.R;



public class RegisterActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}

	public void register(View view) {
		Log.e("RegisterActivity", "Starting registration");
		Toast.makeText(this, "Starting", Toast.LENGTH_LONG).show();
		EditText text = (EditText) findViewById(R.id.editText1);
		text.setText("r2doesinc@gmail.com");
		C2DMessaging.register(this, text.getText().toString());
	}
}
