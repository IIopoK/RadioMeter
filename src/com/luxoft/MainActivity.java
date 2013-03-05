
package com.luxoft;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements Observer {
    public static final String TAG = "WiFiSampler";
    private volatile boolean isStarted = false;
    private Button btn;
    private TextView currentMessage;

    private WifiScanner wifiScanner;
    private WifiScanResultRecorder recorder;

    private LinearLayout resultsLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn = (Button) findViewById(R.id.btn);
        resultsLog = (LinearLayout) findViewById(R.id.results);
    }

    @Override
    public void onStop() {
        super.onStop();
        wifiScanner.stop();
    }

    public void btnPressed(View view) {
        if (isStarted) {
            stopSampling();
        } else {
            startSampling();
        }
    }

    private void stopSampling() {
        Log.d(TAG, "stop sampling..");
        isStarted = false;
        wifiScanner.stop();
        String externalStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
           try{
            recorder.dumpSamples();
           } catch (Exception e) {
               Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
           }
        } else {
            Toast.makeText(this, "External SD card not mounted. Storage state: " + externalStorageState, Toast.LENGTH_LONG).show();
        }

        btn.setText("Start");
    }

    private void startSampling() {
        isStarted = true;
        recorder = new WifiScanResultRecorder(this);
        wifiScanner = new WifiScanner(this);
        wifiScanner.addObserver(this);
        wifiScanner.addObserver(recorder);

        currentMessage = new TextView(MainActivity.this);
        currentMessage.setText("preparing..");
        resultsLog.addView(currentMessage);

        btn.setText("Stop");
    }

    @Override
    public void update(Observable observable, Object o) {
        WifiScanResult res = (WifiScanResult) o;
        currentMessage.setText("collected " + res.num + " scan results");
    }
}
