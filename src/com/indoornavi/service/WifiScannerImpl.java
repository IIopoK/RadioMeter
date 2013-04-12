package com.indoornavi.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.indoornavi.App;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WifiScannerImpl implements WifiScanner {
    private static final int MSG_RESCAN = 0;
    private final long scanPeriod; //milliseconds
    private final Context context;
    private final Listener listener;
    private final WifiManager wifi;

    private int samplesCnt;
    private long scanStartedTime;
    private final Handler scanHandler;

    public WifiScannerImpl(Context context, long scanPeriod, Listener listener) {
        this.context = context;
        this.scanPeriod = scanPeriod;
        this.listener = listener;
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.d(App.TAG, "Starting startScan.. wifi was " + (wifi.isWifiEnabled() ? "enabled" : "disabled"));
        wifi.setWifiEnabled(true);

        scanHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // continue scanning
                startScan();
            }
        };
    }

    @Override
    public void stopScan() {
        try {
            context.unregisterReceiver(wifiReceiver);
            scanHandler.removeMessages(MSG_RESCAN);
        } catch (IllegalArgumentException ignore) {
            //caused if receiver already unregistered
        }
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResults = filter(wifi.getScanResults());
            listener.onScanComplete(WifiScanResult.fromScanResult(scanResults, samplesCnt++));
            long delayRequired = scanPeriod - (System.currentTimeMillis() - scanStartedTime);
            scanHandler.sendEmptyMessageDelayed(MSG_RESCAN, delayRequired);
        }
    };

    @Override
    public void startScan() {
        wifi.startScan();
        scanStartedTime = System.currentTimeMillis();
    }


    private List<ScanResult> filter(List<ScanResult> results) {
        String[] prefix = new String[] {"TP-LINK_710", "TP-LINK_712", "TP-LINK_706" };
        ArrayList<ScanResult> filtered = new ArrayList<ScanResult>();
        for (ScanResult result : results) {
            if (result.frequency >= -90) {
                for (String p : prefix) {
                    if(result.SSID.startsWith(p)) {
                        filtered.add(result);
                    }
                }
            }
        }
        return filtered;
    }
}