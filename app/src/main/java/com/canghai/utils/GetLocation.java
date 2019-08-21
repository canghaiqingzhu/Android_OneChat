package com.canghai.utils;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.canghai.bean.wswrtc.Messages;
import com.neovisionaries.ws.client.WebSocket;

/**
 * Created by dell on 2019/1/8.
 */

public class GetLocation {
    private String myuuid;
    private WebSocket ws;
    private LocationManager locationManager;
    public GetLocation(String myuuid,WebSocket ws,LocationManager locationManager){
        this.locationManager = locationManager;
        this.myuuid = myuuid;
        this.ws = ws;
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true){
                        // 获取一个Message对象，设置what为1
                        Message msg = Message.obtain();
                        msg.what = 1;
                        // 发送这个消息到消息队列中
                        handler.sendMessage(msg);
                        Thread.sleep(1000*60*1);//延时1s
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private Handler handler = new Handler(){
        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    Messages m = new Messages();
                    m.setUfrom(myuuid);
                    m.setLatitude(location.getLatitude());
                    m.setLongitude(location.getLongitude());
                    /*latitude = location.getLatitude(); // 经度
                    longitude = location.getLongitude(); // 纬度*/
                    ws.sendText(JSON.toJSONString(m));
                    System.out.println("经纬度坐标------latitude:"+location.getLatitude()+"-----------------longitude:"+location.getLongitude()+"---------------");
                }
            }
        }
    };
}
