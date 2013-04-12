package com.indoornavi;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.indoornavi.service.WifiScanResult;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.*;

public class RSSIChartPage extends Fragment implements Observer {
    public static final String TAG = App.TAG + " RSSIChartPage";

    private static final int[] COLORS = new int[]{Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.RED, Color.CYAN};
    //private static final PointStyle[] STYLES = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND };

    private GraphicalView chartView;
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private Map<String, TimeSeries> series;

    private void initChart() {
        series = new HashMap<String, TimeSeries>();
        dataset = new XYMultipleSeriesDataset();

        renderer = new XYMultipleSeriesRenderer();
        //renderer.setChartTitle("RSSI chart");
        //renderer.setChartTitleTextSize(20);
        renderer.setXTitle("timestamp");
        renderer.setYTitle("RSSI");
        renderer.setAxisTitleTextSize(16);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        //renderer.setMargins(new int[]{20, 30, 15, 20});

//        renderer.setXAxisMin();
//        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(-90.0d);
        renderer.setYAxisMax(-30.0d);
//        renderer.setAxesColor(axesColor);
//        renderer.setLabelsColor(labelsColor);
        renderer.setClickEnabled(false);
        renderer.setPanEnabled(false);
        renderer.setExternalZoomEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setXLabelsAngle(45.0f);
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setShowGrid(true);
        //renderer.setYLabels();
        renderer.setYLabels(6);
        renderer.setXLabels(6);

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
        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, renderer, "HH:mm:ss");
        chartView.setOnTouchListener(App.pagerDisallowInterceptListener);
        view.addView(chartView);
        return view;
    }

    //TODO: store state for sake of changing configuration
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        //outState.putSerializable("chartData").putString(KEY_CONTENT, mContent);
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
            int devicesCount = series.size();
            deviceSeries = new TimeSeries(deviceName);
            series.put(deviceName, deviceSeries);
            dataset.addSeries(deviceSeries);

            XYSeriesRenderer ssr = new XYSeriesRenderer();
            int colorIdx = devicesCount % COLORS.length;
            ssr.setColor(COLORS[colorIdx]);
            renderer.addSeriesRenderer(ssr);
        }

        Date date = new Date(timestamp);
        deviceSeries.add(date, apData.signalStrength);
        chartView.repaint();
    }
}