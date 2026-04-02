package com.aiosleeve.aiosleeve.fragments;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by oneclickpc001 on 18/1/18.
 */

public class FragmentECGDetails extends AppCompatActivity {
    public static final String TAG = "FragmentECGDetails";

    LineChart mLineChartView;

    TextView mTextViewStep;
    TextView mTextViewDistance;
    TextView mTextViewTime;
    TextView mTextViewTotalMet;
    TextView mTextViewTotalHrv;
    TextView mTextViewTotalBPM;

    public ImageView mImageViewBack;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    String mStringStartDataTime = "";
    String mStringType = "";
    String mStringTotalStep = "";
    String mStringTotalDistance = "";
    String mStringBPMValues = "";
    String mStringHRVValues = "";
    String mStringMETValues = "";
    String mStringECGValues = "";
    String mStringTotalTime = "";

    ArrayList<Entry> yValueECG = new ArrayList<Entry>();

    Utility mUtility;
    private Handler mainHandler;

    ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_ecg_details);

        mUtility = new Utility(FragmentECGDetails.this);
        mainHandler = new Handler(Looper.getMainLooper());
        mStringStartDataTime = getIntent().getExtras().getString("mStringStartDataTime");
        mStringType = getIntent().getExtras().getString("mStringType");

        mDbHelper = new DBHelper(FragmentECGDetails.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProgressBar = (ProgressBar) findViewById(R.id.fragment_ecg_details_progress_bar);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.mandatory_color), PorterDuff.Mode.SRC_IN);


        mLineChartView = (LineChart) findViewById(R.id.fragment_ecg_details_graph_view);
        mLineChartView.setNoDataText("Loading...");
        mLineChartView.setNoDataTextColor(Color.TRANSPARENT);
        mLineChartView.invalidate();

        mTextViewStep = (TextView) findViewById(R.id.fragment_ecg_details_txt_step);
        mTextViewDistance = (TextView) findViewById(R.id.fragment_ecg_details_txt_distance);
        mTextViewTime = (TextView) findViewById(R.id.fragment_ecg_details_txt_time);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_ecg_details_imageview_back);

        mTextViewTotalMet = (TextView) findViewById(R.id.fragment_ecg_details_txt_met);
        mTextViewTotalHrv = (TextView) findViewById(R.id.fragment_ecg_details_txt_hrv);
        mTextViewTotalBPM = (TextView) findViewById(R.id.fragment_ecg_details_txt_bpm);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // no description text
        mLineChartView.getDescription().setEnabled(false);

        // enable touch gestures
        mLineChartView.setTouchEnabled(true);

        mLineChartView.setVisibleXRangeMaximum(100);
        mLineChartView.setScaleMinima(100f, 1f);

        mLineChartView.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mLineChartView.setDragEnabled(true);
        mLineChartView.setScaleEnabled(true);
        mLineChartView.setDrawGridBackground(false);
        mLineChartView.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChartView.setPinchZoom(true);
        // set an alternative background color
        mLineChartView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.color_white));

        getData();
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mLineChartView.animateX(2500);

                // get the legend (only possible after setting data)
                Legend l = mLineChartView.getLegend();

                // modify the legend ...
                l.setForm(Legend.LegendForm.LINE);
                l.setTextSize(11f);
                l.setTextColor(Color.WHITE);
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(false);

                XAxis xAxis = mLineChartView.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//                xAxis.setTextSize(11f);
//                xAxis.setTextColor(getResources().getColor(R.color.brown));
                xAxis.setDrawGridLines(false);
//                xAxis.setDrawAxisLine(false);
//                xAxis.setEnabled(false);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawLabels(false);

                YAxis leftAxis = mLineChartView.getAxisLeft();
                leftAxis.setTextColor(getResources().getColor(R.color.brown));
                leftAxis.setDrawGridLines(false);
                leftAxis.setGranularityEnabled(false);
                leftAxis.setLabelCount(5);
                leftAxis.setAxisMaxValue(1200);
                leftAxis.setAxisMinValue(0);

                mLineChartView.getAxisRight().setEnabled(false);
