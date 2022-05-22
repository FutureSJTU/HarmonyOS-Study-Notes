package com.liangzili.future_bilibilicard.database;

import ohos.data.orm.OrmDatabase;
import ohos.data.orm.annotation.Database;

@Database(entities = {Form.class},version = 1)
public abstract class MyOrmDatabase extends OrmDatabase {
}
