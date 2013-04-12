package com.indoornavi;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.indoornavi.service.WifiScanResult;

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

    public WifiScanResultRecorder(Context context) {
        this.context = context;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof  WifiScanResult) {
            WifiScanResult res = (WifiScanResult) o;
            addSample(res);
        }
    }

    private boolean filterScanResult(WifiScanResult.APData sr) {
        return true;
    }

    private void addSample(WifiScanResult scanResults) {
        Set<String> missedWapSet = new HashSet<String>(samples.keySet());
        for (WifiScanResult.APData sr : scanResults.data) {
            if (filterScanResult(sr)) {
                //String wapName = String.format("%s[%s]; freq: %s; capabilities: %s", sr.SSID, sr.BSSID, sr.frequency, sr.capabilities);
                String wapName = String.format("%s %sMHz", sr.networkId, sr.frequency);
                List<String> levels = samples.get(wapName);
                if (levels == null) {
                    levels = new ArrayList<String>();
                    samples.put(wapName, levels);
                }
                levels.add(String.valueOf(sr.signalStrength));
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
            Log.e(TAG, "Can't save data", e);
            throw new RuntimeException(e);
        }
    }
}
