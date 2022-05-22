/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License,Version 2.0 (the "License");
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

package com.liangzili.future_bilibilicard.cardentity;

/**
 * 表的点
 */
public class ChartPoint { //表8 Point
    // 绘制点的Y轴坐标
    private int value;

    // 当前数据点的绘制样式
    private PointStyle pointStyle;

    // 当前点的注释内容
    private String description;

    // 可选值为top，bottom，none。分别表示注释的绘制位置位于点的上方，下方，以及不绘制
    private String textLocation;

    // 注释文字的颜色
    private String textColor;

    // todo
    // 还有2个参数https://developer.harmonyos.com/cn/docs/documentation/doc-references/js-service-widget-basic-chart-0000001152948425#ZH-CN_TOPIC_0000001152843357__table1470733752019

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public PointStyle getPointStyle() {
        return pointStyle;
    }

    public void setPointStyle(PointStyle pointStyle) {
        this.pointStyle = pointStyle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTextLocation() {
        return textLocation;
    }

    public void setTextLocation(String textLocation) {
        this.textLocation = textLocation;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    /**
     * 描述显示位置
     */
    public enum TextLocation {
        /**
         * 底部
         */
        bottom,
        /**
         * 不显示
         */
        none,
        /**
         * 顶部
         */
        top
    }
}
