package com.aiosleeve.aiosleeve.fragments;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by oneclickpc001 on 18/1/18.
 */

public class FragmentSleepDetails extends AppCompatActivity {

    public static final String TAG = "FragmentSleepDetails";
    LineChart mLineChart;

    //    public TextView mTextViewStep;
//    public TextView mTextViewDistance;
//    public TextView mTextViewTime;
    public TextView mTextViewStartTime;
    public TextView mTextViewEndTime;
    //    public TextView mTextViewAwake;
//    public TextView mTextViewLighter;
//    public TextView mTextViewLight;
//    public TextView mTextViewDeeper;
//    public TextView mTextViewDeep;
    public TextView mTextViewTotalMin;

    public ImageView mImageViewBack;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    String mStringStartDataTime = "";
    String mStringEndDateTime = "";
    String mStringSleepValue = "";
    String mStringTotalTime = "";

    int intTotalAwake = 0;
    int intTotalLighter = 0;
    int intTotalLight = 0;
    int intTotalDeep = 0;
    int intTotalDeeper = 0;

    private LinkedList<Integer> sleepDataQueue = new LinkedList<Integer>();

    Utility mUtility;

    private ProgressBar mProgressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sleep_details);

        mStringStartDataTime = getIntent().getExtras().getString("mStringStartDataTime");

        mUtility = new Utility(FragmentSleepDetails.this);
        mDbHelper = new DBHelper(FragmentSleepDetails.this);
        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        mTextViewStep = (TextView) findViewById(R.id.fragment_sleep_details_txt_step);
//        mTextViewDistance = (TextView) findViewById(R.id.fragment_sleep_details_txt_distance);
//        mTextViewTime = (TextView) findViewById(R.id.fragment_sleep_details_txt_time);

        mProgressBar = (ProgressBar) findViewById(R.id.fragment_sleep_details_progress_bar);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.color_white), PorterDuff.Mode.SRC_IN);
        mProgressBar.setVisibility(View.VISIBLE);

        mTextViewStartTime = (TextView) findViewById(R.id.fragment_sleep_details_txt_start_time);
        mTextViewEndTime = (TextView) findViewById(R.id.fragment_sleep_details_txt_end_time);
//        mTextViewAwake = (TextView) findViewById(R.id.fragment_sleep_details_txt_awake);
//        mTextViewLighter = (TextView) findViewById(R.id.fragment_sleep_details_txt_lighter);
//        mTextViewLight = (TextView) findViewById(R.id.fragment_sleep_details_txt_light);
//        mTextViewDeeper = (TextView) findViewById(R.id.fragment_sleep_details_txt_deeper);
//        mTextViewDeep = (TextView) findViewById(R.id.fragment_sleep_details_txt_deep);
        mTextViewTotalMin = (TextView) findViewById(R.id.fragment_sleep_details_txt_total_min);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_sleep_details_imageview_back);

        mLineChart = (LineChart) findViewById(R.id.fragment_sleep_details_graph_view);
        mLineChart.setNoDataText("");

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                String mStringLable = "";
                return mStringLable;
            }
        });

        YAxis mYAxis = mLineChart.getAxisLeft();
        mYAxis.setEnabled(true);
        mYAxis.setTextColor(ContextCompat.getColor(FragmentSleepDetails.this, R.color.color_white));

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

        getData();


        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void drawAcceleration() {

        if (!mStringStartDataTime.equalsIgnoreCase("")) {
            String[] mString = mStringStartDataTime.split(" ");
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
        xAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.color_white));


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

        mLineChart.setVisibleYRangeMaximum(6, YAxis.AxisDependency.LEFT);
        mLineChart.setExtraLeftOffset(30f);


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
        mProgressBar.setVisibility(View.GONE);

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
            set1.setColor(ContextCompat.getColor(this, R.color.color_white));
            set1.setCircleColor(ContextCompat.getColor(this, R.color.drawer_icon_tint_color));
            set1.setLineWidth(0.5f);
            set1.setCircleRadius(5f);
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

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    public void getData() {

        mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableSleepDetail +
                " where " + DBHelper.mSLEEP_DETAIL_START_TIME + "= '" + mStringStartDataTime +
                "' AND " + DBHelper.mSLEEP_DETAIL_USER_ID + "= '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");

        if (mDataHolder != null && mDataHolder.get_Listholder().size() > 0) {

            intTotalAwake = 0;
            intTotalLighter = 0;
            intTotalLight = 0;
            intTotalDeep = 0;
            intTotalDeeper = 0;

            sleepDataQueue = new LinkedList<>();
            mStringSleepValue = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_SLEEP_VALUE);
            mStringStartDataTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_START_TIME);
            mStringEndDateTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_END_TIME);
            mStringTotalTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_TOTAL_TIME);

            if (mStringSleepValue != null && !mStringSleepValue.equalsIgnoreCase("")) {
                mStringSleepValue = mUtility.removeLastComma(mStringSleepValue);

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

//                mTextViewTime.setText(getResources().getString(R.string.lbl_total_time, getConvertedTime(Integer.parseInt(mStringTotalTime))));

//                mTextViewAwake.setText(getResources().getString(R.string.label_awake, "" + intTotalAwake));
//                mTextViewLighter.setText(getResources().getString(R.string.label_lighter, "" + intTotalLighter));
//                mTextViewLight.setText(getResources().getString(R.string.label_light, "" + intTotalLight));
//                mTextViewDeeper.setText(getResources().getString(R.string.label_deeper, "" + intTotalDeeper));
//                mTextViewDeep.setText(getResources().getString(R.string.label_deep, "" + intTotalDeep));

                Log.d(TAG, Arrays.toString(mStringSleep));
                int intTotalTimeHours = 0;
                int intTotalTimeMinutes = 0;
                if (mStringSleep.length != 0) {
                    intTotalTimeHours = (mStringSleep.length) / 60;
                    intTotalTimeMinutes = (mStringSleep.length) % 60;
                }
//                mTextViewTotalMin.setText(getResources().getString(R.string.label_total_min, "" + mStringSleep.length));
                mTextViewTotalMin.setText(getResources().getString(R.string.label_hours_and_min_sleep_details, intTotalTimeHours, intTotalTimeMinutes));
                drawAcceleration();
            }else{
                mProgressBar.setVisibility(View.GONE);
            }
        }
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
            public void onDisconnect(String devicesName, final String devicesAddress) {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mUtility.errorDialog(getResources().getString(R.string.alert_disconnect_device));
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
            public void dataAvailable(Object obj) {

            }

            @Override
            public void dataStatus(int status) {

            }
        }, TAG);

        mUtility.changeStatusbarColor(R.color.color_violet);

        getData();
    }

    public String getConvertedTime(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return "" + getProperTimeFormat(minutes) + " : " + getProperTimeFormat(seconds);
    }

    @Override
    protected void onDestroy() {
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();
    }
}
