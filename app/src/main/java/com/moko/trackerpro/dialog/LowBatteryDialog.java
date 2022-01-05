package com.moko.trackerpro.dialog;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.trackerpro.R;

import butterknife.BindView;
import butterknife.OnClick;

public class LowBatteryDialog extends BaseDialog implements SeekBar.OnSeekBarChangeListener {


    @BindView(R.id.sb_low_battery_20)
    SeekBar sbLowBattery20;
    @BindView(R.id.tv_low_battery_20_value)
    TextView tvLowBattery20Value;
    @BindView(R.id.sb_low_battery_10)
    SeekBar sbLowBattery10;
    @BindView(R.id.tv_low_battery_10_value)
    TextView tvLowBattery10Value;
    @BindView(R.id.sb_low_battery_5)
    SeekBar sbLowBattery5;
    @BindView(R.id.tv_low_battery_5_value)
    TextView tvLowBattery5Value;

    private int lowBattery20;
    private int lowBattery10;
    private int lowBattery5;

    public LowBatteryDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_low_battery;
    }

    @Override
    protected void renderConvertView(View convertView, Object o) {
        sbLowBattery20.setProgress(lowBattery20);
        sbLowBattery20.setOnSeekBarChangeListener(this);
        tvLowBattery20Value.setText(String.valueOf(lowBattery20));

        sbLowBattery10.setProgress(lowBattery10);
        sbLowBattery10.setOnSeekBarChangeListener(this);
        tvLowBattery10Value.setText(String.valueOf(lowBattery10));

        sbLowBattery5.setProgress(lowBattery5);
        sbLowBattery5.setOnSeekBarChangeListener(this);
        tvLowBattery5Value.setText(String.valueOf(lowBattery5));

    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                dismiss();
                if (lowBatteryListener != null)
                    lowBatteryListener.onEnsure(lowBattery20, lowBattery10, lowBattery5);
                break;
        }
    }

    private LowBatteryListener lowBatteryListener;

    public void setOnLowBatteryListener(LowBatteryListener lowBatteryListener) {
        this.lowBatteryListener = lowBatteryListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_low_battery_20:
                lowBattery20 = progress;
                tvLowBattery20Value.setText(String.valueOf(progress));
                break;
            case R.id.sb_low_battery_10:
                lowBattery10 = progress;
                tvLowBattery10Value.setText(String.valueOf(progress));
                break;
            case R.id.sb_low_battery_5:
                lowBattery5 = progress;
                tvLowBattery5Value.setText(String.valueOf(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface LowBatteryListener {

        void onEnsure(int lowBattery20, int lowBattery10, int lowBattery5);
    }

    public void setLowBattery20(int lowBattery20) {
        this.lowBattery20 = lowBattery20;
    }

    public void setLowBattery10(int lowBattery10) {
        this.lowBattery10 = lowBattery10;
    }

    public void setLowBattery5(int lowBattery5) {
        this.lowBattery5 = lowBattery5;
    }
}
