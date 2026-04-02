package com.aiosleeve.aiosleeve.VO;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by oneclickpc001 on 5/1/18.
 */
public class VoBleDevice implements Serializable {

    BluetoothDevice mBluetoothDevice;

    String mStringName = "";
    String mStringAddress = "";

    boolean isConnected = false;

    boolean isAvailable = false;

    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public String getmStringName() {
        return mStringName;
    }

    public void setmStringName(String mStringName) {
        this.mStringName = mStringName;
    }

    public String getmStringAddress() {
        return mStringAddress;
    }

    public void setmStringAddress(String mStringAddress) {
        this.mStringAddress = mStringAddress;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
