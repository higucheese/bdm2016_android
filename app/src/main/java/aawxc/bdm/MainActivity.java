package aawxc.bdm;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

    private MyGLSurfaceView mMyGLSurfaceView;
    private GestureDetector mGestureDetector = null;

    private CameraPreview mPreview;
    private SensorManager mSensorManager;
    private final float[] mRotationMatrix = new float[16];
    private final float[] mOrientation = new float[9];
    private TextView stateView;

    private Context mContext;

    private float axisX;
    private float axisY;
    private float axisZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        //don't show navigation bar
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        stateView = (TextView) findViewById(R.id.state_view);

    }

    @Override
    public  boolean onTouchEvent(MotionEvent event){
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onResume(){
        super.onResume();

        mMyGLSurfaceView = new MyGLSurfaceView(mContext);
        FrameLayout glsurfaceview = (FrameLayout) findViewById(R.id.opengl_view);
        glsurfaceview.addView(mMyGLSurfaceView);
        mMyGLSurfaceView.onResume();

        mGestureDetector = new GestureDetector(this, (GestureDetector.OnGestureListener)mMyGLSurfaceView);

        mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Sensor accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause(){
        super.onPause();

        mMyGLSurfaceView.onPause();

        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        String tmpStr = "state_view\n";

        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, mRotationMatrix);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);
            tmpStr += "Orientation\n";
            tmpStr += " mOrientation[0]: " + mOrientation[0] + "\n";
            tmpStr += " mOrientation[1]: " + mOrientation[1] + "\n";
            tmpStr += " mOrientation[2]: " + mOrientation[2] + "\n";
        }

        GLRenderer.setRotationValue(mOrientation[0], mOrientation[1], mOrientation[2]);

        stateView.setText(tmpStr);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}