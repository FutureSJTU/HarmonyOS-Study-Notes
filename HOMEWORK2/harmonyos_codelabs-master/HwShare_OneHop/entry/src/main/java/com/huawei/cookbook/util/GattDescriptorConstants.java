/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.cookbook.util;

/**
 * Gatt descriptor constants
 */
public class GattDescriptorConstants {
    /**
     * Enable indication type value
     */
    public static final byte[] ENABLE_INDICATION_VALUE = {0x02, 0x00};

    /**
     * Enable notification type value
     */
    public static final byte[] ENABLE_NOTIFICATION_VALUE = {0x01, 0x00};

    /**
     * get enable indication type value
     *
     * @return enable indication type value
     */
    public static byte[] getEnableIndicationValue() {
        return ENABLE_INDICATION_VALUE.clone();
    }

    /**
     * get enable notification type value
     *
     * @return enable notification type value
     */
    public static byte[] getEnableNotificationValue() {
        return ENABLE_NOTIFICATION_VALUE.clone();
    }
}
