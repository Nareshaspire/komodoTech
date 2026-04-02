package com.aiosleeve.aiosleeve.fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.vanniktech.emoji.EmojiTextView;

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

public class FragmentActivityMode extends Fragment {

    public static final String TAG = "FragmentActivityMode";

    View mCreateView;
    private MainActivity mMainActivity;

    private TextView mButtonStart;
    private TextView mButtonStop;

    private AlertDialog alertDialog;

    private ActivityEvent selectedEvent;


    private TextView mAlertDialogStartButton;

    private TextView mAlertDialogEventRunningButton;
    private TextView mAlertDialogEventCyclingButton;
    private TextView mAlertDialogEventWalkingButton;
    private TextView mAlertDialogEventWorkoutButton;
    private TextView mAlertDialogEventGolfButton;
    private TextView mAlertDialogEventHikingButton;
    private TextView mAlertDialogEventYogaButton;
    private TextView mAlertDialogEventGardeningButton;
    private TextView mAlertDialogEventTennisButton;
    private TextView mAlertDialogEventWorkShiftButton;
    private TextView mAlertDialogEventOtherButton;

    private ProgressBar mProgressBar;


    private TextView mTextViewTotalStep;
    private TextView mTextViewDistanceMeter;
    private TextView mTextViewTotalMin;
    private TextView mTextViewTotalMet;
    private TextView mTextViewTotalSpo2;
    private TextView mTextViewTotalBPM;
    private TextView mTextViewKmOrMile;

    private Drawable mBackgroundGray;
    private Drawable mBackgroundSkyBlue;

    private Drawable mBackgroundGrayStartStopButtons;
    private Drawable mBackgroundSkyBlueStartStopButtons;

    private int mColorGray;
    private int mColorWhite;

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


    String mMaxBpm = null;
    String mAverageBpm = null;

    String mAverageMet = null;

    private boolean isMetric = true;


