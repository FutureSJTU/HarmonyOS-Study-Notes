package com.liangzili.future_bilibilicard;

import com.liangzili.future_bilibilicard.slice.PlayerSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class Player extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(PlayerSlice.class.getName());
    }
}
