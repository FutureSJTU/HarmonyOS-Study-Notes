package com.liangzili.future_bilibilicard.utils;

import com.liangzili.future_bilibilicard.database.PreferenceDataBase;
import ohos.agp.components.webengine.CookieStore;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.HashMap;
import java.util.Map;

public class CookieUtils {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG,0x0,CookieUtils.class.getName());

    /**
     * 使用偏好型数据库[读取]Cookie
     * @param preferences
     * @param url
     */
    public static void ExtarctCookie(Preferences preferences, String url){
        Map<String, ?> map = new HashMap<>();
        //先从数据库中取出cookie
        map = PreferenceDataBase.GetCookieMap(preferences);
        //然后写入到cookieStore
        CookieStore cookieStore = CookieStore.getInstance();//1.获取一个CookieStore的示例
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            HiLog.info(TAG,entry.getKey()+"="+entry.getValue().toString());
            cookieStore.setCookie(url,entry.getKey()+"="+entry.getValue().toString());//2.写入数据，只能一条一条写
        }
    }

    /**
     * 使用偏好型数据库[保存]Cookie
     * @param preferences  数据库的Preferences实例
     * @param url  指定Cookie对应的域名
     */
    public static void SaveCookie(Preferences preferences,String url){
        //先取出要保存的cookie
        CookieStore cookieStore = CookieStore.getInstance();
        String cookieStr = cookieStore.getCookie(url);
        HiLog.info(TAG,"saveCookie(String url)"+url+cookieStr);

        //然后将cooke转成map
        Map<String,String> cookieMap = cookieToMap(cookieStr);

        //最后将map写入数据库
        PreferenceDataBase.SaveMap(preferences,cookieMap);
    }
    // cookieToMap
    public static Map<String,String> cookieToMap(String value) {
        Map<String, String> map = new HashMap<String, String>();
        value = value.replace(" ", "");
        if (value.contains(";")) {
            String values[] = value.split(";");
            for (String val : values) {
                String vals[] = val.split("=");
                map.put(vals[0], vals[1]);
            }
        } else {
            String values[] = value.split("=");
            map.put(values[0], values[1]);
        }
        return map;
    }
}
