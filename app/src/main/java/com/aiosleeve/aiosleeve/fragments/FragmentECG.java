package com.aiosleeve.aiosleeve.fragments;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.VO.VoTakenMedine;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.toshiba.semicon.hcsdp.brighton.controllib.ActivityMeterData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.BleConstants;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.HeartRateData;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.PulseWaveData;


public class FragmentECG extends AppCompatActivity {
    public static final String TAG="FragmentECG";
     /* variables for sensor data */
    /**
     * Received activity meter data (includes total steps, total distance, and total energy consumption).
     */
    private ActivityMeterData activityMeterData = null;

    /**
     * Received heart rate data.
     */
    private int heartRate = -1;
    int globalHRVValue = 0;
    int globalLastRRIntervel = 0;

    /**
     * Status flag that indicates that it has received heart rate data.
     */
    private boolean hr_flag = false;

    /**
     * Update acceleration graph timer interval value. interval is 200msec.
     */
    private static final int ACCEL_TIMER_INTERVAL = (int) (0.2 * 1000);

    /**
     * Object for activity value update exclusive
     */
    private final Object ACTIVITY_UPDATE_LOCK = new Object();
    /**
     * Object for custom mets data exclusive.
     */
    private static final Object CUSTOM_METS_LOCK = new Object();

    /**
     * Draw acceleration graph timer.
     */
    private Timer drawAccelTimer = null;

    /**
     * ui handler
     */
    private Handler handler;

    /* variables for activity */
    /**
     * Update activity timer.
     */
    private Timer updateActivityTimer = null;

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
    DataHolder mDataHolder_Medication;

    RecyclerView mRecyclerViewGroupChannel;

    private Handler mainHandler;

    int intTotlaTime = 0;

    String mStringBPMValues = "";
    String mStringECGValues = "";
    String mStringECGTimeDiff = "";
    String mStringStepValues = "";
    String mStringDistancesValue = "";
    String mStringMetValue = "";
    String mStringStartDateTime = "";
    String mStringMedicationStartDateTime = "";
    String mStringMedicationStartDate = "";
    String mStringEndDateTime = "";
    String mStringParentID = "";
    String mStringHRVValue = "";
    String mStringHRVTestValue = "";

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TextView mTextViewTotalStep;
    private TextView mTextViewDistanceMeter;
    private TextView mTextViewTotalMin;
    private TextView mTextViewTotalMet;
    private TextView mTextViewTotalHrv;
    private TextView mTextViewTotalBPM;
    private TextView mTextViewNoMedicineLabel;

    private ImageView mImageViewProgress;

    private Button mButtonStart;
    private Button mButtonStop;
    private Button mButtonHRV;
    private Button mButtonTakenMedicine;
    private Button mButtonOK;
    private Button mButtonCancel;
    private Button mButtonDismiss;
    private Button mButtonSubmit;

    private CheckBox mCheckBoxMedicine1;
    private CheckBox mCheckBoxMedicine2;
    private CheckBox mCheckBoxMedicine3;

    LineChart mLineChartECG;

    //LineChartView mLineChartView;

    //HRV Dialog
    Dialog mDialogHRV;

    TextView mTextViewHeaderMessage;
    TextView mTextViewResultMessage;
    TextView mTextViewMainMessage;

    RatingBar mRatingBarHeader;

    CountDownTimer mCountDownTimer;

    boolean isCountDownRunning = false;

    private LinkedList<Integer> ecgDataQueue = new LinkedList<Integer>();

    /**
     * Difference Time list of received pulse wave data.
     */
    private LinkedList<Integer> diffTimeQueueP = new LinkedList<Integer>();
    /**
     * value list of received pulse wave data.
     */
//    private LinkedList<Object> pulseQueue = new LinkedList<>();

    private static final int PULSE_WAVE_TIMER_INTERVAL = (int) (0.2 * 1000);
    /**
     * Update activity timer interval value. interval is 1000msec.
     */
    private static final int UPDATE_ACTIVITY_TIMER_INTERVAL = 1000;


    /* color code for pulse wave graph */
    /**
     * Draw pulse wave graph timer.
     */
    private Timer drawPulseWaveTimer = null;

    Utility mUtility;

    public LinearLayout mLinearLayoutTabsHOME;
    public LinearLayout mLinearLayoutTabsBPM;
    public LinearLayout mLinearLayoutTabsSLEEp;
    public LinearLayout mLinearLayoutTabsSETTINGS;

    public ImageView mImageViewHome;
    public ImageView mImageViewBPM;
    public ImageView mImageViewSLEEp;
    public ImageView mImageViewSETTING;
    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;

    public TextView mTextViewHome;
    public TextView mTextViewBPM;
    public TextView mTextViewSLEEP;
    public TextView mTextViewSETTING;

    public PowerManager.WakeLock wl;
    boolean isECGStart = false;
    private Dialog openDialog;
    private ArrayList<VoTakenMedine> mArrayListMyGroup = new ArrayList<>();
    private MyMedicineAdapter mMymedicineAdpter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg);

        handler = new Handler();
        mDbHelper = new DBHelper(FragmentECG.this);
        mainHandler = new Handler(Looper.getMainLooper());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mUtility = new Utility(FragmentECG.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextViewTotalStep = (TextView) findViewById(R.id.fragment_ecg_txt_total_steps);
        mTextViewDistanceMeter = (TextView) findViewById(R.id.fragment_ecg_txt_distance_meter);
        mTextViewTotalMin = (TextView) findViewById(R.id.fragment_ecg_txt_time_min);
        mTextViewTotalMet = (TextView) findViewById(R.id.fragment_ecg_txt_met);
        mTextViewTotalHrv = (TextView) findViewById(R.id.fragment_ecg_txt_hrv);
        mTextViewTotalBPM = (TextView) findViewById(R.id.fragment_ecg_txt_bpm);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_ecg_admin_imageview_back);
        mImageViewAddStory = (ImageView) findViewById(R.id.fragment_ecg_admin_imageview_add_story);

