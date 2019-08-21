package com.canghai.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.canghai.R;
import com.canghai.bean.wswrtc.Messages;
import com.canghai.bean.wswrtc.WsUser;
import com.canghai.utils.BaseApplication;
import com.canghai.utils.GetLocation;
import com.canghai.utils.RingtoneUtils;
import com.canghai.utils.ScreenOffAdminReceiver;
import com.canghai.utils.StringUtils;
import com.canghai.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
public class MainActivity extends BaseApplication implements View.OnClickListener{
    @BindView(R.id.btn_shipin)
    Button btn_shipin;
    private Vibrator mVibrator;  //声明一个振动器对象
    private AlertDialog alertDialog;
    private Ringtone ringtone;
    private boolean isVideo = false;
    private Map<String,String> userms = new HashMap<>();
    /*private DevicePolicyManager policyManager;//息屏
    policyManager.lockNow();*/
    private ComponentName adminReceiver;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initWs(this);
        new GetLocation(myuuid,getWs(),(LocationManager) getSystemService(Context.LOCATION_SERVICE));
        btn_shipin.setOnClickListener(this);
        /**
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         */
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        //亮屏
        adminReceiver = new ComponentName(MainActivity.this, ScreenOffAdminReceiver.class);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,  adminReceiver);
        startActivityForResult(intent, 0);

        //启动 WebSocket 服务
        startService(new Intent(this, MainActivity.class));
    }
    public void turnOnScreen() {
        // turn on screen
        Log.v("ProximityActivity", "ON!");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
        mWakeLock.release();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_shipin:
                openChatVideo();
                break;
        }
    }

    @Override
    public void getMes(Messages messages) {
        super.getMes(messages);
        //System.out.println("---------------->>>>>>>"+messages+"<<<<<------------------");
        if(StringUtils.notEmpty(messages.getType()) && "callVideo".equals(messages.getType()) && !isVideo){
            isVideo = true;
            //从全局池中返回一个message实例，避免多次创建message（如new Message）
            Message msg =Message.obtain();
            msg.obj = messages.getUfrom();
            msg.what=1;   //标志消息的标志
            handler.sendMessage(msg);
        }
        if("rev".equals(messages.getType())){
            if(messages.getEvent() == null || "".equals(messages.getEvent())){
                isVideo = true;
                Message msg =Message.obtain();
                msg.obj = messages.getUfrom();
                msg.what=2;   //标志消息的标志
                handler.sendMessage(msg);
            }else if("alert".equals(messages.getEvent())){
                Message msg =Message.obtain();
                msg.obj = messages.getMsg();
                msg.what=0;   //标志消息的标志
                handler.sendMessage(msg);
            }
        }
        if("alert".equals(messages.getType())){
            Message msg =Message.obtain();
            msg.obj = messages.getMsg();
            msg.what=0;   //标志消息的标志
            handler.sendMessage(msg);
        }
    }
    private void openChatVideo(){
        for(WsUser u : getWsUsers()){
            if(u.isPclogin()){
                userms.put(u.getName(),u.getUuid());
            }
        }
        if(userms.size()<1){
            ToastUtil.showToast(this,"暂无可连接的PC端用户",0);
            return;
        }
        String[] names = new String[userms.keySet().size()];
        //Set-->数组
        userms.keySet().toArray(names);
        alertDialog = new AlertDialog.Builder(this)//注意这里的参数要是dialog依赖的acitivity的引用;
                //.setView(View.inflate(this, R.layout.activity_main,null))
                .setTitle("请指定PC端用户！")
               // .setMessage("请指定PC端用户！")
                .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener(){
                    @SuppressLint("MissingPermission")
                    public void onClick(DialogInterface dialog, int which) {
                        String name = names[which];
                        mVibrator.cancel();
                        if(ringtone != null)
                            ringtone.stop();
                        isVideo = false;
                        dialog.dismiss();

                        Messages msg = new Messages();
                        msg.setUaimd(userms.get(name));
                        msg.setType("ask");
                        msg.setUfrom(myuuid);
                        sendMes(msg);
                        userms.clear();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mVibrator.cancel();
                        userms.clear();
                        if(ringtone != null)
                            ringtone.stop();
                        isVideo = false;
                    }
                })
                .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
    private void openChatVideo(String ufrom){
        String name = "";
        for(WsUser u : getWsUsers()){
            if(u.getUuid().equals(ufrom)){
                name = u.getName();
                break;
            }
        }
        alertDialog = new AlertDialog.Builder(this)//注意这里的参数要是dialog依赖的acitivity的引用;
                //.setView(View.inflate(this, R.layout.activity_main,null))
                .setTitle("视频通话请求")
                .setMessage("PC端用户【"+name+"】向您发起现场直播请求，是否开启直播？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mVibrator.cancel();
                        if(ringtone != null)
                            ringtone.stop();
                        isVideo = false;
                        Intent intent =new Intent(MainActivity.this,ChatActivity.class);
                        intent.putExtra("uaimd",ufrom);
                        //启动
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mVibrator.cancel();
                        ringtone.stop();
                        isVideo = false;
                    }
                })
                .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
    private Handler handler =  new Handler(){
        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    String alert = (String) msg.obj;
                    ToastUtil.showToast(MainActivity.this,alert,0);
                    break;
                case 1:
                    /**
                     获取数据，更新UI
                     */
                    turnOnScreen();
                    String ufrom = (String) msg.obj;
                    openChatVideo(ufrom);
                    /**
                     * 四个参数就是——停止 开启 停止 开启
                     * -1不重复，非-1为从pattern的指定下标开始重复
                     */
                    mVibrator.vibrate(new long[]{1000, 4000, 1000, 4000}, 0);
                    //停止1秒，开启震动10秒，然后又停止1秒，又开启震动10秒，-1不重复/0重复.
                    ringtone = RingtoneUtils.getDefaultRingtone(MainActivity.this, RingtoneManager.TYPE_RINGTONE);
                    ringtone.play();
                    break;
                case 2:
                    String uaimd = (String) msg.obj;
                    Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                    intent.putExtra("uaimd",uaimd);
                    //启动
                    startActivity(intent);
                    break;
            }
        }
    };

}