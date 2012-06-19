package com.agribot.automaticcapture;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agribot.dashboard.R;

public class AgribotAlarmService extends Activity {

	private PendingIntent pendingIntent;
	private TimePicker start;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmservice);
		Button buttonStart = (Button) findViewById(R.id.startAlarm);
		Button buttonStop = (Button) findViewById(R.id.stopAlarm);
		start = (TimePicker) findViewById(R.id.from);
		Intent myIntent = new Intent(AgribotAlarmService.this,
				MyAlarmService.class);
		
		pendingIntent = PendingIntent.getService(
				AgribotAlarmService.this, 0, myIntent, 0);

		buttonStart.setOnClickListener(new Button.OnClickListener() {
 
			public void onClick(View arg0) {

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.SECOND, 1);
				calendar.set(Calendar.HOUR_OF_DAY,start.getCurrentHour());
				calendar.set(Calendar.MINUTE,start.getCurrentMinute());
				
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pendingIntent);
				//alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
				//Toast.makeText(AgribotAlarmService.this, "Start Alarm",	Toast.LENGTH_LONG).show();
			}
		});

		buttonStop.setOnClickListener(new Button.OnClickListener() {
  
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarmManager.cancel(pendingIntent);
				
				// Tell the user about what we did.
				Toast.makeText(AgribotAlarmService.this, "Cancel!",
						Toast.LENGTH_LONG).show();

			}
		});

	}
}