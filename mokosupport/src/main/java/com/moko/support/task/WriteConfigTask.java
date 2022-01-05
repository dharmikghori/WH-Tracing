package com.moko.support.task;

import androidx.annotation.IntRange;

import android.text.TextUtils;

import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

import java.util.Calendar;

/**
 * @Date 2018/1/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.WriteConfigTask
 */
public class WriteConfigTask extends OrderTask {
    public byte[] data;

    public WriteConfigTask() {
        super(OrderType.WRITE_CONFIG, OrderTask.RESPONSE_TYPE_WRITE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ConfigKeyEnum key) {
        switch (key) {
            case GET_DEVICE_MAC:
            case GET_ADV_MOVE_CONDITION:
            case GET_TRACKING_TRIGGER:
            case GET_FILTER_RSSI:
            case GET_TRACKING_INTERVAL:
            case GET_TIME:
            case CLOSE_DEVICE:
            case SET_DEFAULT:
            case GET_BUTTON_POWER:
            case GET_BUTTON_RESET:
            case GET_CONNECT_NOTIFICATION:
            case GET_SAVED_RAW_DATA:
            case GET_LOW_BATTERY:
            case CLEAR_SAVED_DATA:
            case GET_MOVE_SENSITIVE:
            case GET_FILTER_MAC:
            case GET_FILTER_NAME:
            case GET_FILTER_UUID:
            case GET_FILTER_ADV_RAW_DATA:
            case GET_FILTER_ALL_DATA:
            case GET_SCAN_SETTINGS:
            case GET_VIBRATIONS_NUMBER:
            case GET_FILTER_MAJOR_RANGE:
            case GET_FILTER_MINOR_RANGE:
            case GET_SAVED_COUNT:
            case SHAKE:
                createGetConfigData(key.getConfigKey());
                break;
        }
    }

    private void createGetConfigData(int configKey) {
        data = new byte[]{(byte) 0xEA, (byte) configKey, (byte) 0x00, (byte) 0x00};
    }

    public void setDeviceMac(String mac) {
        byte[] macBytes = MokoUtils.hex2bytes(mac);
        data = new byte[10];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_DEVICE_MAC.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x06;
        data[4] = macBytes[0];
        data[5] = macBytes[1];
        data[6] = macBytes[2];
        data[7] = macBytes[3];
        data[8] = macBytes[4];
        data[9] = macBytes[5];
    }

    public void setAdvMoveCondition(@IntRange(from = 0, to = 65535) int seconds) {
        if (seconds == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_ADV_MOVE_CONDITION.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] secondBytes = MokoUtils.toByteArray(seconds, 2);
            data = new byte[6];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_ADV_MOVE_CONDITION.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x02;
            data[4] = secondBytes[0];
            data[5] = secondBytes[1];
        }
    }

