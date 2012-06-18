package com.agribot.automaticcapture;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.widget.Toast;

public class MyAlarmService extends Service {

	
	
	Camera camera;
	Preview preview;
	LayoutInflater layoutInflater;

	
	@Override
	public void onCreate() {
		
	//	Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
		Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_SHORT).show();
		Intent intent1 = new Intent(getBaseContext(), CapturePhoto.class);
		intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent1);
		super.onStart(intent, startId);
		
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	
	
	
	
	
	
	
}