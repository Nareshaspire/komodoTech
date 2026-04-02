package com.aiosleeve.aiosleeve.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiosleeve.aiosleeve.ActivityProfile;
import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.VO.VoBleDevice;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.AbstractList;
import java.util.ArrayList;


public class FragmentManageDevice extends AppCompatActivity {
    public static final String TAG="FragmentManageDevice";

    //    View createView;
//    MainActivity mMainActivity;

    //Global Objects
    public Utility mUtility;

    RecyclerView mRecyclerViewDeviceScan;
    MyDeviceScanAdapter myDeviceScanAdapter;

    TextView mTextViewScanning;

    MyScannerTimer mMyScannerTimer;

    //Toolbar Layouts
    public TextView mTextViewHeader;
    //    public ImageView mImageViewDrawer;
    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;
    public ImageView mImageViewProfileIcon;
    public GradientDrawable gradientDrawable;
    public RelativeLayout mRelativeLayoutMain;
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_manage_device);

        mUtility = new Utility(FragmentManageDevice.this);

        initToolbar();

//        mMainActivity = (MainActivity) getActivity();
        MainActivity.mArrayListBledevices = new ArrayList<>();


        mMyScannerTimer = new MyScannerTimer(6000, 1000);

        mRecyclerViewDeviceScan = (RecyclerView) findViewById(R.id.fragment_manage_device_recyclerview);

        mTextViewScanning = (TextView) findViewById(R.id.fragment_manage_device_txt_scanning);

//        mMainActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMainActivity.onBackPressed();
//            }
//        });

        if (MainActivity.mArrayListBledevices != null) {
            if (MainActivity.mArrayListBledevices != null && MainActivity.mArrayListBledevices.size() > 0) {
                myDeviceScanAdapter = new MyDeviceScanAdapter();
                LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerViewDeviceScan.setLayoutManager(manager);
                mRecyclerViewDeviceScan.setAdapter(myDeviceScanAdapter);
                mTextViewScanning.setVisibility(View.GONE);
            } else {
                mTextViewScanning.setVisibility(View.VISIBLE);
            }
        } else {
            mTextViewScanning.setVisibility(View.VISIBLE);
        }

        MainActivity.startScan();
        mMyScannerTimer.start();

//        mMainActivity.mImageViewDrawer.setVisibility(View.GONE);
//        mMainActivity.mImageViewBack.setVisibility(View.VISIBLE);
//        mMainActivity.mTextViewHeader.setText("SCAN DEVICES");
//        mMainActivity.mRelativeLayoutMain.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.custom_header_color));

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.setDeviceStatusListner(new DevicesStatus() {

            @Override
            public void addScanDevices(final BluetoothDevice bluetoothDevice) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData(bluetoothDevice);

                    }
                });
            }

            @Override
            public void onConnect(final String devicesName, final String devicesAddress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < MainActivity.mArrayListBledevices.size(); i++) {
                            if (MainActivity.mArrayListBledevices.get(i).getmStringAddress().equalsIgnoreCase(devicesAddress)) {
                                MainActivity.mArrayListBledevices.get(i).setConnected(true);
                                break;
                            }
                        }

                        if (myDeviceScanAdapter != null)
                            myDeviceScanAdapter.notifyDataSetChanged();


//                        if (MainActivity.viewPager != null) {
//                            mMainActivity.onBackPressed();
                        finish();
