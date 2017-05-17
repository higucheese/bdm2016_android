package aawxc.bdm;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SubActivity extends Activity {

    private SubGLSurfaceView mSubGLSurfaceView;
    private GestureDetector mGestureDetector = null;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        //don't show navigation bar
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        setContentView(R.layout.activity_sub);
    }

    @Override
    public  boolean onTouchEvent(MotionEvent event){
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onResume(){
        super.onResume();

        mSubGLSurfaceView = new SubGLSurfaceView(mContext);
        FrameLayout glsurfaceview = (FrameLayout) findViewById(R.id.opengl_view);
        glsurfaceview.addView(mSubGLSurfaceView);
        mSubGLSurfaceView.onResume();
        mGestureDetector = new GestureDetector(this, (GestureDetector.OnGestureListener)mSubGLSurfaceView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSubGLSurfaceView.onPause();
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.s2m_button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
