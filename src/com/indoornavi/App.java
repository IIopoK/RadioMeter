package com.indoornavi;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.indoornavi.service.Client;
import com.indoornavi.service.PositioningService;

public class App {
    public static final String TAG = "NAVI";
    public static final Client client = new Client();
    public static Context appContext;

    public static void startService() {
        Intent intent = new Intent(appContext, PositioningService.class);
        appContext.startService(intent);
        client.bindService(appContext);
    }

    public static void stopService() {
        client.unbindService(appContext);
        Intent intent = new Intent(appContext, PositioningService.class);
        appContext.stopService(intent);
    }

    public static boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PositioningService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static final View.OnTouchListener pagerDisallowInterceptListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //http://stackoverflow.com/questions/8122460/viewpager-intercepts-all-x-axis-ontouch-events-how-to-disable
            //process all gestures solely
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        }
    };
}