//                        }
                    }
                });

            }

            @Override
            public void onDisconnect(final String devicesName, final String devicesAddress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < MainActivity.mArrayListBledevices.size(); i++) {
                            if (MainActivity.mArrayListBledevices.get(i).getmStringAddress().equalsIgnoreCase(devicesAddress)) {
                                MainActivity.mArrayListBledevices.get(i).setConnected(false);
                                break;
                            }
                        }

                        if (myDeviceScanAdapter != null)
                            myDeviceScanAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError() {

            }

            @Override
            public void readCharacterStic() {

            }

            @Override
            public void readRssiValue(int updateRssi, String devicesName, String devicesAddress) {

            }

            @Override
            public void dataAvailable(Object obj) {

            }

            @Override
            public void dataStatus(int status) {

            }
        },TAG);

        mMyScannerTimer = new MyScannerTimer(6000, 1000);

    }


    public class MyDeviceScanAdapter extends RecyclerView.Adapter<MyDeviceScanAdapter.ViewHolder> {

        @Override
        public MyDeviceScanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_device_list_row, parent, false);
            return new MyDeviceScanAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyDeviceScanAdapter.ViewHolder holder, final int position) {


            if (MainActivity.mArrayListBledevices.get(position) != null) {

                holder.mTextViewDeviceName.setText(MainActivity.mArrayListBledevices.get(position).getmStringName());
                holder.mTextViewDeviceAddress.setText(MainActivity.mArrayListBledevices.get(position).getmStringAddress());

                holder.mButtonConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.mArrayListBledevices.get(position).isConnected()) {
                            MainActivity.disConnectDevice();
                        } else {
                            Snackbar.make(v, "Connecting...", BaseTransientBottomBar.LENGTH_LONG).show();
                            MainActivity.connectDevice(MainActivity.mArrayListBledevices.get(position).getmBluetoothDevice());
                        }

                    }
                });

                if (MainActivity.mArrayListBledevices.get(position).isConnected()) {
                    holder.mButtonConnect.setText(getResources().getString(R.string.btn_disconnect));
                } else {
                    holder.mButtonConnect.setText(getResources().getString(R.string.btn_connect));
                }
            }
        }

        @Override
        public int getItemCount() {
            return MainActivity.mArrayListBledevices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button mButtonConnect;

            TextView mTextViewDeviceName;
            TextView mTextViewDeviceAddress;

            public ViewHolder(View itemView) {
                super(itemView);
                mButtonConnect = (Button) itemView.findViewById(R.id.custom_device_list_row_button_connect);

                mTextViewDeviceName = (TextView) itemView.findViewById(R.id.custom_device_list_row_textview_device_name);
                mTextViewDeviceAddress = (TextView) itemView.findViewById(R.id.custom_device_list_row_textview_device_address);
            }
        }
    }

    public void setData(BluetoothDevice bluetoothDevice) {

//        if (isAdded()) {
        if (bluetoothDevice.getName().equalsIgnoreCase("BRIGHTON_HR")) {

            VoBleDevice mVoBleDevice = new VoBleDevice();
            mVoBleDevice.setAvailable(true);
            mVoBleDevice.setConnected(false);
            mVoBleDevice.setmStringName(bluetoothDevice.getName());
            mVoBleDevice.setmStringAddress(bluetoothDevice.getAddress());
            mVoBleDevice.setmBluetoothDevice(bluetoothDevice);

            if (MainActivity.mArrayListBledevices.size() > 0) {

                boolean isAdded = false;
                int position = 0;

                for (int i = 0; i < MainActivity.mArrayListBledevices.size(); i++) {
                    if (MainActivity.mArrayListBledevices.get(i).getmStringAddress().equalsIgnoreCase(bluetoothDevice.getAddress())) {
                        isAdded = true;
                        position = i;
                        break;
                    }
                }

                if (isAdded) {
                    MainActivity.mArrayListBledevices.set(position, mVoBleDevice);
                } else {
                    MainActivity.mArrayListBledevices.add(mVoBleDevice);
                }

            } else {
                MainActivity.mArrayListBledevices.add(mVoBleDevice);
            }


            if (myDeviceScanAdapter != null) {
                myDeviceScanAdapter.notifyDataSetChanged();
            } else {
                myDeviceScanAdapter = new MyDeviceScanAdapter();
                LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerViewDeviceScan.setLayoutManager(manager);
                mRecyclerViewDeviceScan.setAdapter(myDeviceScanAdapter);
            }

            mTextViewScanning.setVisibility(View.GONE);
        }
//        }
    }

    public class MyScannerTimer extends CountDownTimer {

        public MyScannerTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mMyScannerTimer.start();
            MainActivity.startScan();
        }
    }

    @Override
    public void onDestroy() {
        if (mMyScannerTimer != null)
            mMyScannerTimer.cancel();
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.fragment_manage_device_main_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        View custom_actionbar = getLayoutInflater().inflate(R.layout.custom_actionbar_admin, null);

        mRelativeLayoutMain = (RelativeLayout) custom_actionbar.findViewById(R.id.custom_actionbar_main_layout);
        mTextViewHeader = (TextView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_textview_header);
//        mImageViewDrawer = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_drawer);
        mImageViewBack = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_back);
//        mImageViewAddStory = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_add_story);
//        mImageViewProfileIcon=(ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_right_drawer_icon);


        mTextViewHeader.setText(getResources().getString(R.string.text_scan_connect_all_caps));
        mTextViewHeader.setTextColor(ContextCompat.getColor(FragmentManageDevice.this, R.color.toolbar_bg_color));
        mTextViewHeader.setTypeface(mTextViewHeader.getTypeface(), Typeface.BOLD);
        mRelativeLayoutMain.setBackgroundColor(ContextCompat.getColor(FragmentManageDevice.this, R.color.color_gray_4));

        mUtility.changeStatusbarColor(R.color.color_gray_4);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mToolbar.addView(custom_actionbar);

    }


}
