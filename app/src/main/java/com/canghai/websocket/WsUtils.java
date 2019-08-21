package com.canghai.websocket;

import com.canghai.inter.DealWs;
import com.canghai.utils.BaseApplication;
import com.canghai.utils.WsListener;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

/**
 * Created by dell on 2019/1/8.
 */

public class WsUtils {
    private static WsListener wsListener;
    private static WebSocket ws;
    public static WebSocket getWs(){return WsUtils.ws;}
    public static WsListener getWsListener(){return WsUtils.wsListener;}
    public static void initWs(DealWs dw){
        try {
            ws = new WebSocketFactory().createSocket("ws://192.168.199.152:8080/wsv/phone/1140018", 30000) //ws地址，和设置超时时间
                    .setFrameQueueSize(5)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(wsListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
            getWsListener().setDealWs("BaseApplication",dw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