    private final int defaultInterval = 2500;
    private long lastTimeStopButtonClicked = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        if (mMainActivity != null) {
            mBackgroundGray = ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_event_gray);
            mBackgroundSkyBlue = ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_detail_sky_blue);

            mBackgroundGrayStartStopButtons = ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_low_radius_disable);
            mBackgroundSkyBlueStartStopButtons = ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_low_radius);

            mColorGray = ContextCompat.getColor(mMainActivity, R.color.color_gray_3);
            mColorWhite = ContextCompat.getColor(mMainActivity, R.color.color_white);
        }

        mUtility = new Utility(mMainActivity);
        handler = new Handler();
        mDbHelper = new DBHelper(mMainActivity);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PowerManager pm = (PowerManager) mMainActivity.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "aiosleeve:My Tag");
        wl.acquire();


        mMainActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        MainActivity.setDeviceStatusListner(new DevicesStatus() {
            @Override
            public void addScanDevices(BluetoothDevice bluetoothDevice) {

            }

            @Override
            public void onConnect(String devicesName, final String devicesAddress) {

            }

            @Override
            public void onDisconnect(String devicesName, final String devicesAddress) {


                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeStartButtonClickable(true);
                        MainActivity.resetTabLock();
                        mUtility.errorDialog(getResources().getString(R.string.alert_disconnect_device));

                        stopDeviceData();
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

//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mImageViewProgress.setVisibility(View.GONE);
//                                mTextViewTotalBPM.setText(String.valueOf(heartRate));//+ "\nBPM");
//
//                                Log.d(TAG, "Heart Rate Ankit-" + String.valueOf(heartRate));
//                            }
//                        });

                        mMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //stuff that updates ui
                                //stuff that updates ui
                                mProgressBar.setVisibility(View.GONE);
                                //mTextViewTotalBPM.setText(String.valueOf(heartRate));//+ "\nBPM");
                                mTextViewTotalBPM.setText(String.valueOf(heartRate));

                                Log.d(TAG, "Heart Rate Ankit-" + String.valueOf(heartRate));
                            }
                        });

                        bpmDataQueue.add(heartRate);
                        if (mMaxBpm == null || heartRate > Integer.parseInt(mMaxBpm)) {
                            mMaxBpm = String.valueOf(heartRate);
                        }

                        mStringBPMValues = mStringBPMValues + heartRate + ",";

                        drawAcceleration();

                    } else if (obj instanceof PulseOximeterData) {
                        pulseOximeterData = (PulseOximeterData) obj;
                        pulseOximeterData.getPoFastValue();
//                        mStringSPO2Values = mStringSPO2Values + String.format("%.02f", pulseOximeterData.getPoNormalValue() * 100) + ",";
                        mStringSPO2Values = mStringSPO2Values + String.format("%.02f",(float) (((3*255+( pulseOximeterData.getPoNormalValue() * 100))/10)/10)+90) + ",";//23rd June,2021
                    }
                }
            }

            @Override
            public void dataStatus(int status) {

            }
        }, TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCreateView = inflater.inflate(R.layout.fragment_activity_mode, container, false);

        setUpViews();
        setUpOnClickListeners();


        return mCreateView;
    }

    private void setUpViews() {
        mButtonStart = mCreateView.findViewById(R.id.fragment_activity_mode_start_button);
        mButtonStop = mCreateView.findViewById(R.id.fragment_activity_mode_stop_button);


        mTextViewTotalStep = (TextView) mCreateView.findViewById(R.id.fragment_activity_mode_text_steps_value);
//        mTextViewDistanceMeter = (TextView) mCreateView.findViewById(R.id.fragment_bpm_txt_distance_meter);
        mTextViewTotalMin = (TextView) mCreateView.findViewById(R.id.fragment_activity_mode_text_time_value);
        mTextViewTotalMet = (TextView) mCreateView.findViewById(R.id.fragment_activity_mode_text_met_value);
        mTextViewTotalSpo2 = (TextView) mCreateView.findViewById(R.id.fragment_activity_mode_spo_value);
        mTextViewTotalBPM = (TextView) mCreateView.findViewById(R.id.fragment_activity_mode_bpm_value);
        mTextViewKmOrMile = (TextView) mCreateView.findViewById(R.id.fragment_activity_mode_text_km);
//        mImageViewProgress = (ImageView) mCreateView.findViewById(R.id.fragment_activity_mode_chart_progress);
        mProgressBar = (ProgressBar) mCreateView.findViewById(R.id.fragment_activity_mode_progress_bar);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(mMainActivity, R.color.mandatory_color), PorterDuff.Mode.SRC_IN);

        mLineChart = (LineChart) mCreateView.findViewById(R.id.fragment_activity_mode_line_chart);

        mLineChart.setNoDataText("");

        mLineChart.getAxisLeft().setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_black)); // left y-axis
        mLineChart.getXAxis().setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_black));

