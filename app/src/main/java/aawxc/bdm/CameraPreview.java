package aawxc.bdm;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by aawxc on 2017/03/31.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context) {
        super(context);

        // Surface Holderのコールバックを登録
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = getCameraInstance();
        // tell the camera where to draw the preview
        try {
            if(holder == null){
                return;
            }
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("surfaceCreated", "Error setting camerapreview:" + e.getMessage());
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception e) {
            Log.e("Camera.open()", "Error opening camera", e);
        }
        return c;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // if your preview can change or rotate, take care of those events here
        // make sure to stop the preview before resizing or reformatting it

        if (mHolder.getSurface() == null) {
            //preview surface does not exist
            return;
        }

        // set preview size and make any resize, rotate or reformatting changes here
        try {
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("surfaceChanged", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}