package com.aiosleeve.aiosleeve.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.toshiba.semicon.hcsdp.brighton.controllib.AccelerationWaveData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.ActivityMeterData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.BleConstants;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.HeartRateData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.PulseOximeterData;

public class FragmentBPM extends AppCompatActivity {
    public static final String TAG="FragmentBPM";

    public TextView mTextViewTotalStep;
    public TextView mTextViewDistanceMeter;
    public TextView mTextViewTotalMin;
    public TextView mTextViewTotalMet;
    public TextView mTextViewTotalSpo2;
    public TextView mTextViewTotalBPM;
    public TextView mTextViewHome;
    public TextView mTextViewHrv;
    public TextView mTextViewSLEEP;
    public TextView mTextViewSETTING;

    public ImageView mImageViewProgress;
    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;
    public ImageView mImageViewHome;
    public ImageView mImageViewHrv;
    public ImageView mImageViewSLEEp;
    public ImageView mImageViewSETTING;

    public Button mButtonStart;
    public Button mButtonStop;

    public LinearLayout mLinearLayoutTabsHOME;
    public LinearLayout mLinearLayoutTabsHRV;
    public LinearLayout mLinearLayoutTabsSLEEp;
    public LinearLayout mLinearLayoutTabsSETTINGS;

    public LineChart mLineChart;

     /* variables for sensor data */
    /**
     * Received activity meter data (includes total steps, total distance, and total energy consumption).
     */
    private ActivityMeterData activityMeterData = null;
    /**
     * Difference Time list of received acceleration.
     */
    private LinkedList<Integer> diffTimeQueueA = new LinkedList<Integer>();
    /**
     * X axis data list of received acceleration.
     */
    private LinkedList<Integer> xDataQueue = new LinkedList<Integer>();
    /**
     * Y axis data list of received acceleration.
     */
    private LinkedList<Integer> yDataQueue = new LinkedList<Integer>();
    /**
     * Z axis data list of received acceleration.
     */
    private LinkedList<Integer> zDataQueue = new LinkedList<Integer>();
    private LinkedList<Integer> bpmDataQueue = new LinkedList<Integer>();


    /**
     * Received heart rate data.
     */
    private int heartRate = -1;

    /**
     * Base timer interval value
     */
    private static final double interval_a = 0.1;
    /**
     * Update acceleration graph timer interval value. interval is 200msec.
     */
    private static final int ACCEL_TIMER_INTERVAL = (int) (interval_a * 1000);
    /**
     * Update activity timer interval value. interval is 1000msec.
     */
    private static final int UPDATE_ACTIVITY_TIMER_INTERVAL = 1000;

    /**
     * Object for activity value update exclusive
     */
    private final Object ACTIVITY_UPDATE_LOCK = new Object();

    /**
     * ui handler
     */
    private Handler handler;

    /* variables for activity */
    /**
     * Update activity timer.
     */
    private Timer updateActivityTimer = null;

    PulseOximeterData pulseOximeterData;

    /**
     * String of total steps.
     */
    private String stepsString = null;
    /**
     * String of total distance.
     */
    private String distanceString = null;
    /**
     * String of total energy consumption.
     */
    private String metsString = null;


    DBHelper mDbHelper;
    DataHolder mDataHolder;

    String mStringBPMValues = "";
    String mStringSPO2Values = "";
    String mStringStepValues = "";
    String mStringDistancesValue = "";
    String mStringMetValue = "";
    String mStringStartDateTime = "";
    String mStringEndDateTime = "";
    String mStringParentID = "";

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public PowerManager.WakeLock wl;

    int intTotlaTime = 0;

    Utility mUtility;

