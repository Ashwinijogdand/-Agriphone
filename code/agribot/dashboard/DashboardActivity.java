package com.agribot.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.agribot.automaticcapture.AgribotAlarmService;
import com.agribot.colorrecognition.AgribotActivity;
import com.agribot.dashboard.R;
import com.agrobot.capturephoto.AgribotCapturePhoto;

public abstract class DashboardActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

	public void onClickHome(View v) {
		goHome(this);
	}

	public void onClickAbout(View v) {
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}

	public void onClickFeature(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.AgribotRealtime:
			startActivity(new Intent(getApplicationContext(), AgribotActivity.class));
			break;
		case R.id.AgribotPhotoCapture:
			startActivity(new Intent(getApplicationContext(), AgribotCapturePhoto.class));
			break;
		case R.id.setPhotoCapture:
			startActivity(new Intent(getApplicationContext(), AgribotAlarmService.class));
			break;

		default:
			break;
		}
	}

	public void goHome(Context context) {
		final Intent intent = new Intent(context, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText(getTitle());
	} // end setTitleText

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

	public void trace(String msg) {
		Log.d("Demo", msg);
		toast(msg);
	}

}