//        AnimationDrawable spinner = (AnimationDrawable) mProgressBar.getBackground();
//        spinner.start();
    }

    private void setUpOnClickListeners() {
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDialog();
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delaying for 2 second to user tapping this button continuously
                if (SystemClock.elapsedRealtime() - lastTimeStopButtonClicked < defaultInterval) {
                    System.out.println("Delay WORKED On stop button activity mode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    return;
                }
                lastTimeStopButtonClicked = SystemClock.elapsedRealtime();
                if (MainActivity.connectedDevice != null) {
                    if (isBPMStart) {//April-2021

                        MainActivity.resetTabLock();
//                    XAxis xAxis = mLineChart.getXAxis();
//                    xAxis.setAxisMinimum(0);
//                    YAxis yAxis = mLineChart.getAxisLeft();
//                    yAxis.setAxisMinimum(30);
//                    mLineChart.zoomToCenter(0,100);

                        stopReceiveData();
                    } else {
                        Toast.makeText(mMainActivity, "Please wait for the process to start.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });
    }

    private void startBPM() {
        if (MainActivity.connectedDevice != null) {

            MainActivity.enableControl("PR");

            reset();

            mMaxBpm = null;//

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    startReceiveData();

                    isBPMStart = true;


                }
            }, 1500);


            mProgressBar.setVisibility(View.VISIBLE);

            makeStartButtonClickable(false);
        } else {
            mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
        }
    }

    private void makeStartButtonClickable(boolean isClickable) {
        if (isClickable) {
            mButtonStart.setBackground(mBackgroundSkyBlueStartStopButtons);
            mButtonStart.setClickable(true);

            mButtonStop.setBackground(mBackgroundGrayStartStopButtons);
            mButtonStop.setClickable(false);
        } else {
            mButtonStart.setBackground(mBackgroundGrayStartStopButtons);
            mButtonStart.setClickable(false);

            mButtonStop.setBackground(mBackgroundSkyBlueStartStopButtons);
            mButtonStop.setClickable(true);
        }

    }

    private void showEventDialog() {
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(mMainActivity);
        View layoutView = mMainActivity.getLayoutInflater().inflate(R.layout.dialog_event, null);

        mAlertDialogStartButton = ((TextView) layoutView.findViewById(R.id.dialog_event_start_button));

        mAlertDialogEventRunningButton = ((TextView) layoutView.findViewById(R.id.dialog_event_running_label));
        mAlertDialogEventCyclingButton = ((TextView) layoutView.findViewById(R.id.dialog_event_cycling_label));
        mAlertDialogEventWalkingButton = ((TextView) layoutView.findViewById(R.id.dialog_event_walking_label));
        mAlertDialogEventWorkoutButton = ((TextView) layoutView.findViewById(R.id.dialog_event_workout_label));
        mAlertDialogEventGolfButton = ((TextView) layoutView.findViewById(R.id.dialog_event_golf_label));
        mAlertDialogEventHikingButton = ((TextView) layoutView.findViewById(R.id.dialog_event_hiking_label));
        mAlertDialogEventYogaButton = ((TextView) layoutView.findViewById(R.id.dialog_event_yoga_label));
        mAlertDialogEventGardeningButton = ((TextView) layoutView.findViewById(R.id.dialog_event_gardening_label));
        mAlertDialogEventTennisButton = ((TextView) layoutView.findViewById(R.id.dialog_event_tennis_label));
        mAlertDialogEventWorkShiftButton = ((TextView) layoutView.findViewById(R.id.dialog_event_work_shift_label));
        mAlertDialogEventOtherButton = ((TextView) layoutView.findViewById(R.id.dialog_event_other_label));

        EmojiTextView mAlertDialogEventRunningIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_running_icon));
        EmojiTextView mAlertDialogEventCyclingIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_cycling_icon));
        EmojiTextView mAlertDialogEventWalkingIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_walking_icon));
        EmojiTextView mAlertDialogEventWorkoutIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_workout_icon));
        EmojiTextView mAlertDialogEventGolfIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_golf_icon));
        EmojiTextView mAlertDialogEventHikingIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_hiking_icon));
        EmojiTextView mAlertDialogEventYogaIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_yoga_icon));
        EmojiTextView mAlertDialogEventGardeningIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_gardening_icon));
        EmojiTextView mAlertDialogEventTennisIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_tennis_icon));
        EmojiTextView mAlertDialogEventWorkShiftIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_work_shift_icon));
        EmojiTextView mAlertDialogEventOtherIcon = ((EmojiTextView) layoutView.findViewById(R.id.dialog_event_other_icon));

        mAlertDialogEventRunningIcon.setText(ActivityEvent.RUNNING.emoji);
        mAlertDialogEventCyclingIcon.setText(ActivityEvent.CYCLING.emoji);
        mAlertDialogEventWalkingIcon.setText(ActivityEvent.WALKING.emoji);
        mAlertDialogEventWorkoutIcon.setText(ActivityEvent.WORKOUT.emoji);
        mAlertDialogEventGolfIcon.setText(ActivityEvent.GOLF.emoji);
        mAlertDialogEventHikingIcon.setText(ActivityEvent.HIKING.emoji);
        mAlertDialogEventYogaIcon.setText(ActivityEvent.YOGA.emoji);
        mAlertDialogEventGardeningIcon.setText(ActivityEvent.GARDENING.emoji);
        mAlertDialogEventTennisIcon.setText(ActivityEvent.TENNIS.emoji);
        mAlertDialogEventWorkShiftIcon.setText(ActivityEvent.WORK_SHIFT.emoji);
        mAlertDialogEventOtherIcon.setText(ActivityEvent.OTHER.emoji);


        mAlertDialogEventRunningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.RUNNING);
            }
        });
        mAlertDialogEventCyclingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.CYCLING);
            }
        });
        mAlertDialogEventWalkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.WALKING);
            }
        });
        mAlertDialogEventWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.WORKOUT);
            }
        });
        mAlertDialogEventGolfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.GOLF);
            }
        });
        mAlertDialogEventHikingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.HIKING);
            }
        });
        mAlertDialogEventYogaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.YOGA);
            }
        });
        mAlertDialogEventGardeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.GARDENING);
            }
        });
        mAlertDialogEventTennisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.TENNIS);
            }
        });
        mAlertDialogEventWorkShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.WORK_SHIFT);
            }
        });
        mAlertDialogEventOtherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEvent(ActivityEvent.OTHER);
            }
        });


        mAlertDialogStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    MainActivity.setTabLock(MainActivity.TabType.ACTIVITY_MODE);
                    startBPM();
                }
            }
        });

        dialogBuilder.setView(layoutView);


        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.getWindow().getAttributes().windowAnimations =
