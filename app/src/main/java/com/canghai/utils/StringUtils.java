package com.canghai.utils;



import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {
	/** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
	public static boolean isEmpty(String value) {
		return !notEmpty(value);
	}
	public static boolean notEmpty(String value) {
		if (value != null && !"".equalsIgnoreCase(value.trim())
				&& !"null".equalsIgnoreCase(value.trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static String getStringByHtml(String value){
		Spanned spand = null;
		if(notEmpty(value)){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				 spand = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY);
			} else {
				spand = Html.fromHtml(value);
			}
		}
		if(spand!=null)
			value = spand.toString();
		 if(StringUtils.isEmpty(value))
		 	value ="";
		return  value;

	}

	public static List<String>  getListByMap (String key,Map<String,Object> map){
		List<String> list = null;
		if(map.containsKey(key)){
			Object object = map.get(key);
			if(object instanceof  List){
				list = (List) object;
			}
		}
		if(list ==null)
		   list = new ArrayList<>();
		return  list;
	}


    public static Map<String,String>  getMapByMap (String key,Map<String,Object> map){
        Map<String,String> mapvalue = null;
        if(map.containsKey(key)){
            Object object = map.get(key);
            if(object instanceof  Map){
                mapvalue = (Map) object;
            }
        }
        if(mapvalue ==null)
            mapvalue = new HashMap<>();
        return  mapvalue;
    }

	/**
	 * 把时间格式转为 yyyy-MM-dd hh:mm:ss
	 * @param date
	 * @return
	 */
    public static String getDateString(Date date){
		String  sdate = "";
		if(date!=null){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			sdate = simpleDateFormat.format(date);

		}
		return sdate;
	}

	/**
	 * 把时间格式转为 yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String getDateYYYYMMDD(Date date){
		String  sdate = "";
		if(date!=null){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			sdate = simpleDateFormat.format(date);

		}
		return sdate;
	}

	/**
	 * 在map得到string
	 */
	public static String getStringByMap(String key,Map map){
		Object object = map.get(key);
		if(object!=null)
			return object.toString();
		else
			return "";

	}

	/**
	 * 防止s为null
	 * @param s
	 * @return
	 */
	public static String getNoEmptyString(String s){
		if(StringUtils.isEmpty(s))
			return "";
		else
			return "";
	}

	/**
	 *
	 *
	 * 处理身份证
	 */
	public static String getHandleCid(String cid){
		if(notEmpty(cid)){
			if(cid.length()>15){
				cid =	cid.substring(0,6)+"********"+cid.substring(14,cid.length());
			}
		}
		return cid;
	}


	/**
	 * 把时间格式转为 yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String getWorkDateString(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
		String sdate = simpleDateFormat.format(new Date());
		return sdate;

	}


}
