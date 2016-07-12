package com.example.ljd.mylibstreaming;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ljd.mylibstreaming.LibRTSP.quality.VideoQuality;
import com.example.ljd.mylibstreaming.LibRTSP.rtsp.RtspServer;
import com.example.ljd.mylibstreaming.LibRTSP.session.Session;
import com.example.ljd.mylibstreaming.LibRTSP.utility.RunState;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MediaProjectionManager mMediaProjectionManager;
    public static MediaProjection mMediaProjection;
    ToggleButton tbtScreenCaptureService;
    private ScreenCaptureService.MyBinder mBinder;
    private boolean SERVICE_HAS_BIND = false;
    private boolean SERVICE_IS_START = false;
    private boolean SC_IS_RUN = false;

    private static final int CAPTURE_CODE = 115;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 123;
    private static final int INTERNET_REQUEST_CODE = 124;
    private int mScreenDensity;
    private int mScreenWidth;
    private int mScreenHeight;

    private int mDestinationPort = 5006;
    private int mOriginPort = 1234;


    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbtScreenCaptureService = (ToggleButton) findViewById(R.id.tbt_screen_capture_service);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (!RunState.getInstance().isRun()) {
            //如果刚启动的话
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), CAPTURE_CODE);
            GetWindowInfo();
            AskForPermission();
        }
        myBindService();

        tbtScreenCaptureService.setChecked(RunState.getInstance().isRun());
        tbtScreenCaptureService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (RunState.getInstance().isRun()) {
                    mBinder.StopScreenCapture();
                    RunState.getInstance().setRun(false);
                    Toast.makeText(MainActivity.this, "屏幕录制服务停止运行", Toast.LENGTH_SHORT).show();
                } else {
                    myShareScreen();
                    RunState.getInstance().setRun(true);
                    Toast.makeText(MainActivity.this, "屏幕录制服务开始运行", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("onActivityResult","onActivityResult");
        if (requestCode == CAPTURE_CODE) {
            if (resultCode != RESULT_OK) {
                return;
            }else{
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode,data);

            }
            SetSession();
        }
    }
    private void AskForPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            Log.v("AskForPermission()", "AskForPermission()");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.v("AskForPermission()", "requestPermissions");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                Log.v("onActivityResult", "myThread.start(); start");
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.v("AskForPermission()", "requestPermissions");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v("PermissionsResult","onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.v("PermissionsResult","myThread.start(); start");
                }else{
                    Log.i("PermissionsResult","WRITE_EXTERNAL_STORAGE permission denied");
                }
                break;
            default:
                break;
        }
    }

    //绑定服务时调用
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (ScreenCaptureService.MyBinder)service;
        }
        //解绑时不会调用
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private RtspServer.CallbackListener mRtspCallbackListener = new RtspServer.CallbackListener() {

        @Override
        public void onError(RtspServer server, Exception e, int error) {
            // We alert the user that the port is already used by another app.
            if (error == RtspServer.ERROR_BIND_FAILED) {

            }
        }

        @Override
        public void onMessage(RtspServer server, int message) {
        }

    };

    private void GetWindowInfo(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        Log.v(TAG,"mScreenWidth is :"+mScreenWidth+";mScreenHeight is :"+mScreenHeight+"mScreenDensity is :"+mScreenDensity);
    }

    private void SetSession(){
        Log.v(TAG,"SetSessionBuilder()");
        session = new Session(1,
                new VideoQuality(mScreenWidth,mScreenHeight,30,8000000,mScreenDensity),200,
                mOriginPort,mMediaProjection);
        session.setDestinationPort(mDestinationPort);
    }

    private void myShareScreen(){
        myStartService();
        mBinder.StartScreenCapture(session);//启动服务
    }

    @Override
    protected void onDestroy() {
        myUnbindService();
        if(!RunState.getInstance().isRun()){
            myStopService();
        }
        super.onDestroy();

    }

    private void myBindService(){
        Intent bindIntent = new Intent(MainActivity.this, ScreenCaptureService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        Log.v(TAG,"bindService");
    }

    private void myUnbindService(){
            unbindService(connection);
            Log.v(TAG,"unbindService");
    }

    private void myStartService(){
        Intent intent = new Intent(MainActivity.this, ScreenCaptureService.class);
        startService(intent);
        Log.v(TAG,"startService");
    }

    private void myStopService(){
        Intent stopIntent = new Intent(MainActivity.this, ScreenCaptureService.class);
        stopService(stopIntent);
        releaseEncoder();
    }


    private void releaseEncoder() {
        Log.d(TAG, "releasing encoder objects");

        if(mMediaProjection!=null){
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }


}