//        mLineChartView = (LineChartView) findViewById(R.id.pulse_rate_graph_view);
        mLineChartECG = (LineChart) findViewById(R.id.pulse_rate_graph_view);
        mLineChartECG.setNoDataText("");

        mLineChartECG.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.color_white)); // left y-axis
        mLineChartECG.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.color_white));
        mLineChartECG.getXAxis().setGranularityEnabled(false);
        mLineChartECG.getXAxis().setGranularity(1.0f);

        mLineChartECG.getAxisLeft().setDrawGridLines(false);
        mLineChartECG.getAxisRight().setDrawGridLines(false);

        mButtonStart = (Button) findViewById(R.id.fragment_ecg_button_start);
        mButtonStop = (Button) findViewById(R.id.fragment_ecg_button_stop);
        mButtonHRV = (Button) findViewById(R.id.fragment_ecg_button_hrv);
        mButtonTakenMedicine = (Button) findViewById(R.id.fragment_ecg_button_taken_medicine);

        mLinearLayoutTabsHOME = (LinearLayout) findViewById(R.id.activity_main_linear_tab_home);
        mLinearLayoutTabsBPM = (LinearLayout) findViewById(R.id.activity_main_linear_bpm);
        mLinearLayoutTabsSLEEp = (LinearLayout) findViewById(R.id.activity_main_linear_tab_sleep);
        mLinearLayoutTabsSETTINGS = (LinearLayout) findViewById(R.id.activity_main_linear_tab_setting);

        mImageViewHome = (ImageView) findViewById(R.id.activity_main_imageview_home);
        mImageViewBPM = (ImageView) findViewById(R.id.activity_main_imageview_bpm);
        mImageViewSLEEp = (ImageView) findViewById(R.id.activity_main_imageview_sleep);
        mImageViewSETTING = (ImageView) findViewById(R.id.activity_main_imageview_setting);

        mTextViewHome = (TextView) findViewById(R.id.activity_main_textview_home);
        mTextViewBPM = (TextView) findViewById(R.id.activity_main_textview_bpm);
        mTextViewSLEEP = (TextView) findViewById(R.id.activity_main_textview_sleep);
        mTextViewSETTING = (TextView) findViewById(R.id.activity_main_textview_setting);

        mButtonStop.setClickable(false);
        mImageViewProgress = (ImageView) findViewById(R.id.fragment_ecg_chart_progress);

        AnimationDrawable spinner = (AnimationDrawable) mImageViewProgress.getBackground();
        spinner.start();

        mLinearLayoutTabsHOME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLinearLayoutTabsSLEEp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentECG.this, FragmentSleep.class);
                startActivity(mIntent);
                finish();
            }
        });

        mLinearLayoutTabsBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentECG.this, FragmentBPM.class);
                startActivity(mIntent);
                finish();
            }
        });

        mLinearLayoutTabsSETTINGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FragmentECG.this, FragmentSettings.class);
                startActivity(mIntent);
                finish();
            }
        });

        mImageViewAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isECGStart) {
                    mUtility.errorDialog(getResources().getString(R.string.alert_ecg_start));
                } else {
                    Intent mIntent = new Intent(FragmentECG.this, FragmentECGList.class);
                    startActivity(mIntent);
                }
            }
        });
