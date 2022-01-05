package com.moko.support;


import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.task.GetAdvIntervalTask;
import com.moko.support.task.GetBatteryPercentTask;
import com.moko.support.task.GetBatteryTask;
import com.moko.support.task.GetConnectionModeTask;
import com.moko.support.task.GetDeviceModelTask;
import com.moko.support.task.GetDeviceNameTask;
import com.moko.support.task.GetDeviceTypeTask;
import com.moko.support.task.GetFirmwareVersionTask;
import com.moko.support.task.GetHardwareVersionTask;
import com.moko.support.task.GetMajorTask;
import com.moko.support.task.GetManufacturerTask;
import com.moko.support.task.GetMeasurePowerTask;
import com.moko.support.task.GetMinorTask;
import com.moko.support.task.GetProductDateTask;
import com.moko.support.task.GetSoftwareVersionTask;
import com.moko.support.task.GetTrackingNotifyTask;
import com.moko.support.task.GetTrackingStateTask;
import com.moko.support.task.GetTransmissionTask;
import com.moko.support.task.GetUUIDTask;
import com.moko.support.task.OrderTask;
import com.moko.support.task.SetAdvIntervlTask;
import com.moko.support.task.SetConnectionModeTask;
import com.moko.support.task.SetDeviceNameTask;
import com.moko.support.task.SetMajorTask;
import com.moko.support.task.SetMeasurePowerTask;
import com.moko.support.task.SetMinorTask;
import com.moko.support.task.SetPasswordTask;
import com.moko.support.task.SetResetTask;
import com.moko.support.task.SetTrackingNotifyTask;
import com.moko.support.task.SetTrackingStateTask;
import com.moko.support.task.SetTransmissionTask;
import com.moko.support.task.SetUUIDTask;
import com.moko.support.task.WriteConfigTask;

public class OrderTaskAssembler {
    ///////////////////////////////////////////////////////////////////////////
    // READ
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask getManufacturer() {
        GetManufacturerTask getManufacturerTask = new GetManufacturerTask();
        return getManufacturerTask;
    }

    public static OrderTask getDeviceModel() {
        GetDeviceModelTask getDeviceModelTask = new GetDeviceModelTask();
        return getDeviceModelTask;
    }

    public static OrderTask getProductDate() {
        GetProductDateTask getProductDateTask = new GetProductDateTask();
        return getProductDateTask;
    }

    public static OrderTask getHardwareVersion() {
        GetHardwareVersionTask getHardwareVersionTask = new GetHardwareVersionTask();
        return getHardwareVersionTask;
    }

    public static OrderTask getFirmwareVersion() {
        GetFirmwareVersionTask getFirmwareVersionTask = new GetFirmwareVersionTask();
        return getFirmwareVersionTask;
    }

    public static OrderTask getSoftwareVersion() {
        GetSoftwareVersionTask getSoftwareVersionTask = new GetSoftwareVersionTask();
        return getSoftwareVersionTask;
    }

    public static OrderTask getBattery() {
        GetBatteryTask getBatteryTask = new GetBatteryTask();
        return getBatteryTask;
    }

    public static OrderTask getBatteryPercent() {
        GetBatteryPercentTask getBatteryPercentTask = new GetBatteryPercentTask();
        return getBatteryPercentTask;
    }

    public static OrderTask getDeviceName() {
        GetDeviceNameTask getDeviceNameTask = new GetDeviceNameTask();
        return getDeviceNameTask;
    }

    public static OrderTask getConnectionMode() {
        GetConnectionModeTask getConnectionModeTask = new GetConnectionModeTask();
        return getConnectionModeTask;
    }

    public static OrderTask getDeviceType() {
        GetDeviceTypeTask getDeviceTypeTask = new GetDeviceTypeTask();
        return getDeviceTypeTask;
    }

    public static OrderTask getMajor() {
        GetMajorTask getMajorTask = new GetMajorTask();
        return getMajorTask;
    }

    public static OrderTask getMinor() {
        GetMinorTask getMinorTask = new GetMinorTask();
        return getMinorTask;
    }

    public static OrderTask getMeasurePower() {
        GetMeasurePowerTask getMeasurePowerTask = new GetMeasurePowerTask();
        return getMeasurePowerTask;
    }

    public static OrderTask getTrackingNotify() {
        GetTrackingNotifyTask getTrackingNotifyTask = new GetTrackingNotifyTask();
        return getTrackingNotifyTask;
    }

