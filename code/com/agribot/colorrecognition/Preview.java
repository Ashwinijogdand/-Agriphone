package com.agribot.colorrecognition;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class Preview  extends ViewGroup implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    private String wbmode = null;

    private void cnstInit(Context context) {
            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public Preview(Context context) {
            super(context);
            cnstInit(context);
    }

    public Preview(Context context, AttributeSet attrs) {
            super(context, attrs);
            cnstInit(context);
    }

    public void zoomIn() {
            Parameters p = mCamera.getParameters();
            if (!p.isZoomSupported())
                    return;
            if (p.getZoom() < p.getMaxZoom())
                    p.setZoom(p.getZoom() + 1);
            mCamera.setParameters(p);
    }

    public void zoomOut() {
            Parameters p = mCamera.getParameters();
            if (!p.isZoomSupported())
                    return;
            if (p.getZoom() > 0)
                    p.setZoom(p.getZoom() - 1);
            mCamera.setParameters(p);
    }

    public void setCamera(Camera camera) {
            mCamera = camera;
            setWB(wbmode);
            if (mCamera != null) {
                    mSupportedPreviewSizes = mCamera.getParameters()
                                    .getSupportedPreviewSizes();
                    requestLayout();
                    if (mPreviewSize != null) {
                            mHolder = mSurfaceView.getHolder();
                            try {
                                    mCamera.setPreviewDisplay(mHolder);
                            } catch (IOException e) {
                                    Log.e(AgribotActivity.TAG, Log.getStackTraceString(e));
                            }
                            mHolder.addCallback(this);
                            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                            surfaceChanged(mHolder, 0, 0, 0);
                    }

            }
    }

    public void setWB(String mode) {
            wbmode = mode;
            if (mCamera != null && mode != null) {
                    Parameters p = mCamera.getParameters();
                    p.setWhiteBalance(mode);
                    mCamera.setParameters(p);
            }
    }

    public void switchCamera(Camera camera) {
            setCamera(camera);
            try {
                    camera.setPreviewDisplay(mHolder);
            } catch (IOException exception) {
                    Log.e(AgribotActivity.TAG, "IOException caused by setPreviewDisplay()",
                                    exception);
            }
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            requestLayout();

            camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // We purposely disregard child measurements because act as a
            // wrapper to a SurfaceView that centers the camera preview instead
            // of stretching it.
            final int width = resolveSize(getSuggestedMinimumWidth(),
                            widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(),
                            heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                    mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                                    height);
            }
    }
/*
    public static void setCameraDisplayOrientation(Activity activity,
                    int cameraId, android.hardware.Camera camera) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay()
                            .getRotation();
            int degrees = 0;
            switch (rotation) {
            case Surface.ROTATION_0:
                    degrees = 0;
                    break;
            case Surface.ROTATION_90:
                    degrees = 90;
                    break;
            case Surface.ROTATION_180:
                    degrees = 180;
                    break;
            case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360; // compensate the mirror
            } else { // back-facing
                    result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
    }
*/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed && getChildCount() > 0) {
                    final View child = getChildAt(0);

                    final int width = r - l;
                    final int height = b - t;

                    int previewWidth = width;
                    int previewHeight = height;
                    if (mPreviewSize != null) {
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                    }

                    // Center the child SurfaceView within the parent.
                    if (width * previewHeight > height * previewWidth) {
                            //Log.e(CTMain.TAG,"AAAAAAA");
                            
                            final int scaledChildWidth = previewWidth * height
                                            / previewHeight;
                            child.layout((width - scaledChildWidth) / 2, 0,
                                            (width + scaledChildWidth) / 2, height);
                    } else {
                            
                            //Log.e(CTMain.TAG,"BBBBBBB");
                            final int scaledChildHeight = previewHeight * width
                                            / previewWidth;
                            child.layout(0, (height - scaledChildHeight) / 2, width,
                                            (height + scaledChildHeight) / 2);
                    }
            }
    }

    public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            try {
                    if (mCamera != null) {
                            mCamera.setPreviewDisplay(holder);
                    }
            } catch (IOException exception) {
                    Log.e(AgribotActivity.TAG, "IOException caused by setPreviewDisplay()",
                                    exception);
            }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                    mCamera.stopPreview();
            }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null)
                    return null;

            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Size size : sizes) {
                    double ratio = (double) size.width / size.height;
                    if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                            continue;
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                            optimalSize = size;
                            minDiff = Math.abs(size.height - targetHeight);
                    }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                    //Log.e(CTMain.TAG,"optNULL");
                    minDiff = Double.MAX_VALUE;
                    for (Size size : sizes) {
                            if (Math.abs(size.height - targetHeight) < minDiff) {
                                    optimalSize = size;
                                    minDiff = Math.abs(size.height - targetHeight);
                            }
                    }
            }
            return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            if (mCamera == null)
                    return;

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            // parameters.setPreviewFormat(ImageFormat.);
            requestLayout();

            mCamera.setParameters(parameters);
            mCamera.startPreview();
    }

}

