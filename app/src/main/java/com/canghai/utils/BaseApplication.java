package com.canghai.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.canghai.bean.wswrtc.Messages;
import com.canghai.bean.wswrtc.WsUser;
import com.canghai.inter.DealWs;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 */
public class BaseApplication extends AppCompatActivity implements  DealWs{
	private static WebSocket ws;
	private static final int MY_PERMISSION_REQUEST_CODE = 10000;
	private static final int PERMISSION_REQUESTCODE = 1;
	protected static String myuuid = "1140018";
	protected static void initWs(DealWs dw){
		try {
			ws = new WebSocketFactory().createSocket("ws://192.168.199.152:8080/wsv/phone/"+myuuid, 30000) //ws地址，和设置超时时间
					.setFrameQueueSize(5)//设置帧队列最大值为5
					.setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
					.addListener(wsListener = new WsListener())//添加回调监听
					.connectAsynchronously();//异步连接
			getWsListener().setDealWs("BaseApplication",dw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static List<WsUser> wsUsers;
	public static List<WsUser> getWsUsers(){return wsUsers;}
	private static WsListener wsListener;
	private static Context context;
	public static WebSocket getWs(){return BaseApplication.ws;}
	public static String getUuid(){return BaseApplication.myuuid;}
	public static WsListener getWsListener(){return BaseApplication.wsListener;}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		initCompat();
	}
	private void initCompat(){
		// 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
		ActivityCompat.requestPermissions(
				this,
				new String[] {
						Manifest.permission.INTERNET,//网络访问权限
						Manifest.permission.SYSTEM_ALERT_WINDOW,
						Manifest.permission.CAMERA,
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.MODIFY_AUDIO_SETTINGS,
						Manifest.permission.WAKE_LOCK,
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.ACCESS_NETWORK_STATE,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION
				},
				MY_PERMISSION_REQUEST_CODE
		);
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			//没有授权
			finish();
		}
	}
	public void sendMes(Messages messages){
		getWs().sendText(JSON.toJSONString(messages));
	}
	@Override
	public void getMes(Messages messages) {
		//System.out.println("-----------------------------BaseApplication-----------------------------------------");
		if(messages.getUsers()!=null && messages.getUsers().size()>0){
			System.out.println("-------------------------------------"+JSON.toJSONString(messages.getUsers())+"----------------------------------------------");
			wsUsers = messages.getUsers();
		}

	}
	/**--------------------------------------------------返回监听事件------------（开始）-----------------------------------------------------------**/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if(!returnToMonitor()){
					dealFinish();
					finish();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	/*返回监听事件 todo:请在子类重写returnToMonitor方法*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK  && returnToMonitor()){
			return true;//拦截事件传递,从而屏蔽back键。
		}else if(keyCode == KeyEvent.KEYCODE_BACK  && !returnToMonitor()){
			dealFinish();
			return super.onKeyDown(keyCode, event);
		}else
			return false;
	}
	/**
	 * 是否返回监听处理类  ：需在子类重写     *   "BACK"返回   "HOME"home    "RECENT"多任务键
	 * @return true 需阻断  false 无需阻断
	 */
	public boolean returnToMonitor(){
		//ToastUtil.showToast(this,"returnToMonitor",0);
		return false;
	}
	/*关闭前执行的方法*/
	public void dealFinish(){
	}
/**--------------------------------------------------返回监听事件---------(结束)--------------------------------------------------------------**/

}