//                R.style.AppDialogTheme;
        alertDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
//        }

        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    private void selectEvent(ActivityEvent event) {
        selectedEvent = event;

        switch (event) {
            case RUNNING:
                mAlertDialogEventRunningButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorWhite);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;

            case CYCLING:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorWhite);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;

            case WALKING:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorWhite);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;

            case WORKOUT:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorWhite);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;

            case GOLF:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorWhite);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;
            case HIKING:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorWhite);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;
            case YOGA:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorWhite);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;
            case GARDENING:
                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorWhite);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;
            case TENNIS:

                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorWhite);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;

            case WORK_SHIFT:

                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundSkyBlue);
                mAlertDialogEventOtherButton.setBackground(mBackgroundGray);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorWhite);
                mAlertDialogEventOtherButton.setTextColor(mColorGray);
                break;


            case OTHER:

                mAlertDialogEventRunningButton.setBackground(mBackgroundGray);
                mAlertDialogEventCyclingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWalkingButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkoutButton.setBackground(mBackgroundGray);
                mAlertDialogEventGolfButton.setBackground(mBackgroundGray);
                mAlertDialogEventHikingButton.setBackground(mBackgroundGray);
                mAlertDialogEventYogaButton.setBackground(mBackgroundGray);
                mAlertDialogEventGardeningButton.setBackground(mBackgroundGray);
                mAlertDialogEventTennisButton.setBackground(mBackgroundGray);
                mAlertDialogEventWorkShiftButton.setBackground(mBackgroundGray);
                mAlertDialogEventOtherButton.setBackground(mBackgroundSkyBlue);

                mAlertDialogEventRunningButton.setTextColor(mColorGray);
                mAlertDialogEventCyclingButton.setTextColor(mColorGray);
                mAlertDialogEventWalkingButton.setTextColor(mColorGray);
                mAlertDialogEventWorkoutButton.setTextColor(mColorGray);
                mAlertDialogEventGolfButton.setTextColor(mColorGray);
                mAlertDialogEventHikingButton.setTextColor(mColorGray);
                mAlertDialogEventYogaButton.setTextColor(mColorGray);
                mAlertDialogEventGardeningButton.setTextColor(mColorGray);
                mAlertDialogEventTennisButton.setTextColor(mColorGray);
                mAlertDialogEventWorkShiftButton.setTextColor(mColorGray);
                mAlertDialogEventOtherButton.setTextColor(mColorWhite);
                break;
        }
    }

    public enum ActivityEvent {
        RUNNING("Running", 0x1F3C3),
        CYCLING("Cycling", 0x1F6B4),
        WALKING("Walking", "\uD83D\uDEB6"),
        WORKOUT("Workout", "\uD83C\uDFCB️\u200D♀️"),
        GOLF("Golf", "\uD83C\uDFCC"),
        HIKING("Hiking", "\uD83E\uDD7E"),
        YOGA("Yoga", "\uD83E\uDDD8\uD83C\uDFFB\u200D♂️"),
        GARDENING("Gardening", "\uD83E\uDDD1\uD83C\uDFFB\u200D\uD83C\uDF3E"),
        TENNIS("Tennis", "\uD83C\uDFBE"),
        WORK_SHIFT("Work Shift", "\uD83D\uDCBC"),
        OTHER("Other", "\u27B0");

        private final String value;
        private final String emoji;
//        private final int emojiUnicode;

        // getter method
        public String getValue() {
            return this.value;
        }

        public String getEmoji() {
            return this.emoji;
        }

//        public int getEmojiUnicode() {
//            return this.emojiUnicode;
//        }

        // enum constructor - cannot be public or protected
        ActivityEvent(String value, String emoji) {
            this.value = value;
            this.emoji = emoji;
        }

        ActivityEvent(String value, int emojiUnicode) {

            this.value = value;
            this.emoji = new String(Character.toChars(emojiUnicode));
        }

    }
