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

package com.huawei.codelab.slice;

import com.huawei.codelab.ResourceTable;
import com.huawei.codelab.callback.MainCallBack;
import com.huawei.codelab.constants.EventConstants;
import com.huawei.codelab.data.ComponentPointData;
import com.huawei.codelab.data.ComponentPointDataMgr;
import com.huawei.codelab.proxy.ConnectManagerIml;
import com.huawei.codelab.utils.AbilityMgr;
import com.huawei.codelab.utils.LogUtils;
import com.huawei.codelab.utils.MovieSearchUtils;
import com.huawei.codelab.utils.PermissionBridge;
import com.huawei.codelab.utils.WindowManagerUtils;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.continuation.DeviceConnectState;
import ohos.aafwk.ability.continuation.ExtraParams;
import ohos.aafwk.ability.continuation.IContinuationDeviceCallback;
import ohos.aafwk.ability.continuation.IContinuationRegisterManager;
import ohos.aafwk.ability.continuation.RequestCallback;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.ScrollView;
import ohos.agp.components.TableLayout;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.event.commonevent.CommonEventSubscribeInfo;
import ohos.event.commonevent.CommonEventSubscriber;
import ohos.event.commonevent.CommonEventSupport;
import ohos.event.commonevent.MatchingSkills;
import ohos.media.image.common.Size;
import ohos.rpc.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MainAbilitySlice
 *
 * @since 2021-02-26
 */
public class MainAbilitySlice extends AbilitySlice implements PermissionBridge.OnPermissionStateListener {
    private static final String ABILITY_NAME = "com.huawei.codelab.RemoteInputAbility";
    private static final String MOVIE_PLAY_ABILITY = "com.huawei.codelab.MoviePlayAbility";
    private static final String MAIN_ABILITY = "com.huawei.codelab.MainAbility";
    private static final int FOCUS_PADDING = 8;
    private static final int SEARCH_PADDING = 3;
    private static final int LIST_INIT_SIZE = 16;
    private static final String TAG = MainAbilitySlice.class.getName();
    private static final Lock MOVE_LOCK = new ReentrantLock();
    private MainAbilitySlice.MyCommonEventSubscriber subscriber;
    private TextField tvTextInput;
    private ScrollView scrollView;
    private DirectionalLayout keyBoardLayout;
    private TableLayout movieTableLayout;
    private Size size;
    private final AbilityMgr abilityMgr = new AbilityMgr(this);

    // ?????????????????????
    private final List<ComponentPointData> movieSearchList = new ArrayList<>(LIST_INIT_SIZE);

    // ????????????????????????
    private ComponentPointData componentPointDataNow;

    // ??????????????????????????????????????????Ability token
    private int abilityToken;

    // ??????????????????????????????????????????????????????ID
    private String selectDeviceId;

    // ???????????????????????????????????????
    private IContinuationRegisterManager continuationRegisterManager;

    // ????????????FA?????????????????????????????????????????????
    private final IContinuationDeviceCallback callback = new IContinuationDeviceCallback() {
        @Override
        public void onDeviceConnectDone(String deviceId, String s1) {
            selectDeviceId = deviceId;
            abilityMgr.openRemoteAbility(selectDeviceId, getBundleName(), ABILITY_NAME);
            getUITaskDispatcher().asyncDispatch(() -> continuationRegisterManager.updateConnectStatus(abilityToken, selectDeviceId,
                    DeviceConnectState.IDLE.getState(), null));
        }

        @Override
        public void onDeviceDisconnectDone(String deviceId) {
        }
    };

