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
        mGestureDetector = new GestureDetector(this, (GestureDetector.OnGestureListener)mMyGLSurfaceView);


        mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Listenerの登録
        Sensor accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        mMyGLSurfaceView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //Listenerを解除
        mSensorManager.unregisterListener(this);

        mMyGLSurfaceView.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        String tmpStr = "state_view\n";

        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            axisX = event.values[0];
            axisY = event.values[1];
            axisZ = event.values[2];
            tmpStr += "回転ベクトルセンサー\n";
            tmpStr += " X: " + axisX + "\n";
            tmpStr += " Y: " + axisY + "\n";
            tmpStr += " Z: " + axisZ + "\n";
        }

        stateView.setText(tmpStr);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}