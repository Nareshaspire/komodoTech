package com.aiosleeve.aiosleeve.interfaces;

import android.bluetooth.BluetoothDevice;

public interface DevicesStatus {

	public static final Object RECEIVE_SENSOR_DATA_LOCK = new Object();

	public void addScanDevices(BluetoothDevice bluetoothDevice);
	public void onConnect(String devicesName, String devicesAddress);
	public void onDisconnect(String devicesName, String devicesAddress);
	public void onError();
	public void readCharacterStic();
	public void readRssiValue(int updateRssi, String devicesName, String devicesAddress);
	public void dataAvailable(Object obj);
	public void dataStatus(int status);
	
}