    // ????????????FA??????????????????????????????
    private final RequestCallback requestCallback = new RequestCallback() {
        @Override
        public void onResult(int result) {
            abilityToken = result;
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        new PermissionBridge().setOnPermissionStateListener(this);

        // ????????????
        WindowManagerUtils.setWindows();

        // ???????????????
        initComponent();

        // ?????????????????????
        initKeyBoardComponent(keyBoardLayout);

        // ???????????????????????????
        initMovieTableComponent(movieTableLayout);

        // ????????????
        subscribe();
    }

    private void registerTransService() {
        continuationRegisterManager = getContinuationRegisterManager();

        // ??????????????????
        ExtraParams params = new ExtraParams();
        String[] devTypes = new String[]{ExtraParams.DEVICETYPE_SMART_PAD, ExtraParams.DEVICETYPE_SMART_PHONE};
        params.setDevType(devTypes);

        // ??????FA??????????????????
        continuationRegisterManager.register(getBundleName(), params, callback, requestCallback);
    }

    private void initMovieTableComponent(TableLayout tableLayout) {
        int index = 0;
        while (index < tableLayout.getChildCount()) {
            DirectionalLayout childLayout = null;
            if (tableLayout.getComponentAt(index) instanceof DirectionalLayout) {
                childLayout = (DirectionalLayout) tableLayout.getComponentAt(index);
            }
            ComponentPointData componentPointData = new ComponentPointData();
            Component component = null;
            int indexMovie = 0;
            while (childLayout != null && indexMovie < childLayout.getChildCount()) {
                Component comChild = childLayout.getComponentAt(indexMovie);
                indexMovie++;
                if (comChild instanceof Text) {
                    componentPointData = ComponentPointDataMgr
                            .getConstantMovie(((Text) comChild).getText()).orElse(null);
                    continue;
                }
                component = findComponentById(comChild.getId());
            }

            if (componentPointData != null && component != null) {
                componentPointData.setComponentId(component.getId());
            }
            ComponentPointDataMgr.getComponentPointDataMgrs().add(componentPointData);
            index++;
        }
    }

    private void initKeyBoardComponent(DirectionalLayout directionalLayout) {
        int index = 0;
        while (index < directionalLayout.getChildCount()) {
            DirectionalLayout childLayout = null;
            if (directionalLayout.getComponentAt(index) instanceof DirectionalLayout) {
                childLayout = (DirectionalLayout) directionalLayout.getComponentAt(index);
            }
            int indexButton = 0;
            while (childLayout != null && indexButton < childLayout.getChildCount()) {
                if (childLayout.getComponentAt(indexButton) instanceof Button) {
                    Button button = (Button) childLayout.getComponentAt(indexButton);
                    buttonInit(button);
                    indexButton++;
                }
            }
            index++;
        }
    }

    private void initComponent() {
        if (findComponentById(ResourceTable.Id_scrollview) instanceof ScrollView) {
            scrollView = (ScrollView) findComponentById(ResourceTable.Id_scrollview);
        }

        if (findComponentById(ResourceTable.Id_TV_input) instanceof TextField) {
            tvTextInput = (TextField) findComponentById(ResourceTable.Id_TV_input);

            // ???????????????????????????
            ComponentPointData componentPointData = new ComponentPointData();
            componentPointData.setComponentId(tvTextInput.getId());
            componentPointData.setPointX(0);
            componentPointData.setPointY(1);
            tvTextInput.requestFocus();
            componentPointDataNow = componentPointData;
            ComponentPointDataMgr.getComponentPointDataMgrs().add(componentPointDataNow);

            // ????????????????????????????????????
            tvTextInput.setClickedListener(component -> showDevicesDialog());
        }

        if (findComponentById(ResourceTable.Id_keyBoardComponent) instanceof DirectionalLayout) {
            keyBoardLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_keyBoardComponent);
        }

        if (findComponentById(ResourceTable.Id_tableLayout) instanceof TableLayout) {
            movieTableLayout = (TableLayout) findComponentById(ResourceTable.Id_tableLayout);
        }

        if (findComponentById(ResourceTable.Id_image_ten) instanceof Image) {
            Image image = (Image) findComponentById(ResourceTable.Id_image_ten);
            size = image.getPixelMap().getImageInfo().size;
        }
    }

    private void showDevicesDialog() {
        ExtraParams extraParams = new ExtraParams();
        extraParams.setDevType(new String[]{ExtraParams.DEVICETYPE_SMART_TV,
            ExtraParams.DEVICETYPE_SMART_PHONE});
        extraParams.setDescription("???????????????");
        continuationRegisterManager.showDeviceList(abilityToken, extraParams, result -> LogUtils.info(TAG, "show devices success"));
    }

