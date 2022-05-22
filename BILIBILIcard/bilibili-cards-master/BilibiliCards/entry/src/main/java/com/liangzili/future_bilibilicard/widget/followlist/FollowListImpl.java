package com.liangzili.future_bilibilicard.widget.followlist;

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
import java.util.List;
import java.util.Map;

/**
 * 订阅服务卡片
 */
public class FollowListImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, FollowListImpl.class.getName());
    private static final int DIMENSION_1X2 = 1;

    public FollowListImpl(Context context, String formName, Integer dimension) {
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

        update(formId);
    }
    public void update(long formId){
        HiLog.info(TAG, "update");

        String vmid = PreferenceDataBase.getVmid(context);
        // type=1：番剧、type=2：电影
        String url = "https://api.bilibili.com/x/space/bangumi/follow/list?type=1&pn=1&ps=16&vmid="+vmid;
        // TODO 坑：这里踩了个坑搞了半天才发现，get追番信息不加SESSDATA，是得不到观看进度（progress）的。即使在个人空间中开启了追番信息。
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
                    //调用fastjson解析
                    FollowListEntity followListEntity = JSON.parseObject(s,FollowListEntity.class);
                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    //如果未登录会返回{"code":-400,"message":"Key: 'Vmid' Error:Field validation for 'Vmid' failed on the 'min' tag","ttl":1}
                    if(followListEntity.getCode() == -400){
                        zsonObject.put("code","true");
                        zsonObject.put("message", "账号未登录");
                    }else {
                        List<FollowListEntity.Data.list> list = followListEntity.getData().getList();  //class list
                        zsonObject.put("list",list);
                        // TODO follow_status=1：想看的追番、follow_status=2：在看的追番、follow_status=3：看过的追番
                        // 看以后是否可以支持切换列表
                    }

                    FormBindingData formBindingData = new FormBindingData(zsonObject); //将其封装在一个FormBindingData的实例中
                    try {
                        ((MainAbility)context).updateForm(formId,formBindingData);//调用MainAbility的方法updateForm()，并将formBindingData作为第二个实参
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
