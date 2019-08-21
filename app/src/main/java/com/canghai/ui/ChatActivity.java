package com.canghai.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.canghai.R;
import com.canghai.bean.wswrtc.Messages;
import com.canghai.utils.BaseApplication;
import com.canghai.webrtc.PeerConnectionParameters;
import com.canghai.webrtc.PermissionChecker;
import com.canghai.webrtc.WebRtcOneClient;

import org.webrtc.AudioTrack;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class ChatActivity extends BaseApplication implements View.OnClickListener,WebRtcOneClient.RtcListener {
    private final static int VIDEO_CALL_SENT = 666;
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    @BindView(R.id.glview_call)
    GLSurfaceView vsv;
    @BindView(R.id.preview)
    SurfaceView preview;
    @BindView(R.id.ib_close)
    TextView ib_close;
    @BindView(R.id.ib_screen_change)
    TextView ib_screen_change;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private static final String[] RequiredPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    protected PermissionChecker permissionChecker = new PermissionChecker();
    public WebRtcOneClient client;
    private String uaimd;
    public static final String TAG = ChatActivity.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ButterKnife.bind(this);
        getWsListener().setDealWs("ChatActivity",this);
        uaimd = getIntent().getStringExtra("uaimd");
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        // local and remote render
        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, true);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        ib_close.setOnClickListener(this);
        ib_screen_change.setOnClickListener(this);
        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = intent.getData().getPathSegments();
        }
        checkPermissions();
    }
    private void checkPermissions(){
        permissionChecker.verifyPermissions(this, RequiredPermissions, new PermissionChecker.VerifyPermissionsCallback() {
            @Override
            public void onPermissionAllGranted(){

            }
            @Override
            public void onPermissionDeny(String[] permissions) {
                Toast.makeText(ChatActivity.this, "Please grant required permissions.", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void init() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y,
                30, 1, VIDEO_CODEC_VP9, true,
                1, AUDIO_CODEC_OPUS, true);
        client = new WebRtcOneClient(this,uaimd, params, VideoRendererGui.getEGLContext());
         answer(null);

    }
    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
        if (client != null) {
            client.onPause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
        if (client != null) {
            client.onResume();
        }
    }
    @Override
    public void onCallReady(String callId) {
       //if (callId != null) {
            answer(callId);
       /*}else {
            call();
       }*/
    }
    public void answer(String callId){
        //client.sendMessage("init", null);
        /*Messages msg = new Messages();
        msg.setUfrom(myuuid);
        msg.setEvent("init");
        //client.emit("message", message);
        getWs().sendText(JSON.toJSONString(msg));*/
        startCam(myuuid,"init",null);
        //hm.get("offer").execute();
    }
    public void startCam(String id,String type,JSONObject payload) {
        // Camera settings
        if (PermissionChecker.hasPermissions(this, RequiredPermissions)) {
            client.start(myuuid,"init",null);
        }
    }
    public void call() {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_TEXT,myuuid);
        //msg.putExtra(Intent.EXTRA_TEXT, mSocketAddress + callId);
        msg.setType("text/plain");
        startActivityForResult(Intent.createChooser(msg, "Call someone :"), VIDEO_CALL_SENT);
    }
    @Override
    public void onStatusChanged(String newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onLocalStream( MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }
    @Override
    public void onAddRemoteStream(MediaStream remoteStream) {
        //remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));

        //AudioTrack at = remoteStream.audioTracks.get(0);
       /*remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        VideoRendererGui.update(remoteRender,remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_X, REMOTE_Y, scalingType, false);
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, false);*/
    }
    @Override
    public void onRemoveRemoteStream() {
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void dealFinish() {
        Messages msg = new Messages();
        msg.setUfrom(myuuid);
        msg.setUaimd(uaimd);
        msg.setEvent("close");
        getWsListener().removeDealWs("ChatActivity");
        getWs().sendText(JSON.toJSONString(msg));
        if (client != null) {
            client.onDestroy();
            client = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_close:
                dealFinish();
                finish();
                break;
            case R.id.ib_screen_change:
                int mCurrentOrientation = getResources().getConfiguration().orientation;
                if ( mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if ( mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE ) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
        }
    }

    @Override
    public void getMes(Messages messages) {
        System.out.println("---------------->>>>>>>"+messages+"<<<<<------------------");
        if(("alert".equals(messages.getType()) || "callVclose".equals(messages.getType()) ) && "close".equals(messages.getEvent())){
            Message msg =Message.obtain();
            msg.obj = messages.getMsg();
            msg.what=0;   //标志消息的标志
            handler.sendMessage(msg);
        }
    }
    private void closAlert(String alert){
        Thread syncTask = new Thread(new Runnable() {
            @Override
            public void run() {
                // 执行耗时操作
                try{
                    Thread.sleep(5000);  // 延迟5秒
                    Message msg =Message.obtain();
                    msg.what=1;   //标志消息的标志
                    handler.sendMessage(msg);
                }catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        syncTask.start();
        AlertDialog alertDialog = new AlertDialog.Builder(this)//注意这里的参数要是dialog依赖的acitivity的引用;
                //.setView(View.inflate(this, R.layout.activity_main,null))
                .setTitle("关闭提示")
                .setMessage(alert+"[5秒后自动关闭]")
                .setNegativeButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            syncTask.interrupt();
                            syncTask.join();
                        }catch (Exception  e){
                            e.printStackTrace();
                        }finally {
                            dealFinish();
                            finish();
                        }
                    }
                })
                .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
    private Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    closAlert((String) msg.obj);
                    //ToastUtil.showToast(getApplicationContext(),alert,0);
                    break;
                case 1:
                    dealFinish();
                    finish();
                    break;
            }
        }
    };
}
