package aawxc.bdm;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by aawxc on 2017/04/03.
 */

public class MyGLSurfaceView extends GLSurfaceView implements GestureDetector.OnGestureListener {

    private final Context mContext;
    private GLRenderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);
        mContext = context;
        setEGLContextClientVersion(2);
        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        renderer = new GLRenderer(context);
        setRenderer(renderer);
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distx, float disty) {
        renderer.setScrollValue(distx, disty);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
    }
    @Override

    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }
}
