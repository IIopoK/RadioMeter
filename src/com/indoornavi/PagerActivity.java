package com.indoornavi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.indoornavi.service.PositioningService;
import com.viewpagerindicator.TitlePageIndicator;

public class PagerActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(App.TAG, "PagerActivity.onCreate()");
        super.onCreate(savedInstanceState);
        App.appContext = getApplicationContext();
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
        menu.getItem(0).setChecked(App.isServiceRunning());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.service:
                if (item.isChecked()) {
                    App.stopService();
                    item.setChecked(false);
                } else {
                    App.startService();
                    item.setChecked(true);
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
