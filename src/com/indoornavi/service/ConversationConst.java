package com.indoornavi.service;

public class ConversationConst {
    // Messages Client -> Service
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;

    // Messages Service -> Client
    public static final int MSG_GOT_WIFI_RSSI = 3;
    public static final int MSG_POSITION_CHANGED = 4;

    public static final String MSG_PARAM_WIFI_RSSI =  "wifirssi";
}
