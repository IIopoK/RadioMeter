package com.indoornavi.service;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class WifiScanResult implements Parcelable {
    public final int num;
    public final List<APData> data;
    public final long timestamp;

    // Subset of android.net.wifi.ScanResult fields. Also note that CREATOR of ScanResult is invisible.
    public static class APData implements Parcelable {
        public final String networkId;
        public final String deviceId;
        public final int signalStrength;
        public final int frequency;

        public APData(ScanResult sr) {
            networkId = sr.SSID;
            deviceId = sr.BSSID;
            signalStrength = sr.level;
            frequency = sr.frequency;
        }

        public APData(String networkId, String deviceId, int signalStrength, int frequency) {
            this.networkId = networkId;
            this.deviceId = deviceId;
            this.signalStrength = signalStrength;
            this.frequency = frequency;
        }

        public APData(Parcel sr) {
            networkId = sr.readString();
            deviceId = sr.readString();
            signalStrength = sr.readInt();
            frequency = sr.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(networkId);
            dest.writeString(deviceId);
            dest.writeInt(signalStrength);
            dest.writeInt(frequency);
        }

        public static final Parcelable.Creator<APData> CREATOR = new Parcelable.Creator<APData>() {
            public APData createFromParcel(Parcel in) {
                return new APData(in);
            }

            public APData[] newArray(int size) {
                return new APData[size];
            }
        };
    }

    public static WifiScanResult fromScanResult(List<ScanResult> results, int packageNumber) {
        List<APData> data = new ArrayList<APData>(results.size());
        for (ScanResult scanResult : results) {
            data.add(new APData(scanResult));
        }
        return new WifiScanResult(packageNumber, data);
    }

    public WifiScanResult(int num, List<APData> data) {
        this.timestamp = System.currentTimeMillis();
        this.num = num;
        this.data = data;
    }

    private WifiScanResult(Parcel in) {
        timestamp = in.readLong();
        num = in.readInt();
        data = new ArrayList<APData>();
        in.readTypedList(data, APData.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeInt(num);
        dest.writeTypedList(data);
    }

    public static final Parcelable.Creator<WifiScanResult> CREATOR
            = new Parcelable.Creator<WifiScanResult>() {
        public WifiScanResult createFromParcel(Parcel in) {
            return new WifiScanResult(in);
        }

        public WifiScanResult[] newArray(int size) {
            return new WifiScanResult[size];
        }
    };
}
