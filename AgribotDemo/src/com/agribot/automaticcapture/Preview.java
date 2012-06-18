package com.agribot.automaticcapture;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;


class Preview extends   SurfaceView implements SurfaceHolder.Callback {
	
    private static final String TAG = "Preview";
    static String phototakenname;
    Context c;
    SurfaceHolder mHolder;  
    public Camera camera; 
    public int count=0;
    
    TextView textView;
    
    Preview(Context context) {
      super(context);
      c=context;
      
      
      
      
      mHolder = getHolder();  
      mHolder.addCallback(this); 
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
      
      
      
    }

   
    public void surfaceCreated(SurfaceHolder holder) { 
      
      camera = Camera.open();
      
      TextView tv= new TextView(getContext());
      
      
      
     try {
        camera.setPreviewDisplay(holder); 

        camera.setPreviewCallback(new PreviewCallback() { 
          
        	public void onPreviewFrame(byte[] data, Camera camera) {  
            Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
            Preview.this.invalidate();  
            if(count==10)
            {  
            	
            	Preview.this.camera.takePicture(null, null, new PictureCallback() {
            		
            		@Override
					public void onPictureTaken(byte[] data, Camera camera) {

            			
            			phototakenname=""+System.currentTimeMillis();
						FileOutputStream outStream = null;
						try {
							
							outStream = new FileOutputStream("/sdcard/"+phototakenname+".jpg");	
							outStream.write(data);
							outStream.close();
							Log.d("", "Photo Taken Name: " +phototakenname);
							Intent i =new Intent(c, AfterCapture.class);
							c.startActivity(i);
							Log.d("", "onPictureTaken - wrote bytes: " + data.length);
							
							
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
						}
						Log.d("", "onPictureTaken - jpeg");
						
						
						
					}
            		
            		
            		
            		
            		
            	});
            
        	}
            else
            	count++;
            
            System.out.println(count);
            
          }
        });
        
      
      } catch (IOException e) { 
        e.printStackTrace();
      }
    }

    // Called when the holder is destroyed
    public void surfaceDestroyed(SurfaceHolder holder) {  // <14>
      camera.stopPreview();
      camera.release();
      camera = null;
    }

    // Called when holder has changed
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { // <15>
      camera.startPreview();
    }


  }