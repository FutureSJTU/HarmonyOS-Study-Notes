package com.liangzili.future_bilibilicard.widget.slideshow;

import com.alibaba.fastjson.JSON;
import com.liangzili.future_bilibilicard.MainAbility;
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
import java.util.List;


public class SlideShowImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, SlideShowImpl.class.getName());
    private static final int DIMENSION_1X2 = 1;

    public SlideShowImpl(Context context, String formName, Integer dimension) {
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
        String url = "https://api.bilibili.com/pgc/operation/api/slideshow?position_id=104";
        ZZRHttp.get(url, new ZZRCallBack.CallBackString() {
            @Override
            public void onFailure(int i, String s) {
                HiLog.info(TAG, "API返回失败");
            }

            @Override
            public void onResponse(String s) {
                HiLog.info(TAG, "API返回成功");

                try {
                    //调用fastjson解析，结果保存在BilibiliApi类
                    SlideShowEntity slideShowEntity = JSON.parseObject(s,SlideShowEntity.class);
                    //获取results列表,列表的集合
                    List<SlideShowEntity.Result> results = slideShowEntity.getResult();
                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    for (int i=0;i<results.size();i++){
                        zsonObject.put("src"+i,results.get(i).getImg()+"?timestamp="+new Date().getTime());
                        zsonObject.put("title"+i,results.get(i).getTitle());
                        //zsonObject.put("index",3);
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
