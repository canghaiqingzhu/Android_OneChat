package com.canghai.utils;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统铃声
 * Created by dell on 2019/1/11.
 */

public class RingtoneUtils {
    /**
     * 获取的是铃声的Uri
     * @param ctx
     * @param type
     * @return
     */
    public static Uri getDefaultRingtoneUri(Context ctx, int type) {
        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);
    }
    /**
     * 获取的是铃声相应的Ringtone
     * @param ctx
     * @param type
     * //RingtoneManager.TYPE_NOTIFICATION;通知声音
    //RingtoneManager.TYPE_ALARM; 警告
    //RingtoneManager.TYPE_RINGTONE; 铃声
     */
    public static Ringtone getDefaultRingtone(Context ctx, int type) {
        return RingtoneManager.getRingtone(ctx,
                RingtoneManager.getActualDefaultRingtoneUri(ctx, type));
    }
    /**
     * 播放铃声
     * @param ctx
     * @param type
     */
    public static void PlayRingTone(Context ctx,int type){
        MediaPlayer mMediaPlayer = MediaPlayer.create(ctx,
                getDefaultRingtoneUri(ctx,type));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

    }

    public static List<Ringtone> getRingtoneList(Context mContext,int type){
        List<Ringtone> resArr = new ArrayList<Ringtone>();
        RingtoneManager manager = new RingtoneManager(mContext);
        manager.setType(type);
        Cursor cursor = manager.getCursor();
        int count = cursor.getCount();
        for(int i = 0 ; i < count ; i ++){
            resArr.add(manager.getRingtone(i));
        }
        return resArr;
    }
    public static Ringtone getRingtone(Context mContext,int type,int pos){
        RingtoneManager manager = new RingtoneManager(mContext);
        manager.setType(type);
        return manager.getRingtone(pos);
    }
    public static List<String> getRingtoneTitleList(Context mContext,int type){
        List<String> resArr = new ArrayList<String>();
        RingtoneManager manager = new RingtoneManager(mContext);
        manager.setType(type);
        Cursor cursor = manager.getCursor();
        if(cursor.moveToFirst()){
            do{
                resArr.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
            }while(cursor.moveToNext());
        }
        return resArr;
    }
    public static String getRingtoneUriPath(Context mContext,int type,int pos,String def){
        RingtoneManager manager = new RingtoneManager(mContext);
        manager.setType(type);
        Uri uri = manager.getRingtoneUri(pos);
        return uri==null?def:uri.toString();
    }
    public static Ringtone getRingtoneByUriPath(Context mContext,int type ,String uriPath){
        RingtoneManager manager = new RingtoneManager(mContext);
        manager.setType(type);
        Uri uri = Uri.parse(uriPath);
        return manager.getRingtone(mContext, uri);

    }
}
