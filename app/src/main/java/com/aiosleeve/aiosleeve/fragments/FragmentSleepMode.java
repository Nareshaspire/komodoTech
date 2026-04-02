package com.aiosleeve.aiosleeve.fragments;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiosleeve.aiosleeve.ActivitySleepModeHistory;
import com.aiosleeve.aiosleeve.BPMSyncService;
import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import jp.co.toshiba.semicon.hcsdp.brighton.controllib.HypnagogicData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.SleepData;


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

public class FragmentSleepMode extends Fragment {

    public static final String TAG="FragmentSleepMode";

    View mCreateView;
    MainActivity mMainActivity;

    private ProgressBar mProgressBar;



    private Drawable mBackgroundGrayStartStopButtons;
    private Drawable mBackgroundSkyBlueStartStopButtons;

    public TextView mTextViewStartTime;
    public TextView mTextViewEndTime;

    public TextView mTextViewAwake;
    public TextView mTextViewLighter;
    public TextView mTextViewLight;
    public TextView mTextViewDeeper;
    public TextView mTextViewDeep;
    public TextView mTextViewTotalMin;

    public ImageView mImageViewHistoryIcon;


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

    private int colorGray;
    private int colorWhite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();

        if (mMainActivity != null) {

            mBackgroundGrayStartStopButtons = ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_low_radius_disable_sleep);
            mBackgroundSkyBlueStartStopButtons = ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_low_radius_sleep);

            colorWhite = ContextCompat.getColor(mMainActivity, R.color.color_white);
            colorGray = ContextCompat.getColor(mMainActivity, R.color.color_gray_10);

        }

        mUtility = new Utility(mMainActivity);
        mDbHelper = new DBHelper(mMainActivity);
        handler = new Handler();

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainActivity.setDeviceStatusListner(new DevicesStatus() {
            @Override
            public void addScanDevices(BluetoothDevice bluetoothDevice) {

            }

            @Override
            public void onConnect(String devicesName, String devicesAddress) {

            }

            @Override
            public void onDisconnect(String devicesName, String devicesAddress) {

                    mMainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            makeStartButtonClickable(true);
                            stopSleepData();
                            MainActivity.resetTabLock();
                            mUtility.errorDialog(mMainActivity.getString(R.string.alert_disconnect_device));

                            mMainActivity.moveToFirstTab();


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCreateView = inflater.inflate(R.layout.fragment_sleep_mode, container, false);


        setOnClickListeners();
        makeStartButtonClickable(true);

        return mCreateView;
    }

    private void setOnClickListeners(){
//        mLinearLayoutTabsHOME = (LinearLayout) mCreateView.findViewById(R.id.activity_main_linear_tab_home);
//        mLinearLayoutTabsBPM = (LinearLayout) mCreateView.findViewById(R.id.activity_main_linear_bpm);
//        mLinearLayoutTabsHRV = (LinearLayout) mCreateView.findViewById(R.id.activity_main_linear_tab_hrv);
//        mLinearLayoutTabsSETTINGS = (LinearLayout) mCreateView.findViewById(R.id.activity_main_linear_tab_setting);

        mLineChart = (LineChart) mCreateView.findViewById(R.id.fragment_sleep_line_chart);
        mLineChart.setNoDataText("");

//        mImageViewHome = (ImageView) mCreateView.findViewById(R.id.activity_main_imageview_home);
//        mImageViewBPM = (ImageView) mCreateView.findViewById(R.id.activity_main_imageview_bpm);
//        mImageViewHRV = (ImageView) mCreateView.findViewById(R.id.activity_main_imageview_hrv);
//        mImageViewSETTING = (ImageView) mCreateView.findViewById(R.id.activity_main_imageview_setting);
//        mImageViewBack = (ImageView) mCreateView.findViewById(R.id.fragment_sleep_imageview_back);
//        mImageViewAddStory = (ImageView) mCreateView.findViewById(R.id.fragment_sleep_imageview_add_story);
//        mImageViewProgress = (ImageView) mCreateView.findViewById(R.id.fragment_sleep_chart_progress);

//        mTextViewHome = (TextView) mCreateView.findViewById(R.id.activity_main_textview_home);
//        mTextViewBPM = (TextView) mCreateView.findViewById(R.id.activity_main_textview_bpm);
//        mTextViewHRV = (TextView) mCreateView.findViewById(R.id.activity_main_textview_hrv);
//        mTextViewSETTING = (TextView) mCreateView.findViewById(R.id.activity_main_textview_setting);
        mTextViewStartTime = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_start_time);
        mTextViewEndTime = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_end_time);

        mTextViewAwake = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_awake);
        mTextViewLighter = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_lighter);
        mTextViewLight = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_light);
        mTextViewDeeper = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_deeper);
        mTextViewDeep = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_deep);
        mTextViewTotalMin = (TextView) mCreateView.findViewById(R.id.fragment_sleep_txt_total_min);

        mImageViewHistoryIcon=(ImageView)mCreateView.findViewById(R.id.fragment_sleep_mode_story_button);

        mButtonStart = (Button) mCreateView.findViewById(R.id.fragment_sleep_button_start);
        mButtonStop = (Button) mCreateView.findViewById(R.id.fragment_sleep_button_stop);

        mProgressBar= (ProgressBar)mCreateView.findViewById(R.id.fragment_sleep_mode_progress_bar);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white), PorterDuff.Mode.SRC_IN);


        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                String mStringLable = "";
                return mStringLable;
            }
        });

        YAxis mYAxis = mLineChart.getAxisLeft();
        mYAxis.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
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

