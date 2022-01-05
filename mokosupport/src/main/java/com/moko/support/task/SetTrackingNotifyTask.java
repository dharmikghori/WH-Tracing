package com.moko.support.task;

import androidx.annotation.IntRange;

import com.moko.support.entity.OrderType;

public class SetTrackingNotifyTask extends OrderTask {
    public byte[] data;

    public SetTrackingNotifyTask() {
        super(OrderType.TRACKING_NOTIFY, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 0, to = 3) int enable) {
        this.data = new byte[1];
        data[0] = (byte) enable;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
