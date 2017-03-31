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
    private Camera cam;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        cam = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // tell the camera where to draw the preview
        try {
            cam.setPreviewDisplay(holder);
            cam.startPreview();
        } catch (IOException e) {
            Log.d("surfaceCreated", "Error setting camerapreview:" + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // if your preview can change or rotate, take care of those events here
        // make sure to stop the preview before resizing or reformatting it

        if (mHolder.getSurface() == null) {
            //preview surface does not exist
            return;
        }

        //stop preview before making changes
        try {
            cam.stopPreview();
        } catch (Exception e) {
            //ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here
        try {
            cam.setPreviewDisplay(mHolder);
            cam.startPreview();
        } catch (Exception e) {
            Log.d("surfaceChanged", "Error starting camera preview: " + e.getMessage());
        }
    }
}