//        mLinearLayoutTabsHOME.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        mLinearLayoutTabsBPM.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(mMainActivity, FragmentBPM.class);
//                startActivity(mIntent);
//                finish();
//            }
//        });
//
//        mLinearLayoutTabsHRV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(mMainActivity, FragmentECG.class);
//                startActivity(mIntent);
//                finish();
//            }
//        });
//
//        mLinearLayoutTabsSETTINGS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(mMainActivity, FragmentSettings.class);
//                startActivity(mIntent);
//                finish();
//            }
//        });

//        mImageViewBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSleepStart) {
//                    mUtility.errorDialog(getResources().getString(R.string.alert_sleep_start));
//                } else {
//                    onBackPressed();
//                }
//            }
//        });
//
//        mImageViewAddStory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSleepStart) {
//                    mUtility.errorDialog(getResources().getString(R.string.alert_sleep_start));
//                } else {
//                    Intent mIntent = new Intent(mMainActivity, FragmentSleepDataList.class);
//                    startActivity(mIntent);
//                }
//            }
//        });

//        AnimationDrawable spinner = (AnimationDrawable) mImageViewProgress.getBackground();
//        spinner.start();

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.connectedDevice != null) {
                    hoursDataList = new ArrayList<>();
                    startTimeList = new ArrayList<>();
                    depthDataList = new ArrayList<SleepData.SingleSleepData>();

                    reset();//Resetting chart and textViews.
                    startSleepData();

                } else {
                    mUtility.errorDialog(mMainActivity.getString(R.string.alert_connect_device));
                }
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    MainActivity.resetTabLock();
                    stopSleepData();
                } else {
                    mUtility.errorDialog(mMainActivity.getString(R.string.alert_connect_device));
                }
            }
        });
        mImageViewHistoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getTabLock() == MainActivity.TabType.NONE) {
                    Intent mIntent = new Intent(mMainActivity, ActivitySleepModeHistory.class);
                    startActivity(mIntent);
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

    public void startSleepData() {

        MainActivity.setTabLock(MainActivity.TabType.SLEEP_MODE);

        MainActivity.getSleepSensorTime();
        MainActivity.clearSleepData();
//        tabSelected();

        mProgressBar.setVisibility(View.VISIBLE);
        makeStartButtonClickable(false);


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
//        tabUnselected();
        makeStartButtonClickable(true);


        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {

                sleepDataQueue = new LinkedList<Integer>();
                mStringSleepDate = "";
                mStringSleepValue = "";
                mStringSleepDiffrence = "";

                MainActivity.getSleepData(null, null);
                MainActivity.getSleepMode();

                mMainActivity.syncTheTables();//March-2021
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

//                                    case 2:
//                                        intTotalAwake++;
//                                        break;

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

                                    default:
                                        intTotalAwake++;
                                        break;
                                }
                            }
                        }
   int intTotalAwakeHours=0;
                        int intTotalAwakeMinutes=0;
                        int intTotalLighterHours=0;
                        int intTotalLighterMinutes=0;
                        int intTotalLightHours=0;
                        int intTotalLightMinutes=0;
                        int intTotalDeeperHours=0;
                        int intTotalDeeperMinutes=0;
                        int intTotalDeepHours=0;
                        int intTotalDeepMinutes=0;
                        int intTotalTimeHours=0;
                        int intTotalTimeMinutes=0;

                        if(intTotalAwake!=0) {
                            intTotalAwakeHours = intTotalAwake / 60;
                            intTotalAwakeMinutes = intTotalAwake % 60;
                        }
                        if(intTotalLighter!=0) {
                            intTotalLighterHours = intTotalLighter / 60;
                            intTotalLighterMinutes = intTotalLighter % 60;
                        }
                        if(intTotalLight!=0) {
                            intTotalLightHours = intTotalLight / 60;
                            intTotalLightMinutes = intTotalLight % 60;
                        }
                        if(intTotalDeeper!=0) {
                            intTotalDeeperHours = intTotalDeeper / 60;
                            intTotalDeeperMinutes = intTotalDeeper % 60;
                        }
                        if(intTotalDeep!=0) {
                            intTotalDeepHours = intTotalDeep / 60;
                            intTotalDeepMinutes = intTotalDeep % 60;
                        }
                        if(sleepDataQueue.size()!=0) {
                            intTotalTimeHours = (sleepDataQueue.size()) / 60;
                            intTotalTimeMinutes = (sleepDataQueue.size()) % 60;
                        }


                        mTextViewAwake.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalAwakeHours,intTotalAwakeMinutes));
                        mTextViewLighter.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalLighterHours,intTotalLighterMinutes));
                        mTextViewLight.setText(mMainActivity.getString(R.string.label_hours_and_min, intTotalLightHours,intTotalLightMinutes));
                        mTextViewDeeper.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalDeeperHours,intTotalDeeperMinutes));
                        mTextViewDeep.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalDeepHours,intTotalDeepMinutes));
                        mTextViewTotalMin.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalTimeHours,intTotalTimeMinutes));

                        drawAcceleration();

                        mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableSleepDetail + " ORDER BY " + DBHelper.mMain_ACTIVITY_ID + " DESC LIMIT 1");

                        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
                            mStringStartDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
                            updateSleepDetail();
                        }
                        mProgressBar.setVisibility(View.GONE);

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
//                    tabSelected();
                } else if (status == 1) {
                    isSleepStart = false;
//                    tabUnselected();
                }
            }
        });
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
        leftAxis.setAxisLineColor(Color.TRANSPARENT);
        leftAxis.setGridColor(Color.TRANSPARENT);//2021
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

        mMainActivity.runOnUiThread(new Runnable() {
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

        int sizeOfZeros=0;

        for (int i = 0; i < sleepDataQueue.size(); i++) {
            float data = (float) sleepDataQueue.get(i);
            if(data==0){
                sizeOfZeros+=1;
            }
            values.add(new Entry(i, data));
        }

        mLineChart.setDragYEnabled(sizeOfZeros != sleepDataQueue.size());//For Stop Scrolling Y-Axis when all values are 0.

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
            set1.setColor(ContextCompat.getColor(mMainActivity, R.color.drawer_icon_tint_color));
            set1.setCircleColor(ContextCompat.getColor(mMainActivity, R.color.color_violet_sleep_graph));
            set1.setLineWidth(1f);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(12f);
            set1.setDrawFilled(false);
            set1.setValueTextColor(ContextCompat.getColor(mMainActivity, R.color.drawer_icon_tint_color));
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

//                        case 2:
//                            intTotalAwake++;
//                            break;

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
                        default:
                            intTotalAwake++;
                            break;
                    }
                }


                int intTotalAwakeHours=0;
                int intTotalAwakeMinutes=0;
                int intTotalLighterHours=0;
                int intTotalLighterMinutes=0;
                int intTotalLightHours=0;
                int intTotalLightMinutes=0;
                int intTotalDeeperHours=0;
                int intTotalDeeperMinutes=0;
                int intTotalDeepHours=0;
                int intTotalDeepMinutes=0;
                int intTotalTimeHours=0;
                int intTotalTimeMinutes=0;

                if(intTotalAwake!=0) {
                    intTotalAwakeHours = intTotalAwake / 60;
                    intTotalAwakeMinutes = intTotalAwake % 60;
                }
                if(intTotalLighter!=0) {
                    intTotalLighterHours = intTotalLighter / 60;
                    intTotalLighterMinutes = intTotalLighter % 60;
                }
                if(intTotalLight!=0) {
                    intTotalLightHours = intTotalLight / 60;
                    intTotalLightMinutes = intTotalLight % 60;
                }
                if(intTotalDeeper!=0) {
                    intTotalDeeperHours = intTotalDeeper / 60;
                    intTotalDeeperMinutes = intTotalDeeper % 60;
                }
                if(intTotalDeep!=0) {
                    intTotalDeepHours = intTotalDeep / 60;
                    intTotalDeepMinutes = intTotalDeep % 60;
                }
                if(mStringSleep.length!=0) {
                    intTotalTimeHours = (mStringSleep.length) / 60;
                    intTotalTimeMinutes = (mStringSleep.length) % 60;
                }


                mTextViewAwake.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalAwakeHours,intTotalAwakeMinutes));
                mTextViewLighter.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalLighterHours,intTotalLighterMinutes));
                mTextViewLight.setText(mMainActivity.getString(R.string.label_hours_and_min, intTotalLightHours,intTotalLightMinutes));
                mTextViewDeeper.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalDeeperHours,intTotalDeeperMinutes));
                mTextViewDeep.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalDeepHours,intTotalDeepMinutes));
                mTextViewTotalMin.setText(mMainActivity.getString(R.string.label_hours_and_min,  intTotalTimeHours,intTotalTimeMinutes));

                drawAcceleration();
            }
            mStringSleepTotlaTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_TOTAL_TIME);

        } else {
            mTextViewAwake.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewLighter.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewLight.setText(mMainActivity.getString(R.string.label_hours_and_min, 0,0));
            mTextViewDeeper.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewDeep.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewTotalMin.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
        }
    }


    public void reset(){
        if(mLineChart!=null &&
                mTextViewAwake!=null &&
                mTextViewLighter!=null &&
                mTextViewLight!=null &&
                mTextViewDeeper!=null &&
                mTextViewDeep!=null &&
                mTextViewTotalMin!=null
        ) {
            mLineChart.clear();
            mTextViewAwake.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewLighter.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewLight.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewDeeper.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewDeep.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));
            mTextViewTotalMin.setText(mMainActivity.getString(R.string.label_hours_and_min,  0,0));

            mTextViewStartTime.setText("");
            mTextViewEndTime.setText("");



            if(mDbHelper.isThereAnyOnGoingSleepRecord()){
                Cursor data=mDbHelper.getSleepRecordWhichIsGoingOn();
                System.out.println("abc");
                if(data!=null && data.moveToFirst()) {

                    //Setting up the data.
                    String userId=data.getString(data.getColumnIndex(DBHelper.mSLEEP_DETAIL_USER_ID));

                    //Only if the same user.
                    if(userId.equals(mUtility.getAppPrefString(Constant.PREFS_USER_ID))){

                        mStringStartDateTime=data.getString(data.getColumnIndex(DBHelper.mSLEEP_DETAIL_START_TIME));
                        makeStartButtonClickable(false);
                        MainActivity.setTabLock(MainActivity.TabType.SLEEP_MODE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }


                }
            }


        }
    }

    private void makeStartButtonClickable(boolean isClickable) {
        if (isClickable) {
            mButtonStart.setBackground(mBackgroundSkyBlueStartStopButtons);
            mButtonStart.setClickable(true);

            mButtonStart.setTextColor(colorWhite);
            mButtonStop.setTextColor(colorGray);

            mButtonStop.setBackground(mBackgroundGrayStartStopButtons);
            mButtonStop.setClickable(false);
        } else {
            mButtonStart.setBackground(mBackgroundGrayStartStopButtons);
            mButtonStart.setClickable(false);

            mButtonStart.setTextColor(colorGray);
            mButtonStop.setTextColor(colorWhite);

            mButtonStop.setBackground(mBackgroundSkyBlueStartStopButtons);
            mButtonStop.setClickable(true);
        }

    }
    @Override
    public void onDestroy() {
//        if (wl != null) {
//            wl.release();
//        }
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();

    }
}