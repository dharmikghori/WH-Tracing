package com.moko.trackerpro.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moko.trackerpro.R;
import com.moko.trackerpro.activity.DeviceInfoActivity;
import com.moko.trackerpro.dialog.AlertMessageDialog;
import com.moko.trackerpro.dialog.ChangePasswordDialog;
import com.moko.trackerpro.dialog.LowBatteryDialog;
import com.moko.trackerpro.dialog.ResetDialog;
import com.moko.trackerpro.dialog.TriggerSensitivityDialog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    @BindView(R.id.tv_change_password)
    TextView tvChangePassword;
    @BindView(R.id.tv_factory_reset)
    TextView tvFactoryReset;
    @BindView(R.id.tv_update_firmware)
    TextView tvUpdateFirmware;
    @BindView(R.id.tv_trigger_sensitivity)
    TextView tvTriggerSensitivity;
    @BindView(R.id.iv_connectable)
    ImageView ivConnectable;
    @BindView(R.id.iv_button_power)
    ImageView ivButtonPower;
    @BindView(R.id.tv_button_power)
    TextView tvButtonPower;
    @BindView(R.id.iv_power_off)
    ImageView ivPowerOff;
    @BindView(R.id.tv_connectable)
    TextView tvConnectable;
    @BindView(R.id.iv_connection_notification)
    ImageView ivConnectionNotification;
    @BindView(R.id.tv_button_reset)
    TextView tvButtonReset;
    @BindView(R.id.iv_button_reset)
    ImageView ivButtonReset;


    private DeviceInfoActivity activity;

    public SettingFragment() {
    }


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        return view;
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

    @OnClick({R.id.tv_change_password, R.id.tv_factory_reset, R.id.tv_update_firmware,
            R.id.tv_trigger_sensitivity, R.id.iv_connectable, R.id.iv_connection_notification,
            R.id.tv_low_power_notification, R.id.iv_button_power, R.id.iv_power_off,
            R.id.iv_button_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_change_password:
                showChangePasswordDialog();
                break;
            case R.id.tv_factory_reset:
                showResetDialog();
                break;
            case R.id.tv_update_firmware:
                activity.chooseFirmwareFile();
                break;
            case R.id.tv_trigger_sensitivity:
                showTriggerSensitivityDialog();
                break;
            case R.id.iv_connectable:
                showConnectableDialog();
                break;
            case R.id.iv_connection_notification:
                int enable = !connectNotification ? 1 : 0;
                activity.changeConnectionNotification(enable);
                break;
            case R.id.tv_low_power_notification:
                showLowBatteryDialog();
                break;
            case R.id.iv_button_power:
                showButtonPowerDialog();
                break;
            case R.id.iv_power_off:
                showPowerOffDialog();
                break;
            case R.id.iv_button_reset:
                showButtonResetDialog();
                break;
        }
    }

    private void showTriggerSensitivityDialog() {
        final TriggerSensitivityDialog dialog = new TriggerSensitivityDialog(getActivity());
        dialog.setData(sensitivityStr);
        dialog.setOnSensitivityClicked(sensitivity -> activity.setSensitivity(sensitivity));
        dialog.show();
    }

    private void showResetDialog() {
        final ResetDialog dialog = new ResetDialog(getActivity());
        dialog.setOnPasswordClicked(password -> activity.reset(password));
        dialog.show();
        Timer resetTimer = new Timer();
        resetTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(() -> dialog.showKeyboard());
            }
        }, 200);
    }

    private void showChangePasswordDialog() {
        final ChangePasswordDialog dialog = new ChangePasswordDialog(getActivity());
        dialog.setOnPasswordClicked(password -> activity.changePassword(password));
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override

            public void run() {
                activity.runOnUiThread(() -> dialog.showKeyboard());
            }
        }, 200);
    }

    private void showConnectableDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        if (connectState) {
            dialog.setMessage("Are you sure to make the device unconnectable？");
        } else {
            dialog.setMessage("Are you sure to make the device connectable？");
        }
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            int value = !connectState ? 1 : 0;
            activity.changeConnectState(value);
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showButtonPowerDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        if (buttonPowerState) {
            dialog.setMessage("If you Disable Button Off function, you cannot turn off the beacon power with the button.");
        } else {
            dialog.setMessage("If you Enable Button Off  function, you can turn off the beacon power with the button.");
        }
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            int value = !buttonPowerState ? 1 : 0;
            activity.changeButtonPowerState(value);
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showButtonResetDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        if (buttonResetState) {
            dialog.setMessage("If you Disable Button Reset function, you cannot Rest the beacon power with the button.");
        } else {
            dialog.setMessage("If you Enable Button Reset  function, you can Reset the beacon power with the button.");
        }
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            int value = !buttonResetState ? 1 : 0;
            activity.changeButtonResetState(value);
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showPowerOffDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure to turn off the device? Please make sure the device has a button to turn on!");
        dialog.setConfirm("OK");
        dialog.setOnAlertConfirmListener(() -> {
            activity.powerOff();
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    private void showLowBatteryDialog() {
        LowBatteryDialog dialog = new LowBatteryDialog(getActivity());
        dialog.setLowBattery20(lowBattery20);
        dialog.setLowBattery10(lowBattery10);
        dialog.setLowBattery5(lowBattery5);
        dialog.setOnLowBatteryListener((lowBattery20, lowBattery10, lowBattery5) -> {
            this.lowBattery20 = lowBattery20;
            this.lowBattery10 = lowBattery10;
            this.lowBattery5 = lowBattery5;
            activity.setLowBattery(lowBattery20, lowBattery10, lowBattery5);
        });
        dialog.show();
    }

    private String sensitivityStr;

    public void setSensitivity(int sensitivity) {
        sensitivityStr = String.valueOf(sensitivity);
        int value = 248 - (sensitivity - 7);
        tvTriggerSensitivity.setText(getString(R.string.trigger_sensitivity, value));
    }


    private boolean connectState;

    public void setConnectable(int connectable) {
        connectState = connectable == 1;
        ivConnectable.setImageResource(connectState ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        tvConnectable.setText(connectState ? "Connectable" : "Non-connectable");
    }

    private boolean buttonPowerState;

    public void setButtonPower(int enable) {
        buttonPowerState = enable == 1;
        ivButtonPower.setImageResource(buttonPowerState ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        tvButtonPower.setText(buttonPowerState ? "Enable Button Off" : "Disable Button Off");
    }

    public void disableTrigger() {
        if (tvTriggerSensitivity == null)
            return;
        tvTriggerSensitivity.setVisibility(View.GONE);
    }

    private boolean connectNotification;

    public void setConnectNotification(int enable) {
        connectNotification = enable == 1;
        ivConnectionNotification.setImageResource(connectNotification ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }

    private int lowBattery20;
    private int lowBattery10;
    private int lowBattery5;

    public void setLowBattery(int lowBattery20, int lowBattery10, int lowBattery5) {
        this.lowBattery20 = lowBattery20;
        this.lowBattery10 = lowBattery10;
        this.lowBattery5 = lowBattery5;
    }

    private boolean buttonResetState;

    public void setButtonReset(int enable) {
        buttonResetState = enable == 1;
        ivButtonReset.setImageResource(buttonResetState ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        tvButtonReset.setText(buttonResetState ? "Enable Button Reset" : "Disable Button Reset");
    }
}
