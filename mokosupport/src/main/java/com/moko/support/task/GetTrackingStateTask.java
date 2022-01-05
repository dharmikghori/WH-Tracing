package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetTrackingStateTask extends OrderTask {
    public byte[] data;

    public GetTrackingStateTask() {
        super(OrderType.TRACKING_STATE, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
