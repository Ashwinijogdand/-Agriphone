/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agribot.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.agribot.automaticcapture.AgribotAlarmService;
import com.agribot.colorrecognition.AgribotActivity;
import com.agribot.dashboard.R;
import com.agrobot.capturephoto.AgribotCapturePhoto;

public class HomeActivity extends DashboardActivity {

	Button b1, b2,b3;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		final Intent i1 = new Intent(this, AgribotActivity.class);
		final Intent i2 = new Intent(this, AgribotCapturePhoto.class);
		final Intent i3 = new Intent(this, AgribotAlarmService.class);

		b1 = (Button) findViewById(R.id.AgribotRealtime);
		b2 = (Button) findViewById(R.id.AgribotPhotoCapture);
		b3 = (Button) findViewById(R.id.setPhotoCapture);
		
		b1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(i1);
			}
		});
		b2.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(i2);
			}
		});
		b3.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(i3);
			}
		});
		
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

}
