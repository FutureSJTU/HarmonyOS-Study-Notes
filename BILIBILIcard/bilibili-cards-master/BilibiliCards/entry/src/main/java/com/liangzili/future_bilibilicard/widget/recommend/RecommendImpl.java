package com.liangzili.future_bilibilicard.widget.recommend;

import com.alibaba.fastjson.JSON;
import com.liangzili.future_bilibilicard.MainAbility;
import com.liangzili.future_bilibilicard.database.PreferenceDataBase;
import com.liangzili.future_bilibilicard.widget.controller.FormController;
import com.zzrv5.mylibrary.ZZRCallBack;
import com.zzrv5.mylibrary.ZZRHttp;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;

import java.util.HashMap;
import java.util.Map;


public class RecommendImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, RecommendImpl.class.getName());
    private static final int DIMENSION_1X2 = 1;
    private static final int DIMENSION_2X2 = 2;

    public RecommendImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.info(TAG, "bind form data");
        ZSONObject zsonObject = new ZSONObject();
        ProviderFormInfo providerFormInfo = new ProviderFormInfo();
        if (dimension == DIMENSION_2X2) {
            zsonObject.put("columns", 1);
        }
        providerFormInfo.setJsBindingData(new FormBindingData(zsonObject));
        return providerFormInfo;
    }

    @Override
    public void updateFormData(long formId, Object... vars) {
        HiLog.info(TAG, "update form data timing, default 30 minutes");

        updata(formId);
    }
    public void updata(long formId){
        String url = "https://api.bilibili.com/x/web-interface/index/top/rcmd?fresh_type=3"; //首页推荐
        Map<String, String> paramsMap = new HashMap<String,String>();       //数据采用的哈希表结构
        Map<String, String> headerMap = new HashMap<String,String>();       //数据采用的哈希表结构
        // 从数据库中提取SESSDATA
        headerMap.put("Cookie", "SESSDATA="+ PreferenceDataBase.getSessData(context));//给map中添加元素

        // 发起get请求
        ZZRHttp.get(url,paramsMap,headerMap,new ZZRCallBack.CallBackString() {
            @Override
            public void onFailure(int i, String s) {
                HiLog.info(TAG, "API返回失败");
            }

            @Override
            public void onResponse(String s) {
                HiLog.info(TAG, "API返回成功");
                try {
                    //调用fastjson解析，结果保存在BilibiliApi类
                    RecommendEntity recommendEntity = JSON.parseObject(s,RecommendEntity.class);

                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    //如果未登录会返回{"code":-101,"message":"账号未登录","ttl":1}
                    if(recommendEntity.getMessage().equals("账号未登录")){
                        zsonObject.put("code","true");
                        zsonObject.put("message", recommendEntity.getMessage());
                    }else {
                        zsonObject.put("item",recommendEntity.getData().getItem());      //更新data
                    }

                    FormBindingData formBindingData = new FormBindingData(zsonObject);
                    try {
                        ((MainAbility)context).updateForm(formId,formBindingData);
                    } catch (FormException e) {
                        e.printStackTrace();
                        HiLog.info(TAG, "更新卡片失败");
                    }
                } catch (Exception e) {
                    HiLog.info(TAG, "解析失败");
                }
            }
        });
    }

    @Override
    public void onTriggerFormEvent(long formId, String message) {
        HiLog.info(TAG, "handle card click event.");
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        HiLog.info(TAG, "get the default page to route when you click card.");
        return null;
    }
}