    public void setTrackingTrigger(@IntRange(from = 0, to = 65535) int second) {
        if (second == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_TRACKING_TRIGGER.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] secondBytes = MokoUtils.toByteArray(second, 2);
            data = new byte[6];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_TRACKING_TRIGGER.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x02;
            data[4] = secondBytes[0];
            data[5] = secondBytes[1];
        }
    }

    public void setStoreRssiCondition(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_FILTER_RSSI.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) rssi;
    }

    public void setTrackingInterval(@IntRange(from = 0, to = 255) int minute) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_TRACKING_INTERVAL.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) minute;
    }

    public void setTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        data = new byte[10];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_TIME.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x06;
        data[4] = (byte) year;
        data[5] = (byte) month;
        data[6] = (byte) date;
        data[7] = (byte) hour;
        data[8] = (byte) minute;
        data[9] = (byte) second;
    }

    public void setTriggerEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_BUTTON_POWER.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setButtonReset(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_BUTTON_RESET.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setConnectNotification(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_CONNECT_NOTIFICATION.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setSavedRawData(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_SAVED_RAW_DATA.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setLowBattery(@IntRange(from = 0, to = 255) int lowBattery20,
                              @IntRange(from = 0, to = 255) int lowBattery10,
                              @IntRange(from = 0, to = 255) int lowBattery5) {
        data = new byte[7];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_LOW_BATTERY.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x03;
        data[4] = (byte) lowBattery20;
        data[5] = (byte) lowBattery10;
        data[6] = (byte) lowBattery5;
    }

    public void setMoveSensitive(@IntRange(from = 7, to = 255) int sensitive) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_MOVE_SENSITIVE.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) sensitive;
    }

    public void setScanSettings(@IntRange(from = 4, to = 16384) int scanWindow,
                                @IntRange(from = 4, to = 16384) int scanInterval) {
        byte[] scanWindowBytes = MokoUtils.toByteArray(scanWindow, 2);
        byte[] scanIntervalBytes = MokoUtils.toByteArray(scanInterval, 2);
        data = new byte[8];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_SCAN_SETTINGS.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x04;
        data[4] = scanWindowBytes[0];
        data[5] = scanWindowBytes[1];
        data[6] = scanIntervalBytes[0];
        data[7] = scanIntervalBytes[1];
    }

    public void setFilterMac(String mac, boolean isReverse) {
        if (TextUtils.isEmpty(mac)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAC.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] macBytes = MokoUtils.hex2bytes(mac);
            int length = macBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAC.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < macBytes.length; i++) {
                data[5 + i] = macBytes[i];
            }
        }
    }

    public void setFilterName(String name, boolean isReverse) {
        if (TextUtils.isEmpty(name)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_NAME.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] nameBytes = name.getBytes();
            int length = nameBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_NAME.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < nameBytes.length; i++) {
                data[5 + i] = nameBytes[i];
            }
        }
    }

    public void setFilterAdvRawData(String adv, boolean isReverse) {
        if (TextUtils.isEmpty(adv)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_ADV_RAW_DATA.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] advBytes = MokoUtils.hex2bytes(adv);
            int length = advBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_ADV_RAW_DATA.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < advBytes.length; i++) {
                data[5 + i] = advBytes[i];
            }
        }
    }

    public void setFilterAllData(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_FILTER_ALL_DATA.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setFilterUUID(String uuid, boolean isReverse) {
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_UUID.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_UUID.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            for (int i = 0; i < uuidBytes.length; i++) {
                data[5 + i] = uuidBytes[i];
            }
        }
    }

    public void setVibrationNumber(@IntRange(from = 1, to = 10) int vibrationNumber) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_VIBRATIONS_NUMBER.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) vibrationNumber;
    }

    public void setFilterMajorRange(@IntRange(from = 0, to = 1) int enable,
                                    @IntRange(from = 0, to = 65535) int majorMin,
                                    @IntRange(from = 0, to = 65535) int majorMax,
                                    boolean isReverse) {
        if (enable == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAJOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] majorMinBytes = MokoUtils.toByteArray(majorMin, 2);
            byte[] majorMaxBytes = MokoUtils.toByteArray(majorMax, 2);
            data = new byte[9];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAJOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x05;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            data[5] = majorMinBytes[0];
            data[6] = majorMinBytes[1];
            data[7] = majorMaxBytes[0];
            data[8] = majorMaxBytes[1];
        }
    }

    public void setFilterMinorRange(@IntRange(from = 0, to = 1) int enable,
                                    @IntRange(from = 0, to = 65535) int minorMin,
                                    @IntRange(from = 0, to = 65535) int minorMax,
                                    boolean isReverse) {
        if (enable == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MINOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] minorMinBytes = MokoUtils.toByteArray(minorMin, 2);
            byte[] minorMaxBytes = MokoUtils.toByteArray(minorMax, 2);
            data = new byte[9];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MINOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x05;
            data[4] = (byte) (isReverse ? 0x02 : 0x01);
            data[5] = minorMinBytes[0];
            data[6] = minorMinBytes[1];
            data[7] = minorMaxBytes[0];
            data[8] = minorMaxBytes[1];
        }
    }
}
