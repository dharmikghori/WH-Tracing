package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetBatteryPercentTask extends OrderTask {

    public byte[] data;

    public GetBatteryPercentTask() {
        super(OrderType.BATTERY_PERCENT, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
