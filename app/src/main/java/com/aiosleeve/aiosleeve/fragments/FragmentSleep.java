package com.aiosleeve.aiosleeve.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

import jp.co.toshiba.semicon.hcsdp.brighton.controllib.HypnagogicData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.SleepData;

public class FragmentSleep extends AppCompatActivity {

    public static final String TAG="FragmentSleep";

    public LinearLayout mLinearLayoutTabsHOME;
    public LinearLayout mLinearLayoutTabsBPM;
    public LinearLayout mLinearLayoutTabsHRV;
    public LinearLayout mLinearLayoutTabsSETTINGS;

    public ImageView mImageViewHome;
    public ImageView mImageViewBPM;
    public ImageView mImageViewHRV;
    public ImageView mImageViewSETTING;

    public TextView mTextViewHome;
    public TextView mTextViewBPM;
    public TextView mTextViewHRV;
    public TextView mTextViewSETTING;
    //    public TextView mTextViewTotalTime;
    public TextView mTextViewStartTime;
    public TextView mTextViewEndTime;

    public TextView mTextViewAwake;
    public TextView mTextViewLighter;
    public TextView mTextViewLight;
    public TextView mTextViewDeeper;
    public TextView mTextViewDeep;
    public TextView mTextViewTotalMin;

    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;
    public ImageView mImageViewProgress;

    public Button mButtonStart;
    public Button mButtonStop;

    public Utility mUtility;

    LineChart mLineChart;

    public boolean isSleepStart = false;
    public static boolean depthdata_flag = false;
    public static boolean hoursdata_flag = false;

    private Calendar depthStartTime = null;

    private ArrayList<SleepData.SingleSleepData> depthDataList = new ArrayList<>();
    private ArrayList<HypnagogicData.SingleHypnagogicData> hoursDataList = new ArrayList<>();
    private ArrayList<Calendar> startTimeList = new ArrayList<>();

    private LinkedList<Integer> sleepDataQueue = new LinkedList<Integer>();

    String mStringStartDateTime = "";
    String mStringEndDateTime = "";
    String mStringSleepValue = "";
    String mStringSleepDate = "";
    String mStringSleepDiffrence = "";
    String mStringSleepTotlaTime = "";

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    int intTotalAwake = 0;
    int intTotalLighter = 0;
    int intTotalLight = 0;
    int intTotalDeep = 0;
    int intTotalDeeper = 0;

    /**
     * ui handler
     */
    private Handler handler;
//    public PowerManager.WakeLock wl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_sleep);

        mUtility = new Utility(FragmentSleep.this);
        mDbHelper = new DBHelper(FragmentSleep.this);
        handler = new Handler();

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLinearLayoutTabsHOME = (LinearLayout) findViewById(R.id.activity_main_linear_tab_home);
        mLinearLayoutTabsBPM = (LinearLayout) findViewById(R.id.activity_main_linear_bpm);
        mLinearLayoutTabsHRV = (LinearLayout) findViewById(R.id.activity_main_linear_tab_hrv);
        mLinearLayoutTabsSETTINGS = (LinearLayout) findViewById(R.id.activity_main_linear_tab_setting);

        mLineChart = (LineChart) findViewById(R.id.fragment_sleep_line_chart);
        mLineChart.setNoDataText("");

        mImageViewHome = (ImageView) findViewById(R.id.activity_main_imageview_home);
        mImageViewBPM = (ImageView) findViewById(R.id.activity_main_imageview_bpm);
        mImageViewHRV = (ImageView) findViewById(R.id.activity_main_imageview_hrv);
        mImageViewSETTING = (ImageView) findViewById(R.id.activity_main_imageview_setting);
        mImageViewBack = (ImageView) findViewById(R.id.fragment_sleep_imageview_back);
        mImageViewAddStory = (ImageView) findViewById(R.id.fragment_sleep_imageview_add_story);
        mImageViewProgress = (ImageView) findViewById(R.id.fragment_sleep_chart_progress);

