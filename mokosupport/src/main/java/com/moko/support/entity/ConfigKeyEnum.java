package com.moko.support.entity;


import java.io.Serializable;

public enum ConfigKeyEnum implements Serializable {
    GET_DEVICE_MAC(0x20),
    SET_DEVICE_MAC(0x30),

    GET_ADV_MOVE_CONDITION(0x21),
    SET_ADV_MOVE_CONDITION(0x31),

    GET_TRACKING_TRIGGER(0x22),
    SET_TRACKING_TRIGGER(0x32),

    GET_FILTER_RSSI(0x23),
    SET_FILTER_RSSI(0x33),

    GET_TRACKING_INTERVAL(0x24),
    SET_TRACKING_INTERVAL(0x34),

    GET_TIME(0x25),
    SET_TIME(0x35),

    CLOSE_DEVICE(0x26),
    SET_DEFAULT(0x27),

    GET_BUTTON_POWER(0x28),
    SET_BUTTON_POWER(0x38),

    CLEAR_SAVED_DATA(0x29),
    GET_SAVED_COUNT(0XF2),

    GET_MOVE_SENSITIVE(0x40),
    SET_MOVE_SENSITIVE(0x50),

    GET_SCAN_SETTINGS(0x60),
    SET_SCAN_SETTINGS(0x70),

    GET_FILTER_MAC(0x41),
    SET_FILTER_MAC(0x51),

    GET_FILTER_NAME(0x42),
    SET_FILTER_NAME(0x52),

    GET_FILTER_ADV_RAW_DATA(0x43),
    SET_FILTER_ADV_RAW_DATA(0x53),

    GET_FILTER_ALL_DATA(0x46),
    SET_FILTER_ALL_DATA(0x56),

    GET_FILTER_UUID(0x47),
    SET_FILTER_UUID(0x57),

    SHAKE(0x61),

    GET_VIBRATIONS_NUMBER(0x62),
    SET_VIBRATIONS_NUMBER(0x72),

    GET_FILTER_MAJOR_RANGE(0x63),
    SET_FILTER_MAJOR_RANGE(0x73),

    GET_FILTER_MINOR_RANGE(0x64),
    SET_FILTER_MINOR_RANGE(0x74),

    GET_BUTTON_RESET(0x65),
    SET_BUTTON_RESET(0x75),

    GET_CONNECT_NOTIFICATION(0x6A),
    SET_CONNECT_NOTIFICATION(0x7A),

    GET_PRODUCTION_DATE_CONFIG(0x6B),
    SET_PRODUCTION_DATE_CONFIG(0x7B),

    GET_SAVED_RAW_DATA(0x69),
    SET_SAVED_RAW_DATA(0x79),

    GET_LOW_BATTERY(0x68),
    SET_LOW_BATTERY(0x78),

    OPEN_SHAKE_TEST(0xF5),
    CLOSE_SHAKE_TEST(0xF6),
    ;

    private int configKey;

    ConfigKeyEnum(int configKey) {
        this.configKey = configKey;
    }


    public int getConfigKey() {
        return configKey;
    }

    public static ConfigKeyEnum fromConfigKey(int configKey) {
        for (ConfigKeyEnum configKeyEnum : ConfigKeyEnum.values()) {
            if (configKeyEnum.getConfigKey() == configKey) {
                return configKeyEnum;
            }
        }
        return null;
    }
}
