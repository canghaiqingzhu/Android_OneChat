package com.canghai.webrtc;

import android.opengl.EGLContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.canghai.bean.wswrtc.Messages;
import com.canghai.inter.DealWs;
import com.canghai.utils.BaseApplication;
import com.canghai.utils.StringUtils;

import org.json.JSONException;
import org.webrtc.AudioSource;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;

import java.util.LinkedList;
//https://blog.csdn.net/xyblog/article/details/49738137
public class WebRtcOneClient implements DealWs,SdpObserver, PeerConnection.Observer {
    private final static String TAG = WebRtcOneClient.class.getCanonicalName();
    /*private VideoCapturer prevc = null;
    private VideoCapturer endvc = null;
    private int localCammer = 0;*/
    private String uaimd;
    private WebRtcOneClient the = this;
    private PeerConnectionFactory factory;
    private PeerConnection pc;
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream localMS;
    private VideoSource videoSource;
    private  AudioSource audioSource;
    private RtcListener mListener;
    Command init = new CreateOfferCommand();
    Command offer = new CreateAnswerCommand();
    Command answer = new SetRemoteSDPCommand();
    Command candidate = new AddIceCandidateCommand();
    /**
     * Implement this interface to be notified of events.
     */
    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {}
    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            mListener.onStatusChanged("DISCONNECTED");
        }
    }
    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) { }
    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        JSONObject payload = new JSONObject();
        payload.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
        payload.put("sdpMid", iceCandidate.sdpMid);
        payload.put("candidate", iceCandidate.sdp);
        sendMessage("candidate", payload);
    }
    @Override
    public void onAddStream(MediaStream mediaStream) {
        mListener.onAddRemoteStream(mediaStream);
    }
    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        mListener.onRemoveRemoteStream();
    }
    @Override
    public void onDataChannel(DataChannel dataChannel) {}
    @Override
    public void onRenegotiationNeeded() {}
    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        JSONObject payload = new JSONObject();
        payload.put("type", sessionDescription.type.canonicalForm());
        payload.put("sdp", sessionDescription.description);
        sendMessage(sessionDescription.type.canonicalForm(), payload);
        pc.setLocalDescription(this, sessionDescription);
    }
    @Override
    public void onSetSuccess() {}
    @Override
    public void onCreateFailure(String s) {}
    @Override
    public void onSetFailure(String s) {}
    public interface RtcListener {
        void onCallReady(String callId);
        void onStatusChanged(String newStatus);
        void onLocalStream(MediaStream localStream);
        void onAddRemoteStream(MediaStream remoteStream);
        void onRemoveRemoteStream();
    }
    public interface Command {
        void execute(String peerId, JSONObject payload);
    }
    /**
     ClientA首先创建PeerConnection对象，然后打开本地音视频设备，将音视频数据封装成MediaStream添加到PeerConnection中。
     ClientA调用PeerConnection的CreateOffer方法创建一个用于offer的SDP对象，SDP对象中保存当前音视频的相关参数。ClientA通过PeerConnection的SetLocalDescription 方法将该SDP对象保存起来，并通过Signal服务器发送给ClientB。
     ClientB接收到ClientA发送过的offer SDP对象，通过PeerConnection的SetRemoteDescription方法将其保存起来，并调用PeerConnection的CreateAnswer方法创建一个应答的SDP对象，通过PeerConnection的SetLocalDescription的方法保存该应答SDP对象并将它通过Signal服务器发送给ClientA。
     ClientA接收到ClientB发送过来的应答SDP对象，将其通过PeerConnection的SetRemoteDescription方法保存起来。
     在SDP信息的offer/answer流程中，ClientA和ClientB已经根据SDP信息创建好相应的音频Channel和视频Channel并开启Candidate数据的收集，Candidate数据可以简单地理解成Client端的IP地址信息（本地IP地址、公网IP地址、Relay服务端分配的地址）。
     当ClientA收集到Candidate信息后，PeerConnection会通过OnIceCandidate接口给ClientA发送通知，ClientA将收到的Candidate信息通过Signal服务器发送给ClientB，ClientB通过PeerConnection的AddIceCandidate方法保存起来。同样的操作ClientB对ClientA再来一次。
     */
    private class CreateOfferCommand implements Command {
        public void execute(String peerId, JSONObject payload) {
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("----------------------------CreateOfferCommand-----------------------------------");
            System.out.println("---------------------------------------------------------------------------------");
            pc.createOffer(the, pcConstraints);
        }
    }
    private class CreateAnswerCommand implements Command {
        public void execute(String peerId, JSONObject payload){
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("-------------------------------CreateAnswerCommand-------------------------------");
            System.out.println("---------------------------------------------------------------------------------");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            pc.setRemoteDescription(the, sdp);
            pc.createAnswer(the, pcConstraints);
        }
    }
    private class SetRemoteSDPCommand implements Command {
        public void execute(String peerId, JSONObject payload){
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("----------------------------SetRemoteSDPCommand----------------------------------");
            System.out.println("---------------------------------------------------------------------------------");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            pc.setRemoteDescription(the, sdp);
        }
    }
    private class AddIceCandidateCommand implements Command {
        public void execute(String peerId, JSONObject payload){
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("----------------------------AddIceCandidateCommand-------------------------------");
            System.out.println("---------------------------------------------------------------------------------");
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("sdpMid"),
                        payload.getInteger("sdpMLineIndex"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }
    /**
     * Send a message through the signaling server
     * @param type    type of message
     * @param payload payload of message
     * @throws JSONException
     */
    public void sendMessage( String type, JSONObject payload){
        Messages msg = new Messages();
        msg.setUfrom(BaseApplication.getUuid());
        msg.setUaimd(uaimd);
        msg.setEvent(type);
        if(payload != null){
            if("candidate".equals(type)){
                JSONObject candidate = new JSONObject();
                candidate.put("candidate", payload);
                msg.setData(candidate);
            }else{
                JSONObject sdp = new JSONObject();
                sdp.put("sdp", payload);
                msg.setData(sdp);
            }
        }

        BaseApplication.getWs().sendText(JSON.toJSONString(msg));
    }
    public WebRtcOneClient(RtcListener listener,String uaimd, PeerConnectionParameters params, EGLContext mEGLcontext) {
        this.mListener = listener;
        this.uaimd = uaimd;
        this.pcParams = params;
        PeerConnectionFactory.initializeAndroidGlobals(listener, true, true,
                params.videoCodecHwAcceleration, mEGLcontext);
        this.factory = new PeerConnectionFactory();
        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
        BaseApplication.getWsListener().setDealWs("WebRtcOneClient",this);
    }
    @Override
    public void getMes(Messages msg) {
        if(pc != null && StringUtils.notEmpty(msg.getEvent()) && msg.getData() != null){
            if("candidate".equals(msg.getEvent())){
                JSONObject payload = JSON.parseObject(msg.getData().toString());
                JSONObject can = JSON.parseObject(payload.getString("candidate"));
                candidate.execute(msg.getUfrom(),can );
            }else{
                JSONObject payload = JSON.parseObject(msg.getData().toString());
                if(payload.containsKey("sdp")){
                    JSONObject can = JSON.parseObject(payload.getString("sdp"));
                    start(msg.getUfrom(),msg.getEvent(),can);
                }else{
                    start(msg.getUfrom(),msg.getEvent(),payload);
                }
            }
        }
    }
    /**Call this method in Activity.onPause()*/
    public void onPause() {
        if (videoSource != null) videoSource.stop();
    }
    /**Call this method in Activity.onResume()*/
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }
    /**Call this method in Activity.onDestroy()*/
    public void onDestroy() {
        /*pc.removeStream(localMS);
        if(localMS!=null)
        localMS.dispose();*/
        if (audioSource != null) audioSource.dispose();
        onPause();
        pc.close();
        factory.dispose();

    }
    /** Start the client.
     * <p>
     * Set up the local stream and notify the signaling server.
     * Call this method after onCallReady.*/
    public void start(String id,String type,JSONObject payload) {
        if("init".equals(type))
            setCamera();
        if("offer".equals(type))
            offer.execute(id, payload);
        else if("answer".equals(type))
            answer.execute(id, payload);
        else
            init.execute(id, payload);
    }
    private void setCamera() {
        localMS = factory.createLocalMediaStream("ARDAMS");
        if (pcParams.videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));
            videoSource = factory.createVideoSource(getVideoCapturer(), videoConstraints);
            localMS.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));
        }
        audioSource = factory.createAudioSource(new MediaConstraints());
        localMS.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));
        pc.addStream(localMS); //, new MediaConstraints()
        mListener.onLocalStream(localMS);
        mListener.onStatusChanged("CONNECTING");
    }
    private VideoCapturer getVideoCapturer() {
        /*prevc = VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice());
        endvc = VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfBackFacingDevice());
        VideoCapturer vc = prevc;
        localCammer = 0;
        if(vc == null){
            localCammer = 1;
            vc = endvc;
        }*/
        return VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfBackFacingDevice());
    }
}
