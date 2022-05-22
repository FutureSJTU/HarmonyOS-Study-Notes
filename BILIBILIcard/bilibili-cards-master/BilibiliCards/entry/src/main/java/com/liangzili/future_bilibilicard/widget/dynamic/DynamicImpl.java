package com.liangzili.future_bilibilicard.widget.dynamic;

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

import java.util.*;

// 动态信息服务卡片
public class DynamicImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, DynamicImpl.class.getName());
    private static final int DIMENSION_2X2 = 2;

    public DynamicImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.info(TAG, "bind form data");
        ZSONObject zsonObject = new ZSONObject();
        ProviderFormInfo providerFormInfo = new ProviderFormInfo();
        if (dimension == DIMENSION_2X2) {
            zsonObject.put("font-size", 20);
        }
        providerFormInfo.setJsBindingData(new FormBindingData(zsonObject));
        return providerFormInfo;
    }

    @Override
    public void updateFormData(long formId, Object... vars) {
        HiLog.info(TAG, "update form data timing, default 30 minutes");

        update(formId);
    }
    public void update(long formId){
        HiLog.info(TAG, "updateDynamicImpl");

        //String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new?uid=383565952&type_list=2";  //貌似得到全部动态
        // type=8 是个人UP，哔哩哔哩电影
        // type=512 是番剧
        // 还有4097,4098,4099,4100,4101等，没有研究
        String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new?uid=383565952&type_list=8";
        Map<String, String> paramsMap = new HashMap<String,String>();       //数据采用的哈希表结构
        Map<String, String> headerMap = new HashMap<String,String>();       //数据采用的哈希表结构
        // 从数据库中提取SESSDATA
        headerMap.put("Cookie", "SESSDATA="+ PreferenceDataBase.getSessData(context));//给map中添加元素

        // 发起get请求
        ZZRHttp.get(url,paramsMap,headerMap,new ZZRCallBack.CallBackString() {

            //ZZRHttp.get(url, new ZZRCallBack.CallBackString() {
            @Override
            public void onFailure(int i, String s) {
                HiLog.info(TAG, "更新失败");
            }

            @Override
            public void onResponse(String s) {
                HiLog.info(TAG, "返回正常");

                try {
                    //调用fastjson解析，结果保存在BilibiliApi类
                    DynamicEntity dynamicEntity = JSON.parseObject(s,DynamicEntity.class);

                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    //如果未登录会返回{"code":-101,"message":"账号未登录","ttl":1}
                    if(dynamicEntity.getCode() == -6){
                        zsonObject.put("code","true");
                        zsonObject.put("message", "账号未登录");
                    }else {
                        DynamicEntity.Data data = dynamicEntity.getData();      //class Data
                        List<DynamicEntity.Data.Cards> cards = data.getCards();  //class list

                        // 存放所有卡片信息的数组
                        ArrayList<ZSONObject> UpdateCards = new ArrayList<>();

                        for (DynamicEntity.Data.Cards value : cards) {
                            CardsEntity cardsEntity = JSON.parseObject(value.getCard(), CardsEntity.class);

                            // 存放单张卡片信息
                            ZSONObject card = new ZSONObject();
                            card.put("name", cardsEntity.getOwner().getName());
                            card.put("title", cardsEntity.getTitle());
                            card.put("pic", cardsEntity.getPic() + "?timestamp=" + new Date().getTime());
                            card.put("short_link",cardsEntity.getShort_link());
                            UpdateCards.add(card);
                        }
                        zsonObject.put("cards",UpdateCards);
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
        //end更新卡片
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
