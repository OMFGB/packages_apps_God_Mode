/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t3hh4xx0r.addons.web.sync;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMReceiver extends C2DMBaseReceiver {
	public C2DMReceiver() {
		
		super("linuxmotion@google.com");
	}

	public void onReceive(Context context, Intent intent) {
		Log.e("C2DM", "Received");
	
	}
	@Override
	public void onRegistered(Context context, String registrationId)
			throws java.io.IOException {
		// The registrationId should be send to your applicatioin server.
		// We just log it to the LogCat view
		// We will copy it from there
		Log.e("C2DM", "Registration ID arrived: Fantastic!!!");
		Log.e("C2DM", registrationId);
	};

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.e("C2DM", "Message: Fantastic!!!");
		// Extract the payload from the message
		Bundle extras = intent.getExtras();
		if (extras != null) {
			System.out.println(extras.get("payload"));
			// Now do something smart based on the information
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e("C2DM", "Error occured!!!");
	}

}
