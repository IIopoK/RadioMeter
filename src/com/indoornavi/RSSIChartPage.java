package com.indoornavi;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.*;

public class RSSIChartPage extends Fragment implements Observer {
    public static final String TAG = App.TAG + " RSSIChartPage";

    private GraphicalView chartView;
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private Map<String, TimeSeries> series;

    private void initChart() {
        series = new HashMap<String, TimeSeries>();
        dataset = new XYMultipleSeriesDataset();

        renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        renderer.setMargins(new int[]{20, 30, 15, 20});
        //int[] colors = new int[]{Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.RED, Color.CYAN};
        int[] colors = new int[]{Color.GREEN};
        //PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND };
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }

        dataset.addSeries(new TimeSeries("123"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.client.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.rssi_chart, container, false);
        initChart();
        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, renderer, null);
        view.addView(chartView);
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
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof WifiScanResult) {
            WifiScanResult res = (WifiScanResult) o;
            for (WifiScanResult.APData apData : res.data) {
                addSeries(apData, res.timestamp);
            }
        }
    }

    private void addSeries(WifiScanResult.APData apData, long timestamp) {
        String deviceName = apData.deviceId;
        TimeSeries deviceSeries = series.get(deviceName);
        if (deviceSeries == null) {
            deviceSeries = new TimeSeries(deviceName);
            series.put(deviceName, deviceSeries);
            dataset.addSeries(deviceSeries);
        }

        Date date = new Date(timestamp);
        deviceSeries.add(date, apData.signalStrength);
        chartView.repaint();
    }
}