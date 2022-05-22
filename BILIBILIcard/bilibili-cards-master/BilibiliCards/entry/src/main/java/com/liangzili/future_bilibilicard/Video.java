package com.liangzili.future_bilibilicard;

import com.liangzili.future_bilibilicard.slice.VideoSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class Video extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(VideoSlice.class.getName());
    }
}
