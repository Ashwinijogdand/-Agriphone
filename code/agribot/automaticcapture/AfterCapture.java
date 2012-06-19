package com.agribot.automaticcapture;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agribot.dashboard.HomeActivity;
import com.agribot.dashboard.R;
import com.agrobot.capturephoto.AgribotCapturePhoto;

public class AfterCapture extends Activity {
	
	
	ImageView imageView;
	ProgressBar progressBar;
	TextView textView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.afterautomaticcapture);
		imageView=(ImageView) findViewById(R.id.afterimagecapture);
		progressBar=(ProgressBar) findViewById(R.id.progressBar1);
		textView=(TextView) findViewById(R.id.afterimagecapturetext);
		imageView.setVisibility(View.INVISIBLE);
		textView.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		
		System.out.println(Preview.phototakenname);
		
		Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/"+Preview.phototakenname+".jpg");
		Bitmap newphoto = AgribotCapturePhoto.doGreyscale(bitmap);
		Bitmap newphoto2 = AgribotCapturePhoto.binarize(newphoto, bitmap);
		String a = "";
		a = AgribotCapturePhoto.findcolor(newphoto2);

		/*imageView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		imageView.getLayoutParams().height = 300;
		imageView.getLayoutParams().width = 300;
		imageView.setImageBitmap(newphoto2);*/
		Log.d("", ""+a);
		 Toast.makeText(this, a, Toast.LENGTH_LONG).show();
		textView.setText(a);
		imageView.setImageBitmap(newphoto2);
		progressBar.setVisibility(View.GONE);
		imageView.setVisibility(View.VISIBLE);
		textView.setVisibility(View.VISIBLE);
		
		//PendingIntent pi = PendingIntent.getActivity(this,0,Intent., 0);
		SmsManager sms = SmsManager.getDefault();
		  sms.sendTextMessage("9967130540", null, a, null,null);
		
		
		
	}


	@Override
	public void onBackPressed() {
		
		Intent i =new Intent(this, HomeActivity.class);
		finish();
		startActivity(i);
	}
	
	
	

}
