package com.liangzili.future_bilibilicard;

import com.liangzili.future_bilibilicard.database.Form;
import com.liangzili.future_bilibilicard.database.MyOrmDatabase;
import com.liangzili.future_bilibilicard.slice.MainAbilitySlice;
import com.liangzili.future_bilibilicard.widget.controller.*;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbility extends Ability {
    public static final int DEFAULT_DIMENSION_2X2 = 2;
    public static final int DIMENSION_1X2 = 1;
    public static final int DIMENSION_2X4 = 3;
    public static final int DIMENSION_4X4 = 4;
    private static final int INVALID_FORM_ID = -1;
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, MainAbility.class.getName());
    private String topWidgetSlice;

    // 对象关系映射数据库1.
    // context入参类型为ohos.app.Context，注意不要使用slice.getContext()来获取context，请直接传入slice，否则会出现找不到类的报错。
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private OrmContext ormContext;

    private Context context;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

        //申请分布式协同权限
        requestPermissionsFromUser(new String[]{"ohos.permission.DISTRIBUTED_DATASYNC"},0);

        //注册数据库
        //PreferenceDataBase.register(this,"name");

        if (intentFromWidget(intent)) {
            topWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice != null) {
                setMainRoute(topWidgetSlice);
            }
        }
        stopAbility(intent);
    }

    @Override
    protected ProviderFormInfo onCreateForm(Intent intent) {
        HiLog.info(TAG, "onCreateForm");
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        String formName = intent.getStringParam(AbilitySlice.PARAM_FORM_NAME_KEY);
        int dimension = intent.getIntParam(AbilitySlice.PARAM_FORM_DIMENSION_KEY, DEFAULT_DIMENSION_2X2);
        HiLog.info(TAG, "onCreateForm: formId=" + formId + ",formName=" + formName);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        formController = (formController == null) ? formControllerManager.createFormController(formId,
                formName, dimension) : formController;
        if (formController == null) {
            HiLog.error(TAG, "Get null controller. formId: " + formId + ", formName: " + formName);
            return null;
        }

        //初始化时先在线更新一下卡片
        onUpdateForm(formId);

        // TODO 获取添加到桌面的服务卡片ID

        // 存储卡片信息。对象关系映射数据库2.通过对象数据操作接口OrmContext，创建一个别名为“FormDatabase”，数据库文件名为“FormDatabase.db”的数据库
        ormContext = databaseHelper.getOrmContext("FormDatabase", "FormDatabase.db", MyOrmDatabase.class);
        // 对象关系映射数据库3.实例化数据表
        HiLog.info(TAG, "存储卡片信息: formId=" + formId + ",formName=" + formName + ",dimension=" + dimension);
        Form form = new Form(formId, formName, dimension);
        // 对象关系映射数据库4.添加方法
        ormContext.insert(form);
        ormContext.flush();


        return formController.bindFormData();
    }

    @Override
    protected void onUpdateForm(long formId) {
        HiLog.info(TAG, "onUpdateForm");
        super.onUpdateForm(formId);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        formController.updateFormData(formId);
    }

    @Override
    protected void onDeleteForm(long formId) {
        HiLog.info(TAG, "onDeleteForm: formId=" + formId);
        super.onDeleteForm(formId);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        formControllerManager.deleteFormController(formId);

        //TODO 从数据库删除卡片信息
//        ormContext = databaseHelper.getOrmContext("FormDatabase", "FormDatabase.db", MyOrmDatabase.class);
//        OrmPredicates ormPredicates = new OrmPredicates(Form.class);
//        List<Form> forms = ormContext.query(ormPredicates); //查询
//
//        if (forms.size() <= 0) {
//            return;
//        }
//        for (Form form : forms) {
//            if(form.getFormId() == formId){
//                ormContext.delete(form);
//            };
//
//        }

    }

    @Override
    protected void onTriggerFormEvent(long formId, String message) {
        HiLog.info(TAG, "onTriggerFormEvent: " + message);
        super.onTriggerFormEvent(formId, message);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        formController.onTriggerFormEvent(formId, message);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intentFromWidget(intent)) { // Only response to it when starting from a service widget.
            String newWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice == null || !topWidgetSlice.equals(newWidgetSlice)) {
                topWidgetSlice = newWidgetSlice;
                restart();
            }
        }
    }

    private boolean intentFromWidget(Intent intent) {
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        return formId != INVALID_FORM_ID;
    }

    private String getRoutePageSlice(Intent intent) {
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        if (formId == INVALID_FORM_ID) {
            return null;
        }
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        if (formController == null) {
            return null;
        }
        Class<? extends AbilitySlice> clazz = formController.getRoutePageSlice(intent);
        if (clazz == null) {
            return null;
        }
        return clazz.getName();
    }
}
