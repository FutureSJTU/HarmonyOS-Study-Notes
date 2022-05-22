package com.liangzili.future_bilibilicard.slice;

import com.liangzili.future_bilibilicard.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.webengine.WebView;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoSlice extends AbilitySlice {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG,0x0,AbilitySlice.class.getName());
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_video);

        Text text = (Text) findComponentById(ResourceTable.Id_text);
        text.setText("页面跳转中");

        // 随机图片数组
        int[] resource = {ResourceTable.Media_36e,ResourceTable.Media_36g,ResourceTable.Media_36h,ResourceTable.Media_38p};
        Component component = findComponentById(ResourceTable.Id_image);
        if (component instanceof Image) {
            Image image = (Image) component;
            image.setPixelMap(resource[(int)(Math.random()*3)]);//随机显示一张图片
        }

        String url = "https://m.bilibili.com";

        String param = intent.getStringParam("params");//从intent中获取 跳转事件定义的params字段的值
        if(param !=null){
            ZSONObject data = ZSONObject.stringToZSON(param);
            url = data.getString("url");
            String regex = "BV.*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(url);//正则获取BV号
            if (m.find()) {
                url = m.group(0);
                url = "bilibili://video/"+url;
            } else {
                webview(url);//没有BV号的番剧还是只能跳链接
            }
        }
        Intent intent1 = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withUri(Uri.parse(url))
                .build();
        intent1.setOperation(operation);
        startAbility(intent1);//通过scheme链接跳转到官方客户端

        //webview(url);
    }
    //启动webview
    public void webview(String url){
        WebView webView = (WebView) findComponentById(ResourceTable.Id_webview);
        webView.getWebConfig().setJavaScriptPermit(true);  // 如果网页需要使用JavaScript，增加此行；如何使用JavaScript下文有详细介绍
//        url = url.replace("www","m"); //www会跳转浏览器或Bilibili，m会直接在webview中打开
        webView.load(url);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