//                mLineChartView.getXAxis().setEnabled(false);


                mLineChartView.getLegend().setEnabled(false);
                mLineChartView.setExtraOffsets(0, 20f, 0, 20f);
            }
        };
        mainHandler.post(myRunnable);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        MainActivity.setDeviceStatusListner(new DevicesStatus() {
//            @Override
//            public void addScanDevices(BluetoothDevice bluetoothDevice) {
//
//            }
//
//            @Override
//            public void onConnect(String devicesName, String devicesAddress) {
//
//            }
//
//            @Override
//            public void onDisconnect(String devicesName, final String devicesAddress) {
//                if (!isFinishing()) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            mUtility.errorDialog(getResources().getString(R.string.alert_disconnect_device));
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//            @Override
//            public void readCharacterStic() {
//
//            }
//
//            @Override
//            public void readRssiValue(int updateRssi, String devicesName, String devicesAddress) {
//
//            }
//
//            @Override
//            public void dataAvailable(Object obj) {
//
//            }
//
//            @Override
//            public void dataStatus(int status) {
//
//            }
//        },TAG);
//
////        mUtility.changeStatusbarColor(R.color.custom_header_color);
//    }

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    public void getData() {
        new MyAsyncTasks().execute();
    }

    public String removeLastComma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public String getConvertedTime(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return "" + getProperTimeFormat(minutes) + " : " + getProperTimeFormat(seconds);
    }

    public class MyAsyncTasks extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

            // display a progress dialog for good user experiance
        }

        @Override
        protected String doInBackground(String... params) {
            String current = "";

            try {
                mDataHolder = mDbHelper.read("SELECT bpm.id as bpm_id, bpm.bpm_value," +
                        " distance.id as distance_id, distance.diatance_value,  " +
                        " step.id as step_id, step.step_value, step.date_time, " +
                        " hrv.id as hrv_id, hrv.hrv_value," +
                        " met.id as met_id, met.met_value," +
                        " ecg.id as ecg_id, ecg.ecg_value," +
                        " main.start_time, main.end_time, main.total_time, main.date FROM " + DBHelper.mTableMainActivityTable + " as main " +
                        "LEFT JOIN " + DBHelper.mTableStepDetails + " as step ON step.parent_id = main.id " +
                        "LEFT JOIN " + DBHelper.mTableDistanceDetails + " as distance ON distance.parent_id = main.id " +
                        "LEFT JOIN " + DBHelper.mTableBPMDetails + " as bpm ON bpm.parent_id = main.id " +
                        "LEFT JOIN " + DBHelper.mTableHRVDetails + " as hrv ON hrv.parent_id = main.id " +
                        "LEFT JOIN " + DBHelper.mTableMETDetails + " as met ON met.parent_id = main.id " +
                        "LEFT JOIN " + DBHelper.mTableECGDetails + " as ecg ON ecg.parent_id = main.id " +
                        "where main.start_time = '" + mStringStartDataTime + "' AND main.type = '" + mStringType +
                        "' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (mDataHolder != null && mDataHolder.get_Listholder().size() > 0) {

                    mStringTotalStep = mDataHolder.get_Listholder().get(0).get(DBHelper.mSTEP_DETAILS_STEP_Value);
                    mStringTotalDistance = mDataHolder.get_Listholder().get(0).get(DBHelper.mDISTANCE_DETAILS_Distance_Value);
                    mStringTotalTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME);

                    mStringBPMValues = mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_BPM_Value);
                    mStringHRVValues = mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_HRV_VALUE);
                    mStringMETValues = mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_Met_Value);
                    mStringECGValues = mDataHolder.get_Listholder().get(0).get(DBHelper.mECG_DETAILS_ECG_VALUE);

                    if (mStringECGValues != null && !mStringECGValues.equalsIgnoreCase("")) {
                        mStringECGValues = removeLastComma(mStringECGValues);
                        if (mStringECGValues.contains(",")) {
                            String[] mStringECG = mStringECGValues.split(",");
                            for (int i = 0; i < mStringECG.length; i++) {
                                if (i < mStringECG.length) {
                                    yValueECG.add(new Entry(i, Float.parseFloat(mStringECG[i])));
                                    final float arrr = Float.parseFloat(mStringECG[i]);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            mLineChartView.setVisibleXRangeMaximum(10);
                                            //mLineChartView.moveViewToX(arrr+10);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return current;
        }

        @Override
        protected void onPostExecute(String s) {

            LineDataSet set1 = null;

            if (mStringTotalTime != null && !mStringTotalTime.equalsIgnoreCase("")) {
                mTextViewTime.setText(getResources().getString(R.string.lbl_total_time, getConvertedTime(Integer.parseInt(mStringTotalTime))));
            }

            if (mStringTotalStep != null && !mStringTotalStep.equalsIgnoreCase("")) {
                mStringTotalStep = removeLastComma(mStringTotalStep);
                if (mStringTotalStep.contains(",")) {
                    mTextViewStep.setText(getResources().getString(R.string.lbl_total_step, mStringTotalStep.substring(mStringTotalStep.lastIndexOf(",") + 1, mStringTotalStep.length())));
                } else {
                    mTextViewStep.setText(getResources().getString(R.string.lbl_total_step, mStringTotalStep));
                }
            }

            if (mStringTotalDistance != null && !mStringTotalDistance.equalsIgnoreCase("")) {
                mStringTotalDistance = removeLastComma(mStringTotalDistance);
                if (mStringTotalDistance.contains(",")) {
                    mTextViewDistance.setText(getResources().getString(R.string.lbl_total_distance, mStringTotalDistance.substring(mStringTotalDistance.lastIndexOf(",") + 1, mStringTotalDistance.length())));
                } else {
                    mTextViewDistance.setText(getResources().getString(R.string.lbl_total_distance, mStringTotalDistance));
                }
            }


            if (mStringBPMValues != null && !mStringBPMValues.equalsIgnoreCase("")) {
                mStringBPMValues = removeLastComma(mStringBPMValues);
                if (mStringBPMValues.contains(",")) {
                    mTextViewTotalBPM.setText(getResources().getString(R.string.lbl_total_bpm, mStringBPMValues.substring(mStringBPMValues.lastIndexOf(",") + 1, mStringBPMValues.length())));
                } else {
                    mTextViewTotalBPM.setText(getResources().getString(R.string.lbl_total_bpm, mStringBPMValues));
                }
            }

            if (mStringHRVValues != null && !mStringHRVValues.equalsIgnoreCase("")) {
                mStringHRVValues = removeLastComma(mStringHRVValues);
                if (mStringHRVValues.contains(",")) {
//                    mTextViewTotalHrv.setText(getResources().getString(R.string.lbl_total_hrv, mStringHRVValues.substring(mStringHRVValues.lastIndexOf(",") + 1, mStringHRVValues.length())));

                    float hrvValue = Float.parseFloat(mStringHRVValues.substring(mStringHRVValues.lastIndexOf(",") + 1));

                    if (hrvValue > 5) {
                        hrvValue = 5.00f;
                    }
                    mTextViewTotalHrv.setText(getResources()
                            .getString(R.string.lbl_total_hrv,
                                    getString(R.string.two_digit_after_decimal,
                                            hrvValue)
                            )
                    );
                } else {
                    float hrvValue = Float.parseFloat(mStringHRVValues);

                    if (hrvValue > 5) {
                        hrvValue = 5.00f;
                    }
                    mTextViewTotalHrv.setText(getResources()
                            .getString(R.string.lbl_total_hrv,
                                    getString(R.string.two_digit_after_decimal,
                                            hrvValue)
                            )
                    );
//                    mTextViewTotalHrv.setText(getResources().getString(R.string.lbl_total_hrv,
//                            mStringHRVValues));
                }
            }

            if (mStringMETValues != null && !mStringMETValues.equalsIgnoreCase("")) {
                mStringMETValues = removeLastComma(mStringMETValues);
                if (mStringMETValues.contains(",")) {
                    mTextViewTotalMet.setText(getResources().getString(R.string.lbl_total_met, mStringMETValues.substring(mStringMETValues.lastIndexOf(",") + 1, mStringMETValues.length())));
                } else {
                    mTextViewTotalMet.setText(getResources().getString(R.string.lbl_total_met, mStringMETValues));
                }
            }
            mProgressBar.setVisibility(View.GONE);

            if (mLineChartView.getData() != null &&
                    mLineChartView.getData().getDataSetCount() > 0) {

                set1 = (LineDataSet) mLineChartView.getData().getDataSetByIndex(0);

                if (yValueECG != null && yValueECG.size() > 0) {
                    set1.setValues(yValueECG);
                }


                mLineChartView.getData().notifyDataChanged();
                mLineChartView.notifyDataSetChanged();

            } else {
                // create a dataset and give it a type

                LineData data = new LineData();
                if (yValueECG != null && yValueECG.size() > 0) {
                    set1 = new LineDataSet(yValueECG, getResources().getString(R.string.tital_ecg));
                    set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                    set1.setColor(getResources().getColor(R.color.color_active_tab));
                    set1.setCircleColor(getResources().getColor(R.color.color_active_tab));
                    set1.setLineWidth(2f);
                    set1.setCircleRadius(2f);
                    set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//                    set1.setDrawIcons(false);
                    set1.setValueTextSize(0f);
                    set1.setDrawCircles(false);
                    set1.setFillAlpha(65);
                    set1.setFormLineWidth(1f);
                    set1.setDrawValues(false);
                    set1.setHighLightColor(getResources().getColor(R.color.color_active_tab));
                    set1.setDrawCircleHole(false);
                    data.addDataSet(set1);
                }

                // create a data object with the datasets
                data.setValueTextColor(getResources().getColor(R.color.color_active_tab));
                data.setValueTextSize(9f);

                // set data
                mLineChartView.setData(data);
            }
        }
    }
//    @Override
//    protected void onDestroy() {
//        MainActivity.removeDeviceStatusListner(TAG);
//        super.onDestroy();
//    }
}