    boolean isBPMStart = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_bpm);

        mUtility = new Utility(FragmentBPM.this);
        handler = new Handler();
        mDbHelper = new DBHelper(FragmentBPM.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextViewTotalStep = (TextView) findViewById(R.id.fragment_bpm_txt_total_steps);
        mTextViewDistanceMeter = (TextView) findViewById(R.id.fragment_bpm_txt_distance_meter);
        mTextViewTotalMin = (TextView) findViewById(R.id.fragment_bpm_txt_time_min);
        mTextViewTotalMet = (TextView) findViewById(R.id.fragment_bpm_txt_met);
        mTextViewTotalSpo2 = (TextView) findViewById(R.id.fragment_bpm_txt_spo2);
        mTextViewTotalBPM = (TextView) findViewById(R.id.fragment_bpm_txt_bpm);

        mImageViewProgress = (ImageView) findViewById(R.id.fragment_bpm_chart_progress);
        mImageViewBack = (ImageView) findViewById(R.id.fragment_bpm_imageview_back);
        mImageViewAddStory = (ImageView) findViewById(R.id.fragment_bpm_imageview_add_story);

        mLinearLayoutTabsHOME = (LinearLayout) findViewById(R.id.activity_main_linear_tab_home);
        mLinearLayoutTabsHRV = (LinearLayout) findViewById(R.id.activity_main_linear_hrv);
        mLinearLayoutTabsSLEEp = (LinearLayout) findViewById(R.id.activity_main_linear_tab_sleep);
        mLinearLayoutTabsSETTINGS = (LinearLayout) findViewById(R.id.activity_main_linear_tab_setting);

        mImageViewHome = (ImageView) findViewById(R.id.activity_main_imageview_home);
        mImageViewHrv = (ImageView) findViewById(R.id.activity_main_imageview_hrv);
        mImageViewSLEEp = (ImageView) findViewById(R.id.activity_main_imageview_sleep);
        mImageViewSETTING = (ImageView) findViewById(R.id.activity_main_imageview_setting);

        mTextViewHome = (TextView) findViewById(R.id.activity_main_textview_home);
        mTextViewHrv = (TextView) findViewById(R.id.activity_main_textview_hrv);
        mTextViewSLEEP = (TextView) findViewById(R.id.activity_main_textview_sleep);
        mTextViewSETTING = (TextView) findViewById(R.id.activity_main_textview_setting);

        AnimationDrawable spinner = (AnimationDrawable) mImageViewProgress.getBackground();
        spinner.start();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        mLineChart = (LineChart) findViewById(R.id.fragment_bpm_line_chart);
        mLineChart.setNoDataText("");

        mLineChart.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.color_white)); // left y-axis
        mLineChart.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.color_white));


        mButtonStart = (Button) findViewById(R.id.fragment_bpm_button_start);
        mButtonStop = (Button) findViewById(R.id.fragment_bpm_button_stop);
        mButtonStop.setClickable(false);

        mLinearLayoutTabsHOME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLinearLayoutTabsSLEEp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentBPM.this, FragmentSleep.class);
                startActivity(mIntent);
                finish();
            }
        });

        mLinearLayoutTabsHRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentBPM.this, FragmentECG.class);
                startActivity(mIntent);
                finish();
            }
        });

        mLinearLayoutTabsSETTINGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentBPM.this, FragmentSettings.class);
                startActivity(mIntent);
                finish();
            }
        });

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.connectedDevice != null) {

                    MainActivity.enableControl("PR");

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startReceiveData();
                        }
                    }, 1500);

                    tabUnselected();

                    isBPMStart = true;

                    mImageViewProgress.setVisibility(View.VISIBLE);

                    mButtonStart.setBackgroundResource(R.drawable.button_background_disabled);
                    mButtonStart.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.button_enabled_color));
                    mButtonStart.setClickable(false);

                    mButtonStop.setBackgroundResource(R.drawable.button_background_enabled);
                    mButtonStop.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
                    mButtonStop.setClickable(true);
                } else {
                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                }

            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.connectedDevice != null) {
//                    XAxis xAxis = mLineChart.getXAxis();
//                    xAxis.setAxisMinimum(0);
//                    YAxis yAxis = mLineChart.getAxisLeft();
//                    yAxis.setAxisMinimum(30);
//                    mLineChart.zoomToCenter(0,100);
                    stopReceiveData();
                }
            }
        });

        mImageViewAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBPMStart) {
                    mUtility.errorDialog(getResources().getString(R.string.alert_bpm_start));
                } else {
                    Intent mIntent = new Intent(FragmentBPM.this, FragmentBPMList.class);
                    startActivity(mIntent);
                }
            }
        });


        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBPMStart) {
                    mUtility.errorDialog(getResources().getString(R.string.alert_bpm_start));
                } else {
                    onBackPressed();
                }
            }
        });



        if (isBPMStart) {
            mButtonStart.setBackgroundResource(R.drawable.button_background_disabled);
            mButtonStart.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.button_enabled_color));
            mButtonStart.setClickable(false);

            mButtonStop.setBackgroundResource(R.drawable.button_background_enabled);
            mButtonStop.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
            mButtonStop.setClickable(true);

