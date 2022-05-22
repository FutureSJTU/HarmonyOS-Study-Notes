package com.liangzili.future_bilibilicard.slice;

import com.liangzili.future_bilibilicard.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.surfaceprovider.SurfaceProvider;
import ohos.agp.graphics.Surface;
import ohos.agp.graphics.SurfaceOps;
import ohos.agp.window.service.WindowManager;
import ohos.global.resource.RawFileDescriptor;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.common.Source;
import ohos.media.player.Player;
import com.liangzili.future_bilibilicard.utils.DistributedUtils;

public class PlayerSlice extends AbilitySlice {
    private static final HiLogLabel logLabel = new HiLogLabel(HiLog.LOG_APP, 0x0, AbilitySlice.class.getName());

    private Player player;
    private Image image;
    private SurfaceProvider sfProvider;
    private String params;
    private String videoSource;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_player);
        params = intent.getStringParam("params");//从intent中获取 跳转事件定义的params字段的值

        // 1.七夕隐藏效果，粉丝数设置为1314
//        ZSONObject zsonObject = new ZSONObject();
//        zsonObject.put("follower","1314");
//        try {
//            ((MainAbility)contextmain).updateForm(formId,new FormBindingData(zsonObject)); //4.调用MainAbility的方法updateForm()，并将formBindingData作为第二个实参
//        } catch (FormException e) {
//            e.printStackTrace();
//            HiLog.info(logLabel, "更新卡片失败");
//        }

        // 2.七夕隐藏效果，跳转页面
        if(params.equals("true")){
            Intent intent0 = new Intent();
            Operation op = new Intent.OperationBuilder()
                    .withDeviceId(DistributedUtils.getDeviceId())//参数1.是否跨设备，空，不跨设备
                    .withBundleName("com.liangzili.future_bilibilicard")//参数2.在config.json中的bundleName
                    .withAbilityName("com.liangzili.future_bilibilicard.Player")//参数3.要跳转的ability名
                    .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                    .build();
            intent0.setOperation(op);
            intent0.setParam("params","false");
            startAbility(intent0);
            videoSource = "resources/base/media/right.mp4";
        }else{
            videoSource = "resources/base/media/left.mp4";
        }


        //设置沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutConfig.MARK_TRANSLUCENT_STATUS);
        initPlayer();
    }
    //需要重写两个回调：VideoSurfaceCallback 、VideoPlayerCallback
    private void initPlayer() {
        sfProvider=(SurfaceProvider) findComponentById(ResourceTable.Id_surfaceProvider);
//        image=(Image) findComponentById(ResourceTable.Id_img);
        sfProvider.getSurfaceOps().get().addCallback(new VideoSurfaceCallback());
        // sfProvider.pinToZTop(boolean)--如果设置为true, 视频控件会在最上层展示，但是设置为false时，虽然不在最上层展示，却出现黑屏，
        // 需加上一行代码：WindowManager.getInstance().getTopWindow().get().setTransparent(true);
        sfProvider.pinToZTop(true);
        //WindowManager.getInstance().getTopWindow().get().setTransparent(true);
        player=new Player(getContext());
        //sfProvider添加监听事件
        sfProvider.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if(player.isNowPlaying()){
                    //如果正在播放，就暂停
                    player.pause();
                    //播放按钮可见
                    image.setVisibility(Component.VISIBLE);
                }else {
                    //如果暂停，点击继续播放
                    player.play();
                    //播放按钮隐藏
                    image.setVisibility(Component.HIDE);
                }
            }
        });
    }
    private class VideoSurfaceCallback implements SurfaceOps.Callback {
        @Override
        public void surfaceCreated(SurfaceOps surfaceOps) {
            HiLog.info(logLabel,"surfaceCreated() called.");
            if (sfProvider.getSurfaceOps().isPresent()) {
                Surface surface = sfProvider.getSurfaceOps().get().getSurface();
                playLocalFile(surface);
            }
        }
        @Override
        public void surfaceChanged(SurfaceOps surfaceOps, int i, int i1, int i2) {
            HiLog.info(logLabel,"surfaceChanged() called.");
        }
        @Override
        public void surfaceDestroyed(SurfaceOps surfaceOps) {
            HiLog.info(logLabel,"surfaceDestroyed() called.");
        }
    }
    private void playLocalFile(Surface surface) {
        try {
            RawFileDescriptor filDescriptor = getResourceManager().getRawFileEntry(videoSource).openRawFileDescriptor();
            Source source = new Source(filDescriptor.getFileDescriptor(),filDescriptor.getStartPosition(),filDescriptor.getFileSize());
            player.setSource(source);
            player.setVideoSurface(surface);
            player.setPlayerCallback(new VideoPlayerCallback());
            player.prepare();
            sfProvider.setTop(0);
            player.play();
        } catch (Exception e) {
            HiLog.info(logLabel,"playUrl Exception:" + e.getMessage());
        }
    }
    private class VideoPlayerCallback implements Player.IPlayerCallback {
        @Override
        public void onPrepared() {
            HiLog.info(logLabel,"onPrepared");
        }
        @Override
        public void onMessage(int i, int i1) {
            HiLog.info(logLabel,"onMessage");
        }
        @Override
        public void onError(int i, int i1) {
            HiLog.info(logLabel,"onError: i=" + i + ", i1=" + i1);
        }
        @Override
        public void onResolutionChanged(int i, int i1) {
            HiLog.info(logLabel,"onResolutionChanged");
        }
        @Override
        public void onPlayBackComplete() {
            //播放完成回调，重新播放
            if (player != null) {
                player.prepare();
                player.play();
            }
        }
        @Override
        public void onRewindToComplete() {
            HiLog.info(logLabel,"onRewindToComplete");
        }
        @Override
        public void onBufferingChange(int i) {
            HiLog.info(logLabel,"onBufferingChange");
        }
        @Override
        public void onNewTimedMetaData(Player.MediaTimedMetaData mediaTimedMetaData) {
            HiLog.info(logLabel,"onNewTimedMetaData");
        }
        @Override
        public void onMediaTimeIncontinuity(Player.MediaTimeInfo mediaTimeInfo) {
            HiLog.info(logLabel,"onMediaTimeIncontinuity");
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
}
