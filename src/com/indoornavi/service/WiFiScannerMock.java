package com.indoornavi.service;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class WifiScannerMock implements WifiScanner {
    String[] scans = new String[]{
            "-70 -46 -71",
            "-70 -42 -73",
            "-71 -43 -77",
            "-76 -48 -75",
            "-76 -39 -75",
            "-73 -42 -75",
            "-73 -51 -66",
            "-76 -60 -66",
            "-77 -55 -64",
            "-77 -60 -55",
            "-78 -65 -57",
            "-70 -74 -63",
            "-73 -65 -53",
            "-71 -77 -58",
            "-66 -83 -52",
            "-67 -83 -52",
            "-74 -70 -50",
            "-65 -70 -47",
            "-59 -80 -54",
            "-60 -85 -56",
            "-64 -85 -64",
            "-58 -85 -65",
            "-56 -83 -60",
            "-59 -83 -60",
            "-47 -83 -65",
            "-48 -84 -70",
            "-47 -85 -69",
            "-45 -79 -66",
            "-40 -82 -64",
            "-34 -77 -67",
            "-39 -74 -64" };

    private final Listener listener;
    private Handler handler;
    private Runnable scan;

    public WifiScannerMock(Listener listener) {
        this.listener = listener;
        this.handler = new Handler();
        scan = new Runnable() {
            @Override
            public void run() {
                int step = cnt++ % scans.length;
                String[] apLevels = scans[step].split(" ");
                List<WifiScanResult.APData> data = new ArrayList<WifiScanResult.APData>();
                for (int i = 0; i < apLevels.length; i++) {
                    data.add(new WifiScanResult.APData("n" + i, "d" + i, Integer.parseInt(apLevels[i]), 0));
                }
                WifiScannerMock.this.listener.onScanComplete(new WifiScanResult(cnt++, data));
                handler.postDelayed(this, 300);
            }
        };
    }

    @Override
    public void stopScan() {
        handler.removeCallbacks(scan);
    }

    private int cnt = 0;
    @Override
    public void startScan() {
        handler.postDelayed(scan, 300);
    }
}