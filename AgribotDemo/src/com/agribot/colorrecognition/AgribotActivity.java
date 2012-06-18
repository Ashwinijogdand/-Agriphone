package com.agribot.colorrecognition;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.prefs.Preferences;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.agribot.dashboard.R;

public class AgribotActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

	 public static final String TAG = "colortone";

     private Preview mPreview;
     private Camera mCamera;

     private Classifier mClassifier;
     private ValueEstimator mEstimator;

     private SurfaceView colorSurface;
     private ImageButton lockButton, ttsButton;
     private TextView rgbInfoTV, hsvInfoTV, colorNameTV, debugTV;
     private ZoomControls mZoom;
     private ImageView crosshairIV;

     private Handler mHandler = new Handler();
     private boolean reconRunning = false;

     private boolean ttsInstalled = false;
     private String ttsColorName = "";

     ColorSniffer mColorSniffer = new ColorSniffer();
     final int DELAY = 50;

     int red,green,blue;
     
     
     
     class ColorSniffer implements Runnable {

             
             public void run() {
                     if (!reconRunning)
                             return;
                     mCamera.setOneShotPreviewCallback(mSensor);
             }

     }

     class Sensor implements PreviewCallback {

             String colorName;
             int colorValue;

             ArchiveHelper mHelper;
             SQLiteDatabase mDB;

             byte[] data;
             Camera.Parameters parameters;

             
             public void onPreviewFrame(byte[] data, Camera camera) {
                     int[] pxdata = new int[data.length];

                     try {
                             parameters = camera.getParameters();
                     } catch (Exception e) {
                             mHandler.removeCallbacks(mColorSniffer);

                             if (reconRunning)
                                     mHandler.postDelayed(mColorSniffer, DELAY);
                             return;
                     }

                     Size size = parameters.getPreviewSize();

                     decodeYUV420SP(pxdata, data, size.width, size.height);
                     int value = mEstimator.getAverageColor(pxdata, size.width,
                                     size.height);
                     NamedColor color = new NamedColor(value, mClassifier);

                     colorSurface.setBackgroundColor(color.getInteger());
                     rgbInfoTV.setText(color.asHTML() + "\n" + color.asRGB());
                     
                     System.out.println(""+ color.asRGB());
                    // hsvInfoTV.setText(color.asHSV());
                     colorNameTV.setText(color.getName());

                     ttsColorName = color.getName();
                     /*------------------------------ Code Added here*/
                     
                     
                      red=color.asRed();
                      blue=color.asBlue();
                      green=color.asGreen();
                    

                      if((red>=152)&&(red<=182)&&(green>=139)&&(green<=203)&&(blue>=22)&&(blue<=80))
                      {
                     //	 Toast.makeText(this, "HHHHHHHHHHHHHHHHHHHHH", Toast.LENGTH_SHORT).show();
                    	  
                    	  hsvInfoTV.setText("unripe");
                     	 System.out.println("unripe");
                      }
                      else
                      
                      if((red>=160)&&(red<=200)&&(green>=100)&&(green<=175)&&(blue>=0)&&(blue<=60))
                      {
                     	// Toast.makeText(this, "HHHHHHHHHHHHHHHHHHHHH", Toast.LENGTH_SHORT).show();
                    	  hsvInfoTV.setText("Medium  Ripped");
                      	  System.out.println("Medium ripped");
                           }
                      
                      else
                      if((red>=175)&&(red<=250)&&(green>=0)&&(green<=25)&&(blue>=0)&&(blue<10))
                      {
                     	// Toast.makeText(this, "HHHHHHHHHHHHHHHHHHHHH", Toast.LENGTH_SHORT).show();
                    	  hsvInfoTV.setText(" Ripped");
                      	  System.out.println(" Ripped ");
                          }
                      else
                      {
                    	  hsvInfoTV.setText(" unable to identify");
                      }
                     
                     /*------------------------------ Code completed here*/
                     this.colorName = color.getName();
                     this.colorValue = value;
                     this.data = data;

                     mHandler.removeCallbacks(mColorSniffer);

                     if (reconRunning)
                             mHandler.postDelayed(mColorSniffer, DELAY);
             }

             public void saveToArchive() {
                     if (colorName == null || data == null || parameters == null)
                             return;

                     Log.e(TAG, AgribotActivity.this.toString());
                     mHelper = new ArchiveHelper(AgribotActivity.this);
                     Log.e(TAG, "ctx created");
                     if (mHelper == null)
                             Log.e(TAG, "no database!");
                     mDB = mHelper.getWritableDatabase();

                     ContentValues c = new ContentValues();
                     c.put("color_name", this.colorName);
                     c.put("color_value", this.colorValue);
                     int id = (int) mDB.insert("archive", null, c);
                     Size size = parameters.getPreviewSize();
                     YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                                     size.width, size.height, null);

                     try {
                             FileOutputStream fos = openFileOutput(Integer.toString(id)
                                             + ".jpg", Context.MODE_WORLD_READABLE);
                             image.compressToJpeg(new Rect(0, 0, image.getWidth(), image
                                             .getHeight()), 90, fos);
                             fos.close();
                     } catch (FileNotFoundException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                     } catch (IOException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                     }

             }

             // (c) Ketai project
             void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

                     final int frameSize = width * height;

                     for (int j = 0, yp = 0; j < height; j++) {
                             int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                             for (int i = 0; i < width; i++, yp++) {
                                     int y = (0xff & ((int) yuv420sp[yp])) - 16;
                                     if (y < 0)
                                             y = 0;
                                     if ((i & 1) == 0) {
                                             v = (0xff & yuv420sp[uvp++]) - 128;
                                             u = (0xff & yuv420sp[uvp++]) - 128;
                                     }

                                     int y1192 = 1192 * y;
                                     int r = (y1192 + 1634 * v);
                                     int g = (y1192 - 833 * v - 400 * u);
                                     int b = (y1192 + 2066 * u);

                                     if (r < 0)
                                             r = 0;
                                     else if (r > 262143)
                                             r = 262143;
                                     if (g < 0)
                                             g = 0;
                                     else if (g > 262143)
                                             g = 262143;
                                     if (b < 0)
                                             b = 0;
                                     else if (b > 262143)
                                             b = 262143;

                                     rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                                                     | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                             }
                     }
             }

     };

     Sensor mSensor = new Sensor();

     private void installInterfaceCallbacks() {
             lockButton = (ImageButton) this.findViewById(R.id.lock_button);

             lockButton.setOnClickListener(new OnClickListener() {

                     
                     public void onClick(View v) {
                             if (reconRunning) {
                                     reconRunning = false;
                                     mHandler.removeCallbacks(mColorSniffer);
                                     lockButton.setImageResource(R.drawable.lock);
                             } else {
                                     reconRunning = true;
                                     mHandler.removeCallbacks(mColorSniffer);
                                     mHandler.postDelayed(mColorSniffer, DELAY);
                                     lockButton.setImageResource(R.drawable.unlock);
                             }
                     }

             });

             ttsButton = (ImageButton) this.findViewById(R.id.tts_button);

             ttsButton.setOnClickListener(new OnClickListener() {

                     
                     public void onClick(View v) {
                             if (mTts != null && ttsInstalled) {
                                     mTts.speak(ttsColorName, TextToSpeech.QUEUE_FLUSH, null);
                             }
                     }

             });

             mZoom = (ZoomControls) this.findViewById(R.id.zoomControls1);
             mZoom.setZoomSpeed(20);
             mZoom.setOnZoomInClickListener(new OnClickListener() {

                     
                     public void onClick(View v) {
                             mPreview.zoomIn();
                     }
             });

             mZoom.setOnZoomOutClickListener(new OnClickListener() {

                     
                     public void onClick(View v) {
                             mPreview.zoomOut();
                     }
             });

             debugTV = (TextView) this.findViewById(R.id.debugtv);
             colorSurface = (SurfaceView) this.findViewById(R.id.color_surface);
             rgbInfoTV = (TextView) this.findViewById(R.id.rgbinfo);
             hsvInfoTV = (TextView) this.findViewById(R.id.hsvinfo);
             colorNameTV = (TextView) this.findViewById(R.id.color_name);
             crosshairIV = (ImageView) this.findViewById(R.id.imageView1);
     }

     private final int TTS_CHECK_CODE = 1;

     private void ttsSetup() {
             Intent checkIntent = new Intent();
             checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
             startActivityForResult(checkIntent, TTS_CHECK_CODE);
     }

     private TextToSpeech mTts;

     
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             if (requestCode == TTS_CHECK_CODE) {
                     if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                             // success, create the TTS instance
                             mTts = new TextToSpeech(this, new OnInitListener() {

                                     
                                     public void onInit(int arg0) {
                                             Log.d(TAG, "TTS initialized");
                                             ttsInstalled = true;

                                             mTts.setLanguage(Locale.ENGLISH);
                                             // mTts.speak("TTS at your service",
                                             // TextToSpeech.QUEUE_FLUSH, null);

                                     }
                             });
                     } else {
                             Log.d(TAG, "no TTS support");
                     }
             }
     }

     
     protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);

             requestWindowFeature(Window.FEATURE_NO_TITLE);
             getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

             // Log.d(TAG, "onCreate");

             setContentView(R.layout.main2);

             setupPreferred();

             SharedPreferences prefs = PreferenceManager
                             .getDefaultSharedPreferences(this);

             prefs.registerOnSharedPreferenceChangeListener(this);

             ttsSetup();

             installInterfaceCallbacks();
             
             
     }

     
     protected void onResume() {
             super.onResume();
             // Log.d(TAG, "onResume");
             mPreview = (Preview) this.findViewById(R.id.m_preview);

             while (true) {
                     try {
                             mCamera = Camera.open();
                             break;
                     } catch (RuntimeException e) {
                     }
                     // Log.d(TAG, "onResume: camfail");
             }

             // Log.d(TAG, "onResume: camera open " + mCamera);

             /*
              * // Find the total number of cameras available int numberOfCameras =
              * Camera.getNumberOfCameras(); int defaultCameraId = 0;
              * 
              * // Find the ID of the default camera Camera.CameraInfo cameraInfo =
              * new Camera.CameraInfo(); for (int i = 0; i < numberOfCameras; i++) {
              * Camera.getCameraInfo(i, cameraInfo); if (cameraInfo.facing ==
              * Camera.CameraInfo.CAMERA_FACING_BACK) { defaultCameraId = i; } }
              */
             // mPreview.setCameraDisplayOrientation(this, defaultCameraId, mCamera);
             mPreview.setCamera(mCamera);

             // Log.d(TAG, "onResume: prev set");

             reconRunning = true;
             mHandler.removeCallbacks(mColorSniffer);
             mHandler.postDelayed(mColorSniffer, DELAY);
     }

     
     protected void onPause() {
             super.onPause();
             // Log.d(TAG, "onPause");
             mHandler.removeCallbacks(mColorSniffer);
             reconRunning = false;
             mPreview = (Preview) this.findViewById(R.id.m_preview);
             // Because the Camera object is a shared resource, it's very
             // important to release it when the activity is paused.
             if (mCamera != null) {
                     mPreview.setCamera(null);
                     mCamera.release();
                     mCamera = null;
             }
     }

     
     public boolean onCreateOptionsMenu(Menu menu) {
             MenuInflater inflater = getMenuInflater();
             inflater.inflate(R.menu.main_menu, menu);
             return true;
     }

     
     public boolean onOptionsItemSelected(MenuItem item) {
             // Handle item selection
             switch (item.getItemId()) {
             case R.id.settings:
                     Intent settingsActivity = new Intent(getBaseContext(),
                                     Preferences.class);
                     startActivity(settingsActivity);
                     return true;
             case R.id.save_to_archive:
                     mSensor.saveToArchive();
                     return true;
             case R.id.open_archive:
                     Intent archiveActivity = new Intent(getBaseContext(),
                                     CTarchive.class);
                     startActivity(archiveActivity);
                     return true;
             default:
                     return super.onOptionsItemSelected(item);
             }
     }

     private void setupPreferred() {
             SharedPreferences prefs = PreferenceManager
                             .getDefaultSharedPreferences(this);

             try {
                     Class estClass = Class.forName(prefs.getString("estimator",
                                     "com.agribot.colorrecognition.FlatRectEstimator"));
                     mEstimator = (ValueEstimator) estClass.newInstance();
                     Log.i(TAG, "Estimator: " + estClass.getName());
             } catch (Exception e) {
                     e.printStackTrace();
             }

             try {
                     if (mClassifier == null
                                     || !mClassifier.getName().equals(
                                                     prefs.getString("classifier", "xkcd.dat")))
                             mClassifier = new SimpleDataClassifier(getAssets(), prefs
                                             .getString("classifier", "xkcd.dat"));
             } catch (IOException e) {
                     Log.e("mib", "invalid asset: "
                                     + prefs.getString("classifier", "xkcd.dat"));
                     this.finish();
             }

             rgbInfoTV = (TextView) this.findViewById(R.id.rgbinfo);
             hsvInfoTV = (TextView) this.findViewById(R.id.hsvinfo);
             crosshairIV = (ImageView) this.findViewById(R.id.imageView1);
             mPreview = (Preview) this.findViewById(R.id.m_preview);

             if (prefs.getBoolean("show_rgb", true)) {
                     rgbInfoTV.setVisibility(View.VISIBLE);
             } else {
                     rgbInfoTV.setVisibility(View.INVISIBLE);
             }

             if (prefs.getBoolean("show_hsv", true)) {
                     hsvInfoTV.setVisibility(View.VISIBLE);
             } else {
                     hsvInfoTV.setVisibility(View.INVISIBLE);
             }

             if (prefs.getBoolean("crosshair_enabled", true)) {
                     crosshairIV.setVisibility(View.VISIBLE);
             } else {
                     crosshairIV.setVisibility(View.INVISIBLE);
             }

             if (prefs.getString("white_balance", null) != null) {
                     mPreview.setWB(prefs.getString("white_balance", null));
             }

     }


     public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
             setupPreferred();
     }

}