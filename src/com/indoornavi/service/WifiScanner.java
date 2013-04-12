package com.indoornavi.service;

public interface WifiScanner {
    void stopScan();
    void startScan();
    public interface Listener {
        public void onScanComplete(WifiScanResult scanResult);
    }
}