//    ************************************************************************************************

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
        leftAxis.setAxisLineColor(ContextCompat.getColor(mMainActivity, R.color.drawer_icon_tint_color));
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mLineChart.getAxisRight().setEnabled(false);

        // add data
        if (bpmDataQueue != null && bpmDataQueue.size() > 0) {
            setData(45, 100);
        }

        mMainActivity.runOnUiThread(new Runnable() {
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
            set1.setColor(ContextCompat.getColor(mMainActivity, R.color.color_active_tab));
            set1.setCircleColor(ContextCompat.getColor(mMainActivity, R.color.color_active_tab));
            set1.setValueTextColor(ContextCompat.getColor(mMainActivity, R.color.color_black));
//            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(0f, 0f, 0f);
            set1.enableDashedHighlightLine(0f, 0f, 0f);
            set1.setColor(ContextCompat.getColor(mMainActivity, R.color.color_active_tab));
            set1.setCircleColor(ContextCompat.getColor(mMainActivity, R.color.color_active_tab));
            set1.setLineWidth(1f);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(false);
            set1.setValueTextColor(ContextCompat.getColor(mMainActivity, R.color.color_black));
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

        mProgressBar.setVisibility(View.GONE);

        isBPMStart = false;
//
//        tabSelected();

        makeStartButtonClickable(true);

//        mButtonStop.setBackgroundResource(R.drawable.button_background_disabled);
//        mButtonStop.setTextColor(getResources().getColor(R.color.button_enabled_color));
//        mButtonStop.setClickable(false);
//
//        mButtonStart.setBackgroundResource(R.drawable.button_background_enabled);
//        mButtonStart.setTextColor(getResources().getColor(R.color.color_white));
//        mButtonStart.setClickable(true);

        updateIntoMainActivity();

        /* stop update activity timer */
        stopUpdateActivity();
//        stopDrawAcceleration();

        mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableMainActivityTable + " ORDER BY " + DBHelper.mMain_ACTIVITY_ID + " DESC LIMIT 1");

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
            mStringParentID = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_ID);
            mStringStartDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
        }


        mAverageMet = mMainActivity.getAverageValue(mStringMetValue);
        mAverageBpm = mMainActivity.getAverageValue(mStringBPMValues);
        Log.d(TAG, "mAverageBpm=" + mAverageBpm);
        Log.d(TAG, "mMaxBpm=" + mMaxBpm);
        Log.d(TAG, "mAverageMet=" + mAverageMet);

        insertIntoBPMDetails();
        insertIntoMETDetails();
        insertIntoStepDetails();
        insertIntoDistanceDetails();
        insertIntoSPO2Details();
        insertIntoTimeTable();

        mMainActivity.syncTheTables();//March-2021
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
//        this.stepsString = String.valueOf(this.activityMeterData.getTotalSteps());
        if (isMetric) {
            this.stepsString = getString(R.string.two_digit_after_decimal, ((double) this.activityMeterData.getTotalSteps() / 1000.0));//Dividing by 1000 for making it km.
        } else {
            this.stepsString = getString(R.string.two_digit_after_decimal, ((double) this.activityMeterData.getTotalSteps() * 0.00062137));//Multiplying for making it mile.
        }
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

                if (pulseOximeterData != null)
                setTextInSPO2Value("" + String.format("%.02f",(float) (((3*255+( pulseOximeterData.getPoNormalValue() * 100))/10)/10)+90));//+"\nSpO2");