    public static OrderTask getTransmission() {
        GetTransmissionTask getTransmissionTask = new GetTransmissionTask();
        return getTransmissionTask;
    }

    public static OrderTask getUUID() {
        GetUUIDTask getUUIDTask = new GetUUIDTask();
        return getUUIDTask;
    }

    public static OrderTask getAdvInterval() {
        GetAdvIntervalTask getAdvIntervalTask = new GetAdvIntervalTask();
        return getAdvIntervalTask;
    }

    public static OrderTask getTrackingState() {
        GetTrackingStateTask getTrackingStateTask = new GetTrackingStateTask();
        return getTrackingStateTask;
    }

    public static OrderTask getAdvTrigger() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_ADV_MOVE_CONDITION);
        return task;
    }

    public static OrderTask getTrackingInterval() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_TRACKING_INTERVAL);
        return task;
    }

    public static OrderTask getTrackingTrigger() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_TRACKING_TRIGGER);
        return task;
    }

    public static OrderTask getMacAddress() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_DEVICE_MAC);
        return task;
    }

    public static OrderTask getTriggerSensitivity() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_MOVE_SENSITIVE);
        return task;
    }

    public static OrderTask getScanSettings() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_SCAN_SETTINGS);
        return task;
    }

    public static OrderTask getButtonPower() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_BUTTON_POWER);
        return task;
    }

    public static OrderTask getButtonReset() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_BUTTON_RESET);
        return task;
    }

    public static OrderTask getConnectNotification() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_CONNECT_NOTIFICATION);
        return task;
    }

    public static OrderTask getSavedRawData() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_SAVED_RAW_DATA);
        return task;
    }

    public static OrderTask getLowBattery() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_LOW_BATTERY);
        return task;
    }

    public static OrderTask closePower() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.CLOSE_DEVICE);
        return task;
    }

    public static OrderTask getRssiFilter() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_RSSI);
        return task;
    }

    public static OrderTask getFilterEnable() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_ALL_DATA);
        return task;
    }

    public static OrderTask getFilterMac() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_MAC);
        return task;
    }

    public static OrderTask getFilterName() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_NAME);
        return task;
    }

    public static OrderTask getFilterUUID() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_UUID);
        return task;
    }

    public static OrderTask getFilterAdvRawData() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_ADV_RAW_DATA);
        return task;
    }

    public static OrderTask getVibrationNumber() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_VIBRATIONS_NUMBER);
        return task;
    }

    public static OrderTask getFilterMajorRange() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_MAJOR_RANGE);
        return task;
    }

    public static OrderTask getFilterMinorRange() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_FILTER_MINOR_RANGE);
        return task;
    }

    ///////////////////////////////////////////////////////////////////////////
    // WRITE
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask setAdvInterval(int advInterval) {
        SetAdvIntervlTask setAdvIntervlTask = new SetAdvIntervlTask();
        setAdvIntervlTask.setData(advInterval);
        return setAdvIntervlTask;
    }

    public static OrderTask setConnectionMode(int connectionMode) {
        SetConnectionModeTask setConnectionModeTask = new SetConnectionModeTask();
        setConnectionModeTask.setData(connectionMode);
        return setConnectionModeTask;
    }

    public static OrderTask setDeviceName(String deviceName) {
        SetDeviceNameTask setDeviceNameTask = new SetDeviceNameTask();
        setDeviceNameTask.setData(deviceName);
        return setDeviceNameTask;
    }

    public static OrderTask setMajor(int major) {
        SetMajorTask setMajorTask = new SetMajorTask();
        setMajorTask.setData(major);
        return setMajorTask;
    }

    public static OrderTask setMeasurePower(int measurePower) {
        SetMeasurePowerTask setMeasurePowerTask = new SetMeasurePowerTask();
        setMeasurePowerTask.setData(measurePower);
        return setMeasurePowerTask;
    }

    public static OrderTask setMinor(int minor) {
        SetMinorTask setMinorTask = new SetMinorTask();
        setMinorTask.setData(minor);
        return setMinorTask;
    }

    public static OrderTask setPassword(String password) {
        SetPasswordTask setPasswordTask = new SetPasswordTask();
        setPasswordTask.setData(password);
        return setPasswordTask;
    }

    public static OrderTask setReset(String password) {
        SetResetTask setResetTask = new SetResetTask();
        setResetTask.setData(password);
        return setResetTask;
    }

    public static OrderTask setTrackingState(int trackingState) {
        SetTrackingStateTask setTrackingStateTask = new SetTrackingStateTask();
        setTrackingStateTask.setData(trackingState);
        return setTrackingStateTask;
    }

    public static OrderTask setTrackingNotify(int enable) {
        SetTrackingNotifyTask setTrackingNotifyTask = new SetTrackingNotifyTask();
        setTrackingNotifyTask.setData(enable);
        return setTrackingNotifyTask;
    }

    public static OrderTask setTransmission(int transmission) {
        SetTransmissionTask setTransmissionTask = new SetTransmissionTask();
        setTransmissionTask.setData(transmission);
        return setTransmissionTask;
    }

    public static OrderTask setUUID(String uuid) {
        SetUUIDTask setUUIDTask = new SetUUIDTask();
        setUUIDTask.setData(uuid);
        return setUUIDTask;
    }


    public static WriteConfigTask setWriteConfig(ConfigKeyEnum configKeyEnum) {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setData(configKeyEnum);
        return writeConfigTask;
    }

    public static WriteConfigTask setTime() {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setTime();
        return writeConfigTask;
    }

    public static WriteConfigTask setAdvMoveCondition(int seconds) {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setAdvMoveCondition(seconds);
        return writeConfigTask;
    }

    public static WriteConfigTask setTrackingInterval(int minutes) {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setTrackingInterval(minutes);
        return writeConfigTask;
    }

    public static WriteConfigTask setTrackingTrigger(int seconds) {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setTrackingTrigger(seconds);
        return writeConfigTask;
    }

    public static WriteConfigTask setSensitivity(int sensitivity) {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setMoveSensitive(sensitivity);
        return writeConfigTask;
    }

    public static WriteConfigTask setScanSettings(int startWindow, int scanInterval) {
        WriteConfigTask writeConfigTask = new WriteConfigTask();
        writeConfigTask.setScanSettings(startWindow, scanInterval);
        return writeConfigTask;
    }

    public static OrderTask setButtonPower(int enable) {
        WriteConfigTask task = new WriteConfigTask();
        task.setTriggerEnable(enable);
        return task;
    }

    public static OrderTask setButtonReset(int enable) {
        WriteConfigTask task = new WriteConfigTask();
        task.setButtonReset(enable);
        return task;
    }

    public static OrderTask setConnectNotification(int enable) {
        WriteConfigTask task = new WriteConfigTask();
        task.setConnectNotification(enable);
        return task;
    }

    public static OrderTask setSavedRawData(int enable) {
        WriteConfigTask task = new WriteConfigTask();
        task.setSavedRawData(enable);
        return task;
    }

    public static OrderTask setLowBattery(int lowBattery20, int lowBattery10, int lowBattery5) {
        WriteConfigTask task = new WriteConfigTask();
        task.setLowBattery(lowBattery20, lowBattery10, lowBattery5);
        return task;
    }

    public static OrderTask setFilterRssi(int rssi) {
        WriteConfigTask task = new WriteConfigTask();
        task.setStoreRssiCondition(rssi);
        return task;
    }

    public static OrderTask setFilterAllData(int enable) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterAllData(enable);
        return task;
    }

    public static OrderTask setFilterMac(String mac, boolean isReverse) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterMac(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterName(String name, boolean isReverse) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterName(name, isReverse);
        return task;
    }

    public static OrderTask setFilterUUID(String uuid, boolean isReverse) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterUUID(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterAdvRawData(String rawData, boolean isReverse) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterAdvRawData(rawData, isReverse);
        return task;
    }

    public static OrderTask deleteTrackedData() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.CLEAR_SAVED_DATA);
        return task;
    }

    public static OrderTask getSavedCount() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.GET_SAVED_COUNT);
        return task;
    }

    public static OrderTask shake() {
        WriteConfigTask task = new WriteConfigTask();
        task.setData(ConfigKeyEnum.SHAKE);
        return task;
    }

    public static OrderTask setVibrationNumber(int vibrationNumber) {
        WriteConfigTask task = new WriteConfigTask();
        task.setVibrationNumber(vibrationNumber);
        return task;
    }

    public static OrderTask setFilterMajorRange(int enable, int majorMin, int majorMax, boolean isReverse) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterMajorRange(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRange(int enable, int majorMin, int majorMax, boolean isReverse) {
        WriteConfigTask task = new WriteConfigTask();
        task.setFilterMinorRange(enable, majorMin, majorMax, isReverse);
        return task;
    }
}
