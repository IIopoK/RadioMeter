package com.indoornavi.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.indoornavi.*;

import java.util.ArrayList;

//TODO: hide all conversation stuff to subclass
public class PositioningService extends Service {
    private static final String TAG = App.TAG + " Service";

    // connected clients
    ArrayList<Messenger> clients = new ArrayList<Messenger>();
    // Target we publish for clients to send messages to IncomingHandler.
    final Messenger messenger = new Messenger(new IncomingHandler());

    private WifiScanner wifiScanner;
    private Positioning positioning;
    private Runnable changePosition;
    private Handler handler;
    private WifiScanner.Listener wifiScanReady;

    @Override
    public void onCreate() {
        handler = new Handler();
      /*  changePosition = new Runnable() {
            @Override
            public void run() {
                position = position % 7 + 1;
                sendClients(ConversationConst.MSG_POSITION_CHANGED, position);
                handler.postDelayed(changePosition, 3 * 1000);
            }
        };*/

        wifiScanReady = new WifiScanner.Listener() {
            @Override
            public void onScanComplete(WifiScanResult scanResult) {
                sendClients(ConversationConst.MSG_GOT_WIFI_RSSI, ConversationConst.MSG_PARAM_WIFI_RSSI, scanResult);
                positioning.putWifiScan(scanResult);
            }
        };

        //wifiScanner = new WifiScannerImpl(getApplicationContext(), 2 * 1000, wifiScanReady);
        wifiScanner = new WiFiScannerMock(wifiScanReady);

        positioning = new Positioning(new Positioning.Listener() {
            @Override
            public void onPositionChanged(String positionName) {
                String num = positionName.substring(1);
                sendClients(ConversationConst.MSG_POSITION_CHANGED, Integer.parseInt(num) +1);
            }
        });

        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        wifiScanner.stopScan();
        //handler.removeCallbacks(changePosition);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        wifiScanner.startScan();

        //handler.postDelayed(changePosition, 3 * 1000);

        return START_NOT_STICKY;
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConversationConst.MSG_REGISTER_CLIENT:
                    clients.add(msg.replyTo);
                    break;
                case ConversationConst.MSG_UNREGISTER_CLIENT:
                    clients.remove(msg.replyTo);
                    break;
                    /*Bundle data = msg.getData();
                    for (int i= clients.size()-1; i>=0; i--) {
                        try {
                            clients.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            clients.remove(i);
                        }
                    }
                    break;*/
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendClients(int msg, String paramName, Parcelable paramValue) {
        Message message = Message.obtain(null, msg);
        Bundle data = new Bundle();
        data.putParcelable(paramName, paramValue);
        message.setData(data);
        sendClients(message);
    }

    private void sendClients(int msg, int value) {
        sendClients(Message.obtain(null, msg, value, 0));
    }

    private void sendClients(Message msg) {
        Log.d(TAG, "sending message:" + msg + " for " + clients.size() + " clients");
        for (int i = clients.size() - 1; i >= 0; i--) {
            try {
                clients.get(i).send(msg);
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                clients.remove(i);
            }
        }
    }

}