        mTextViewHome = (TextView) findViewById(R.id.activity_main_textview_home);
        mTextViewBPM = (TextView) findViewById(R.id.activity_main_textview_bpm);
        mTextViewHRV = (TextView) findViewById(R.id.activity_main_textview_hrv);
        mTextViewSETTING = (TextView) findViewById(R.id.activity_main_textview_setting);
        mTextViewStartTime = (TextView) findViewById(R.id.fragment_sleep_txt_start_time);
        mTextViewEndTime = (TextView) findViewById(R.id.fragment_sleep_txt_end_time);

        mTextViewAwake = (TextView) findViewById(R.id.fragment_sleep_txt_awake);
        mTextViewLighter = (TextView) findViewById(R.id.fragment_sleep_txt_lighter);
        mTextViewLight = (TextView) findViewById(R.id.fragment_sleep_txt_light);
        mTextViewDeeper = (TextView) findViewById(R.id.fragment_sleep_txt_deeper);
        mTextViewDeep = (TextView) findViewById(R.id.fragment_sleep_txt_deep);
        mTextViewTotalMin = (TextView) findViewById(R.id.fragment_sleep_txt_total_min);

        mButtonStart = (Button) findViewById(R.id.fragment_sleep_button_start);
        mButtonStop = (Button) findViewById(R.id.fragment_sleep_button_stop);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                String mStringLable = "";
                return mStringLable;
            }
        });

        YAxis mYAxis = mLineChart.getAxisLeft();
        mYAxis.setTextColor(ContextCompat.getColor(FragmentSleep.this, R.color.color_white));
        mYAxis.setEnabled(true);

        mYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                String mStringLable = "";

                if (value >= 0 && value < 1) {
                    mStringLable = "";

                } else if (value >= 1 && value < 2) {
                    mStringLable = "";

                } else if (value >= 2 && value < 3) {
                    if (sleepDataQueue.contains(2)) {
                        mStringLable = "Awake";
                    } else {
                        mStringLable = "";
                    }

                } else if (value >= 3 && value < 4) {
                    if (sleepDataQueue.contains(3)) {
                        mStringLable = "Lighter";
                    } else {
                        mStringLable = "";
                    }

                } else if (value >= 4 && value < 5) {
                    if (sleepDataQueue.contains(4)) {
                        mStringLable = "Light";
                    } else {
                        mStringLable = "";
                    }

                } else if (value >= 5 && value < 6) {
                    if (sleepDataQueue.contains(5)) {
                        mStringLable = "Deep";
                    } else {
                        mStringLable = "";
                    }

                } else if (value >= 6 && value < 7) {
                    if (sleepDataQueue.contains(6)) {
                        mStringLable = "Deeper";
                    } else {
                        mStringLable = "";
                    }

                }
                return mStringLable;
            }
        });

        mLinearLayoutTabsHOME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLinearLayoutTabsBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentSleep.this, FragmentBPM.class);
                startActivity(mIntent);
                finish();
            }
        });

        mLinearLayoutTabsHRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentSleep.this, FragmentECG.class);
                startActivity(mIntent);
                finish();
            }
        });

        mLinearLayoutTabsSETTINGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentSleep.this, FragmentSettings.class);
                startActivity(mIntent);
                finish();
            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSleepStart) {
                    mUtility.errorDialog(getResources().getString(R.string.alert_sleep_start));
                } else {
                    onBackPressed();
                }
            }
        });

        mImageViewAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSleepStart) {
                    mUtility.errorDialog(getResources().getString(R.string.alert_sleep_start));
                } else {
                    Intent mIntent = new Intent(FragmentSleep.this, FragmentSleepDataList.class);
                    startActivity(mIntent);
                }
            }
        });

        AnimationDrawable spinner = (AnimationDrawable) mImageViewProgress.getBackground();
        spinner.start();

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.connectedDevice != null) {
                    hoursDataList = new ArrayList<>();
                    startTimeList = new ArrayList<>();
                    depthDataList = new ArrayList<SleepData.SingleSleepData>();
                    startSleepData();
                } else {
                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                }
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    stopSleepData();
                } else {
                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                }
            }
        });

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.getSleepMode();
            }
        }, 1000);

        getLastRecord();

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.setDeviceStatusListner(new DevicesStatus() {
            @Override
            public void addScanDevices(BluetoothDevice bluetoothDevice) {

            }

            @Override
            public void onConnect(String devicesName, String devicesAddress) {

            }

            @Override
            public void onDisconnect(String devicesName, String devicesAddress) {

                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mUtility.errorDialog(getResources().getString(R.string.alert_disconnect_device));
                            tabSelected();
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
            public void dataAvailable(Object data) {

                synchronized (DevicesStatus.RECEIVE_SENSOR_DATA_LOCK) {
                    if (data instanceof SleepData) {
                        SleepData sleepDepthData = (SleepData) data;
                        sleepDepthDataProcessor(sleepDepthData);
                    } else if (data instanceof HypnagogicData) {
                        HypnagogicData hypnagogicData = (HypnagogicData) data;
                        hypnagogicDataProcessor(hypnagogicData);
                    }
                }
            }

            @Override
            public void dataStatus(int status) {
                viewSleepMode(status);
            }

        },TAG);

        mUtility.changeStatusbarColor(R.color.custom_header_color);

    }

    public void startSleepData() {

        MainActivity.getSleepSensorTime();
        MainActivity.clearSleepData();
        tabSelected();

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                insertIntoSleepDetails();
                MainActivity.setSleepMode(true);
                MainActivity.getSleepMode();
            }
        }, 1000);
    }

    public void stopSleepData() {

        MainActivity.setSleepMode(false);
        tabUnselected();
        mImageViewProgress.setVisibility(View.VISIBLE);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {

                sleepDataQueue = new LinkedList<Integer>();
                mStringSleepDate = "";
                mStringSleepValue = "";
                mStringSleepDiffrence = "";

                MainActivity.getSleepData(null, null);
                MainActivity.getSleepMode();
            }
        }, 1000);
    }

    public void sleepDepthDataProcessor(final SleepData sleepData) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                int dataFormat = sleepData.getDataFormat();
                boolean sendStatus = sleepData.isSendStatus();
                Calendar date = sleepData.getDate();
                ArrayList<SleepData.SingleSleepData> sleepDataList = sleepData.getSleepDataList();

                switch (dataFormat) {
                    case 0:
                        depthStartTime = date;
                        break;

                    case 4:
                        for (int i = 0; i < sleepDataList.size(); i++) {
                            depthDataList.add(sleepDataList.get(i));
                            startTimeList.add(depthStartTime);
                        }
                        break;

                    case 5:
                        if (sendStatus) {
                            setDataProcess();
                        }

                        intTotalAwake = 0;
                        intTotalLighter = 0;
                        intTotalLight = 0;
                        intTotalDeep = 0;
                        intTotalDeeper = 0;

                        for (int i = 0; i < depthDataList.size(); i++) {
                            if (depthDataList.get(i).getSleepData() < 7) {

                                sleepDataQueue.add(depthDataList.get(i).getSleepData());
//                                }

                                mStringSleepValue = mStringSleepValue + depthDataList.get(i).getSleepData() + ",";
                                mStringSleepDiffrence = mStringSleepDiffrence + depthDataList.get(i).getDiffTime() + ",";

                                switch (depthDataList.get(i).getSleepData()) {

                                    case 2:
                                        intTotalAwake++;
                                        break;

                                    case 3:
                                        intTotalLighter++;
                                        break;

                                    case 4:
                                        intTotalLight++;
                                        break;

                                    case 5:
                                        intTotalDeep++;
                                        break;

                                    case 6:
                                        intTotalDeeper++;
                                        break;
                                }
                            }
                        }

                        mTextViewAwake.setText(getResources().getString(R.string.label_awake, "" + intTotalAwake));
                        mTextViewLighter.setText(getResources().getString(R.string.label_lighter, "" + intTotalLighter));
                        mTextViewLight.setText(getResources().getString(R.string.label_light, "" + intTotalLight));
                        mTextViewDeeper.setText(getResources().getString(R.string.label_deeper, "" + intTotalDeeper));
                        mTextViewDeep.setText(getResources().getString(R.string.label_deep, "" + intTotalDeep));
                        mTextViewTotalMin.setText(getResources().getString(R.string.label_total_min, "" + sleepDataQueue.size()));

                        drawAcceleration();

                        mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableSleepDetail + " ORDER BY " + DBHelper.mMain_ACTIVITY_ID + " DESC LIMIT 1");

                        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
                            mStringStartDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
                            updateSleepDetail();
                        }
                        mImageViewProgress.setVisibility(View.GONE);

                        break;

                    default:
                        break;
                }
            }
        });
    }

    public void hypnagogicDataProcessor(final HypnagogicData hypnagogicData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int dataFormat = hypnagogicData.getDataFormat();
                boolean sendStatus = hypnagogicData.isSendStatus();

                ArrayList<HypnagogicData.SingleHypnagogicData> sleepDataList = hypnagogicData.getHypnagogicDataList();
                switch (dataFormat) {
                    case 0:
                        for (int i = 0; i < sleepDataList.size(); i++) {
                            hoursDataList.add(sleepDataList.get(i));
                        }
                        break;

                    case 4:
                        if (sendStatus) {
                            hoursdata_flag = true;
                            setDataProcess();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

    public void setDataProcess() {
        if(depthdata_flag && hoursdata_flag) {
            if(depthDataList.size() > 0) {

                /* allocate sleep data memory */
                ByteBuffer size1 = ByteBuffer.allocateDirect(4);
                size1.order(ByteOrder.nativeOrder());
                IntBuffer depthSize = size1.asIntBuffer();
                depthSize.put(0, depthDataList.size());

                ByteBuffer buf1 = ByteBuffer.allocateDirect(depthDataList.size() * 8);
                buf1.order(ByteOrder.nativeOrder());
                LongBuffer depthTimeArray = buf1.asLongBuffer();

                ByteBuffer buf2 = ByteBuffer.allocateDirect(depthDataList.size() * 4);
                buf2.order(ByteOrder.nativeOrder());
                IntBuffer depthDataArray = buf2.asIntBuffer();

                ByteBuffer size2 = ByteBuffer.allocateDirect(4);
                size2.order(ByteOrder.nativeOrder());
                IntBuffer hoursSize = size2.asIntBuffer();
                hoursSize.put(0, hoursDataList.size());

                ByteBuffer buf3 = ByteBuffer.allocateDirect(hoursDataList.size() * 8);
                buf3.order(ByteOrder.nativeOrder());
                LongBuffer hoursTimeArray = buf3.asLongBuffer();

                ByteBuffer buf4 = ByteBuffer.allocateDirect(hoursDataList.size() * 4);
                buf4.order(ByteOrder.nativeOrder());
                IntBuffer hoursFlagArray = buf4.asIntBuffer();

                // adjust array
                adjustDepthData(depthDataList, startTimeList, depthTimeArray, depthDataArray);
                adjustHoursData(hoursDataList, hoursTimeArray, hoursFlagArray);

                // analysis

                // Flag Clear
                depthdata_flag = false;
                hoursdata_flag = false;
            }
        }
    }

    public void adjustDepthData(ArrayList<SleepData.SingleSleepData> sensor_data, ArrayList<Calendar> startTime, LongBuffer depthTime, IntBuffer depthData) {

        for (int i = 0; i < sensor_data.size(); i++) {
            int diff = sensor_data.get(i).getDiffTime();
            startTime.get(i).add(Calendar.SECOND, diff);
            int sec = startTime.get(i).get(Calendar.SECOND);
            long dtime = startTime.get(i).getTimeInMillis() / 1000L;
            long buf = dtime - sec;
            depthTime.put(i, buf);
            depthData.put(i, sensor_data.get(i).getSleepData());
        }
    }

    public void adjustHoursData(ArrayList<HypnagogicData.SingleHypnagogicData> sensor_data, LongBuffer hoursTime, IntBuffer hoursFrag) {
        for (int i = 0; i < sensor_data.size(); i++) {
            if (sensor_data.get(i).getHypnagogicData() > -1 && sensor_data.get(i).getHypnagogicData() < 4) {
            } else {
                sensor_data.remove(i);
            }
        }

        for (int i = 0; i < sensor_data.size(); i++) {
            int sec = sensor_data.get(i).getDate().get(Calendar.SECOND);
            hoursTime.put(i, (sensor_data.get(i).getDate().getTimeInMillis() / 1000L) - sec);
            hoursFrag.put(i, sensor_data.get(i).getHypnagogicData());
        }
    }

    public void viewSleepMode(final int status) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (status == 0) {
                    isSleepStart = true;
                    tabSelected();
                } else if (status == 1) {
                    isSleepStart = false;
                    tabUnselected();
                }
            }
        });
    }

    public void tabSelected() {

        this.handler.post(new Runnable() {
            @Override
            public void run() {
                isSleepStart = false;

                mButtonStop.setBackgroundResource(R.drawable.button_background_disabled);
                mButtonStop.setTextColor(getResources().getColor(R.color.button_enabled_color));
                mButtonStop.setClickable(false);

                mButtonStart.setBackgroundResource(R.drawable.button_background_enabled);
                mButtonStart.setTextColor(getResources().getColor(R.color.color_white));
                mButtonStart.setClickable(true);

                mImageViewHome.setImageResource(R.drawable.icon_home_selected);
                mImageViewHRV.setImageResource(R.drawable.icon_ecg_selected);
                mImageViewBPM.setImageResource(R.drawable.icon_sleep_selected);
                mImageViewSETTING.setImageResource(R.drawable.icon_setting_selected);

                mLinearLayoutTabsHOME.setClickable(true);
                mLinearLayoutTabsHRV.setClickable(true);
                mLinearLayoutTabsBPM.setClickable(true);
                mLinearLayoutTabsSETTINGS.setClickable(true);

                mTextViewHome.setTextColor(getResources().getColor(R.color.color_white));
                mTextViewHRV.setTextColor(getResources().getColor(R.color.color_white));
                mTextViewBPM.setTextColor(getResources().getColor(R.color.color_white));
                mTextViewSETTING.setTextColor(getResources().getColor(R.color.color_white));
            }
        });
    }

    public void tabUnselected() {

        this.handler.post(new Runnable() {
            @Override
            public void run() {

                isSleepStart = true;

                mButtonStart.setBackgroundResource(R.drawable.button_background_disabled);
                mButtonStart.setTextColor(getResources().getColor(R.color.button_enabled_color));
                mButtonStart.setClickable(false);

                mButtonStop.setBackgroundResource(R.drawable.button_background_enabled);
                mButtonStop.setTextColor(getResources().getColor(R.color.color_white));
                mButtonStop.setClickable(true);

                mImageViewHome.setImageResource(R.drawable.icon_home_unselect);
                mImageViewHRV.setImageResource(R.drawable.icon_ecg_unselected);
                mImageViewBPM.setImageResource(R.drawable.icon_sleep_unselected);
                mImageViewSETTING.setImageResource(R.drawable.icon_setting_unselected);

                mLinearLayoutTabsHOME.setClickable(false);
                mLinearLayoutTabsHRV.setClickable(false);
                mLinearLayoutTabsBPM.setClickable(false);
                mLinearLayoutTabsSETTINGS.setClickable(false);

                mTextViewHome.setTextColor(getResources().getColor(R.color.color_inactive_tab));
                mTextViewHRV.setTextColor(getResources().getColor(R.color.color_inactive_tab));
                mTextViewBPM.setTextColor(getResources().getColor(R.color.color_inactive_tab));
                mTextViewSETTING.setTextColor(getResources().getColor(R.color.color_inactive_tab));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSleepStart) {
            mUtility.errorDialog(getResources().getString(R.string.alert_sleep_start));
            return;
        }
        super.onBackPressed();
    }

    public void insertIntoSleepDetails() {

        mStringStartDateTime = mUtility.getDateTime();

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mSLEEP_DETAIL_RANDOM_NUMBER, getRendomNo());
        mContentValues.put(DBHelper.mSLEEP_DETAIL_SERVER_SLEEP_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mSLEEP_DETAIL_USER_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mContentValues.put(DBHelper.mSLEEP_DETAIL_IS_SYNC, "no");
        mContentValues.put(DBHelper.mSLEEP_DETAIL_START_TIME, mStringStartDateTime);
        mContentValues.put(DBHelper.mSLEEP_DETAIL_DATE, mUtility.getDate());

        mDbHelper.insertRecord(DBHelper.mTableSleepDetail, mContentValues);
    }

    public void updateSleepDetail() {

        mStringEndDateTime = mUtility.getDateTime();
        mStringSleepTotlaTime = mUtility.getTimeDiffrence(mStringStartDateTime, mStringEndDateTime);

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mSLEEP_DETAIL_END_TIME, mStringEndDateTime);
        mContentValues.put(DBHelper.mSLEEP_DETAIL_SLEEP_VALUE, mStringSleepValue);
        mContentValues.put(DBHelper.mSLEEP_DETAIL_SLEEP_DIFFERENCE, mStringSleepDiffrence);
        mContentValues.put(DBHelper.mSLEEP_DETAIL_TOTAL_TIME, mStringSleepTotlaTime);
        mContentValues.put(DBHelper.mSLEEP_DETAIL_SERVER_SLEEP_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));

        mDbHelper.updateRecord(DBHelper.mTableSleepDetail, mContentValues, DBHelper.mMain_ACTIVITY_USER_ID + " = ? AND "
                + DBHelper.mMain_ACTIVITY_START_TIME + "= ?", new String[]{mUtility.getAppPrefString(Constant.PREFS_USER_ID), mStringStartDateTime});
    }

    public String getRendomNo() {

        Random rand = new Random();

        int min = 10000;
        int max = 1000000;
        int random = rand.nextInt((max - min) + 1) + min;

        return "" + System.currentTimeMillis() + "_" + random;
    }

    private void drawAcceleration() {

        if (!mStringStartDateTime.equalsIgnoreCase("")) {
            String[] mString = mStringStartDateTime.split(" ");
            if (mString.length > 1) {
                mTextViewStartTime.setText(mString[1]);
            }
        }

        if (!mStringEndDateTime.equalsIgnoreCase("")) {
            String[] mString = mStringEndDateTime.split(" ");
            if (mString.length > 1) {
                mTextViewEndTime.setText(mString[1]);
            }
        }

        /* get accel data from receive data */
        mLineChart.setDrawGridBackground(false);

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

        if (sleepDataQueue != null && sleepDataQueue.size() > 0) {
            xAxis.setAxisMinimum(0);
            xAxis.setAxisMaximum(sleepDataQueue.size());
            xAxis.setLabelCount(sleepDataQueue.size(), true);
        } else {
            xAxis.setAxisMinimum(0);
            xAxis.setAxisMaximum(50);
        }

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

        if (sleepDataQueue != null && sleepDataQueue.size() > 0) {
            leftAxis.setAxisMaximum(7);
            leftAxis.setAxisMinimum(0);
            leftAxis.setLabelCount(7, true);
        } else {
            leftAxis.setAxisMaximum(7);
            leftAxis.setAxisMinimum(0);
            leftAxis.setLabelCount(0);
        }

        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mLineChart.getAxisRight().setEnabled(false);

        // add data
        if (sleepDataQueue != null && sleepDataQueue.size() > 0) {
            setData();
        }

        mLineChart.setVisibleYRangeMaximum(6, YAxis.AxisDependency.LEFT);
        mLineChart.setExtraLeftOffset(30f);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLineChart.animateX(0);
            }
        });

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();

        l.setEnabled(false);
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData() {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < sleepDataQueue.size(); i++) {
            float data = (float) sleepDataQueue.get(i);
            values.add(new Entry(i, data));
        }

        LineDataSet set1;

        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");
