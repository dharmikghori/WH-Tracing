package com.moko.trackerpro.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.task.OrderTask;
import com.moko.trackerpro.AppConstants;
import com.moko.trackerpro.R;
import com.moko.trackerpro.activity.DeviceInfoActivity;
import com.moko.trackerpro.activity.ExportDataActivity;
import com.moko.trackerpro.activity.FilterOptionsActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class TrackerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, NumberPickerView.OnValueChangeListener {
    private static final String TAG = TrackerFragment.class.getSimpleName();
    @BindView(R.id.sb_tracking_interval)
    SeekBar sbTrackingInterval;
    @BindView(R.id.tv_tracking_interval_value)
    TextView tvTrackingIntervalValue;
    @BindView(R.id.tv_tracking_interval_tips)
    TextView tvTrackingIntervalTips;
    @BindView(R.id.npv_tracking_notify)
    NumberPickerView npvTrackingNotify;
    @BindView(R.id.npv_vibrations_number)
    NumberPickerView npvVibrationsNumber;
    @BindView(R.id.rl_vibrations_number)
    RelativeLayout rlVibrationsNumber;
    @BindView(R.id.et_scan_window)
    EditText etScanWindow;
    @BindView(R.id.et_scan_interval)
    EditText etScanInterval;
    @BindView(R.id.et_tracking_trigger)
    EditText etTrackingTrigger;
    @BindView(R.id.tv_tracking_trigger_tips)
    TextView tvTrackingTriggerTips;
    @BindView(R.id.cl_tracking)
    ConstraintLayout clTracking;
    @BindView(R.id.iv_tracking)
    ImageView ivTracking;
    @BindView(R.id.npv_tracking_data_format)
    NumberPickerView npvTrackingDataFormat;
    @BindView(R.id.cl_tracking_trigger)
    ConstraintLayout clTrackingTrigger;

    private DeviceInfoActivity activity;

    public TrackerFragment() {
    }


    public static TrackerFragment newInstance() {
        TrackerFragment fragment = new TrackerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        sbTrackingInterval.setOnSeekBarChangeListener(this);
        npvTrackingNotify.setDisplayedValues(getResources().getStringArray(R.array.tracking_notify));
        npvTrackingNotify.setMaxValue(3);
        npvTrackingNotify.setMinValue(0);
        npvTrackingNotify.setValue(0);
        npvTrackingNotify.setOnValueChangedListener(this);

        npvVibrationsNumber.setDisplayedValues(getResources().getStringArray(R.array.vibrations_number));
        npvVibrationsNumber.setMaxValue(5);
        npvVibrationsNumber.setMinValue(0);
        npvVibrationsNumber.setValue(0);

        npvTrackingDataFormat.setDisplayedValues(getResources().getStringArray(R.array.tracking_data_format));
        npvTrackingDataFormat.setMaxValue(1);
        npvTrackingDataFormat.setMinValue(0);
        npvTrackingDataFormat.setValue(0);

        etTrackingTrigger.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int trigger = 0;
                if (!TextUtils.isEmpty(text)) {
                    trigger = Integer.parseInt(text);
                }
                tvTrackingTriggerTips.setText(getString(R.string.tracking_trigger_tips, trigger));
            }
        });
        return view;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_tracking_interval:
                tvTrackingIntervalValue.setText(String.format("%dmin", progress));
                tvTrackingIntervalTips.setText(getString(R.string.tracking_interval, progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public void disableTrigger() {
        if (clTrackingTrigger == null)
            return;
        clTrackingTrigger.setVisibility(View.GONE);
    }

    @OnClick({R.id.iv_tracking, R.id.tv_filter_options, R.id.tv_tracked_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_tracking:
                isTrackingOpen = !isTrackingOpen;
                ivTracking.setImageResource(isTrackingOpen ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                clTracking.setVisibility(isTrackingOpen ? View.VISIBLE : View.GONE);
                break;
            case R.id.tv_filter_options:
                startActivity(new Intent(getActivity(), FilterOptionsActivity.class));
                break;
            case R.id.tv_tracked_data:
                activity.getSavedCount();
                break;
        }
    }

    public boolean isValid() {
        if (isTrackingOpen) {
            if (activity.deviceType != 4 && activity.deviceType != 6) {
                String trackingTriggerStr = etTrackingTrigger.getText().toString();
                if (TextUtils.isEmpty(trackingTriggerStr))
                    return false;
                int trackingTrigger = Integer.parseInt(trackingTriggerStr);
                if (trackingTrigger > 65535)
                    return false;
            }
            String scanWindowStr = etScanWindow.getText().toString();
            if (TextUtils.isEmpty(scanWindowStr))
                return false;
            int scanWindow = Integer.parseInt(scanWindowStr);
            if (scanWindow < 4 || scanWindow > 16384)
                return false;
            String scanIntervalStr = etScanInterval.getText().toString();
            if (TextUtils.isEmpty(scanIntervalStr))
                return false;
            int scanInterval = Integer.parseInt(scanIntervalStr);
            if (scanInterval < 4 || scanInterval > 16384)
                return false;
            if (scanWindow > scanInterval)
                return false;
        }
        return true;
    }


    public void saveParams() {
        final int trackingInterval = sbTrackingInterval.getProgress();
        final int trackNotify = npvTrackingNotify.getValue();
        final int dataFormat = npvTrackingDataFormat.getValue();
        final String trackingTriggerStr = etTrackingTrigger.getText().toString();
        final String scanWindowStr = etScanWindow.getText().toString();
        final String scanIntervalStr = etScanInterval.getText().toString();
        List<OrderTask> orderTasks = new ArrayList<>();
        if (isTrackingOpen) {
            orderTasks.add(OrderTaskAssembler.setTrackingState(1));
            int scanWindow = Integer.parseInt(scanWindowStr);
            int scanInterval = Integer.parseInt(scanIntervalStr);
            orderTasks.add(OrderTaskAssembler.setScanSettings(scanWindow, scanInterval));
            if (activity.deviceType != 4 && activity.deviceType != 6) {
                int scannerTrigger = Integer.parseInt(trackingTriggerStr);
                orderTasks.add(OrderTaskAssembler.setTrackingTrigger(scannerTrigger));
            }
        } else {
            orderTasks.add(OrderTaskAssembler.setTrackingState(0));
        }
        orderTasks.add(OrderTaskAssembler.setTrackingInterval(trackingInterval));
        orderTasks.add(OrderTaskAssembler.setTrackingNotify(trackNotify));
        if (trackNotify > 1) {
            final int vibrationsNumber = npvVibrationsNumber.getValue() + 1;
            orderTasks.add(OrderTaskAssembler.setVibrationNumber(vibrationsNumber));
        }
        orderTasks.add(OrderTaskAssembler.setSavedRawData(dataFormat));
        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void setTrackingInterval(int time) {
        if (time <= 100)
            sbTrackingInterval.setProgress(time);
    }

    public void setTrackingNotify(int trackingNotify) {
        if (trackingNotify <= 3)
            npvTrackingNotify.setValue(trackingNotify);
    }

    public void setVibrationsNumber(int vibrationsNumber) {
        final int trackNotify = npvTrackingNotify.getValue();
        if (trackNotify > 1) {
            rlVibrationsNumber.setVisibility(View.VISIBLE);
        }
        npvVibrationsNumber.setValue(vibrationsNumber - 1);
    }

    public void setTrackingDataFormat(int dataFormat) {
        npvTrackingDataFormat.setValue(dataFormat);
    }


    private boolean isTrackingOpen;

    public void setTracking(int tracking) {
        isTrackingOpen = tracking == 1;
        ivTracking.setImageResource(isTrackingOpen ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        clTracking.setVisibility(isTrackingOpen ? View.VISIBLE : View.GONE);
    }

    public void setScanSettings(String scanWindowStr, String scanIntervalStr) {
        etScanWindow.setText(scanWindowStr);
        etScanInterval.setText(scanIntervalStr);
    }

    public void setTrackingTrigger(String trackingTrigger) {
        etTrackingTrigger.setText(trackingTrigger);
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        rlVibrationsNumber.setVisibility(newVal > 1 ? View.VISIBLE : View.GONE);
    }


    public void gotoTrackedData(int savedCount, int leftCount) {
        Intent intent = new Intent(getActivity(), ExportDataActivity.class);
        intent.putExtra(AppConstants.EXTRA_KEY_SAVED_COUNT, savedCount);
        intent.putExtra(AppConstants.EXTRA_KEY_LEFT_COUNT, leftCount);
        startActivity(intent);
    }
}
