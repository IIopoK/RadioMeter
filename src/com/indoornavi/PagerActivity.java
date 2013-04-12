package com.indoornavi;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.indoornavi.service.PositioningService;
import com.indoornavi.service.WifiScanResult;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.Observable;
import java.util.Observer;

public class PagerActivity extends FragmentActivity implements Observer, ViewPager.OnPageChangeListener {

    private TextView counterView;
    private WifiScanResultRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(App.TAG, "PagerActivity.onCreate()");
        super.onCreate(savedInstanceState);
        App.appContext = getApplicationContext();

        setupPager();
        setupAB();
        recorder = new WifiScanResultRecorder(getApplicationContext());
        App.client.addObserver(recorder);
    }

    private void setupAB() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_ab);
        counterView = (TextView)actionBar.getCustomView();
        App.client.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof WifiScanResult) {
            WifiScanResult scanResult = (WifiScanResult) data;
            counterView.setText(String.valueOf(scanResult.num + 1));
        }
    }

    private void setupPager() {
        setContentView(R.layout.page);
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
        indicator.setOnPageChangeListener(this);
    }

    @Override
    protected void onStart() {
        Log.d(App.TAG, "PagerActivity.onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(App.TAG, "PagerActivity.onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(App.TAG, "PagerActivity.onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean serviceRunning = App.isServiceRunning();
        if(serviceRunning) {
            counterView.setVisibility(View.VISIBLE);
        }
        menu.getItem(0).setChecked(serviceRunning);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.service:
                if (item.isChecked()) {
                    App.stopService();
                    item.setChecked(false);
                    counterView.setVisibility(View.INVISIBLE);
                    recorder.dumpSamples();
                    App.client.deleteObserver(recorder);
                } else {
                    App.startService();
                    item.setChecked(true);
                    counterView.setVisibility(View.VISIBLE);
                    recorder = new WifiScanResultRecorder(getApplicationContext());
                    App.client.addObserver(recorder);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
