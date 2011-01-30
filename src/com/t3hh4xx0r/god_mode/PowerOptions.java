package com.t3hh4xx0r.god_mode;

import com.android.spare_parts.R;
import com.android.spare_parts.R.drawable;
import com.android.spare_parts.R.id;
import com.android.spare_parts.R.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PowerOptions extends Activity implements View.OnClickListener{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		
		Context mContext = getApplicationContext();
		Dialog dialog = new Dialog(mContext);

		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Custom Dialog");

		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText("Hello, this is a custom dialog!");
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(R.drawable.icon1);
		dialog.show();
    }
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
