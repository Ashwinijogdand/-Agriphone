package com.agrobot.capturephoto;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agribot.dashboard.R;

public class AgribotCapturePhoto extends Activity {
	
	Button button;
	ImageView imageView;
	int requestcode = 114;
	Bitmap photo, newphoto, newphoto2, newphoto3;
	TextView textView;

	 public static Bitmap doGreyscale(Bitmap src) {
		// constant factors
		final double GS_RED = 0.299;
		final double GS_GREEN = 0.587;
		final double GS_BLUE = 0.114;
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// pixel information
		int A, R, G, B;
		int pixel;

		// get image size
		int width = src.getWidth();
		int height = src.getHeight();

		// scan through every single pixel
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get one pixel color
				pixel = src.getPixel(x, y);
				// retrieve color of all channels
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// take conversion up to one single value
				R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	 public static Bitmap binarize(Bitmap original, Bitmap original2) {

		int red;
		int newPixel, pixel;

		int threshold = otsuTreshold(original);
		System.out.println(">>>>>>>>>>>>>     " + threshold);
		Bitmap binarized = Bitmap.createBitmap(original.getWidth(),
				original.getHeight(), original.getConfig());
		int[] pix = new int[original.getWidth() * original.getHeight()];

		for (int i = 0; i < original.getWidth(); i++) {
			for (int j = 0; j < original.getHeight(); j++) {

				float[] hsv = new float[3];

				pixel = original.getPixel(i, j); // Get pixels
				int index = i * original.getWidth() + j;
				int A = Color.alpha(pixel);
				int R = Color.red(pixel);
				Color.colorToHSV(original2.getPixel(i, j), hsv);

				float H = hsv[0];
				float S = hsv[1];
				float V = hsv[2];

				if (H > threshold) {
					newPixel = 255;
					newPixel = colorToRGB(0xff, newPixel, newPixel, newPixel);
					binarized.setPixel(i, j, newPixel);
				} else {

					binarized.setPixel(i, j, Color.HSVToColor(hsv));

				}
				// newPixel = colorToRGB(0xff, newPixel, newPixel, newPixel);

			}
		}

		return binarized;
	}

	 public static int colorToRGB(int alpha, int red, int green, int blue) {

		int newPixel = 0;
		newPixel += alpha;
		newPixel = newPixel << 8;
		newPixel += red;
		newPixel = newPixel << 8;
		newPixel += green;
		newPixel = newPixel << 8;
		newPixel += blue;

		return newPixel;

	}

	 public static int[] imageHistogram(Bitmap input) {

		int[] histogram = new int[256];
		int[] pix = new int[input.getWidth() * input.getHeight()];
		int pixel;
		for (int i = 0; i < histogram.length; i++)
			histogram[i] = 0;

		for (int i = 0; i < input.getWidth(); i++) {
			for (int j = 0; j < input.getHeight(); j++) {

				pixel = input.getPixel(i, j); // Get pixels

				int index = i * input.getWidth() + j;
				int R = Color.red(pixel);
				// bitwise shifting

				histogram[R]++;
			}
		}

		return histogram;

	}

	 public static int otsuTreshold(Bitmap original) {

		int[] histogram = imageHistogram(original);
		int total = original.getHeight() * original.getWidth();

		float sum = 0;
		for (int i = 0; i < 256; i++)
			sum += i * histogram[i];

		float sumB = 0;
		int wB = 0;
		int wF = 0;

		float varMax = 0;
		int threshold = 0;

		for (int i = 0; i < 256; i++) {
			wB += histogram[i];
			if (wB == 0)
				continue;
			wF = total - wB;

			if (wF == 0)
				break;

			sumB += (float) (i * histogram[i]);
			float mB = sumB / wB;
			float mF = (sum - sumB) / wF;

			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = i;
			}
		}

		return threshold;

	}

