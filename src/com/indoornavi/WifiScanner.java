package com.indoornavi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;
import java.util.Observable;

public class WifiScanner extends Observable {
    public static final String TAG = "WifiScanner";
    private final Context context;
    private WifiManager wifi;
    private int samplesCnt;
    private long lastScanTime;

    private final long scanPeriod = 5 * 1000; //milliseconds

    public WifiScanner(Context context) {
        this.context = context;
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.d(App.TAG, "Starting scan.. wifi was " + (wifi.isWifiEnabled() ? "enabled" : "disabled"));
        wifi.setWifiEnabled(true);
        scan();
    }

    public void stop() {
        try {
            context.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException ignore) {
            //caused if receiver already unregistered
        }
        deleteObservers();
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifi.getScanResults();
            Log.d(App.TAG, "Received data from " + results.size() + " access points. Notify " + countObservers() + " observers");
            setChanged();
            notifyObservers(new WifiScanResult(samplesCnt++, results));
            scan();
        }
    };

    private void scan() {
        long sleepTime = scanPeriod - (System.currentTimeMillis() - lastScanTime);
        if (sleepTime > 0) {
            try {
                Log.e(TAG, "Going to sleep for " + sleepTime);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Log.e(TAG, "scan sleep interrupted", e);
            }
        }
        lastScanTime = System.currentTimeMillis();
        wifi.startScan();
    }
}
