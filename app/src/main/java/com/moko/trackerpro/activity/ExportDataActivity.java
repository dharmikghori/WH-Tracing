package com.moko.trackerpro.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.ExportData;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.event.OrderTaskResponseEvent;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;
import com.moko.trackerpro.AppConstants;
import com.moko.trackerpro.R;
import com.moko.trackerpro.adapter.ExportDataListAdapter;
import com.moko.trackerpro.dialog.AlertMessageDialog;
import com.moko.trackerpro.dialog.LoadingMessageDialog;
import com.moko.trackerpro.entity.BeaconInfo;
import com.moko.trackerpro.utils.ToastUtils;
import com.moko.trackerpro.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExportDataActivity extends BaseActivity {

    @BindView(R.id.iv_sync)
    ImageView ivSync;
    @BindView(R.id.tv_export)
    TextView tvExport;
    @BindView(R.id.tv_sync)
    TextView tvSync;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.rv_export_data)
    RecyclerView rvExportData;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.tv_count)
    TextView tvCount;

    private boolean mReceiverTag = false;
    private StringBuilder storeString;
    private ArrayList<ExportData> exportDatas;
    private boolean isSync;
    private ExportDataListAdapter adapter;
    public String mDeviceMac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);
        ButterKnife.bind(this);
//        exportDatas = new ArrayList<>();
//        for (int i = 0; i < 126; i++) {
//            ExportData exportData = new ExportData();
//            exportData.time = "" + i;
//            exportDatas.add(exportData);
//            if (i % 25 == 0) {
//                apiCall(getAPIRequestJson(exportDatas));
//                exportDatas.clear();
//            }
//        }

        int savedCount = getIntent().getIntExtra(AppConstants.EXTRA_KEY_SAVED_COUNT, 0);
        int leftCount = getIntent().getIntExtra(AppConstants.EXTRA_KEY_LEFT_COUNT, 0);
        int sum = savedCount + leftCount;
        tvSum.setText(String.format("Sum:%d/%d", savedCount, sum));
        exportDatas = MokoSupport.getInstance().exportDatas;
        storeString = MokoSupport.getInstance().storeString;
        if (exportDatas != null && exportDatas.size() > 0 && storeString != null) {
            tvCount.setText(String.format("Count:%d", exportDatas.size()));
            tvExport.setEnabled(true);
        } else {
            exportDatas = new ArrayList<>();
            storeString = new StringBuilder();
        }
        adapter = new ExportDataListAdapter();
        adapter.openLoadAnimation();
        adapter.replaceData(exportDatas);
        rvExportData.setLayoutManager(new LinearLayoutManager(this));
        rvExportData.setAdapter(adapter);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
        } else {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getMacAddress());
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
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderType) {
                    case STORE_DATA_NOTIFY:
                        int length = value.length;

                        if (length >= 13) {
                            int index = 0;
                            while (index < length) {
                                int len = value[index];
                                index += 1;
                                byte[] timeBytes = Arrays.copyOfRange(value, index, index + 6);
                                byte[] macBytes = Arrays.copyOfRange(value, index + 6, index + 12);

                                int year = timeBytes[0] & 0xff;
                                int month = timeBytes[1] & 0xff;
                                int day = timeBytes[2] & 0xff;
                                int hour = timeBytes[3] & 0xff;
                                int minute = timeBytes[4] & 0xff;
                                int second = timeBytes[5] & 0xff;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, 2000 + year);
                                calendar.set(Calendar.MONTH, month - 1);
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, second);
                                final String time = Utils.calendar2strDate(calendar, AppConstants.PATTERN_YYYY_MM_DD_HH_MM_SS);

                                StringBuffer stringBuffer = new StringBuffer();
                                for (int i = macBytes.length - 1, l = 0; i >= l; i--) {
                                    stringBuffer.append(MokoUtils.byte2HexString(macBytes[i]));
                                    if (i > l)
                                        stringBuffer.append(":");
                                }
                                final String mac = stringBuffer.toString();

                                final int rssi = value[index + 12];
                                final String rssiStr = String.format("%ddBm", rssi);

                                String rawData = "";
                                if (len > 13) {
                                    byte[] rawDataBytes = Arrays.copyOfRange(value, index + 13, index + len);
                                    rawData = MokoUtils.bytesToHexString(rawDataBytes);
                                }

                                ExportData exportData = new ExportData();

                                exportData.time = time;
                                exportData.rssi = rssi;
                                exportData.mac = mac;
                                exportData.rawData = rawData;
                                exportData.watchMac = mDeviceMac;
                                exportDatas.add(exportData);
                                tvCount.setText(String.format("Count:%d", exportDatas.size()));

                                storeString.append(String.format("Time:%s", time));
                                storeString.append("\n");
                                storeString.append(String.format("Mac Address:%s", mac));
                                storeString.append("\n");
                                storeString.append(String.format("RSSI:%s", rssiStr));
                                storeString.append("\n");
                                if (!TextUtils.isEmpty(rawData)) {
                                    storeString.append(String.format("Raw Data:%s", rawData));
                                    storeString.append("\n");
                                }
                                storeString.append("\n");
                                index += len;
                            }
                            adapter.replaceData(exportDatas);
                        }
                        apiCall(getAPIRequestJson());
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderType) {
                    case WRITE_CONFIG:
                        if (value.length >= 2) {
                            int key = value[1] & 0xff;
                            ConfigKeyEnum configKeyEnum = ConfigKeyEnum.fromConfigKey(key);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            switch (configKeyEnum) {
                                case CLEAR_SAVED_DATA:
                                    if (length == 0) {
                                        storeString = new StringBuilder();
                                        LogModule.writeTrackedFile("");
                                        exportDatas.clear();
                                        adapter.replaceData(exportDatas);
                                        tvExport.setEnabled(false);
                                        ToastUtils.showToast(ExportDataActivity.this, "Empty success!");
                                        tvEmpty.postDelayed(() -> {
                                            showSyncingProgressDialog();
                                            MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getSavedCount());
                                        }, 700);
                                    } else {
                                        ToastUtils.showToast(ExportDataActivity.this, "Failed");
                                    }
                                    break;
                                case GET_SAVED_COUNT:
                                    dismissSyncProgressDialog();
                                    if (length == 4) {
                                        byte[] savedCount = Arrays.copyOfRange(value, 4, 6);
                                        byte[] leftCount = Arrays.copyOfRange(value, 6, 8);
                                        int saved = MokoUtils.toInt(savedCount);
                                        int left = MokoUtils.toInt(leftCount);
                                        int sum = saved + left;
                                        tvSum.setText(String.format("Sum:%d/%d", saved, sum));
                                        tvCount.setText("Count:N/A");
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

    @OnClick({R.id.tv_back, R.id.tv_empty, R.id.ll_sync, R.id.tv_export})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                back();
                break;
            case R.id.tv_empty:
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Warning!");
                dialog.setMessage("Are you sure to empty the saved tracked datas?");
                dialog.setOnAlertConfirmListener(() -> {
                    showSyncingProgressDialog();
                    MokoSupport.getInstance().sendOrder(OrderTaskAssembler.deleteTrackedData());
                });
                dialog.show(getSupportFragmentManager());
                break;
            case R.id.ll_sync:
                if (!isSync) {
                    isSync = true;
                    tvEmpty.setEnabled(false);
                    tvExport.setEnabled(false);
                    MokoSupport.getInstance().enableStoreDataNotify();
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
                    ivSync.startAnimation(animation);
                    tvSync.setText("Stop");
                } else {
                    MokoSupport.getInstance().disableStoreDataNotify();
                    isSync = false;
                    tvEmpty.setEnabled(true);
                    if (exportDatas != null && exportDatas.size() > 0 && storeString != null) {
                        tvExport.setEnabled(true);
                    }
                    ivSync.clearAnimation();
                    tvSync.setText("Sync");
                }
                break;
            case R.id.tv_export:
                showSyncingProgressDialog();
                LogModule.writeTrackedFile("");
                tvExport.postDelayed(() -> {
                    dismissSyncProgressDialog();
                    final String log = storeString.toString();
                    if (!TextUtils.isEmpty(log)) {
                        LogModule.writeTrackedFile(log);
                        File file = LogModule.getTrackedFile();
                        // 发送邮件
                        String address = "Development@mokotechnology.com";
                        String title = "Tracked Log";
                        String content = title;
                        Utils.sendEmail(ExportDataActivity.this, address, content, title, "Choose Email Client", file);
                    }
                }, 500);
                break;
        }
    }

    private void back() {
        // 关闭通知
        MokoSupport.getInstance().disableStoreDataNotify();
        MokoSupport.getInstance().exportDatas = exportDatas;
        MokoSupport.getInstance().storeString = storeString;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private String uploadDeviceLog(
            String logData
    ) {
        String strResponse = "";

        try {
            URL url =
                    new URL("http://contact-track-demo-api.whalpha.site/v1/log");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            byte[] postDataBytes = logData.getBytes("UTF-8");
            con.getOutputStream().write(postDataBytes);

            if (con.getResponseCode() == 200) {
                StringBuilder readTextBuf = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String strLine = br.readLine();
                readTextBuf.append(strLine);
                con.disconnect();
                strResponse = strLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strResponse;
    }


    private String getAPIRequestJson() {
        List<ExportData> data = adapter.getData();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < data.size(); i++) {
                ExportData beaconInfo = data.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("device", beaconInfo.watchMac);
                jsonObject.put("mac_address", beaconInfo.mac);
                jsonObject.put("rssi", beaconInfo.rssi);
                jsonObject.put("datetime", beaconInfo.time);
                jsonArray.put(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    private void apiCall(String jsonRequest) {
        class UploadLog extends AsyncTask<String, Void, String> {

            protected String doInBackground(String... urls) {
                return uploadDeviceLog(jsonRequest);
            }

            protected void onPostExecute(String strResponse) {
                if (!strResponse.isEmpty()) {
                    Log.d("DataSend", jsonRequest);
                    Log.d("DataSend-", strResponse);
                }
            }
        }

        new UploadLog().execute();
    }
}