//
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isECGStart) {
                    mUtility.errorDialog(getResources().getString(R.string.alert_ecg_start));
                } else {
                    onBackPressed();
                }
            }
        });

        mButtonTakenMedicine.setBackgroundResource(R.drawable.button_background_enabled);
        mButtonTakenMedicine.setTextColor(getResources().getColor(R.color.color_white));
        mButtonTakenMedicine.setVisibility(View.GONE);

        mButtonTakenMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedicationDialog();
            }
        });

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (MainActivity.connectedDevice != null) {

                        MainActivity.enableControl("HR");

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startReceiveData();
                            }
                        }, 1200);

                        isECGStart = true;
                        tabUnselected();

                        mImageViewProgress.setVisibility(View.VISIBLE);

                        mButtonStart.setBackgroundResource(R.drawable.button_background_disabled);
                        mButtonStart.setTextColor(getResources().getColor(R.color.button_enabled_color));
                        mButtonStart.setClickable(false);


                        mButtonStop.setBackgroundResource(R.drawable.button_background_enabled);
                        mButtonStop.setTextColor(getResources().getColor(R.color.color_white));
                        mButtonStop.setClickable(true);

                        mButtonHRV.setBackgroundResource(R.drawable.button_background_enabled);
                        mButtonHRV.setTextColor(getResources().getColor(R.color.color_white));
                        mButtonHRV.setClickable(true);

                    } else {
                        mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (MainActivity.connectedDevice != null) {
                        stopReceiveData();
                        isECGStart = false;
                        tabSelected();
                    } else {
                        mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonHRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    OpenHRVDialog();
                } else {
                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                }
            }
        });

    }

    public void tabSelected() {

        mImageViewHome.setImageResource(R.drawable.icon_home_selected);
        mImageViewBPM.setImageResource(R.drawable.icon_bpm_selected);
        mImageViewSLEEp.setImageResource(R.drawable.icon_sleep_selected);
        mImageViewSETTING.setImageResource(R.drawable.icon_setting_selected);

        mLinearLayoutTabsHOME.setClickable(true);
        mLinearLayoutTabsBPM.setClickable(true);
        mLinearLayoutTabsSLEEp.setClickable(true);
        mLinearLayoutTabsSETTINGS.setClickable(true);

        mTextViewHome.setTextColor(getResources().getColor(R.color.color_white));
        mTextViewBPM.setTextColor(getResources().getColor(R.color.color_white));
        mTextViewSLEEP.setTextColor(getResources().getColor(R.color.color_white));
        mTextViewSETTING.setTextColor(getResources().getColor(R.color.color_white));
    }

    public void tabUnselected() {

        mImageViewHome.setImageResource(R.drawable.icon_home_unselect);
        mImageViewBPM.setImageResource(R.drawable.icon_bpm_unselected);
        mImageViewSLEEp.setImageResource(R.drawable.icon_sleep_unselected);
        mImageViewSETTING.setImageResource(R.drawable.icon_setting_unselected);

        mLinearLayoutTabsHOME.setClickable(false);
        mLinearLayoutTabsBPM.setClickable(false);
        mLinearLayoutTabsSLEEp.setClickable(false);
        mLinearLayoutTabsSETTINGS.setClickable(false);

        mTextViewHome.setTextColor(getResources().getColor(R.color.color_inactive_tab));
        mTextViewBPM.setTextColor(getResources().getColor(R.color.color_inactive_tab));
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
            public void onConnect(String devicesName, String devicesAddress) {

            }

            @Override
            public void onDisconnect(String devicesName, String devicesAddress) {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                mUtility.errorDialog(getResources().getString(R.string.alert_disconnect_device));
                                stopReceiveData();
                                isECGStart = false;
                                tabSelected();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
                try {
                    synchronized (DevicesStatus.RECEIVE_SENSOR_DATA_LOCK) {
                        if ((obj instanceof ActivityMeterData)) {
                            activityMeterData = (ActivityMeterData) obj;
                        } else if (obj instanceof HeartRateData) {

                            HeartRateData heartRateData = (HeartRateData) obj;

                            heartRate = heartRateData.getHeartRate();
                            hr_flag = true; /* set received flag */

                            if (isCountDownRunning) {
                                mStringHRVTestValue = mStringHRVTestValue + globalHRVValue + ",";
                            }

                            int rrInteravalValue[] = heartRateData.getRRIntervalValues();

                            if (rrInteravalValue != null && rrInteravalValue.length > 0)
                                globalHRVValue = globalLastRRIntervel - rrInteravalValue[rrInteravalValue.length - 1];

                            if (globalHRVValue < 0) {
                                globalHRVValue = Math.abs(globalHRVValue);
                            }

                            mStringHRVValue = mStringHRVValue + globalHRVValue + ",";

                            if (rrInteravalValue != null && rrInteravalValue.length > 0)
                                globalLastRRIntervel = rrInteravalValue[rrInteravalValue.length - 1];

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    mTextViewTotalHrv.setText("" + globalHRVValue + "\nHRV");
                                    mTextViewTotalBPM.setText("" + heartRate + "\nBPM");
                                }
                            });

                            mStringBPMValues = mStringBPMValues + heartRate + ",";

                        } else if (obj instanceof PulseWaveData) {
                            PulseWaveData pulseWaveData = (PulseWaveData) obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    mImageViewProgress.setVisibility(View.GONE);
                                }
                            });

                            mStringECGValues = mStringECGValues + pulseWaveData.getValue() + ",";
                            mStringECGTimeDiff = mStringECGTimeDiff + pulseWaveData.getDiffTime() + ",";
                            ecgDataQueue.add(pulseWaveData.getValue());

                            if(ecgDataQueue.size() > 6000) {
                                ecgDataQueue.subList(0,2000).clear();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

//                                        mLineChartECG.invalidate();
//                                        mLineChartECG.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void dataStatus(int status) {

            }
        },TAG);

        initializePulseWaveGraph();

        mUtility.changeStatusbarColor(R.color.custom_header_color);
    }


    @Override
    public void onBackPressed() {

        if (isECGStart) {
            mUtility.errorDialog(getResources().getString(R.string.alert_ecg_start));
            return;
        }
        super.onBackPressed();
    }

    public void showMedicationDialog() {

        try {
            mDataHolder_Medication = mDbHelper.read("SELECT * from " + DBHelper.mTableMedication);
            mArrayListMyGroup.clear();
            if (mDataHolder_Medication != null && mDataHolder_Medication.get_Listholder() != null && mDataHolder_Medication.get_Listholder().size() > 0) {
                VoTakenMedine mvoTakenMedine;

                for (int i = 0; i < mDataHolder_Medication.get_Listholder().size(); i++) {
                    mvoTakenMedine = new VoTakenMedine();
                    mvoTakenMedine.setMedicine_name(mDataHolder_Medication.get_Listholder().get(i).get(DBHelper.mTable_Medication_name));
                    mvoTakenMedine.setmID(mDataHolder_Medication.get_Listholder().get(i).get(DBHelper.mTaken_Medication_Table_ID));
                    mArrayListMyGroup.add(mvoTakenMedine);
                }
            }

            openDialog = new Dialog(FragmentECG.this);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            openDialog.setContentView(R.layout.pop_taken_medication_check_box_layout);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            openDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mRecyclerViewGroupChannel = (RecyclerView) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_recyclerview);
            mButtonSubmit = (Button) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_button_ok);
            mButtonDismiss = (Button) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_button_cancel);

