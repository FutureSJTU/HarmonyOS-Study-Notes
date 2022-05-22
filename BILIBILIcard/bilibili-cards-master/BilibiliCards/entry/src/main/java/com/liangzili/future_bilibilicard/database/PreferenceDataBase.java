package com.liangzili.future_bilibilicard.database;

import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.HashMap;
import java.util.Map;

public class PreferenceDataBase {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG,0x0,PreferenceDataBase.class.getName());

    /**
     * 获取Preferences实例，参考：https://developer.harmonyos.com/cn/docs/documentation/doc-guides/database-preference-guidelines-0000000000030083
     * @param context  数据库文件将存储在由context上下文指定的目录里。
     * @param name  fileName表示文件名，其取值不能为空，也不能包含路径
     * @return  //返回对应数据库的Preferences实例
     */
    public static Preferences register(Context context,String name) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Preferences preferences = databaseHelper.getPreferences(name);
        return preferences;
    }

    /**
     * Map[保存]到偏好型数据库
     * @param preferences  数据库的Preferences实例
     * @param map  要保存的map
     */
    public static void SaveMap(Preferences preferences,Map<String,String> map){
        // 遍历map
        for (Map.Entry<String, String> entry : map.entrySet()) {
            HiLog.info(TAG,entry.getKey() + "=" + entry.getValue());
            preferences.putString(entry.getKey(),entry.getValue());//3.将数据写入Preferences实例，
        }
        preferences.flushSync();//4.通过flush()或者flushSync()将Preferences实例持久化。
    }

    /**
     *  从偏好型数据库[读取]Map
     * @param preferences  数据库的Preferences实例
     * @return  要读取的map
     */
    public static Map<String,?> GetCookieMap(Preferences preferences){
        Map<String, ?> map = new HashMap<>();
        map = preferences.getAll();//3.读取数据
        return map;
    }

    /**
     * 获取Cookie中的SESSDATA值
     * @param context 上下文用来指定数据文件存储路径
     * @return  Cookie中的SESSDATA值
     */
    public static String getSessData(Context context){
        // 开启数据库
        DatabaseHelper databaseHelper = new DatabaseHelper(context);//1.创建数据库使用数据库操作的辅助类
        Preferences preferences = databaseHelper.getPreferences("bilibili");//2.获取到对应文件名的Preferences实例,filename是String类型
        String SESSDATA = preferences.getString("SESSDATA","");    //3.读取数据
        return SESSDATA;
    }

    /**
     * 获取Cookie中的Vmid值
     * @param context
     * @return Cookie中的Vmid值
     */
    public static String getVmid(Context context){
        // 开启数据库
        DatabaseHelper databaseHelper = new DatabaseHelper(context);//1.创建数据库使用数据库操作的辅助类
        Preferences preferences = databaseHelper.getPreferences("bilibili");//2.获取到对应文件名的Preferences实例,filename是String类型
        String DedeUserID = preferences.getString("DedeUserID","");    //3.读取数据
        return DedeUserID;
    }
}
