package com.canghai.utils;

/**
 * Created by dell on 2018/12/28.
 */

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.canghai.bean.wswrtc.Messages;
import com.canghai.inter.DealWs;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 继承默认的监听空实现WebSocketAdapter,重写我们需要的方法(Activity需接受消息时要注册)
 * onTextMessage 收到文字信息
 * onConnected 连接成功
 * onConnectError 连接失败
 * onDisconnected 连接关闭
 */
public class WsListener extends WebSocketAdapter {
    private Map<String,DealWs>  dws = new HashMap<>();
    public void setDealWs(String key,DealWs dw){
        dws.put(key,dw);
    }
    public void removeDealWs(String key){
        dws.remove(key);
    }
    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        //System.out.println("-----onTextMessage   >>>   "+text);
        Messages msg = JSON.parseObject(text, Messages.class);
        //System.out.println("-----msg   >>>   "+msg);
        if(dws != null && dws.size() > 0 ){
            for(String k : dws.keySet()){
                dws.get(k).getMes(msg);
            }
        }
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
            throws Exception {
        super.onConnected(websocket, headers);
        Log.e("___onConnected__","连接成功");
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception)
            throws Exception {
        super.onConnectError(websocket, exception);
        Log.e("___onConnectError__","连接错误：" + exception.getMessage());
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
            throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        Log.e("___onConnectError__","断开连接");
    }

}
