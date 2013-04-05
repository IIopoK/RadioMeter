package com.indoornavi;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class SignalRecorderPage extends Fragment implements Observer, View.OnClickListener {
    public static final String TAG = App.TAG + " SignalRecorderPage";
    private volatile boolean isStarted = false;
    private Button btn;
    private TextView currentMessage;
    private LinearLayout resultsLog;

    //private WifiScannerImpl wifiScanner;
    private WifiScanResultRecorder recorder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signal_recorder, container, false);
        btn = (Button) view.findViewById(R.id.recorderStartStopBtn);
        btn.setOnClickListener(this);
        resultsLog = (LinearLayout) view.findViewById(R.id.results);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_CONTENT, mContent);
    }

    @Override
    public void onStop() {
        Log.d(App.TAG, "onStop()");
        super.onStop();
        //wifiScanner.stopScan();
    }

    private void stopSampling() {
        Log.d(App.TAG, "stop sampling..");
        isStarted = false;
        //wifiScanner.stopScan();
        String externalStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            try {
                recorder.dumpSamples();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "External SD card not mounted. Storage state: " + externalStorageState, Toast.LENGTH_LONG).show();
        }

        btn.setText("Start");
    }

    private void startSampling() {
        isStarted = true;
        recorder = new WifiScanResultRecorder(getActivity());
//        wifiScanner = new WifiScannerImpl(getActivity());
//        wifiScanner.addObserver(this);
//        wifiScanner.addObserver(recorder);

        currentMessage = new TextView(getActivity());
        currentMessage.setText("preparing..");
        resultsLog.addView(currentMessage);

        btn.setText("Stop");
    }

    @Override
    public void update(Observable observable, Object o) {
        WifiScanResult res = (WifiScanResult) o;
        currentMessage.setText("collected " + res.num + " scan data");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recorderStartStopBtn: {
                if (isStarted) {
                    stopSampling();
                } else {
                    startSampling();
                }
                break;
            }
        }
    }
}