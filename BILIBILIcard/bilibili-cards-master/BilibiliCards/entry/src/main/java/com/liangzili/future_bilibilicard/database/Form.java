package com.liangzili.future_bilibilicard.database;

import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.PrimaryKey;

@Entity(tableName = "form")
public class Form extends OrmObject {
    @PrimaryKey()
    // 卡片id
    private Long formId;

    // 卡片名称
    private String formName;

    // 卡片名称
    private int dimension;

    /**
     * 有参构造
     *
     * @param formId 卡片id
     * @param formName 卡片名
     * @param dimension 卡片规格
     */
    public Form(Long formId, String formName, int dimension) {
        this.formId = formId;
        this.formName = formName;
        this.dimension = dimension;
    }

    /**
     * 无参构造
     */
    public Form() {
        super();
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