//            mTextViewNoMedicineLabel = (TextView) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_no_medicine_text);

            mMymedicineAdpter = new MyMedicineAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(FragmentECG.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewGroupChannel.setLayoutManager(manager);
            mRecyclerViewGroupChannel.setAdapter(mMymedicineAdpter);
            mMymedicineAdpter.notifyDataSetChanged();

            if (mArrayListMyGroup !=null && mArrayListMyGroup.size()==0){
                mTextViewNoMedicineLabel.setVisibility(View.VISIBLE);
                mRecyclerViewGroupChannel.setVisibility(View.INVISIBLE);
            }else {
                mTextViewNoMedicineLabel.setVisibility(View.GONE);
                mRecyclerViewGroupChannel.setVisibility(View.VISIBLE);
            }

            mStringMedicationStartDateTime = mUtility.getDateTime();
            mStringMedicationStartDate = mUtility.getDate();
            mButtonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog.dismiss();

                    if (mArrayListMyGroup != null && mArrayListMyGroup.size()>0) {
                        for (int i = 0; i < mArrayListMyGroup.size(); i++) {
                            if (mArrayListMyGroup.get(i).isChecked()){
                                insertIntoMeditationTable(mArrayListMyGroup.get(i).getmID(),mArrayListMyGroup.get(i).getMedicine_name(),mUtility.getAppPrefString(Constant.PREFS_USER_ID),mStringMedicationStartDateTime,mStringMedicationStartDate);
                            }else {
                                Log.e("ERROR"," :( not Checked");
                            }
                        }

                    }
                }
            });

            mButtonDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog.dismiss();
                }
            });

            openDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MyMedicineAdapter extends RecyclerView.Adapter<MyMedicineAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_my_checkbox_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (mArrayListMyGroup != null && mArrayListMyGroup.size()>0){

                holder.mCheckBox.setText(mArrayListMyGroup.get(position).getMedicine_name());
                holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            if (isChecked){
                                mArrayListMyGroup.get(position).setChecked(true);
                            }else {
                                mArrayListMyGroup.get(position).setChecked(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mArrayListMyGroup.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CheckBox mCheckBox;
            public ViewHolder(View itemView) {
                super(itemView);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.raw_my_checkbox_item_checkbox);
            }
        }
    }
    public void insertIntoMeditationTable(String mId,String medication_name,String UserId,String DateNTime,String Date) {
        try {
            Log.e("DATA INSERT ","MID: "+mId+" MEDI_NAME: "+medication_name+" USERID: "+UserId+" DATENTIME: "+DateNTime+" DATE: "+Date);
            ContentValues mContentValues = new ContentValues();
            //   mContentValues.put(DBHelper.mTaken_Medication_Table_ID,mId);
            mContentValues.put(DBHelper.mTaken_Medication_Table_Medication_Name,medication_name);
            mContentValues.put(DBHelper.mTaken_Medication_Table_User_ID,UserId);
            mContentValues.put(DBHelper.mTaken_Medication_Date_N_Time,DateNTime);
            mContentValues.put(DBHelper.mTaken_Medication_Date,Date);
//
            mDbHelper.insertRecord(DBHelper.mTableTakenMedication, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateIntoMainActivity(String mId,String medication_name,String UserId,String DateNTime,String Date) {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mTaken_Medication_Table_ID,mId);
            mContentValues.put(DBHelper.mTaken_Medication_Table_Medication_Name,medication_name);
            mContentValues.put(DBHelper.mTaken_Medication_Table_User_ID,UserId);
            mContentValues.put(DBHelper.mTaken_Medication_Date_N_Time,DateNTime);
            mContentValues.put(DBHelper.mTaken_Medication_Date,Date);


            mDbHelper.updateRecord(DBHelper.mTableTakenMedication,
                    mContentValues, DBHelper.mTaken_Medication_Table_ID + " = ? AND "
                            + DBHelper.mTaken_Medication_Table_User_ID + "= ? AND "
                            + DBHelper.mTaken_Medication_Table_Medication_Name + "= ? AND "
                            + DBHelper.mTaken_Medication_Date_N_Time + "= ?",
                    new String[]{mId});

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void OpenHRVDialog() {

        mDialogHRV = new Dialog(FragmentECG.this, R.style.DialogAnimation);
        FragmentECG.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mDialogHRV.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogHRV.setContentView(R.layout.raw_hrv_popup);
        mDialogHRV.setCanceledOnTouchOutside(false);

        FragmentECG.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDialogHRV.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogHRV.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTextViewHeaderMessage = (TextView) mDialogHRV.findViewById(R.id.raw_hrv_popup_textview_textview_header);
        mTextViewResultMessage = (TextView) mDialogHRV.findViewById(R.id.raw_hrv_popup_textview_result);
        mTextViewMainMessage = (TextView) mDialogHRV.findViewById(R.id.raw_hrv_popup_textview_message);

        mButtonOK = (Button) mDialogHRV.findViewById(R.id.raw_hrv_popup_button_ok);
        mButtonCancel = (Button) mDialogHRV.findViewById(R.id.raw_hrv_popup_button_cancel);

        mRatingBarHeader = (RatingBar) mDialogHRV.findViewById(R.id.raw_hrv_popup_rating_header);

        mTextViewResultMessage.setVisibility(View.GONE);
        mTextViewMainMessage.setText(getResources().getString(R.string.text_hrv_popup_message));
        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isCountDownRunning) {
                        mStringHRVTestValue = "";
                        mTextViewResultMessage.setVisibility(View.GONE);
                        mTextViewHeaderMessage.setVisibility(View.VISIBLE);
                        mRatingBarHeader.setVisibility(View.GONE);
                        isCountDownRunning = true;
                        mTextViewMainMessage.setText(String.format("%02d", 2) + ":" + String.format("%02d", 0));

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mCountDownTimer = new CountDownTimer(121000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        int seconds = (int) (millisUntilFinished / 1000);
                                        int minutes = seconds / 60;
                                        seconds = seconds - (minutes * 60);
                                        final int finalMinutes = minutes;
                                        final int finalSeconds = seconds;

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //stuff that updates ui
                                                mTextViewMainMessage.setText(String.format("%02d", finalMinutes) + ":"
                                                        + String.format("%02d", finalSeconds));
                                            }
                                        });
                                    }

                                    public void onFinish() {

                                        isCountDownRunning = false;
                                        calculateHRVData();

                                        mButtonOK.setBackgroundResource(R.drawable.button_background_disabled);
                                        mButtonOK.setTextColor(getResources().getColor(R.color.color_white));
                                    }
                                }.start();
                            }
                        };
                        mainHandler.post(myRunnable);
                        mButtonOK.setBackgroundResource(R.drawable.button_background_enabled);
                        mButtonOK.setTextColor(getResources().getColor(R.color.button_disabled_color));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogHRV.dismiss();
                mButtonOK.setBackgroundResource(R.drawable.button_background_disabled);
                mButtonOK.setTextColor(getResources().getColor(R.color.color_white));

                isCountDownRunning = false;

                if (mCountDownTimer != null)
                    mCountDownTimer.cancel();
            }
        });
        mDialogHRV.show();
    }


    private void drawAcceleration() {
        try {
            // add data

            if (ecgDataQueue != null && ecgDataQueue.size() > 0) {
                setData();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLineChartECG.animateX(0);
                }
            });

            // get the legend (only possible after setting data)
            Legend l = mLineChartECG.getLegend();

            // modify the legend ...
            l.setForm(Legend.LegendForm.LINE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateHRVData() {
        try {
            int intHrvData = 0;
            float totalScrore = 0f;
            String strDisplay = "";

            if (!mStringHRVTestValue.equalsIgnoreCase("")) {
                String[] mStringHRVData = removeLastComma(mStringHRVTestValue).split(",");

                for (int i = 0; i < mStringHRVData.length; i++) {
                    intHrvData = intHrvData + Integer.parseInt(mStringHRVData[i]);
                }
                totalScrore = intHrvData / mStringHRVData.length;
            }

            if (totalScrore <= 0) {
                strDisplay = getResources().getString(R.string.alert_hrv_0);
            } else if (totalScrore > 0 && totalScrore <= 1) {
                strDisplay = getResources().getString(R.string.alert_hrv_1);
            } else if (totalScrore > 1 && totalScrore <= 2) {
                strDisplay = getResources().getString(R.string.alert_hrv_2);
            } else if (totalScrore > 2 && totalScrore <= 3) {
                strDisplay = getResources().getString(R.string.alert_hrv_3);
            } else if (totalScrore > 3 && totalScrore <= 4) {
                strDisplay = getResources().getString(R.string.alert_hrv_4);
            } else if (totalScrore > 4 && totalScrore <= 5) {
                strDisplay = getResources().getString(R.string.alert_hrv_5);
            } else {
                strDisplay = getResources().getString(R.string.alert_hrv_any);
            }

            mTextViewResultMessage.setText(""+totalScrore);

            mTextViewResultMessage.setVisibility(View.VISIBLE);
            mTextViewHeaderMessage.setVisibility(View.GONE);
            mRatingBarHeader.setVisibility(View.VISIBLE);
            mTextViewMainMessage.setText(strDisplay);
            if (totalScrore > 5) {
                mRatingBarHeader.setRating(5);
            } else {
                mRatingBarHeader.setRating(totalScrore);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String removeLastComma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void stopRecevingDataView() {
        try {
            isECGStart = false;
            tabSelected();

            mButtonStop.setBackgroundResource(R.drawable.button_background_disabled);
            mButtonStop.setTextColor(getResources().getColor(R.color.button_enabled_color));
            mButtonStop.setClickable(false);

            mButtonHRV.setBackgroundResource(R.drawable.button_background_disabled);
            mButtonHRV.setTextColor(getResources().getColor(R.color.button_enabled_color));
            mButtonHRV.setClickable(false);

            mImageViewProgress.setVisibility(View.GONE);

            mButtonStart.setBackgroundResource(R.drawable.button_background_enabled);
            mButtonStart.setTextColor(getResources().getColor(R.color.color_white));
            mButtonStart.setClickable(true);

            updateIntoMainActivity();

        /* stop update activity timer */
            stopUpdateActivity();
            stopDrawAcceleration();

        /* stop update pulse wave graph timer */

            mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableMainActivityTable + " ORDER BY " + DBHelper.mMain_ACTIVITY_ID + " DESC LIMIT 1");

            if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
                mStringParentID = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_ID);
                mStringStartDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
            }

            insertIntoBPMDetails();
            insertIntoMETDetails();
            insertIntoStepDetails();
            insertIntoDistanceDetails();
            insertIntoECGDetails();
            insertIntoTimeTable();
            insertIntoHRVTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start receive data from brighton
     */
    private void startReceiveData() {
        /* clear receive data */
        try {
            mStringBPMValues = "";
            mStringECGValues = "";
            mStringECGTimeDiff = "";
            mStringStepValues = "";
            mStringDistancesValue = "";
            mStringMetValue = "";
            intTotlaTime = 0;

            MainActivity.setMetsValue(0, false);

        /* set receive data types */
            ArrayList<BleConstants.NotificationDataType> dataTypeList = new ArrayList<BleConstants.NotificationDataType>() {{
                add(BleConstants.NotificationDataType.HEART_RATE);
                add(BleConstants.NotificationDataType.ACTIVITY_METER);
                add(BleConstants.NotificationDataType.ACC_VALUE_DATA);
                add(BleConstants.NotificationDataType.HYPNAGOGIC_DATA);
                add(BleConstants.NotificationDataType.PULSE_WAVE_DATA);
                add(BleConstants.NotificationDataType.PULSE_OXIMETER_DATA);
            }};

            MainActivity.enableMonitorize(true, dataTypeList);

            startUpdateActivity();
            insertIntoMainActivity();

         /* start update accel graph timer */
            startDrawPulseWave();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startUpdateActivity() {
        try {
            if (updateActivityTimer == null) {
                updateActivityTimer = new Timer();
                updateActivityTimer.scheduleAtFixedRate(new TimerTask() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setData() {
        try {
            ArrayList<Entry> values = new ArrayList<Entry>();

            for (int i = 0; i < ecgDataQueue.size(); i++) {
                values.add(new Entry(i, ecgDataQueue.get(i)));
            }

            LineDataSet set1;

            if (mLineChartECG.getData() != null && mLineChartECG.getData().getDataSetCount() > 0) {

                set1 = (LineDataSet) mLineChartECG.getData().getDataSetByIndex(0);

                if (values.isEmpty()) {
                    mLineChartECG.clear();
                } else {
                    // set data
                    set1.setValues(values);
                }

                mLineChartECG.getData().notifyDataChanged();
                mLineChartECG.notifyDataSetChanged();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLineChartECG.setVisibleXRangeMaximum(300);
                    }
                });

                mLineChartECG.moveViewToX(set1.getEntryCount());
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(values, "ECG Data");
//                set1.setDrawIcons(false);

                // set the line to be drawn like this "- - - - - -"
                set1.enableDashedLine(0f, 0f, 0f);
                set1.enableDashedHighlightLine(0f, 0f, 0f);
                set1.setColor(ContextCompat.getColor(FragmentECG.this, R.color.color_active_tab));
                set1.setCircleColor(Color.TRANSPARENT);
                set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                set1.setLineWidth(2f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(0f);
                set1.setDrawFilled(false);
                set1.setValueTextColor(Color.TRANSPARENT);
                set1.setFormLineWidth(2f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{0f, 0f}, 0f));
                set1.setFormSize(15);
                mLineChartECG.setVisibleXRangeMinimum(1f);

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

                mLineChartECG.setData(data);
                //  Log.e("PULSEWAVE", "SET DONE");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stop update acceleration graph timer.
     */
    private void stopDrawAcceleration() {
        if (this.drawAccelTimer != null) {
            this.drawAccelTimer.cancel();
            this.drawAccelTimer = null;
        }
    }

    /**
     * Stop receive data from brighton
     */
    private void stopReceiveData() {
        try {
          /* set receive data types */
            ArrayList<BleConstants.NotificationDataType> dataTypeList = new ArrayList<BleConstants.NotificationDataType>() {{
                add(BleConstants.NotificationDataType.HEART_RATE);
                add(BleConstants.NotificationDataType.ACTIVITY_METER);
                add(BleConstants.NotificationDataType.ACC_VALUE_DATA);
                add(BleConstants.NotificationDataType.HYPNAGOGIC_DATA);
                add(BleConstants.NotificationDataType.PULSE_WAVE_DATA);
                add(BleConstants.NotificationDataType.PULSE_OXIMETER_DATA);

            }};

            if (drawPulseWaveTimer != null) {
                drawPulseWaveTimer.cancel();
                drawPulseWaveTimer = null;
            }

            MainActivity.enableMonitorize(false, dataTypeList);

            //  stopDrawPulseWave();

            stopRecevingDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
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

//            changeDataViewHR();
            changeDataViewAM();
//        drawPulseWave();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
     *
     * @see #updateActivity()
     */
//    private void changeDataViewHR() {
//        try {
//            int heartrate;
//            boolean received;
//
//            heartrate = heartRate;
//            received = hr_flag;
//            this.hr_flag = false; /* clear receive status */
//
//
//            if (!received) {
//            /* not received data */
//                this.nothing_a++;
//                if (this.nothing_a > 5) {
//                    this.heartRateString = this.getString(R.string.default_pulse_rate);
//                    heartrate = -1;
//                }
//            } else {
//            /* received data */
//                if (heartrate == 0 || heartrate == -1) {
//                    this.heartRateString = this.getString(R.string.default_pulse_rate);
//                } else {
//                    this.heartRateString = String.valueOf(heartrate);
//                }
//                this.nothing_a = 0;
//            }
//
//        /* calculation new exercise intensity */
//            this.new_exercise_intensity = (heartrate / (220.0 - age_a)) * 100;
//
//        /* update pulse rate level */
//            this.handler.post(new Runnable() {
//                /**
//                 * {@inheritDoc}
//                 */
//                @Override
//                public void run() {
//                    mTextViewTotalBPM.setText("" + heartRateString + "\nBPM");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * It receives the latest activity data, and display it.</ br>
     * It is called from the period timer to update activity.
     *
     * @see #updateActivity()
     */
    private void changeDataViewAM() {
        try {
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

                    mTextViewTotalStep.setText(stepsString);
                    mTextViewDistanceMeter.setText(distanceString);
                    mTextViewTotalMet.setText("" + metsString + "\nMET");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertIntoMainActivity() {
        try {
            mStringStartDateTime = mUtility.getDateTime();

            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mMain_ACTIVITY_START_TIME, mStringStartDateTime);
            mContentValues.put(DBHelper.mMain_ACTIVITY_END_TIME, "");
            mContentValues.put(DBHelper.mMain_ACTIVITY_TOTAL_TIME, "");
            mContentValues.put(DBHelper.mMain_ACTIVITY_DATE, mUtility.getDate());
            mContentValues.put(DBHelper.mMain_ACTIVITY_SERVER_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mMain_ACTIVITY_USER_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mMain_ACTIVITY_Is_Sync, "no");
            mContentValues.put(DBHelper.mMain_ACTIVITY_TYPE, "ecg");
            mContentValues.put(DBHelper.mMain_ACTIVITY_RANDOM_NUMBER, getRendomNo());

            mDbHelper.insertRecord(DBHelper.mTableMainActivityTable, mContentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateIntoMainActivity() {
        try {
            mStringEndDateTime = mUtility.getDateTime();

            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mMain_ACTIVITY_END_TIME, mStringEndDateTime);
            mContentValues.put(DBHelper.mMain_ACTIVITY_TOTAL_TIME, getTimeDiffrence(mStringStartDateTime, mStringEndDateTime));
            mContentValues.put(DBHelper.mMain_ACTIVITY_SERVER_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));

            mDbHelper.updateRecord(DBHelper.mTableMainActivityTable, mContentValues, DBHelper.mMain_ACTIVITY_USER_ID + " = ? AND "
                    + DBHelper.mMain_ACTIVITY_START_TIME + "= ?", new String[]{mUtility.getAppPrefString(Constant.PREFS_USER_ID), mStringStartDateTime});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertIntoBPMDetails() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mBPM_DETAILS_Parent_Id, mStringParentID);
            mContentValues.put(DBHelper.mBPM_DETAILS_Server_RecordId, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mBPM_DETAILS_BPM_Value, mStringBPMValues);
            mContentValues.put(DBHelper.mBPM_DETAILS_date_time, mStringStartDateTime);
            mContentValues.put(DBHelper.mBPM_DETAILS_Type, "ecg");
            mContentValues.put(DBHelper.mBPM_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mBPM_DEATILS_Is_Sync, "no");

            mDbHelper.insertRecord(DBHelper.mTableBPMDetails, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void insertIntoMETDetails() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mMET_DETAILS_Parent_ID, mStringParentID);
            mContentValues.put(DBHelper.mMET_DETAILS_SERVER_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mMET_DETAILS_Met_Value, mStringMetValue);
            mContentValues.put(DBHelper.mMET_DETAILS_Date_Time, mStringStartDateTime);
            mContentValues.put(DBHelper.mMET_DETAILS_Type, "ecg");
            mContentValues.put(DBHelper.mMET_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mMET_DETAILS_Is_Sync, "no");

            mDbHelper.insertRecord(DBHelper.mTableMETDetails, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void insertIntoStepDetails() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mSTEP_DETAILS_Parent_ID, mStringParentID);
            mContentValues.put(DBHelper.mSTEP_DETAILS_SERVER_STEP_RECORD_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mSTEP_DETAILS_STEP_Value, mStringStepValues);
            mContentValues.put(DBHelper.mSTEP_DETAILS_Date_Time, mStringStartDateTime);
            mContentValues.put(DBHelper.mSTEP_DETAILS_Type, "ecg");
            mContentValues.put(DBHelper.mSTEP_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mSTEP_DETAILS_Is_Sync, "no");

            mDbHelper.insertRecord(DBHelper.mTableStepDetails, mContentValues);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertIntoDistanceDetails() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_Parent_ID, mStringParentID);
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_Server_Distance_Record_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_Distance_Value, mStringDistancesValue);
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_Date_Time, mStringStartDateTime);
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_Type, "ecg");
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mDISTANCE_DETAILS_Is_Sync, "no");

            mDbHelper.insertRecord(DBHelper.mTableDistanceDetails, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void insertIntoECGDetails() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mECG_DETAILS_Parent_ID, mStringParentID);
            mContentValues.put(DBHelper.mECG_DETAILS_Server_ECG_Record_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mECG_DETAILS_ECG_VALUE, mStringECGValues);
            mContentValues.put(DBHelper.mECG_DETAILS_DATE_TIME, mStringStartDateTime);
            mContentValues.put(DBHelper.mECG_DETAILS_TYPE, "ecg");
            mContentValues.put(DBHelper.mECG_DETAILS_USER_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mECG_DETAILS_Is_Sync, "no");
//            mContentValues.put(DBHelper.mECG_DETAILS_Time_Diff, mStringECGTimeDiff);

            mDbHelper.insertRecord(DBHelper.mTableECGDetails, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void insertIntoTimeTable() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mTable_Parent_ID, mStringParentID);
            mContentValues.put(DBHelper.mTable_SERVER_STEP_RECORD_TIME, "");
            mContentValues.put(DBHelper.mTable_STEP_Value, mStringStepValues);
            mContentValues.put(DBHelper.mTable_Date_Time, mStringStartDateTime);
            mContentValues.put(DBHelper.mTable_Type, "ecg");
            mContentValues.put(DBHelper.mTable_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mTable_Is_Sync, "no");
            mContentValues.put(DBHelper.mTable_DATE, mUtility.getDate());

            mDbHelper.insertRecord(DBHelper.mTableTimeTable, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void insertIntoHRVTable() {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mHRV_DETAILS_Parent_ID, mStringParentID);
            mContentValues.put(DBHelper.mHRV_DETAILS_Server_HRV_Record_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mHRV_DETAILS_HRV_VALUE, mStringHRVValue);
            mContentValues.put(DBHelper.mHRV_DETAILS_DATE_TIME, mStringStartDateTime);
            mContentValues.put(DBHelper.mHRV_DETAILS_TYPE, "ecg");
            mContentValues.put(DBHelper.mHRV_DETAILS_USER_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mContentValues.put(DBHelper.mHRV_DETAILS_Is_Sync, "no");

            mDbHelper.insertRecord(DBHelper.mTableHRVDetails, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    /**
     * Initialize pulse wave graph.
     */
    private void initializePulseWaveGraph() {
        try {

             /* get accel data from receive data */
            mLineChartECG.setDrawGridBackground(false);

            // no description text
            mLineChartECG.getDescription().setEnabled(false);

            // enable touch gestures
            mLineChartECG.setTouchEnabled(true);

            // enable scaling and dragging
            mLineChartECG.setDragEnabled(true);
            mLineChartECG.setScaleEnabled(true);


            // if disabled, scaling can be done on x- and y-axis separately
            mLineChartECG.setPinchZoom(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLineChartECG.setHardwareAccelerationEnabled(true);
                }
            });

            XAxis xAxis = mLineChartECG.getXAxis();
            xAxis.enableGridDashedLine(0f, 0f, 0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGridLineWidth(0f);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularityEnabled(false);
            xAxis.setEnabled(false);
            xAxis.setGranularity(1.0f);


            YAxis leftAxis = mLineChartECG.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

            leftAxis.setDrawTopYLabelEntry(true);
            leftAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
            leftAxis.setDrawZeroLine(false);
            leftAxis.setLabelCount(5);
            leftAxis.setAxisMaxValue(1200);
            leftAxis.setAxisMinValue(0);
            // limit lines are drawn behind data (and not on top)
            leftAxis.setDrawLimitLinesBehindData(true);

            mLineChartECG.getAxisRight().setEnabled(false);
            mLineChartECG.getXAxis().setEnabled(false);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLineChartECG.setVisibleXRangeMaximum(200);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Start update pulse wave graph timer.
     */
    private void startDrawPulseWave() {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // LineChartData data = mLineChartView.getLineChartData();

            /* return horizontal axis value */
                    // pulseWaveHorizAxis.setValues(saveHorizAxisValue);
//            data.setAxisXBottom(pulseWaveHorizAxis);
//
//            data.setAxisYLeft(null); /* disable vertical axis */

//            mLineChartView.setLineChartData(data); /* return axis setting when the user clicks the stop button */
//
//            mLineChartView.setZoomEnabled(false); /* disable zoom */

                    if (drawPulseWaveTimer == null) {
                        drawPulseWaveTimer = new Timer();
                        drawPulseWaveTimer.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
//                        setChartData();
                                //   Log.e("PULSEWAVE", "PULSE DRAWING");
                                drawAcceleration();

                            }
                        }, PULSE_WAVE_TIMER_INTERVAL, PULSE_WAVE_TIMER_INTERVAL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);
    }


//    public void setChartData() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // draw the data
//                drawPulseWave();
//            }
//        }).start();
//    }

    /**
     * Stop update pulse wave graph timer.
     */
//    private void stopDrawPulseWave() {
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//        try {
//            if (drawPulseWaveTimer != null) {
//                drawPulseWaveTimer.cancel();
//                drawPulseWaveTimer = null;
//            }
//
//            LineChartData data = mLineChartView.getLineChartData();
//
//            /* save current horizontal axis value */
//            saveHorizAxisValue = null;
//            saveHorizAxisValue = data.getAxisXBottom().getValues();
//            /* set horizontal axis for zoom (scale value is auto-generated) */
//            pulseWaveHorizAxis.setValues(null);
//            pulseWaveHorizAxis.setAutoGenerated(true);
//            data.setAxisXBottom(pulseWaveHorizAxis);
//
//            if (vertAxisZoomStatus) {
//                /* enable vertical axis */
//                Viewport v = mLineChartView.getMaximumViewport();
//                int max = (int) v.top;
//                pulseWaveVertAxis.setMaxLabelChars(String.valueOf(max).length());
//                data.setAxisYLeft(pulseWaveVertAxis);
//            }
//            mLineChartView.setScrollEnabled(true);
//            mLineChartView.setLineChartData(data); /* set new axis setting for zoom */
//
//            mLineChartView.setZoomEnabled(true); /* enable zoom */
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//            }
//        };
//        mainHandler.post(myRunnable);
//    }

    /**
     * Pulse wave graph drawing.
     */
//    private void drawPulseWave() {
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//        try{
//            int countplusp = 0;
//
//        /* get pulse wave data from receive data */
//            Iterator iteDiffTime = diffTimeQueueP.iterator();
//
//            Iterator itePulse = pulseQueue.iterator();
//
//            if (!pulseWaveTime.isEmpty()) {
//                countplusp = pulseWaveTime.getLast().intValue();
//            }
//
//            try {
//                while (iteDiffTime.hasNext() && itePulse.hasNext()) {
//                    Integer diffTimeP = (Integer) iteDiffTime.next(); // java.util.ConcurrentModificationException Found Here
//                    Object pulseValue = itePulse.next();
//                    Integer setdata_drawPulseWave = null;
//
//                    if (pulseValue instanceof Float) {
//                        Float fbuf = (Float) pulseValue;
//                        fbuf *= 100000; // for adjust R/IR wave on graph area
//                        setdata_drawPulseWave = new Integer(fbuf.intValue());
//                        pulseWaveData.add(new Integer(setdata_drawPulseWave.intValue()));
//                    } else if (pulseValue instanceof Integer) {
//                        setdata_drawPulseWave = (Integer) pulseValue;
//                        pulseWaveData.add(new Integer(setdata_drawPulseWave.intValue()));
//                    }
//
//                    countplusp += diffTimeP.intValue();
//                /* add receive data to pulse wave graph value */
//                    pulseWaveTime.add(new Integer(countplusp));
//                    yvalue_p.add(setdata_drawPulseWave.intValue());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            /* clear receive data */
//            diffTimeQueueP.clear();
//            pulseQueue.clear();
//
//
//            if (!(countplusp > 0)) {
//            /* not received data */
//                return;
//            }
//
//        /* calculate new range of horizontal axis */
//            float twpmin = 0f;
//            float twpmax = 0f;
//            if ((countplusp / 1000f) < 4.8f) { /* unit convert from msec to sec */
//                twpmin = 0f - PULSE_WAVE_SCALE_MARGIN;
//                twpmax = 5f + PULSE_WAVE_SCALE_MARGIN;
//            } else {
//                twpmin = (countplusp / 1000f) - 4.8f - PULSE_WAVE_SCALE_MARGIN; /* unit convert from msec to sec */
//                twpmax = (countplusp / 1000f) + 0.2f + PULSE_WAVE_SCALE_MARGIN; /* unit convert from msec to sec */
//            }
//
//        /* search the index of the minimum value of the horizontal axis */
//            int index = 0;
//            if (twpmin > (0.0f - PULSE_WAVE_SCALE_MARGIN)) {
//                for (ListIterator listIte = pulseWaveTime.listIterator(); listIte.hasNext(); ) {
//                    Integer time = (Integer) listIte.next();
//
//                    if ((time.floatValue() / 1000f) > twpmin) { /* unit convert from msec to sec */
//                        index = listIte.previousIndex() - 1; /* get the index of the minimum value */
//                        break;
//                    }
//                }
//            }
//
//        /* delete unused pulse wave value */
//            if (index > 0) {
//                pulseWaveTime.subList(0, index).clear();
//                pulseWaveData.subList(0, index).clear();
//            }
//
//        /*
//         *  NOTES.
//         *  When the line value is not new to the time of the drawing, there are things that IndexOutOfBoundsException occurs.
//         */
//            LinkedList<PointValue> gPulseWave = new LinkedList<PointValue>();
//            Iterator iteT = pulseWaveTime.iterator();
//            Iterator iteP = pulseWaveData.iterator();
//
//            while (iteT.hasNext() && iteP.hasNext()) {
//                Integer timeValue = (Integer) iteT.next();
//                Integer pulseValue = (Integer) iteP.next();
//                if (pulseValue == null){
//                    pulseValue = 0;
//                }
//            /* copy pulse wave data to new line values */
//                gPulseWave.add(new PointValue((timeValue.intValue() / 1000f), pulseValue.floatValue())); /* unit convert from msec to sec */
//            }
//
//        /* set pulse wave values to line graph */
//            List<Line> lines = new ArrayList<Line>();
//            Line lineP = new Line(gPulseWave);
//        /* set line setting */
//            lineP.setColor(getResources().getColor(R.color.color_active_tab));
//            lineP.setStrokeWidth(1);    /* most thin line */
//            lineP.setHasPoints(false); /* not have point */
//            lineP.setCubic(true);      /* use cubic line */
//            lines.add(lineP);
//
//        /* calculate new scale value of axis */
//            List<AxisValue> axisValues = new ArrayList<AxisValue>();
//            int value = ((int) (twpmin / 5)) * 2;
//            while (value <= twpmax) {
//                axisValues.add(new AxisValue(value));
//                value += 5;
//            }
//
//        /* set new scale value of horizontal axis */
//            pulseWaveHorizAxis.setValues(axisValues);
//
//        /* set lines and horizontal axis */
//            LineChartData data = new LineChartData();
//            data.setLines(lines);
//            data.setAxisXBottom(pulseWaveHorizAxis);
//
//        /* set chart data */
//            mLineChartView.setLineChartData(data);
//
//        /* calculate new range of vertical axis */
//            final Viewport v = new Viewport(mLineChartView.getMaximumViewport());
//            float vertMin = v.bottom;
//            float vertMax = v.top;
//            if (yvalue_p.size() > 50) {
//                Collections.sort(yvalue_p);
//                int new_min_p = yvalue_p.getFirst().intValue();
//                int new_max_p = yvalue_p.getLast().intValue();
//                int standard_value_p = yvalue_p.get(yvalue_p.size() / 2);
//
//                yvalue_p.clear();
//
//                int diff_min_p = Math.abs(new_min_p - standard_value_p);
//                int diff_max_p = Math.abs(new_max_p - standard_value_p);
//                int a_p = Math.max(diff_max_p, diff_min_p);
//
//                vertMin = standard_value_p - (a_p * 1.2f);
//                vertMax = standard_value_p + (a_p * 1.2f);
//            }
//
//        /* set new range of axis */
//            if (v.bottom != vertMin) {
//                v.bottom = vertMin;
//            }
//            if (v.top != vertMax) {
//                v.top = vertMax;
//            }
//            v.left = twpmin;
//            v.right = twpmax;
//            mLineChartView.setMaximumViewport(v);
//            mLineChartView.setCurrentViewport(v);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//            }
//        };
//        mainHandler.post(myRunnable);
//    }
    @Override
    protected void onDestroy() {
        if (wl != null) {
            wl.release();
        }
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();

    }
}