    private void buttonInit(Button button) {
        if (button.getId() == ResourceTable.Id_del) {
            findComponentById(button.getId()).setClickedListener(component -> {
                if (tvTextInput.getText().length() > 0) {
                    tvTextInput.setText(tvTextInput.getText().substring(0, tvTextInput.getText().length() - 1));
                }
            });
        } else if (button.getId() == ResourceTable.Id_clear) {
            findComponentById(button.getId()).setClickedListener(component -> tvTextInput.setText(""));
        } else {
            findComponentById(button.getId()).setClickedListener(component -> tvTextInput.append(button.getText()));
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void onPermissionGranted() {
        registerTransService();
    }

    @Override
    public void onPermissionDenied() {
        terminate();
    }

    /**
     * MyCommonEventSubscriber
     *
     * @since 2020-12-03
     */
    class MyCommonEventSubscriber extends CommonEventSubscriber {
        MyCommonEventSubscriber(CommonEventSubscribeInfo info) {
            super(info);
        }

        @Override
        public void onReceiveEvent(CommonEventData commonEventData) {
            Intent intent = commonEventData.getIntent();
            int requestType = intent.getIntParam("requestType", 0);
            String inputString = intent.getStringParam("inputString");
            if (requestType == ConnectManagerIml.REQUEST_SEND_DATA) {
                tvTextInput.setText(inputString);
            } else if (requestType == ConnectManagerIml.REQUEST_SEND_SEARCH) {
                // ????????????????????????????????????????????????;?????????????????????????????????????????????;X????????????0?????????????????????;X?????????1??????????????????
                if (componentPointDataNow.getPointX() == 0) {
                    // ???????????????????????????
                    searchMovies(tvTextInput.getText());
                    return;
                }

                // ????????????
                abilityMgr.playMovie(getBundleName(), MOVIE_PLAY_ABILITY);
            } else {
                // ????????????
                String moveString = intent.getStringParam("move");
                MainCallBack.movePoint(MainAbilitySlice.this, moveString);
            }
        }
    }

    /**
     * goBack
     */
    public void goBack() {
        clearLastBackg();
        scrollView.scrollTo(0, 0);
        componentPointDataNow = ComponentPointDataMgr.getMoviePoint(0, 1).orElse(null);
        findComponentById(ResourceTable.Id_TV_input).requestFocus();
        abilityMgr.returnMainAbility(getBundleName(), MAIN_ABILITY);
    }

    /**
     * move
     *
     * @param pointX pointX
     * @param pointY pointY
     */
    public void move(int pointX, int pointY) {
        MOVE_LOCK.lock();
        try {
            // ??????????????????
            if (pointX == 0 && componentPointDataNow.getPointX() > 0) {
                scrollView.fluentScrollByY(pointY * size.height);
            }
            if (componentPointDataNow.getPointX() == 0 && pointX == 1) {
                scrollView.scrollTo(0, 0);
            }

            // ????????????
            if (componentPointDataNow.getPointX() + pointX == 0) {
                setBackGround(componentPointDataNow.getPointX() + pointX, 1);
            } else {
                setBackGround(componentPointDataNow.getPointX() + pointX, componentPointDataNow.getPointY() + pointY);
            }
        } finally {
            MOVE_LOCK.unlock();
        }
    }

    private void setBackGround(int pointX, int pointY) {
        ComponentPointData componentPointDataNew = ComponentPointDataMgr.getMoviePoint(pointX, pointY).orElse(null);
        if (componentPointDataNew == null) {
            return;
        }

        // ???????????????????????????
        clearLastBackg();
        componentPointDataNow = componentPointDataNew;
        if (findComponentById(componentPointDataNow.getComponentId()) instanceof Image) {
            Image newImage = (Image) findComponentById(componentPointDataNow.getComponentId());
            newImage.setPadding(FOCUS_PADDING, FOCUS_PADDING, FOCUS_PADDING, FOCUS_PADDING);
        } else {
            Component component = findComponentById(componentPointDataNow.getComponentId());
            component.requestFocus();
        }
    }

    private void clearLastBackg() {
        Component component = null;
        if (findComponentById(componentPointDataNow.getComponentId()) instanceof TextField) {
            TextField textField = (TextField) findComponentById(componentPointDataNow.getComponentId());
            textField.clearFocus();
        } else {
            component = findComponentById(componentPointDataNow.getComponentId());
            component.setPadding(0, 0, 0, 0);
        }

        // ??????????????????????????????????????????????????????
        for (ComponentPointData componentPointData : movieSearchList) {
            if (component != null && componentPointData.getComponentId() == component.getId()) {
                component.setPadding(SEARCH_PADDING, SEARCH_PADDING, SEARCH_PADDING, SEARCH_PADDING);
            }
        }
    }

    private void searchMovies(String text) {
        if (text == null || "".equals(text)) {
            return;
        }

        // ???????????????????????????????????????
        clearHistroyBackGround();

        for (ComponentPointData componentPointData : ComponentPointDataMgr.getComponentPointDataMgrs()) {
            if (MovieSearchUtils.isContainMovie(componentPointData.getMovieName(), text)
                    || MovieSearchUtils.isContainMovie(componentPointData.getMovieFirstName(), text)) {
                movieSearchList.add(componentPointData);
                Component component = findComponentById(componentPointData.getComponentId());
                component.setPadding(SEARCH_PADDING, SEARCH_PADDING, SEARCH_PADDING, SEARCH_PADDING);
            }
        }

        if (movieSearchList.size() > 0) {
            componentPointDataNow = movieSearchList.get(0);
            Component component = findComponentById(componentPointDataNow.getComponentId());
            component.setPadding(FOCUS_PADDING, FOCUS_PADDING, FOCUS_PADDING, FOCUS_PADDING);
        } else {
            Component component = findComponentById(componentPointDataNow.getComponentId());
            component.requestFocus();
        }
    }

    private void clearHistroyBackGround() {
        // ???????????????????????????
        for (ComponentPointData componentPointData : movieSearchList) {
            Component component = findComponentById(componentPointData.getComponentId());
            component.setPadding(0, 0, 0, 0);
        }
        movieSearchList.clear();

        // ????????????????????????
        clearLastBackg();
    }

    private void subscribe() {
        MatchingSkills matchingSkills = new MatchingSkills();
        matchingSkills.addEvent(EventConstants.SCREEN_REMOTE_CONTROLL_EVENT);
        matchingSkills.addEvent(CommonEventSupport.COMMON_EVENT_SCREEN_ON);
        CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(matchingSkills);
        subscriber = new MainAbilitySlice.MyCommonEventSubscriber(subscribeInfo);
        try {
            CommonEventManager.subscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtils.error("", "subscribeCommonEvent occur exception.");
        }
    }

    private void unSubscribe() {
        try {
            CommonEventManager.unsubscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtils.error(TAG, "unSubscribe Exception");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribe();
    }
}
