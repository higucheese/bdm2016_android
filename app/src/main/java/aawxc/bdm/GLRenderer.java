package aawxc.bdm;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GLRenderer implements GLSurfaceView.Renderer {
    //システム
    private final Context mContext;
    private boolean validProgram=false; //シェーダプログラムが有効

    private float aspect;//アスペクト比
    private static final float viewlength = 5.0f; //視点距離
    private static final float scale_x = 2.0f;
    private static final float scale_y = 1.5f;
    private static final float scale_z = 3.0f;
    private static final float PROPOTION_VALUE = 1f;

    //視点変更テスト変数
    private static float[] rotValue = {0.0f, 0.0f, 0.0f};

    //光源の座標　x,y,z
    private  float[] LightPos={0f,1.5f,3f,1f};//x,y,z,1

    //変換マトリックス
    private  float[] pMatrix=new float[16]; //プロジェクション変換マトリックス
    private  float[] mMatrix=new float[16]; //モデル変換マトリックス
    private  float[] cMatrix=new float[16]; //カメラビュー変換マトリックス

    private Axis MyAxes= new Axis();  //原点周囲の軸表示とためのオブジェクトを作成
    private RectangularWithTex surface1;
    private RectangularWithTex surface2;
    private RectangularWithTex surface3;
    private RectangularWithTex surface4;
    private RectangularWithTex surface5;
    private RectangularWithTex surface6;

    //シェーダのattribute属性の変数に値を設定していないと暴走するのでそのための準備
    private static float[] DummyFloat= new float[1];
    private static final FloatBuffer DummyBuffer=BufferUtil.makeFloatBuffer(DummyFloat);

    GLRenderer(final Context context) {
        mContext = context;
    }

    //サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10,EGLConfig eglConfig) {
        //プログラムの生成
        validProgram = GLES.makeProgram();

        //頂点配列の有効化
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES20.glEnableVertexAttribArray(GLES.normalHandle);
        GLES20.glEnableVertexAttribArray(GLES.texcoordHandle);

        //デプスバッファの有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // カリングの有効化
        GLES20.glEnable(GLES20.GL_CULL_FACE); //裏面を表示しないチェックを行う

        // 透過の有効化
        GLES20.glEnable(GLES20.GL_ALPHA);

        // 裏面を描画しない
        GLES20.glFrontFace(GLES20.GL_CCW); //表面のvertexのindex番号はCCWで登録
        GLES20.glCullFace(GLES20.GL_BACK); //裏面は表示しない

        //光源色の指定 (r, g, b,a)
        GLES20.glUniform4f(GLES.lightAmbientHandle, 0.15f, 0.15f, 0.15f, 1.0f); //周辺光
        GLES20.glUniform4f(GLES.lightDiffuseHandle, 0.5f, 0.5f, 0.5f, 1.0f); //乱反射光
        GLES20.glUniform4f(GLES.lightSpecularHandle, 0.9f, 0.9f, 0.9f, 1.0f); //鏡面反射光

        //背景色の設定(透過のためにコメントアウト)
        //GLES20.glClearColor(0.5f, 0.5f, 1.0f, 1.0f);

        //テクスチャの有効化
        //GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        // 背景とのブレンド方法を設定します。
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);    // 単純なアルファブレンド

        surface1 = new RectangularWithTex(mContext, R.drawable.woodenbox1);
        surface2 = new RectangularWithTex(mContext, R.drawable.woodenbox2);
        surface3 = new RectangularWithTex(mContext, R.drawable.woodenbox3);
        surface4 = new RectangularWithTex(mContext, R.drawable.woodenbox4);
        surface5 = new RectangularWithTex(mContext, R.drawable.woodenbox5);
        surface6 = new RectangularWithTex(mContext, R.drawable.woodenbox6);
    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 glUnused) {
        if (!validProgram) return;
        //シェーダのattribute属性の変数に値を設定していないと暴走するのでここでセットしておく。この位置でないといけない
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3, GLES20.GL_FLOAT, false, 0, DummyBuffer);
        GLES20.glVertexAttribPointer(GLES.normalHandle, 3, GLES20.GL_FLOAT, false, 0, DummyBuffer);
        GLES20.glVertexAttribPointer(GLES.texcoordHandle, 3, GLES20.GL_FLOAT, false, 0, DummyBuffer);

        GLES.disableTexture();  //テクスチャ機能を無効にする。（デフォルト
        GLES.enableShading();   //シェーディング機能を有効にする。（デフォルト）

        //画面のクリア
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
                GLES20.GL_DEPTH_BUFFER_BIT);

        //プロジェクション変換（射影変換）--------------------------------------
        //透視変換（遠近感を作る）
        //カメラは原点に有り，z軸の負の方向を向いていて，上方向はy軸＋方向である。
        GLES.gluPerspective(pMatrix,
                45.0f,  //Y方向の画角
                aspect, //アスペクト比
                1.0f,   //ニアクリップ　　　z=-1から
                100.0f);//ファークリップ　　Z=-100までの範囲を表示することになる
        GLES.setPMatrix(pMatrix);

        //カメラビュー変換（視野変換）-----------------------------------
        //カメラ視点が原点になるような変換
        Matrix.setLookAtM(cMatrix, 0,
                0.0f, 0.0f, 10.0f,
                (float) (viewlength * Math.sin(rotValue[0])),  //カメラの視点 x
                (float) (viewlength * Math.sin(-rotValue[1])),  //カメラの視点 y
                viewlength,  //カメラの視点 z
                0.0f, 1.0f, 0.0f);
        //カメラビュー変換はこれで終わり。
        GLES.setCMatrix(cMatrix);

        //cMatrixをセットしてから光源位置をセット
        GLES.setLightPosition(LightPos);

        GLES.disableShading(); //シェーディング機能は使わない

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        //Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        MyAxes.draw(1f, 1f, 1f, 1f, 10.f, 2f);//座標軸の描画本体

        //GLES.enableShading(); //シェーディング機能を使う設定に戻す

        GLES.enableTexture();

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        Matrix.translateM(mMatrix, 0, 0f, 0f, 0.5f);
        Matrix.rotateM(mMatrix, 0, 180f, 0, 1, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        surface1.draw(1f, 1f, 1f, 1f, 5f);

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        Matrix.translateM(mMatrix, 0, 0f, 0f, -0.5f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        surface3.draw(1f, 1f, 1f, 1f, 5f);

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        Matrix.translateM(mMatrix, 0, 0.5f, 0f, 0f);
        Matrix.rotateM(mMatrix, 0, -90f, 0, 1, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        surface2.draw(1f, 1f, 1f, 1f, 5f);

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        Matrix.translateM(mMatrix, 0, -0.5f, 0f, 0f);
        Matrix.rotateM(mMatrix, 0, 90f, 0, 1, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        surface4.draw(1f, 1f, 1f, 1f, 5f);

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        Matrix.translateM(mMatrix, 0, 0f, 0.5f, 0f);
        Matrix.rotateM(mMatrix, 0, 90f, 1, 0, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        surface5.draw(1f, 1f, 1f, 1f, 5f);

        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.scaleM(mMatrix, 0, scale_x, scale_y, scale_z);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[0]), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, (float)Math.toDegrees(rotValue[1]), 1, 0, 0);
        Matrix.translateM(mMatrix, 0, 0f, -0.5f, 0f);
        Matrix.rotateM(mMatrix, 0, -90f, 1, 0, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        surface6.draw(1f, 1f, 1f, 1f, 5f);


        GLES.disableTexture();
    }

    public static void setRotationValue(float yaw, float pitch, float roll){
        /*
        rotValue[0] = yaw * PROPOTION_VALUE;
        rotValue[1] = pitch * PROPOTION_VALUE;
        rotValue[2] = roll * PROPOTION_VALUE;
        */
    }

    private float Scroll[] = {0f, 0f}; //１本指のドラッグ[rad]
    public void setScrollValue(float DeltaX, float DeltaY) {
        Scroll[0] += DeltaX * 0.01;
        if (3.14f<Scroll[0]) Scroll[0]=3.14f;
        if (Scroll[0]<-3.14) Scroll[0]=-3.14f;
        Scroll[1] -= DeltaY * 0.01;
        if (1.57f<Scroll[1]) Scroll[1]=1.57f;
        if (Scroll[1]<-1.57) Scroll[1]=-1.57f;
        rotValue[1]=-Scroll[1];
        rotValue[0]=Scroll[0];
    }

}
