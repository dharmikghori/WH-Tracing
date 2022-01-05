package com.moko.trackerpro.activity;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.event.OrderTaskResponseEvent;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;
import com.moko.trackerpro.AppConstants;
import com.moko.trackerpro.R;
import com.moko.trackerpro.dialog.AlertMessageDialog;
import com.moko.trackerpro.dialog.LoadingMessageDialog;
import com.moko.trackerpro.fragment.AdvFragment;
import com.moko.trackerpro.fragment.DeviceFragment;
import com.moko.trackerpro.fragment.SettingFragment;
import com.moko.trackerpro.fragment.TrackerFragment;
import com.moko.trackerpro.service.DfuService;
import com.moko.trackerpro.utils.FileUtils;
import com.moko.trackerpro.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class DeviceInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    public static final int REQUEST_CODE_SELECT_FIRMWARE = 0x10;

    @BindView(R.id.frame_container)
    FrameLayout frameContainer;
    @BindView(R.id.radioBtn_adv)
    RadioButton radioBtnAdv;
    @BindView(R.id.radioBtn_scanner)
    RadioButton radioBtnScanner;
    @BindView(R.id.radioBtn_setting)
    RadioButton radioBtnSetting;
    @BindView(R.id.radioBtn_device)
    RadioButton radioBtnDevice;
    @BindView(R.id.rg_options)
    RadioGroup rgOptions;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    private FragmentManager fragmentManager;
    private AdvFragment advFragment;
    private TrackerFragment trackerFragment;
    private SettingFragment settingFragment;
    private DeviceFragment deviceFragment;
    public String mDeviceMac;
    public String mDeviceName;
    private boolean mReceiverTag = false;
    private int disConnectType;
    public int deviceType;
    public int mBattery;
    private boolean isSaveFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);
        deviceType = getIntent().getIntExtra(AppConstants.EXTRA_KEY_DEVICE_TYPE, -1);
        if (deviceType < 0) {
            finish();
            return;
        }
        fragmentManager = getFragmentManager();
        initFragment();
        radioBtnAdv.setChecked(true);
        tvTitle.setText(R.string.title_advertiser);
        rgOptions.setOnCheckedChangeListener(this);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            MokoSupport.getInstance().enableBluetooth();
        } else {
            showSyncingProgressDialog();
            List<OrderTask> orderTasks = new ArrayList<>();
            // sync time after connect success;
            orderTasks.add(OrderTaskAssembler.setTime());
            orderTasks.add(OrderTaskAssembler.getDeviceName());
            orderTasks.add(OrderTaskAssembler.getUUID());
            orderTasks.add(OrderTaskAssembler.getMajor());
            orderTasks.add(OrderTaskAssembler.getMinor());
            orderTasks.add(OrderTaskAssembler.getAdvInterval());
            orderTasks.add(OrderTaskAssembler.getTransmission());
            orderTasks.add(OrderTaskAssembler.getMeasurePower());
            if (deviceType != 4 && deviceType != 6) {
                orderTasks.add(OrderTaskAssembler.getAdvTrigger());
            }
            MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
        tvTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (deviceType == 4 || deviceType == 6) {
                    advFragment.disableTrigger();
                    trackerFragment.disableTrigger();
                    settingFragment.disableTrigger();
                }
            }
        }, 500);
    }

    private void initFragment() {
        advFragment = AdvFragment.newInstance();
        trackerFragment = TrackerFragment.newInstance();
        settingFragment = SettingFragment.newInstance();
        deviceFragment = DeviceFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.frame_container, advFragment)
                .add(R.id.frame_container, trackerFragment)
                .add(R.id.frame_container, settingFragment)
                .add(R.id.frame_container, deviceFragment)
                .show(advFragment)
                .hide(trackerFragment)
                .hide(settingFragment)
                .hide(deviceFragment)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
                    if (MokoSupport.getInstance().exportDatas != null) {
                        MokoSupport.getInstance().exportDatas.clear();
                        MokoSupport.getInstance().storeString = null;
                    }
                    showDisconnectDialog();
                }
                if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
                }
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderType) {
                    case DISCONNECTED_NOTIFY:
                        int type = value[0] & 0xFF;
                        disConnectType = type;
                        if (type == 0) {
                            // valid password timeout
                        } else if (type == 1) {
                            // change password success
                        } else if (type == 2) {
                            // reset success
                        } else if (type == 3) {
                            // no data exchange timeout
                        } else if (type == 4) {
                            // close device
                        }
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                if (responseType != OrderTask.RESPONSE_TYPE_WRITE)
                    return;
                switch (orderType) {
                    case RESET:
                        showResetErrorDialog();
                        break;
                    case DEVICE_NAME:
                    case UUID:
                    case MAJOR:
                    case MINOR:
                    case ADV_INTERVAL:
                    case MEASURE_POWER:
                    case TRANSMISSION:
                    case TRACKING_NOTIFY:
                    case TRACKING_STATE:
                    case CONNECTION_MODE:
                    case WRITE_CONFIG:
                        isSaveFailed = true;
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                tvTitle.postDelayed(() -> {
                    if (isSaveFailed) {
                        isSaveFailed = false;
                        ToastUtils.showToast(this, "Saved failed");
                    }
                    dismissSyncProgressDialog();
                }, 500);
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderType) {
                    case DEVICE_NAME:
                        final String deviceName = new String(value);
                        mDeviceName = deviceName;
                        advFragment.setDeviceName(deviceName);
                        break;
                    case UUID:
                        final String uuid = MokoUtils.bytesToHexString(value);
                        advFragment.setUUID(uuid);
                        break;
                    case MAJOR:
                        final int major = MokoUtils.toInt(value);
                        advFragment.setMajor(major);
                        break;
                    case MINOR:
                        final int minor = MokoUtils.toInt(value);
                        advFragment.setMinor(minor);
                        break;
                    case ADV_INTERVAL:
                        final int advInterval = MokoUtils.toInt(value);
                        advFragment.setAdvInterval(advInterval);
                        break;
                    case MEASURE_POWER:
                        if (responseType == OrderTask.RESPONSE_TYPE_READ) {
                            int rssi_1m = value[0];
                            advFragment.setMeasurePower(rssi_1m);
                        }
                        if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
                            if (isSaveFailed)
                                return;
                            AlertMessageDialog dialog = new AlertMessageDialog();
                            dialog.setMessage("Saved Successfully！");
                            dialog.setConfirm("OK");
                            dialog.setCancelGone();
                            dialog.show(getSupportFragmentManager());
                        }
                        break;
                    case TRANSMISSION:
                        int txPower = value[0];
                        advFragment.setTransmission(txPower);
                        break;
                    case TRACKING_NOTIFY:
                        int trackNotify = value[0] & 0xFF;
                        trackerFragment.setTrackingNotify(trackNotify);
                        break;
                    case BATTERY:
                        int battery = MokoUtils.toInt(value);
                        mBattery = battery;
                        break;
                    case DEVICE_MODEL:
                        String productModel = new String(value);
                        deviceFragment.setProductModel(productModel);
                        break;
                    case SOFTWARE_VERSION:
                        String softwareVersion = new String(value);
                        deviceFragment.setSoftwareVersion(softwareVersion);
                        break;
                    case FIRMWARE_VERSION:
                        String firmwareVersion = new String(value);
                        int begin = firmwareVersion.indexOf("-");
                        if (begin > 0) {
                            firmwareVersion = firmwareVersion.substring(begin + 1);
                        }
                        deviceFragment.setFirmwareVersion(firmwareVersion);
                        break;
                    case BATTERY_PERCENT:
                        int batteryPercent = value[0] & 0xFF;
                        deviceFragment.setBatteryValtage(mBattery, batteryPercent);
                        break;
                    case HARDWARE_VERSION:
                        String hardwareVersion = new String(value);
                        deviceFragment.setHardwareVersion(hardwareVersion);
                        break;
                    case PRODUCT_DATE:
                        String manufactureDate = new String(value);
                        deviceFragment.setManufactureDate(manufactureDate);
                        break;
                    case MANUFACTURER:
                        String manufacture = new String(value);
                        deviceFragment.setManufacture(manufacture);
                        break;
                    case TRACKING_STATE:
                        int tracking = value[0] & 0xFF;
                        trackerFragment.setTracking(tracking);
                        break;
                    case CONNECTION_MODE:
                        int connectable = value[0] & 0xFF;
                        settingFragment.setConnectable(connectable);
                        break;
                    case WRITE_CONFIG:
                        if (value.length >= 2) {
                            int key = value[1] & 0xFF;
                            ConfigKeyEnum configKeyEnum = ConfigKeyEnum.fromConfigKey(key);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            switch (configKeyEnum) {
                                case GET_ADV_MOVE_CONDITION:
                                    if (length == 1) {
                                        advFragment.setAdvTriggerClose();
                                    }
                                    if (length == 2) {
                                        final int duration = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 6));
                                        advFragment.setAdvTrigger(duration);
                                    }
                                    break;
                                case GET_TRACKING_INTERVAL:
                                    if (length == 1) {
                                        final int time = value[4] & 0xFF;
                                        trackerFragment.setTrackingInterval(time);
                                    }
                                    break;
                                case GET_TRACKING_TRIGGER:
                                    if (length == 1) {
                                        trackerFragment.setTrackingTrigger("0");
                                    }
                                    if (length == 2) {
                                        final String duration = String.valueOf(MokoUtils.toInt(Arrays.copyOfRange(value, 4, 6)));
                                        trackerFragment.setTrackingTrigger(duration);
                                    }
                                    break;
                                case GET_DEVICE_MAC:
                                    if (length == 6) {
                                        byte[] macBytes = Arrays.copyOfRange(value, 4, 10);
                                        StringBuffer stringBuffer = new StringBuffer();
                                        for (int i = 0, l = macBytes.length; i < l; i++) {
                                            stringBuffer.append(MokoUtils.byte2HexString(macBytes[i]));
                                            if (i < (l - 1))
                                                stringBuffer.append(":");
                                        }
                                        mDeviceMac = stringBuffer.toString();
                                        deviceFragment.setMacAddress(stringBuffer.toString());
                                    }
                                    break;
                                case GET_MOVE_SENSITIVE:
                                    if (length == 1) {
                                        int sensitivity = value[4] & 0xFF;
                                        settingFragment.setSensitivity(sensitivity);
                                    }
                                    break;
                                case GET_SCAN_SETTINGS:
                                    if (length == 4) {
                                        byte[] scanWindowBytes = Arrays.copyOfRange(value, 4, 6);
                                        byte[] scanIntervalBytes = Arrays.copyOfRange(value, 6, 8);
                                        String scanWindowStr = String.valueOf(MokoUtils.toInt(scanWindowBytes));
                                        String scanIntervalStr = String.valueOf(MokoUtils.toInt(scanIntervalBytes));
                                        trackerFragment.setScanSettings(scanWindowStr, scanIntervalStr);
                                    }
                                    break;
                                case GET_BUTTON_POWER:
                                    if (length == 1) {
                                        int enable = value[4] & 0xFF;
                                        settingFragment.setButtonPower(enable);
                                    }
                                    break;
                                case GET_CONNECT_NOTIFICATION:
                                    if (length == 1) {
                                        int enable = value[4] & 0xFF;
                                        settingFragment.setConnectNotification(enable);
                                    }
                                    break;
                                case GET_LOW_BATTERY:
                                    if (length == 3) {
                                        int lowBattery20 = value[4] & 0xFF;
                                        int lowBattery10 = value[5] & 0xFF;
                                        int lowBattery5 = value[6] & 0xFF;
                                        settingFragment.setLowBattery(lowBattery20, lowBattery10, lowBattery5);
                                    }
                                    break;
                                case GET_BUTTON_RESET:
                                    if (length == 1) {
                                        int enable = value[4] & 0xFF;
                                        settingFragment.setButtonReset(enable);
                                    }
                                    break;
                                case GET_VIBRATIONS_NUMBER:
                                    if (length == 1) {
                                        int vibrationsNumber = value[4] & 0xFF;
                                        trackerFragment.setVibrationsNumber(vibrationsNumber);
                                    }
                                    break;
                                case GET_SAVED_RAW_DATA:
                                    if (length == 1) {
                                        int dataFormat = value[4] & 0xFF;
                                        trackerFragment.setTrackingDataFormat(dataFormat);
                                    }
                                    break;
                                case GET_SAVED_COUNT:
                                    if (length == 4) {
                                        byte[] savedCount = Arrays.copyOfRange(value, 4, 6);
                                        byte[] leftCount = Arrays.copyOfRange(value, 6, 8);
                                        trackerFragment.gotoTrackedData(MokoUtils.toInt(savedCount), MokoUtils.toInt(leftCount));
                                    }
                                    break;
                                case SET_ADV_MOVE_CONDITION:
                                    // EB 31 00 00
                                    break;
                                case SET_SAVED_RAW_DATA:
                                    // EB 32 00 00
                                    if (length == 0) {
                                        if (isSaveFailed)
                                            return;
                                        AlertMessageDialog dialog = new AlertMessageDialog();
                                        dialog.setMessage("Saved Successfully！");
                                        dialog.setConfirm("OK");
                                        dialog.setCancelGone();
                                        dialog.show(getSupportFragmentManager());
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        });
    }

    private void showDisconnectDialog() {
        if (disConnectType == 1) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Change Password");
            dialog.setMessage("Password changed successfully!Please reconnect the device.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 2) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setTitle("Reset");
            dialog.setMessage("Reset successfully!Please reconnect the device.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 3) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("No data communication for 2 minutes, the device is disconnected.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else if (disConnectType == 4) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("The Beacon is disconnected.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        } else {
            if (MokoSupport.getInstance().isBluetoothOpen() && !isUpgrade) {
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Dismiss");
                dialog.setMessage("The Beacon disconnected!");
                dialog.setConfirm("Exit");
                dialog.setCancelGone();
                dialog.setOnAlertConfirmListener(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
                dialog.show(getSupportFragmentManager());
            }
        }
    }

    private void showResetErrorDialog() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setMessage("Password is incorrect ! Please try again.");
        dialog.setConfirm("OK");
        dialog.setCancelGone();
        dialog.show(getSupportFragmentManager());
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
                            builder.setTitle("Dismiss");
                            builder.setCancelable(false);
                            builder.setMessage("The current system of bluetooth is not available!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeviceInfoActivity.this.setResult(RESULT_OK);
                                    finish();
                                }
                            });
                            builder.show();
                            break;

                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_FIRMWARE) {
            if (resultCode == RESULT_OK) {
                //得到uri，后面就是将uri转化成file的过程。
                Uri uri = data.getData();
                String firmwareFilePath = FileUtils.getPath(this, uri);
                if (TextUtils.isEmpty(firmwareFilePath))
                    return;
                final File firmwareFile = new File(firmwareFilePath);
                if (firmwareFile.exists()) {
                    final DfuServiceInitiator starter = new DfuServiceInitiator(mDeviceMac)
                            .setDeviceName(mDeviceName)
                            .setKeepBond(false)
                            .setDisableNotification(true);
                    starter.setZip(null, firmwareFilePath);
                    starter.start(this, DfuService.class);
                    showDFUProgressDialog("Waiting...");
                } else {
                    Toast.makeText(this, "file is not exists!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    @OnClick({R.id.tv_back, R.id.iv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                back();
                break;
            case R.id.iv_save:
                if (radioBtnAdv.isChecked()) {
                    if (advFragment.isValid()) {
                        showSyncingProgressDialog();
                        advFragment.saveParams();
                    } else {
                        ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                    }
                }
                if (radioBtnScanner.isChecked()) {
                    if (trackerFragment.isValid()) {
                        showSyncingProgressDialog();
                        trackerFragment.saveParams();
                    } else {
                        ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                    }
                }
                break;
        }
    }

    private void back() {
        MokoSupport.getInstance().disConnectBle();
//        mIsClose = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radioBtn_adv:
                showAdvAndGetData();
                break;
            case R.id.radioBtn_scanner:
                showTrackerAndGetData();
                break;
            case R.id.radioBtn_setting:
                showSettingAndGetData();
                break;
            case R.id.radioBtn_device:
                showDeviceAndGetData();
                break;
        }
    }

    private void showDeviceAndGetData() {
        tvTitle.setText(R.string.title_device);
        ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .hide(advFragment)
                .hide(trackerFragment)
                .hide(settingFragment)
                .show(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // device
        orderTasks.add(OrderTaskAssembler.getBattery());
        orderTasks.add(OrderTaskAssembler.getBatteryPercent());
        orderTasks.add(OrderTaskAssembler.getMacAddress());
        orderTasks.add(OrderTaskAssembler.getDeviceModel());
        orderTasks.add(OrderTaskAssembler.getSoftwareVersion());
        orderTasks.add(OrderTaskAssembler.getFirmwareVersion());
        orderTasks.add(OrderTaskAssembler.getHardwareVersion());
        orderTasks.add(OrderTaskAssembler.getProductDate());
        orderTasks.add(OrderTaskAssembler.getManufacturer());
        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showSettingAndGetData() {
        tvTitle.setText(R.string.title_setting);
        ivSave.setVisibility(View.GONE);
        fragmentManager.beginTransaction()
                .hide(advFragment)
                .hide(trackerFragment)
                .show(settingFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // setting
        orderTasks.add(OrderTaskAssembler.getButtonPower());
        orderTasks.add(OrderTaskAssembler.getConnectionMode());
        orderTasks.add(OrderTaskAssembler.getMacAddress());
        orderTasks.add(OrderTaskAssembler.getConnectNotification());
        orderTasks.add(OrderTaskAssembler.getLowBattery());
        if (deviceType != 4 && deviceType != 6) {
            orderTasks.add(OrderTaskAssembler.getTriggerSensitivity());
        }
        orderTasks.add(OrderTaskAssembler.getButtonReset());
        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showTrackerAndGetData() {
        tvTitle.setText(R.string.title_tracker);
        ivSave.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
                .hide(advFragment)
                .show(trackerFragment)
                .hide(settingFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // tracker
        orderTasks.add(OrderTaskAssembler.getTrackingState());
        orderTasks.add(OrderTaskAssembler.getScanSettings());
        if (deviceType != 4 && deviceType != 6) {
            orderTasks.add(OrderTaskAssembler.getTrackingTrigger());
        }
        orderTasks.add(OrderTaskAssembler.getTrackingNotify());
        orderTasks.add(OrderTaskAssembler.getVibrationNumber());
        orderTasks.add(OrderTaskAssembler.getTrackingInterval());
        orderTasks.add(OrderTaskAssembler.getSavedRawData());

        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private void showAdvAndGetData() {
        tvTitle.setText(R.string.title_advertiser);
        ivSave.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
                .show(advFragment)
                .hide(trackerFragment)
                .hide(settingFragment)
                .hide(deviceFragment)
                .commit();
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        // get adv params
        orderTasks.add(OrderTaskAssembler.getDeviceName());
        orderTasks.add(OrderTaskAssembler.getUUID());
        orderTasks.add(OrderTaskAssembler.getMajor());
        orderTasks.add(OrderTaskAssembler.getMinor());
        orderTasks.add(OrderTaskAssembler.getAdvInterval());
        orderTasks.add(OrderTaskAssembler.getTransmission());
        orderTasks.add(OrderTaskAssembler.getMeasurePower());
        if (deviceType != 4 && deviceType != 6) {
            orderTasks.add(OrderTaskAssembler.getAdvTrigger());
        }
        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

//    private boolean isModifyPassword;

    public void changePassword(String password) {
//        isModifyPassword = true;
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setPassword(password));
    }

    public void reset(String password) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setReset(password));
    }

    public void setSensitivity(int sensitivity) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setSensitivity(sensitivity), OrderTaskAssembler.getTriggerSensitivity());
    }

    public void changeConnectState(int connectState) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setConnectionMode(connectState), OrderTaskAssembler.getConnectionMode());
    }

    public void changeConnectionNotification(int enable) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setConnectNotification(enable), OrderTaskAssembler.getConnectNotification());
    }

    public void setLowBattery(int lowBattery20, int lowBattery10, int lowBattery5) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setLowBattery(lowBattery20, lowBattery10, lowBattery5), OrderTaskAssembler.getLowBattery());
    }

    public void changeButtonPowerState(int buttonPowerState) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setButtonPower(buttonPowerState), OrderTaskAssembler.getButtonPower());
    }

    public void changeButtonResetState(int buttonResetState) {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setButtonReset(buttonResetState), OrderTaskAssembler.getButtonReset());
    }

    public void powerOff() {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.closePower());
    }

    public void getSavedCount() {
        showSyncingProgressDialog();
        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getSavedCount());
    }

    public void chooseFirmwareFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "select file first!"), REQUEST_CODE_SELECT_FIRMWARE);
        } catch (ActivityNotFoundException ex) {
            ToastUtils.showToast(this, "install file manager app");
        }
    }

    private ProgressDialog mDFUDialog;

    private void showDFUProgressDialog(String tips) {
        mDFUDialog = new ProgressDialog(DeviceInfoActivity.this);
        mDFUDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDFUDialog.setCanceledOnTouchOutside(false);
        mDFUDialog.setCancelable(false);
        mDFUDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDFUDialog.setMessage(tips);
        if (!isFinishing() && mDFUDialog != null && !mDFUDialog.isShowing()) {
            mDFUDialog.show();
        }
    }

    private void dismissDFUProgressDialog() {
        mDeviceConnectCount = 0;
        if (!isFinishing() && mDFUDialog != null && mDFUDialog.isShowing()) {
            mDFUDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
        builder.setTitle("Dismiss");
        builder.setCancelable(false);
        builder.setMessage("The device disconnected!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isUpgrade = false;
                DeviceInfoActivity.this.setResult(RESULT_OK);
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
    }

    private int mDeviceConnectCount;
    private boolean isUpgrade;

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            LogModule.w("onDeviceConnecting...");
            mDeviceConnectCount++;
            if (mDeviceConnectCount > 3) {
                Toast.makeText(DeviceInfoActivity.this, "Error:DFU Failed", Toast.LENGTH_SHORT).show();
                dismissDFUProgressDialog();
                final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(DeviceInfoActivity.this);
                final Intent abortAction = new Intent(DfuService.BROADCAST_ACTION);
                abortAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
                manager.sendBroadcast(abortAction);
            }
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            LogModule.w("onDeviceDisconnecting...");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            isUpgrade = true;
            mDFUDialog.setMessage("DfuProcessStarting...");
        }


        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            mDFUDialog.setMessage("EnablingDfuMode...");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            mDFUDialog.setMessage("FirmwareValidating...");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            ToastUtils.showToast(DeviceInfoActivity.this, "DFU Successfully!");
            dismissDFUProgressDialog();
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            mDFUDialog.setMessage("DfuAborted...");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            mDFUDialog.setMessage("Progress:" + percent + "%");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            ToastUtils.showToast(DeviceInfoActivity.this, "Opps!DFU Failed. Please try again!");
            LogModule.i("Error:" + message);
            dismissDFUProgressDialog();
        }
    };
}