	 public static String findcolor(Bitmap newphoto4) {

		// Toast.makeText(this,""+newphoto4.getHeight()*newphoto4.getWidth(),Toast.LENGTH_SHORT);

		int[] pix = new int[newphoto4.getWidth() * newphoto4.getHeight()];
		int pixel, unripe = 0, medripe = 0, ripe = 0, total = 0;
		for (int i = 0; i < newphoto4.getWidth(); i++) {
			for (int j = 0; j < newphoto4.getHeight(); j++) {

				float[] hsv = new float[3];

				pixel = newphoto4.getPixel(i, j); // Get pixels
				Color.colorToHSV(newphoto4.getPixel(i, j), hsv);

				float H = hsv[0];
				float S = hsv[1];
				float V = hsv[2];
				int R = Color.red(pixel);
				int G = Color.green(pixel);
				int B = Color.blue(pixel);

				//System.out.println("  H -->" + H + "   S -->" + S + "   V -->"	+ V);

				if (((H >= 2.0 && H <= 6.0) || (H >= 350.0 && H <= 360.0)
						&& S >= 0.83 && S <= 0.85 && V >= 0.45 && V <= 1.0)
						|| ((R >= 150) && (R <= 255) && (G >= 0) && (G <= 75)
								&& (B >= 0) && (B < 75))) {
					//System.out.println("RED");
					ripe++;
				}

				if (((H >= 20.0 && H <= 50.0) && (S >= 0.65 && S <= 1.00) && (V >= 0.65 && V <= 1.00))
						|| ((R >= 233 && H <= 255) && (R >= 69 && R <= 160) && ((G >= 0 && G <= 122)))) {
					//System.out.println("ORANGE");
					medripe++;
				}
				//
				if (R == 178 && G == 236 && B == 93)
					unripe++;
				if (R == 124 && G == 252 && B == 0)
					unripe++;
				if (R == 102 && G == 255 && B == 0)
					unripe++;
				if (R == 172 && G == 225 && B == 175)
					unripe++;
				if (R == 178 && G == 190 && B == 181)
					unripe++;
				if (R == 163 && G == 193 && B == 173)
					unripe++;
				if (R == 147 && G == 197 && B == 114)
					unripe++;
				if (R == 133 && G == 187 && B == 101)
					unripe++;
				if (R == 3 && G == 192 && B == 60)
					unripe++;
				if (R == 120 && G == 134 && B == 107)
					unripe++;
				if (R == 115 && G == 134 && B == 120)
					unripe++;
				if (R == 0 && G == 128 && B == 0)
					unripe++;
				if (R == 19 && G == 136 && B == 8)
					unripe++;

				if ((R >= 175) && (R <= 250) && (G >= 100) && (G <= 255)
						&& (B >= 0) && (B < 10)) {
					unripe++;

					//System.out.println(" Unripe ");
				}

				// if (!((H == 60.0) && (S == 0.0) && (V == 1.0)) || ((H == 0.0)
				// && (S == 0.0)&& ((V >= 0.30) && (V <= 0.40)))) {
				// total++;
				//
				//
				// }
				if (!((R == 255 && G == 255 && B == 255))
						|| (((H == 60.0) && (S == 0.0) && (V == 1.0)) || ((H == 0.0)
								&& (S == 0.0) && ((V >= 0.30) && (V <= 0.40))))) {
					total++;
				}

			}
		}

		int totalpercent = unripe + medripe + ripe;

		String a = "UNRIPE: " + ((unripe * 100) / totalpercent) + "%  MEDRIPE: "
				+ ((medripe * 100) / totalpercent) + "%  RIPE: "
				+ ((ripe * 100) / totalpercent) + " %    ";
				//+ "    Threshold :" + otsuTreshold(newphoto4);
		// TODO Auto-generated method stub
		return a;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agribotrealtime);

		button = (Button) findViewById(R.id.button1);
		imageView = (ImageView) findViewById(R.id.imageview);
		textView = (TextView) findViewById(R.id.state);
		// imageView.getLayoutParams().height = 50;

		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(i, requestcode);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestcode) {
			photo = (Bitmap) data.getExtras().get("data");

			newphoto = doGreyscale(photo);
			newphoto2 = binarize(newphoto, photo);
			String a = "";
			a = findcolor(newphoto2);

			imageView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			imageView.getLayoutParams().height = 300;
			imageView.getLayoutParams().width = 300;
			imageView.setImageBitmap(newphoto2);
			textView.setText(a);

			
			
			  //Sending sms
			PendingIntent pi = PendingIntent.getActivity(this,0,data, 0);
			SmsManager sms = SmsManager.getDefault();
			  sms.sendTextMessage("8097511597", null, a, pi,null);
			 
		}
		
		
		

	}

}