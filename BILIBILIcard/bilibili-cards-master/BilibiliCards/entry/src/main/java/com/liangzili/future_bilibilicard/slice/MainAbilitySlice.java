package com.liangzili.future_bilibilicard.slice;

import com.liangzili.future_bilibilicard.ResourceTable;
import com.liangzili.future_bilibilicard.database.PreferenceDataBase;
import com.liangzili.future_bilibilicard.utils.CookieUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.webengine.*;
import ohos.app.Context;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

public class MainAbilitySlice extends AbilitySlice {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.LOG_APP, 0x0, AbilitySlice.class.getName());

    //DatabaseHelper的构造需要传入context，Ability和AbilitySlice都实现了ohos.app.Context接口。因此可以从应用中的Ability或AbilitySlice调用getContext()方法来获得context。
    private Context context;
    //偏好数据库
    private Preferences preferences;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        HiLog.info(TAG, "onStart时调用");

        String url = "https://m.bilibili.com";
//        String url = "https://passport.bilibili.com/login";

        //获取偏好数据库实例
        context = getContext();
        preferences = PreferenceDataBase.register(context,"bilibili");
        //从偏好数据库中读取cookie
        CookieUtils.ExtarctCookie(preferences,url);

        //启动webview
        webview(url);
    }
    //启动webview
    public void webview(String url){
        WebView webView = (WebView) findComponentById(ResourceTable.Id_webview);
        webView.getWebConfig().setJavaScriptPermit(true);  // 如果网页需要使用JavaScript，增加此行；如何使用JavaScript下文有详细介绍
        webView.load(url);

        webView.setWebAgent(new WebAgent() {
            public static final String EXAMPLE_URL = "...";
            @Override  //当Web页面进行链接跳转时，WebView默认会打开目标网址，通过以下方式可以定制该行为。
            public boolean isNeedLoadUrl(WebView webview, ResourceRequest request) {
                if (request == null || request.getRequestUrl() == null) {
                    return false;
                }
                Uri uri = request.getRequestUrl();
                System.out.println("链接跳转时" + uri);
                if(uri.toString().startsWith("bilibili://")){
                    System.out.println("测试");
                    Intent intent1 = new Intent();
                    Operation operation = new Intent.OperationBuilder()
                            .withUri(uri)
                            .build();
                    intent1.setOperation(operation);
                    startAbility(intent1);//通过scheme链接跳转到官方客户端
                    return false;
                }else {
                    // EXAMPLE_URL由开发者自定义
                    if (uri.getDecodedHost().equals(EXAMPLE_URL)) {
                        // 增加开发者自定义逻辑
                        return false;
                    } else {
                        return super.isNeedLoadUrl(webview, request);
                    }
                }
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onBackground() {
        HiLog.info(TAG, "Page不再对用户可见时调用");
        CookieUtils.SaveCookie(preferences,"https://m.bilibili.com");
    }

    @Override
    protected void onStop() {
        HiLog.info(TAG, "系统将要销毁Page时调用");
    }
}
