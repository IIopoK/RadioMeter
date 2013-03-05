package com.luxoft;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class WifiScanResultRecorder implements Observer {
    public static final String TAG = "WifiScanResultRecorder";
    private Map<String, List<String>> samples = new TreeMap<String, List<String>>();
    //private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private Date startDate = new Date();
    private final Context context;
    private Set<String> myWAPs;

    public WifiScanResultRecorder(Context context) {
        this.context = context;
        myWAPs = new HashSet<String>();
        myWAPs.add("TP-LINK_706F86");
        myWAPs.add("TP-LINK_706BD6");
        myWAPs.add("TP-LINK_706BD9");
        myWAPs.add("TP-LINK_704FFE");
    }

    @Override
    public void update(Observable observable, Object o) {
        WifiScanResult res = (WifiScanResult) o;
        addSample(res);
    }

    private boolean filterScanResult(ScanResult sr) {
        return !TextUtils.isEmpty(sr.SSID) && myWAPs.contains(sr.SSID);
    }

    private void addSample(WifiScanResult scanResults) {
        Set<String> missedWapSet = new HashSet<String>(samples.keySet());
        for (ScanResult sr : scanResults.results) {
            if (filterScanResult(sr)) {
                //String wapName = String.format("%s[%s]; freq: %s; capabilities: %s", sr.SSID, sr.BSSID, sr.frequency, sr.capabilities);
                String wapName = String.format("%s %sMHz", sr.SSID, sr.frequency);
                List<String> levels = samples.get(wapName);
                if (levels == null) {
                    levels = new ArrayList<String>();
                    samples.put(wapName, levels);
                }
                levels.add(String.valueOf(sr.level));
                missedWapSet.remove(wapName);
            }
        }

        for (String missedWap : missedWapSet) {
            samples.get(missedWap).add("");
        }
        Log.d(TAG, "Sample added");
    }

    public void dumpSamples() {
        if (samples.isEmpty()) {
            Log.d(TAG, "No samples");
            return;
        }

        try {
            String fileName = "WFS" + sdf.format(startDate) + ".csv";
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(path, fileName);

            Log.d(TAG, "Dumping samples to " + file.getCanonicalPath());
            Log.d(TAG, "External storage state: " + Environment.getExternalStorageState());

            if (path.mkdirs()) {
                Log.d(TAG, "Created dirs for " + path.getCanonicalPath());
            }
            if (!file.createNewFile()) {
                Log.d(TAG, "Error creating new file for " + file.getCanonicalPath());
                return;
            }

            Writer out = new FileWriter(file);
            for (Map.Entry<String, List<String>> RSSISamples : samples.entrySet()) {
                out.write(RSSISamples.getKey());
                out.write(",");
                for (String level : RSSISamples.getValue()) {
                    out.write(String.valueOf(level));
                    out.write(",");
                }
                out.write("\n");
            }
            out.flush();
            out.close();

            //https://code.google.com/p/android/issues/detail?id=38282
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);

            samples = new TreeMap<String, List<String>>();
        } catch (Exception e) {
            Log.e(TAG, "Can't save results", e);
            throw new RuntimeException(e);
        }
    }
}