//                    setTextInSPO2Value("" + String.format("%.02f", pulseOximeterData.getPoNormalValue() * 100));//+"\nSpO2");

                mTextViewTotalStep.setText(stepsString);
//                mTextViewDistanceMeter.setText(distanceString);
                mTextViewTotalMet.setText("" + metsString);//+ "\nMET");
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
        mContentValues.put(DBHelper.mMain_ACTIVITY_ACTIVITY_TYPE, selectedEvent.getValue());
        mContentValues.put(DBHelper.mMain_ACTIVITY_RANDOM_NUMBER, getRendomNo());

        Log.d(TAG, "insertIntoMainActivity() selectedEvent=" + selectedEvent.getValue());

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
        mContentValues.put(DBHelper.mBPM_DETAILS_MAX_BPM, mMaxBpm);//2021
        mContentValues.put(DBHelper.mBPM_DETAILS_AVERAGE_BPM, mAverageBpm);//2021
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
        mContentValues.put(DBHelper.mMET_DETAILS_Average_Met, mAverageMet);//2021
        if (selectedEvent == null) {
            mContentValues.put(DBHelper.mMET_DETAILS_ACTIVITY_TYPE, String.valueOf((Object) null));//2021
        } else {
            mContentValues.put(DBHelper.mMET_DETAILS_ACTIVITY_TYPE, selectedEvent.getValue());//2021
        }
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
    public void onDestroy() {
        if (wl != null) {
            wl.release();
        }
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();

    }

    public void reset() {
        if (
                mLineChart != null && mTextViewTotalSpo2 != null && mTextViewTotalBPM != null && mTextViewTotalStep != null && mTextViewTotalMet != null && mTextViewTotalMin != null && mMainActivity != null
        ) {
            mLineChart.clear();
            mTextViewTotalBPM.setText(String.valueOf(0));
            setTextInSPO2Value(String.valueOf(0));

            if (mUtility.getAppPrefString(Constant.PREFS_METRIC_OR_IMPERIAL) != null && mUtility.getAppPrefString(Constant.PREFS_METRIC_OR_IMPERIAL).equalsIgnoreCase(Constant.UNIT_IMPERIAL)) {
                mTextViewKmOrMile.setText(mMainActivity.getString(R.string.text_mile));
                isMetric = false;
            } else {
                mTextViewKmOrMile.setText(mMainActivity.getString(R.string.text_km));
                isMetric = true;
            }

            mTextViewTotalStep.setText(String.valueOf(0));
            mTextViewTotalMet.setText(String.valueOf(0));
            mTextViewTotalMin.setText("0:0");
        }

    }

    private void setTextInSPO2Value(String text) {
        setTextSizeOfSPO2(text);
        mTextViewTotalSpo2.setText(text);

    }

    private void setTextSizeOfSPO2(String text) {
//        Log.d("Ankit","setTextSizeOfBPM)_length="+text.length());
        if (text.length() <= 2) {
            mTextViewTotalSpo2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mMainActivity.getResources().getDimension(R.dimen._49ssp));
        } else if (text.length() > 2 && text.length() < 6) {
            mTextViewTotalSpo2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mMainActivity.getResources().getDimension(R.dimen._40ssp));
        } else {
            mTextViewTotalSpo2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mMainActivity.getResources().getDimension(R.dimen._35ssp));
        }

    }
}