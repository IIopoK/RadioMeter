package com.indoornavi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import com.viewpagerindicator.TitlePageIndicator;

public class PagerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PagerActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page);

        PagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu, menu);
        return false;
    }
*/
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.random:
                final int page = RANDOM.nextInt(mAdapter.getCount());
                Toast.makeText(this, "Changing to page " + page, Toast.LENGTH_SHORT);
                mPager.setCurrentItem(page);
                return true;

            case R.id.add_page:
                if (mAdapter.getCount() < 10) {
                    mAdapter.setCount(mAdapter.getCount() + 1);
                    mIndicator.notifyDataSetChanged();
                }
                return true;

            case R.id.remove_page:
                if (mAdapter.getCount() > 1) {
                    mAdapter.setCount(mAdapter.getCount() - 1);
                    mIndicator.notifyDataSetChanged();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