//            startDrawAcceleration();
            stopUpdateActivity();
        } else {
            mButtonStop.setBackgroundResource(R.drawable.button_background_disabled);
            mButtonStop.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.button_enabled_color));
            mButtonStop.setClickable(false);

            mButtonStart.setBackgroundResource(R.drawable.button_background_enabled);
            mButtonStart.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
            mButtonStart.setClickable(true);
        }
    }

    public void tabSelected() {

        mImageViewHome.setImageResource(R.drawable.icon_home_selected);
        mImageViewHrv.setImageResource(R.drawable.icon_ecg_selected);
        mImageViewSLEEp.setImageResource(R.drawable.icon_sleep_selected);
        mImageViewSETTING.setImageResource(R.drawable.icon_setting_selected);

        mLinearLayoutTabsHOME.setClickable(true);
        mLinearLayoutTabsHRV.setClickable(true);
        mLinearLayoutTabsSLEEp.setClickable(true);
        mLinearLayoutTabsSETTINGS.setClickable(true);

        mTextViewHome.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
        mTextViewHrv.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
        mTextViewSLEEP.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
        mTextViewSETTING.setTextColor(ContextCompat.getColor(FragmentBPM.this, R.color.color_white));
    }

    public void tabUnselected() {

        mImageViewHome.setImageResource(R.drawable.icon_home_unselect);
        mImageViewHrv.setImageResource(R.drawable.icon_ecg_unselected);
        mImageViewSLEEp.setImageResource(R.drawable.icon_sleep_unselected);
        mImageViewSETTING.setImageResource(R.drawable.icon_setting_unselected);

        mLinearLayoutTabsHOME.setClickable(false);
        mLinearLayoutTabsHRV.setClickable(false);
        mLinearLayoutTabsSLEEp.setClickable(false);
        mLinearLayoutTabsSETTINGS.setClickable(false);

        mTextViewHome.setTextColor(getResources().getColor(R.color.color_inactive_tab));
        mTextViewHrv.setTextColor(getResources().getColor(R.color.color_inactive_tab));
        mTextViewSLEEP.setTextColor(getResources().getColor(R.color.color_inactive_tab));
        mTextViewSETTING.setTextColor(getResources().getColor(R.color.color_inactive_tab));

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.setDeviceStatusListner(new DevicesStatus() {
            @Override
            public void addScanDevices(BluetoothDevice bluetoothDevice) {

            }

            @Override
            public void onConnect(String devicesName, final String devicesAddress) {

            }

            @Override
            public void onDisconnect(String devicesName, final String devicesAddress) {

                if(!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mUtility.errorDialog(getResources().getString(R.string.alert_disconnect_device));
                            stopDeviceData();
                        }
                    });
                }
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
            public void dataAvailable(final Object obj) {

                synchronized (DevicesStatus.RECEIVE_SENSOR_DATA_LOCK) {

                    if (obj instanceof AccelerationWaveData) {
                /* save received acceleration data */
                        AccelerationWaveData accelerationWaveData = (AccelerationWaveData) obj;
                        diffTimeQueueA.add(Integer.valueOf(accelerationWaveData.getDiffTime()));
                        xDataQueue.add(Integer.valueOf(accelerationWaveData.getX()));
                        yDataQueue.add(Integer.valueOf(accelerationWaveData.getY()));
                        zDataQueue.add(Integer.valueOf(accelerationWaveData.getZ()));

                    } else if ((obj instanceof ActivityMeterData)) {
                /* save received activity data */
                        activityMeterData = (ActivityMeterData) obj;
                    } else if (obj instanceof HeartRateData) {
                /* save received heart rate data */
                        HeartRateData heartRateData = (HeartRateData) obj;
                        heartRate = heartRateData.getHeartRate();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //stuff that updates ui
                                mImageViewProgress.setVisibility(View.GONE);
                                mTextViewTotalBPM.setText("" + heartRate + "\nBPM");
                            }
                        });

                        bpmDataQueue.add(heartRate);
                        mStringBPMValues = mStringBPMValues + heartRate + ",";

                        drawAcceleration();

                    } else if (obj instanceof PulseOximeterData) {
                        pulseOximeterData = (PulseOximeterData) obj;
                        pulseOximeterData.getPoFastValue();
                        mStringSPO2Values = mStringSPO2Values + String.format("%.02f", pulseOximeterData.getPoNormalValue() * 100) + ",";
                    }
                }
            }

            @Override
            public void dataStatus(int status) {

            }
        },TAG);

        mUtility.changeStatusbarColor(R.color.custom_header_color);
    }

    @Override
    public void onBackPressed() {

        if (isBPMStart) {
            mUtility.errorDialog(getResources().getString(R.string.alert_bpm_start));
            return;
        }
        super.onBackPressed();
    }

    /**
     * Acceleration graph drawing.
     */
    private void drawAcceleration() {
        int countplusa = 0;

        /* get accel data from receive data */
        mLineChart.setDrawGridBackground(false);

        mLineChart.setVisibleXRangeMaximum(10);

        // no description text
        mLineChart.getDescription().setEnabled(false);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);

        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.enableGridDashedLine(0f, 0f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridLineWidth(0f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(false);

        if (bpmDataQueue != null && bpmDataQueue.size() > 0) {
            mLineChart.moveViewToX(bpmDataQueue.size() + 10);
        }

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

        if (bpmDataQueue != null && bpmDataQueue.size() > 0) {
            int mIntMinValue = Collections.min(bpmDataQueue);
            int mIntMaxValue = Collections.max(bpmDataQueue);

            if (mIntMinValue > 10) {
                leftAxis.setAxisMinimum(mIntMinValue - 10);
            } else {
                leftAxis.setAxisMinimum(0);
            }

            leftAxis.setAxisMaximum(mIntMaxValue + 10);
        } else {
            leftAxis.setAxisMaximum(50);
            leftAxis.setAxisMinimum(0);
        }

        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mLineChart.getAxisRight().setEnabled(false);

        // add data
        if (bpmDataQueue != null && bpmDataQueue.size() > 0) {
            setData(45, 100);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLineChart.animateX(0);
            }
        });

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < bpmDataQueue.size(); i++) {
            values.add(new Entry(i, bpmDataQueue.get(i)));
        }

        LineDataSet set1;

        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "BPM Data");
            set1.setColor(ContextCompat.getColor(this, R.color.color_active_tab));
            set1.setCircleColor(ContextCompat.getColor(this, R.color.color_active_tab));
            set1.setValueTextColor(ContextCompat.getColor(this, R.color.color_black));
