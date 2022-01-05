package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetTrackingNotifyTask extends OrderTask {
    public byte[] data;

    public GetTrackingNotifyTask() {
        super(OrderType.TRACKING_NOTIFY, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
