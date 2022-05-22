package com.liangzili.future_bilibilicard.widget.stat;

import com.alibaba.fastjson.JSON;
import com.liangzili.future_bilibilicard.MainAbility;
import com.liangzili.future_bilibilicard.cardentity.ChartDataset;
import com.liangzili.future_bilibilicard.cardentity.ChartPoint;
import com.liangzili.future_bilibilicard.cardentity.PointStyle;
import com.liangzili.future_bilibilicard.database.Form;
import com.liangzili.future_bilibilicard.database.MyOrmDatabase;
import com.liangzili.future_bilibilicard.database.PreferenceDataBase;
import com.liangzili.future_bilibilicard.widget.controller.FormController;
import com.liangzili.future_bilibilicard.widget.pandect.PandectEntity;
import com.zzrv5.mylibrary.ZZRCallBack;
import com.zzrv5.mylibrary.ZZRHttp;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;

import java.util.*;


public class StatImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, StatImpl.class.getName());
    private static final int DIMENSION_1X2 = 1;
    private static final int DIMENSION_4X4 = 4;

    public StatImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
    }

    private DatabaseHelper helper = new DatabaseHelper(context);
    private OrmContext connect;

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.info(TAG, "bind form data");
        ZSONObject zsonObject = new ZSONObject();
        ProviderFormInfo providerFormInfo = new ProviderFormInfo();
        if (dimension == DIMENSION_1X2) {
            zsonObject.put("mini", true);
        }
        if (dimension == DIMENSION_4X4) {
            zsonObject.put("imagePaddingTop", "12px");
        }
        providerFormInfo.setJsBindingData(new FormBindingData(zsonObject));
        return providerFormInfo;
    }

    @Override
    public void updateFormData(long formId, Object... vars) {
        HiLog.info(TAG, "update form data timing, default 30 minutes");

        // 获取stat
        update(formId);
    }
    //获取stat
    public void update(long formId){
        HiLog.info(TAG, "update");

        String url = "https://member.bilibili.com/x/web/index/stat";
        Map<String, String> paramsMap = new HashMap<String,String>();       //数据采用的哈希表结构
        Map<String, String> headerMap = new HashMap<String,String>();       //数据采用的哈希表结构
        // 从数据库中提取SESSDATA
        headerMap.put("Cookie", "SESSDATA="+PreferenceDataBase.getSessData(context));//给map中添加元素

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
                    StatEntity statEntity = JSON.parseObject(s, StatEntity.class);

                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    //如果未登录会返回{"code":-101,"message":"账号未登录","ttl":1}
                    if(statEntity.getMessage().equals("账号未登录")){
                        zsonObject.put("code","true");
                        zsonObject.put("message", statEntity.getMessage());
                    }else {
                        zsonObject.put("data", statEntity.getData());      //更新data
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
    //获取pandect
    public void updatePandect(long formId,String type){
        HiLog.info(TAG, "update"+formId);

        String url = "https://member.bilibili.com/x/web/data/pandect?type="+type;
        Map<String, String> paramsMap = new HashMap<String,String>();       //数据采用的哈希表结构
        Map<String, String> headerMap = new HashMap<String,String>();       //数据采用的哈希表结构
        // 从数据库中提取SESSDATA
        headerMap.put("Cookie", "SESSDATA="+PreferenceDataBase.getSessData(context));//给map中添加元素

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
                    PandectEntity pandectEntity = JSON.parseObject(s, PandectEntity.class);

                    //这部分用来更新卡片信息
                    ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
                    //如果未登录会返回{"code":-101,"message":"账号未登录","ttl":1}
                    if(pandectEntity.getMessage().equals("账号未登录")){
                        zsonObject.put("code","true");
                        zsonObject.put("message", pandectEntity.getMessage());
                    }else {
                        // Chart的datasets属性：Array<ChartDataset>数据集合，可以设置多条数据集及其背景色。
                        // https://developer.harmonyos.com/cn/docs/documentation/doc-references/js-service-widget-basic-chart-0000001152948425#ZH-CN_TOPIC_0000001152843357__table13810518157
                        List<ChartDataset> datasets = new ArrayList<>(1);
                        ChartDataset chartDataset =new ChartDataset();
                        // 线条颜色。仅线形图支持
                        chartDataset.setStrokeColor("#FF00CEF4");
                        // 填充颜色。线形图表示填充的渐变颜色
                        chartDataset.setFillColor("#FF00CEF4");

                        PointStyle pointStyle = new PointStyle();
                        pointStyle.setShape("circle");
                        pointStyle.setSize(5);
                        pointStyle.setFillColor("#FF9C28");
                        pointStyle.setStrokeColor("#FF9C28");

                        List<ChartPoint> chartPoints = new ArrayList<>(1);

                        int[] ValList = new int[30];//保存所有数据的数组
                        for (int i = 0; i < 30; i++) {
                            ValList[i] = pandectEntity.getData().get(i).getTotal_inc();
                        }
                        int maxVal = Arrays.stream(ValList).max().getAsInt();//求数据数组中的最大值
                        int digit = String.valueOf(maxVal).length();//最大值的位数
                        maxVal = (int)Math.pow(10,digit);//Y轴的最大值
                        int yVal = (int)Math.pow(10,digit-1);//Y轴坐标值 除数

                        zsonObject.put("xAxis1",maxVal);
                        zsonObject.put("xAxis2",maxVal*0.8);
                        zsonObject.put("xAxis3",maxVal*0.6);
                        zsonObject.put("xAxis4",maxVal*0.4);
                        zsonObject.put("xAxis5",maxVal*0.2);

                        //更新卡片数据
                        for (int i = 30; i > 0; i--) {
                            ChartPoint chartPoint =new ChartPoint();;
                            // 绘制点的Y轴坐标 百分比
                            chartPoint.setValue(ValList[i-1]/yVal);

                            //用来显示描述
                            //chartPoint.setDescription(String.valueOf(pandect.getData().get(i).getTotal_inc()));
                            //chartPoint.setTextLocation("top");
                            //chartPoint.setTextColor("#CDCACA");
                            //chartPoint.setPointStyle(pointStyle);

                            chartPoints.add(chartPoint);
                        }

                        // 设置绘制线型图中的点集
                        chartDataset.setData(chartPoints);

                        // 设置是否显示填充渐变颜色。仅线形图支持
                        chartDataset.setGradient(true);

                        datasets.add(chartDataset);
                        zsonObject.put("datasets",datasets);
                        HiLog.info(TAG,zsonObject.toString());
                    }

                    FormBindingData formBindingData = new FormBindingData(zsonObject); //将其封装在一个FormBindingData的实例中
                    try {
                        ((MainAbility)context).updateForm(formId,formBindingData);//调用MainAbility的方法updateForm()，并将formBindingData作为第二个实参
                    } catch (FormException e) {
                        e.printStackTrace();
                        // todo 在这里删除不存在的卡片
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

        //这部分用来更新卡片信息
        ZSONObject zsonObject = new ZSONObject(); //首先，将要刷新的数据存放在一个ZSONObject实例中
        for (int i=1;i<9;i++){
            zsonObject.put("color"+i,"");
        }
        zsonObject.put("color"+message,"#8dd5ed");      //更新data
        try {
            ((MainAbility)context).updateForm(formId,new FormBindingData(zsonObject));//调用MainAbility的方法updateForm()，并将formBindingData作为第二个实参
        } catch (FormException e) {
            e.printStackTrace();
        }

        //执行Pandect的updata
        connect = helper.getOrmContext("FormDatabase", "FormDatabase.db", MyOrmDatabase.class);
        OrmPredicates ormPredicates = new OrmPredicates(Form.class);
        List<Form> forms = connect.query(ormPredicates); //查询

        if (forms.size() <= 0) {
            return;
        }
        for (Form form : forms) {
            if(form.getFormName().equals("pandect")){
                ZSONObject zsonObject1 = new ZSONObject();
                updatePandect(form.getFormId(),message);
            };

        }

    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        HiLog.info(TAG, "get the default page to route when you click card.");
        return null;
    }

}
