package com.indoornavi;

import android.util.Log;

import java.util.*;

public class Positioning {
    Map<String, LimitedQueue<Integer>> samples = new HashMap<String, LimitedQueue<Integer>>();
    private static final int SAMPLES_LIMIT = 5;
    private static final int ZERO_RSSI_VALUE = -100;

    private int samplesCount;
    private final Listener listener;
    private String currentPosition;

    public Positioning(Listener listener) {
        this.listener = listener;
    }

    public void putWifiScan(WifiScanResult scanResults) {
        samplesCount++;
        Set<String> missedWapSet = new HashSet<String>(samples.keySet());
        for (WifiScanResult.APData sr : scanResults.data) {
            String wapName = formatWAPName(sr);
            LimitedQueue<Integer> levels = samples.get(wapName);
            if (levels == null) {
                levels = new LimitedQueue<Integer>(SAMPLES_LIMIT);
                samples.put(wapName, levels);
            }
            levels.add(sr.signalStrength);
            missedWapSet.remove(wapName);
        }

        for (String missedWap : missedWapSet) {
            samples.get(missedWap).add(ZERO_RSSI_VALUE);
        }

        if(samplesCount > SAMPLES_LIMIT) {
            String measuredPosition = null;
            float minAvg = 0;
            for (Map.Entry<String, LimitedQueue<Integer>> entry : samples.entrySet()) {
                LimitedQueue<Integer> ss = entry.getValue();
                float avg = calculateAverage(ss);
                if(measuredPosition == null || avg < minAvg) {
                    minAvg = avg;
                    measuredPosition = entry.getKey();
                }
            }
            if(!measuredPosition.equals(currentPosition)) {
                currentPosition = measuredPosition;
                listener.onPositionChanged(measuredPosition);
            }
        }
    }

    private String formatWAPName(WifiScanResult.APData sr) {
        return String.format("%s", sr.deviceId);
    }

    private float calculateAverage(List<Integer> ints) {
        int sum = 0;
        for (Integer i : ints) {
            sum += i;
        }
        return sum / ints.size();
    }

    public interface Listener {
        public void onPositionChanged(String positionName);
    }

    private class LimitedQueue<E> extends LinkedList<E> {
        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            super.add(o);
            while (size() > limit) {
                super.remove();
            }
            return true;
        }
    }
}
