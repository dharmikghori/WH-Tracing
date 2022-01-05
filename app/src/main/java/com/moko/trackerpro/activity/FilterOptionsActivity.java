package com.moko.trackerpro.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.event.OrderTaskResponseEvent;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;
import com.moko.trackerpro.R;
import com.moko.trackerpro.dialog.AlertMessageDialog;
import com.moko.trackerpro.dialog.LoadingMessageDialog;
import com.moko.trackerpro.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterOptionsActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";
    @BindView(R.id.sb_rssi_filter)
    SeekBar sbRssiFilter;
    @BindView(R.id.tv_rssi_filter_value)
    TextView tvRssiFilterValue;
    @BindView(R.id.tv_rssi_filter_tips)
    TextView tvRssiFilterTips;
    @BindView(R.id.iv_adv_data_filter)
    ImageView ivAdvDataFilter;
    @BindView(R.id.iv_mac_address)
    ImageView ivMacAddress;
    @BindView(R.id.et_mac_address)
    EditText etMacAddress;
    @BindView(R.id.iv_adv_name)
    ImageView ivAdvName;
    @BindView(R.id.et_adv_name)
    EditText etAdvName;
    @BindView(R.id.iv_ibeacon_uuid)
    ImageView ivIbeaconUuid;
    @BindView(R.id.et_ibeacon_uuid)
    EditText etIbeaconUuid;
    @BindView(R.id.iv_ibeacon_major)
    ImageView ivIbeaconMajor;
    @BindView(R.id.iv_ibeacon_minor)
    ImageView ivIbeaconMinor;
    @BindView(R.id.iv_raw_adv_data)
    ImageView ivRawAdvData;
    @BindView(R.id.et_raw_adv_data)
    EditText etRawAdvData;
    @BindView(R.id.et_ibeacon_major_min)
    EditText etIbeaconMajorMin;
    @BindView(R.id.et_ibeacon_major_max)
    EditText etIbeaconMajorMax;
    @BindView(R.id.ll_ibeacon_major)
    LinearLayout llIbeaconMajor;
    @BindView(R.id.et_ibeacon_minor_min)
    EditText etIbeaconMinorMin;
    @BindView(R.id.et_ibeacon_minor_max)
    EditText etIbeaconMinorMax;
    @BindView(R.id.ll_ibeacon_minor)
    LinearLayout llIbeaconMinor;
    @BindView(R.id.cb_mac_address)
    CheckBox cbMacAddress;
    @BindView(R.id.cb_adv_name)
    CheckBox cbAdvName;
    @BindView(R.id.cb_ibeacon_uuid)
    CheckBox cbIbeaconUuid;
    @BindView(R.id.cb_ibeacon_major)
    CheckBox cbIbeaconMajor;
    @BindView(R.id.cb_ibeacon_minor)
    CheckBox cbIbeaconMinor;
    @BindView(R.id.cb_raw_adv_data)
    CheckBox cbRawAdvData;
    private boolean mReceiverTag = false;

    private boolean savedParamsError;

    private boolean isSaveFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        sbRssiFilter.setOnSeekBarChangeListener(this);
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        etAdvName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), inputFilter});

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
            orderTasks.add(OrderTaskAssembler.getRssiFilter());
            orderTasks.add(OrderTaskAssembler.getFilterEnable());
            orderTasks.add(OrderTaskAssembler.getFilterMac());
            orderTasks.add(OrderTaskAssembler.getFilterName());
            orderTasks.add(OrderTaskAssembler.getFilterUUID());
            orderTasks.add(OrderTaskAssembler.getFilterMajorRange());
            orderTasks.add(OrderTaskAssembler.getFilterMinorRange());
            orderTasks.add(OrderTaskAssembler.getFilterAdvRawData());
            MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                if (responseType != OrderTask.RESPONSE_TYPE_WRITE)
                    return;
                switch (orderType) {
                    case WRITE_CONFIG:
                        isSaveFailed = true;
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                sbRssiFilter.postDelayed(() -> {
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
                    case WRITE_CONFIG:
                        if (value.length >= 2) {
                            int key = value[1] & 0xFF;
                            ConfigKeyEnum configKeyEnum = ConfigKeyEnum.fromConfigKey(key);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            switch (configKeyEnum) {
                                case GET_FILTER_RSSI:
                                    if (length == 1) {
                                        final int rssi = value[4];
                                        int progress = rssi + 127;
                                        sbRssiFilter.setProgress(progress);
                                        tvRssiFilterValue.setText(String.format("%ddBm", rssi));
                                        tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
                                    }
                                    break;
                                case GET_FILTER_ALL_DATA:
                                    if (length == 1) {
                                        final int enable = value[4] & 0xFF;
                                        allDataFilterEnable = enable == 1;
                                        ivAdvDataFilter.setImageResource(allDataFilterEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                    }
                                    break;
                                case GET_FILTER_MAC:
                                    if (length > 0) {
                                        final int enable = value[4] & 0xFF;
                                        filterMacEnable = enable > 0;
                                        ivMacAddress.setImageResource(filterMacEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        etMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                                        cbMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                                        cbMacAddress.setChecked(enable > 1);
                                        if (length > 1) {
                                            byte[] macBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                            String filterMac = MokoUtils.bytesToHexString(macBytes).toUpperCase();
                                            etMacAddress.setText(filterMac);
                                        }
                                    }
                                    break;
                                case GET_FILTER_NAME:
                                    if (length > 0) {
                                        final int enable = value[4] & 0xFF;
                                        filterNameEnable = enable > 0;
                                        ivAdvName.setImageResource(filterNameEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        etAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                                        cbAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                                        cbAdvName.setChecked(enable > 1);
                                        if (length > 1) {
                                            byte[] nameBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                            String filterName = new String(nameBytes);
                                            etAdvName.setText(filterName);
                                        }
                                    }
                                    break;
                                case GET_FILTER_UUID:
                                    if (length > 0) {
                                        final int enable = value[4] & 0xFF;
                                        filterUUIDEnable = enable > 0;
                                        ivIbeaconUuid.setImageResource(filterUUIDEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        etIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                                        cbIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                                        cbIbeaconUuid.setChecked(enable > 1);
                                        if (length > 1) {
                                            byte[] uuidBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                            String filterUUID = MokoUtils.bytesToHexString(uuidBytes).toUpperCase();
                                            etIbeaconUuid.setText(filterUUID);
                                        }
                                    }
                                    break;
                                case GET_FILTER_MAJOR_RANGE:
                                    if (length > 0) {
                                        final int enable = value[4] & 0xFF;
                                        filterMajorEnable = enable > 0;
                                        ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        llIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                                        cbIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                                        cbIbeaconMajor.setChecked(enable > 1);
                                        if (length > 1) {
                                            byte[] majorMinBytes = Arrays.copyOfRange(value, 5, 7);
                                            int majorMin = MokoUtils.toInt(majorMinBytes);
                                            etIbeaconMajorMin.setText(String.valueOf(majorMin));
                                            byte[] majorMaxBytes = Arrays.copyOfRange(value, 7, 9);
                                            int majorMax = MokoUtils.toInt(majorMaxBytes);
                                            etIbeaconMajorMax.setText(String.valueOf(majorMax));
                                        }
                                    }
                                    break;
                                case GET_FILTER_MINOR_RANGE:
                                    if (length > 0) {
                                        final int enable = value[4] & 0xFF;
                                        filterMinorEnable = enable > 0;
                                        ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        llIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                                        cbIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                                        cbIbeaconMinor.setChecked(enable > 1);
                                        if (length > 1) {
                                            byte[] minorMinBytes = Arrays.copyOfRange(value, 5, 7);
                                            int minorMin = MokoUtils.toInt(minorMinBytes);
                                            etIbeaconMinorMin.setText(String.valueOf(minorMin));
                                            byte[] minorMaxBytes = Arrays.copyOfRange(value, 7, 9);
                                            int minorMax = MokoUtils.toInt(minorMaxBytes);
                                            etIbeaconMinorMax.setText(String.valueOf(minorMax));
                                        }
                                    }
                                    break;
                                case GET_FILTER_ADV_RAW_DATA:
                                    if (length > 0) {
                                        final int enable = value[4] & 0xFF;
                                        filterRawAdvDataEnable = enable > 0;
                                        ivRawAdvData.setImageResource(filterRawAdvDataEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                                        etRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                        cbRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                                        cbRawAdvData.setChecked(enable > 1);
                                        if (length > 1) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 5, 4 + length);
                                            String filterRawData = MokoUtils.bytesToHexString(rawDataBytes).toUpperCase();
                                            etRawAdvData.setText(filterRawData);
                                        }
                                    }
                                    break;
                                case SET_FILTER_RSSI:
                                case SET_FILTER_MAC:
                                case SET_FILTER_NAME:
                                case SET_FILTER_UUID:
                                case SET_FILTER_MAJOR_RANGE:
                                case SET_FILTER_MINOR_RANGE:
                                case SET_FILTER_ADV_RAW_DATA:
                                    if (length != 0) {
                                        savedParamsError = true;
                                    }
                                    break;
                                case SET_FILTER_ALL_DATA:
                                    if (length != 0) {
                                        savedParamsError = true;
                                    }
                                    if (savedParamsError) {
                                        ToastUtils.showToast(FilterOptionsActivity.this, "Save failed!");
                                    } else {
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
                            FilterOptionsActivity.this.setResult(RESULT_OK);
                            finish();
                            break;
                    }
                }
            }
        }
    };

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

    private boolean allDataFilterEnable;
    private boolean filterMacEnable;
    private boolean filterNameEnable;
    private boolean filterUUIDEnable;
    private boolean filterMajorEnable;
    private boolean filterMinorEnable;
    private boolean filterRawAdvDataEnable;

    @OnClick({R.id.tv_back, R.id.iv_save, R.id.iv_adv_data_filter, R.id.iv_mac_address,
            R.id.iv_adv_name, R.id.iv_ibeacon_uuid, R.id.iv_ibeacon_major,
            R.id.iv_ibeacon_minor, R.id.iv_raw_adv_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_save:
                if (isValid()) {
                    showSyncingProgressDialog();
                    saveParams();
                } else {
                    ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                }
                break;
            case R.id.iv_adv_data_filter:
                allDataFilterEnable = !allDataFilterEnable;
                ivAdvDataFilter.setImageResource(allDataFilterEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                break;
            case R.id.iv_mac_address:
                filterMacEnable = !filterMacEnable;
                ivMacAddress.setImageResource(filterMacEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                cbMacAddress.setVisibility(filterMacEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_adv_name:
                filterNameEnable = !filterNameEnable;
                ivAdvName.setImageResource(filterNameEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                cbAdvName.setVisibility(filterNameEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_uuid:
                filterUUIDEnable = !filterUUIDEnable;
                ivIbeaconUuid.setImageResource(filterUUIDEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                cbIbeaconUuid.setVisibility(filterUUIDEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_major:
                filterMajorEnable = !filterMajorEnable;
                ivIbeaconMajor.setImageResource(filterMajorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                llIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                cbIbeaconMajor.setVisibility(filterMajorEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_ibeacon_minor:
                filterMinorEnable = !filterMinorEnable;
                ivIbeaconMinor.setImageResource(filterMinorEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                llIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                cbIbeaconMinor.setVisibility(filterMinorEnable ? View.VISIBLE : View.GONE);
                break;
            case R.id.iv_raw_adv_data:
                filterRawAdvDataEnable = !filterRawAdvDataEnable;
                ivRawAdvData.setImageResource(filterRawAdvDataEnable ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                etRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                cbRawAdvData.setVisibility(filterRawAdvDataEnable ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void saveParams() {
        final int progress = sbRssiFilter.getProgress();
        int filterRssi = progress - 127;
        List<OrderTask> orderTasks = new ArrayList<>();
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        final String majorMin = etIbeaconMajorMin.getText().toString();
        final String majorMax = etIbeaconMajorMax.getText().toString();
        final String minorMin = etIbeaconMinorMin.getText().toString();
        final String minorMax = etIbeaconMinorMax.getText().toString();
        final String rawData = etRawAdvData.getText().toString();

        orderTasks.add(OrderTaskAssembler.setFilterRssi(filterRssi));
        orderTasks.add(OrderTaskAssembler.setFilterMac(filterMacEnable ? mac : "", cbMacAddress.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterName(filterNameEnable ? name : "", cbAdvName.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterUUID(filterUUIDEnable ? uuid : "", cbIbeaconUuid.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterMajorRange(
                filterMajorEnable ? 1 : 0,
                filterMajorEnable ? Integer.parseInt(majorMin) : 0,
                filterMajorEnable ? Integer.parseInt(majorMax) : 0,
                cbIbeaconMajor.isChecked())
        );
        orderTasks.add(OrderTaskAssembler.setFilterMinorRange(
                filterMinorEnable ? 1 : 0,
                filterMinorEnable ? Integer.parseInt(minorMin) : 0,
                filterMinorEnable ? Integer.parseInt(minorMax) : 0,
                cbIbeaconMinor.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterAdvRawData(filterRawAdvDataEnable ? rawData : "", cbRawAdvData.isChecked()));
        orderTasks.add(OrderTaskAssembler.setFilterAllData(allDataFilterEnable ? 1 : 0));
        MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        final String mac = etMacAddress.getText().toString();
        final String name = etAdvName.getText().toString();
        final String uuid = etIbeaconUuid.getText().toString();
        final String majorMin = etIbeaconMajorMin.getText().toString();
        final String majorMax = etIbeaconMajorMax.getText().toString();
        final String minorMin = etIbeaconMinorMin.getText().toString();
        final String minorMax = etIbeaconMinorMax.getText().toString();
        final String rawData = etRawAdvData.getText().toString();
        if (filterMacEnable) {
            if (TextUtils.isEmpty(mac))
                return false;
            int length = mac.length();
            if (length % 2 != 0)
                return false;
        }
        if (filterNameEnable) {
            if (TextUtils.isEmpty(name))
                return false;
        }
        if (filterUUIDEnable) {
            if (TextUtils.isEmpty(uuid))
                return false;
            int length = uuid.length();
            if (length % 2 != 0)
                return false;
        }
        if (filterMajorEnable) {
            if (TextUtils.isEmpty(majorMin))
                return false;
            if (Integer.parseInt(majorMin) > 65535)
                return false;
            if (TextUtils.isEmpty(majorMax))
                return false;
            if (Integer.parseInt(majorMax) > 65535)
                return false;
            if (Integer.parseInt(majorMin) > Integer.parseInt(majorMax))
                return false;

        }
        if (filterMinorEnable) {
            if (TextUtils.isEmpty(minorMin))
                return false;
            if (Integer.parseInt(minorMin) > 65535)
                return false;
            if (TextUtils.isEmpty(minorMax))
                return false;
            if (Integer.parseInt(minorMax) > 65535)
                return false;
            if (Integer.parseInt(minorMin) > Integer.parseInt(minorMax))
                return false;
        }
        if (filterRawAdvDataEnable) {
            if (TextUtils.isEmpty(rawData))
                return false;
            int length = rawData.length();
            if (length % 2 != 0)
                return false;

        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int rssi = progress - 127;
        tvRssiFilterValue.setText(String.format("%ddBm", rssi));
        tvRssiFilterTips.setText(getString(R.string.rssi_filter, rssi));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