//            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(0f, 0f, 0f);
            set1.enableDashedHighlightLine(0f, 0f, 0f);
            set1.setColor(ContextCompat.getColor(this, R.color.color_active_tab));
            set1.setCircleColor(ContextCompat.getColor(this, R.color.color_active_tab));
            set1.setLineWidth(1f);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(false);
            set1.setValueTextColor(ContextCompat.getColor(this, R.color.color_black));
            set1.setFormLineWidth(2f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{0f, 0f}, 0f));
            set1.setFormSize(15);

            if (Utils.getSDKInt() >= 18) {
                set1.setFillColor(Color.BLACK);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mLineChart.setPinchZoom(true);
            mLineChart.setData(data);
        }
    }

    /**
     * Start receive data from brighton
     *
     * @see MainActivity#enableMonitorize(boolean, ArrayList)
     */
    private void startReceiveData() {
        /* clear receive data */

        mStringBPMValues = "";
        mStringSPO2Values = "";
        mStringStepValues = "";
        mStringDistancesValue = "";
        mStringMetValue = "";

        intTotlaTime = 0;

        if (MainActivity.connectedDevice != null) {
            MainActivity.setMetsValue(0, false);

        }

        /* set receive data types */
        ArrayList<BleConstants.NotificationDataType> dataTypeList = new ArrayList<BleConstants.NotificationDataType>() {{
            add(BleConstants.NotificationDataType.HEART_RATE);
            add(BleConstants.NotificationDataType.ACTIVITY_METER);
            add(BleConstants.NotificationDataType.ACC_VALUE_DATA);
            add(BleConstants.NotificationDataType.PULSE_OXIMETER_DATA);
        }};

        MainActivity.enableMonitorize(true, dataTypeList);

        Calendar mCalendar = Calendar.getInstance();
        MainActivity.writeTimeOnDevice(mCalendar);

        startUpdateActivity();

        insertIntoMainActivity();

    }

    private void startUpdateActivity() {
        if (this.updateActivityTimer == null) {
            this.updateActivityTimer = new Timer();
            this.updateActivityTimer.scheduleAtFixedRate(new TimerTask() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    synchronized (ACTIVITY_UPDATE_LOCK) {
                        updateActivity();
                    }
                }
            }, UPDATE_ACTIVITY_TIMER_INTERVAL, UPDATE_ACTIVITY_TIMER_INTERVAL);
        }
    }

    /**
     * Stop receive data from brighton
     *
     * @see MainActivity#enableMonitorize(boolean, ArrayList)
     */
    private void stopReceiveData() {
        /* set receive data types */
        ArrayList<BleConstants.NotificationDataType> dataTypeList = new ArrayList<BleConstants.NotificationDataType>() {{
            add(BleConstants.NotificationDataType.HEART_RATE);
            add(BleConstants.NotificationDataType.ACTIVITY_METER);
            add(BleConstants.NotificationDataType.ACC_VALUE_DATA);
            add(BleConstants.NotificationDataType.PULSE_OXIMETER_DATA);
        }};

        MainActivity.enableMonitorize(false, dataTypeList);

        stopDeviceData();
    }


    public void stopDeviceData() {

        mImageViewProgress.setVisibility(View.GONE);

        isBPMStart = false;

        tabSelected();

        mButtonStop.setBackgroundResource(R.drawable.button_background_disabled);
        mButtonStop.setTextColor(getResources().getColor(R.color.button_enabled_color));
        mButtonStop.setClickable(false);

        mButtonStart.setBackgroundResource(R.drawable.button_background_enabled);
        mButtonStart.setTextColor(getResources().getColor(R.color.color_white));
        mButtonStart.setClickable(true);

        updateIntoMainActivity();

        /* stop update activity timer */
        stopUpdateActivity();
//        stopDrawAcceleration();

        mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableMainActivityTable + " ORDER BY " + DBHelper.mMain_ACTIVITY_ID + " DESC LIMIT 1");

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
            mStringParentID = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_ID);
            mStringStartDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
        }

        insertIntoBPMDetails();
        insertIntoMETDetails();
        insertIntoStepDetails();
        insertIntoDistanceDetails();
        insertIntoSPO2Details();
        insertIntoTimeTable();
    }

    /**
     * Stop update activity timer.
     */
    private void stopUpdateActivity() {
        if (this.updateActivityTimer != null) {
            this.updateActivityTimer.cancel();
            this.updateActivityTimer = null;
        }
    }

    /**
     * Period timer to update activity.</ br>
     * Calculation and display of elapsed time.
     */
    private void updateActivity() {
        /* update elapsed time */
        this.handler.post(new Runnable() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                intTotlaTime++;
                mTextViewTotalMin.setText(getConvertedTime(intTotlaTime));
            }
        });

