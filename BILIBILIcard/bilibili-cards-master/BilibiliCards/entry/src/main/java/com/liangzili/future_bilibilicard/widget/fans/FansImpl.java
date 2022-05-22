package com.liangzili.future_bilibilicard.widget.fans;

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

import java.util.Date;

public class FansImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, FansImpl.class.getName());
    private static final int DIMENSION_1X2 = 1;

    public FansImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.info(TAG, "bind form data");
        ZSONObject zsonObject = new ZSONObject();
        ProviderFormInfo providerFormInfo = new ProviderFormInfo();
        providerFormInfo.setJsBindingData(new FormBindingData(zsonObject));
        return providerFormInfo;
    }

    @Override
    public void updateFormData(long formId, Object... vars) {
        HiLog.info(TAG, "update form data timing, default 30 minutes");

        updata(formId);
    }
    public void updata(long formId){
        //获取粉丝数
        String vmid = PreferenceDataBase.getVmid(context);
        String url = "https://api.bilibili.com/x/relation/stat?vmid="+vmid;

        // 发起get请求
        ZZRHttp.get(url,new ZZRCallBack.CallBackString() {
            @Override
            public void onFailure(int i, String s) {
                HiLog.info(TAG, "API返回失败");
            }
            @Override
            public void onResponse(String s) {
                HiLog.info(TAG, "API返回成功");
                try {
                    //1.调用fastjson解析，结果保存在JSON对应的类
                    FansEntity fansEntity = JSON.parseObject(s,FansEntity.class);

                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    //如果未登录会返回{"code":-400,"message":"请求错误","ttl":1}
                    if(fansEntity.getCode() == -400){
                        zsonObject.put("code","true");
                        zsonObject.put("message", "账号未登录");
                    }else {
                        zsonObject.put("follower",fansEntity.getData().getFollower()); //2.更新数据，data.getFollower()就是在API数据请求中获取的粉丝数。
                    }
                   FormBindingData formBindingData = new FormBindingData(zsonObject); //3.将其封装在一个FormBindingData的实例中
                    try {
                        ((MainAbility)context).updateForm(formId,formBindingData); //4.调用MainAbility的方法updateForm()，并将formBindingData作为第二个实参
                    } catch (FormException e) {
                        e.printStackTrace();
                        HiLog.info(TAG, "更新卡片失败");
                    }
                } catch (Exception e) {
                    HiLog.info(TAG, "解析失败");
                }
            }
        });

        //获取头像
        String urlMyinfo = "https://api.bilibili.com/x/space/acc/info?mid="+vmid;
        ZZRHttp.get(urlMyinfo, new ZZRCallBack.CallBackString() {
            @Override
            public void onFailure(int i, String s) {
                HiLog.info(TAG, "API返回失败");
            }

            @Override
            public void onResponse(String s) {
                HiLog.info(TAG, "API返回成功");
                try {
                    //1.调用fastjson解析，结果保存在JSON对应的类
                    Myinfo myinfo = JSON.parseObject(s,Myinfo.class);

                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //1.将要刷新的数据存放在一个ZSONObject实例中
                    zsonObject.put("src",myinfo.getData().getFace()+"?timestamp="+new Date().getTime()); //3.更新数据
                    zsonObject.put("vip",myinfo.getData().getVip().getAvatar_subscript_url()+"?timestamp="+new Date().getTime()); //3.更新数据
                    FormBindingData formBindingData = new FormBindingData(zsonObject); //3.将其封装在一个FormBindingData的实例中
                    try {
                        ((MainAbility)context).updateForm(formId,formBindingData);//4.调用MainAbility的方法updateForm()，并将formBindingData作为第二个实参
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
