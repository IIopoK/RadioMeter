package com.luxoft;

import android.net.wifi.ScanResult;

import java.util.List;

public class WifiScanResult {
    public final int num;
    public final List<ScanResult> results;

    public WifiScanResult(int num, List<ScanResult> results) {
        this.num = num;
        this.results = results;
    }
}