//            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(0f, 0f, 0f);
            set1.enableDashedHighlightLine(0f, 0f, 0f);
            set1.setColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
            set1.setCircleColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(12f);
            set1.setDrawFilled(false);
            set1.setValueTextColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
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
            data.setDrawValues(false);

            // set data
            mLineChart.setData(data);
        }
    }

    public String getConvertedTime(int totalSecs) {

        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return "" + mUtility.getProperTimeFormat(hours) + " Hours  " + mUtility.getProperTimeFormat(minutes) + " mins";
    }

    public void getLastRecord() {

        mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableSleepDetail + " WHERE "+DBHelper.mSLEEP_DETAIL_USER_ID +"='"+mUtility.getAppPrefString(Constant.PREFS_USER_ID)+"'AND NULLIF(" + DBHelper.mSLEEP_DETAIL_SLEEP_VALUE + ", ' ') IS NOT NULL  ORDER BY " + DBHelper.mMain_ACTIVITY_ID + " DESC LIMIT 1");

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
            sleepDataQueue = new LinkedList<>();
            mStringSleepValue = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_SLEEP_VALUE);
            mStringStartDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_START_TIME);
            mStringEndDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_END_TIME);

            if (mStringSleepValue != null && !mStringSleepValue.equalsIgnoreCase("")) {

                mStringSleepValue = mUtility.removeLastComma(mStringSleepValue);

                intTotalAwake = 0;
                intTotalLighter = 0;
                intTotalLight = 0;
                intTotalDeep = 0;
                intTotalDeeper = 0;

                String[] mStringSleep = mStringSleepValue.split(",");
                for (int i = 0; i < mStringSleep.length; i++) {
                    int intSleepData = Integer.parseInt(mStringSleep[i]);

                    sleepDataQueue.add(intSleepData);

                    switch (intSleepData) {

                        case 2:
                            intTotalAwake++;
                            break;

                        case 3:
                            intTotalLighter++;
                            break;

                        case 4:
                            intTotalLight++;
                            break;

                        case 5:
                            intTotalDeep++;
                            break;

                        case 6:
                            intTotalDeeper++;
                            break;
                    }
                }

                mTextViewAwake.setText(getResources().getString(R.string.label_awake, "" + intTotalAwake));
                mTextViewLighter.setText(getResources().getString(R.string.label_lighter, "" + intTotalLighter));
                mTextViewLight.setText(getResources().getString(R.string.label_light, "" + intTotalLight));
                mTextViewDeeper.setText(getResources().getString(R.string.label_deeper, "" + intTotalDeeper));
                mTextViewDeep.setText(getResources().getString(R.string.label_deep, "" + intTotalDeep));
                mTextViewTotalMin.setText(getResources().getString(R.string.label_total_min, "" + mStringSleep.length));

                drawAcceleration();
            }
            mStringSleepTotlaTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_TOTAL_TIME);

        } else {
            mTextViewAwake.setText(getResources().getString(R.string.label_awake, "" + 0));
            mTextViewLighter.setText(getResources().getString(R.string.label_lighter, "" + 0));
            mTextViewLight.setText(getResources().getString(R.string.label_light, "" + 0));
            mTextViewDeeper.setText(getResources().getString(R.string.label_deeper, "" + 0));
            mTextViewDeep.setText(getResources().getString(R.string.label_deep, "" + 0));
            mTextViewTotalMin.setText(getResources().getString(R.string.label_total_min, "" + 0));
        }
    }

    @Override
    protected void onDestroy() {
//        if (wl != null) {
//            wl.release();
//        }
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();
    }
}
