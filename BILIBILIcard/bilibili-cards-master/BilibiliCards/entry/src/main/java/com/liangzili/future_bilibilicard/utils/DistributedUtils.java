package com.liangzili.future_bilibilicard.utils;

import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;

import java.util.ArrayList;
import java.util.List;

public class DistributedUtils {
    public static String getDeviceId(){
        //获取在线设备列表，getDeviceList拿到的设备不包含本机。
        List<DeviceInfo> deviceList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);

        if(deviceList.isEmpty()){
            return null;
        }
        int deviceNum = deviceList.size();
        List<String> deviceIds = new ArrayList<>(deviceNum);    //提取设备Id
        List<String> deviceNames = new ArrayList<>(deviceNum);  //提取设备名
        deviceList.forEach((device)->{
            deviceIds.add(device.getDeviceId());
            deviceNames.add(device.getDeviceName());
        });

        //我们这里的实验环境，就两部手机，组件还没讲
        //我就直接使用deviceIds的第一个元素，做为启动远程设备的目标id
        String devcieIdStr = deviceIds.get(0);
        return devcieIdStr;
    }
}
