package aawxc.bdm;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    private CameraPreview mPreview;
    private SensorManager mSensorManager;
    private TextView stateView;
    private float sensorX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onResume(){
        super.onResume();

        mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Listenerの登録
        Sensor accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        //Listenerを解除
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            sensorX = event.values[0];

            String strTmp = "加速度センサー\n"
                    + " X: " + sensorX + "\n";
            stateView.setText(strTmp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}