//        changeDataViewHR();
        changeDataViewAM();
    }

    public String getConvertedTime(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return "" + getProperTimeFormat(minutes) + " : " + getProperTimeFormat(seconds);
    }

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    /**
     * To receive the latest heart rate data, and display it.</ br>
     * It is called from the period timer to update activity.
     */
//    private void changeDataViewHR() {
//        int heartrate;
//        boolean received;
//
//        /* get heart rate data from receive data */
//        heartrate = this.heartRate;
//        received = this.hr_flag;
//        this.hr_flag = false; /* clear receive status */
//
//        if (!received) {
//            /* not received data */
//            this.nothing_a++;
//            if (this.nothing_a > 5) {
//                this.heartRateString = this.getString(R.string.default_pulse_rate);
//                heartrate = -1;
//            }
//        } else {
//            /* received data */
//            if (heartrate == 0 || heartrate == -1) {
//                this.heartRateString = this.getString(R.string.default_pulse_rate);
//            } else {
//                this.heartRateString = String.valueOf(heartrate);
//            }
//            this.nothing_a = 0;
//        }
//
//        /* calculation new exercise intensity */
//        this.new_exercise_intensity = (heartrate / (220.0 - age_a)) * 100;
//
////        /* update pulse rate level */
//        this.handler.post(new Runnable() {
//            /**
//             * {@inheritDoc}
//             */
//            @Override
//            public void run() {
//                mTextViewTotalBPM.setText("" + heartRateString + "\nBPM");
//            }
//        });
//    }

    /**
     * It receives the latest activity data, and display it.</ br>
     * It is called from the period timer to update activity.
     *
     * @see #updateActivity()
     */
    private void changeDataViewAM() {

        if (this.activityMeterData == null) {
            /* not received data */
            return;
        }

        /* get activity data from receive data */
        this.stepsString = String.valueOf(this.activityMeterData.getTotalSteps());
        this.distanceString = String.valueOf(this.activityMeterData.getTotalDistance());
        int mets = this.activityMeterData.getTotalEnergyConsumption();
        this.metsString = String.valueOf((Math.floor(mets / 100.0f)) / 10.0f);

        /* update activity values */
        this.handler.post(new Runnable() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {

                mStringDistancesValue = mStringDistancesValue + distanceString + ",";
                mStringStepValues = mStringStepValues + stepsString + ",";
                mStringMetValue = mStringMetValue + metsString + ",";

                if(pulseOximeterData != null)
                    mTextViewTotalSpo2.setText("" + String.format("%.02f", pulseOximeterData.getPoNormalValue() * 100) +"\nSpO2");

                mTextViewTotalStep.setText(stepsString);
                mTextViewDistanceMeter.setText(distanceString);
                mTextViewTotalMet.setText("" + metsString + "\nMET");
            }
        });
    }

    public void insertIntoMainActivity() {

        mStringStartDateTime = mUtility.getDateTime();

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mMain_ACTIVITY_START_TIME, mStringStartDateTime);
        mContentValues.put(DBHelper.mMain_ACTIVITY_END_TIME, "");
        mContentValues.put(DBHelper.mMain_ACTIVITY_TOTAL_TIME, "");
        mContentValues.put(DBHelper.mMain_ACTIVITY_DATE, mUtility.getDate());
        mContentValues.put(DBHelper.mMain_ACTIVITY_SERVER_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mMain_ACTIVITY_USER_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mMain_ACTIVITY_Is_Sync, "no");
        mContentValues.put(DBHelper.mMain_ACTIVITY_TYPE, "bpm");
        mContentValues.put(DBHelper.mMain_ACTIVITY_RANDOM_NUMBER, getRendomNo());

        mDbHelper.insertRecord(DBHelper.mTableMainActivityTable, mContentValues);
    }


    public void updateIntoMainActivity() {

        mStringEndDateTime = mUtility.getDateTime();

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mMain_ACTIVITY_END_TIME, mStringEndDateTime);
        mContentValues.put(DBHelper.mMain_ACTIVITY_TOTAL_TIME, getTimeDiffrence(mStringStartDateTime, mStringEndDateTime));
        mContentValues.put(DBHelper.mMain_ACTIVITY_SERVER_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));

        mDbHelper.updateRecord(DBHelper.mTableMainActivityTable, mContentValues, DBHelper.mMain_ACTIVITY_USER_ID + " = ? AND "
                + DBHelper.mMain_ACTIVITY_START_TIME + "= ?", new String[]{mUtility.getAppPrefString(Constant.PREFS_USER_ID), mStringStartDateTime});
    }

    public void insertIntoBPMDetails() {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mBPM_DETAILS_Parent_Id, mStringParentID);
        mContentValues.put(DBHelper.mBPM_DETAILS_Server_RecordId, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mBPM_DETAILS_BPM_Value, mStringBPMValues);
        mContentValues.put(DBHelper.mBPM_DETAILS_date_time, mStringStartDateTime);
        mContentValues.put(DBHelper.mBPM_DETAILS_Type, "bpm");
        mContentValues.put(DBHelper.mBPM_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mBPM_DEATILS_Is_Sync, "no");

        mDbHelper.insertRecord(DBHelper.mTableBPMDetails, mContentValues);
    }

    public void insertIntoMETDetails() {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mMET_DETAILS_Parent_ID, mStringParentID);
        mContentValues.put(DBHelper.mMET_DETAILS_SERVER_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mMET_DETAILS_Met_Value, mStringMetValue);
        mContentValues.put(DBHelper.mMET_DETAILS_Date_Time, mStringStartDateTime);
        mContentValues.put(DBHelper.mMET_DETAILS_Type, "bpm");
        mContentValues.put(DBHelper.mMET_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mMET_DETAILS_Is_Sync, "no");

        mDbHelper.insertRecord(DBHelper.mTableMETDetails, mContentValues);
    }

    public void insertIntoStepDetails() {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mSTEP_DETAILS_Parent_ID, mStringParentID);
        mContentValues.put(DBHelper.mSTEP_DETAILS_SERVER_STEP_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mSTEP_DETAILS_STEP_Value, mStringStepValues);
        mContentValues.put(DBHelper.mSTEP_DETAILS_Date_Time, mStringStartDateTime);
        mContentValues.put(DBHelper.mSTEP_DETAILS_Type, "bpm");
        mContentValues.put(DBHelper.mSTEP_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mSTEP_DETAILS_Is_Sync, "no");

        mDbHelper.insertRecord(DBHelper.mTableStepDetails, mContentValues);
    }

    public void insertIntoDistanceDetails() {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_Parent_ID, mStringParentID);
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_Server_Distance_Record_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_Distance_Value, mStringDistancesValue);
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_Date_Time, mStringStartDateTime);
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_Type, "bpm");
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mDISTANCE_DETAILS_Is_Sync, "no");

        mDbHelper.insertRecord(DBHelper.mTableDistanceDetails, mContentValues);
    }

    public void insertIntoSPO2Details() {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mSPO2_DETAILS_Parent_ID, mStringParentID);
        mContentValues.put(DBHelper.mSPO2_DETAILS_SERVER_SPO2_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mSPO2_DETAILS_SPO2_Value, mStringSPO2Values);
        mContentValues.put(DBHelper.mSPO2_DETAILS_Date_Time, mStringStartDateTime);
        mContentValues.put(DBHelper.mSPO2_DETAILS_Type, "bpm");
        mContentValues.put(DBHelper.mSPO2_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mSPO2_DETAILS_Is_Sync, "no");

        mDbHelper.insertRecord(DBHelper.mTableSPO2Details, mContentValues);
    }

    public void insertIntoTimeTable() {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mTable_Parent_ID, mStringParentID);
        mContentValues.put(DBHelper.mTable_SERVER_STEP_RECORD_TIME, "");
        mContentValues.put(DBHelper.mTable_STEP_Value, mStringStepValues);
        mContentValues.put(DBHelper.mTable_Date_Time, mStringStartDateTime);
        mContentValues.put(DBHelper.mTable_Type, "bpm");
        mContentValues.put(DBHelper.mTable_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mTable_Is_Sync, "no");
        mContentValues.put(DBHelper.mTable_DATE, mUtility.getDate());

        mDbHelper.insertRecord(DBHelper.mTableTimeTable, mContentValues);
    }

    public String getRendomNo() {
        Random rand = new Random();

        int min = 10000;
        int max = 1000000;
        int random = rand.nextInt((max - min) + 1) + min;

        return "" + System.currentTimeMillis() + "_" + random;
    }

    public String getTimeDiffrence(String startDate, String endDate) {

        try {
            Date mDateStart = mSimpleDateFormatDateTime.parse(startDate);
            Date mDateEnd = mSimpleDateFormatDateTime.parse(endDate);

            long diff = mDateEnd.getTime() - mDateStart.getTime();

            return "" + (int) (diff / (1000));

        } catch (Exception e) {

        }
        return "";
    }

    @Override
    protected void onDestroy() {
        if (wl != null){
            wl.release();
        }
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();

    }
}
