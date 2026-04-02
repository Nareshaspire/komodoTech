package com.aiosleeve.aiosleeve.fragments;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Collections;

/**
 * Created by oneclickpc001 on 18/1/18.
 */

public class FragmentBPMDetails extends AppCompatActivity {
    public static final String TAG="FragmentBPMDetails";

    LineChart mLineChartView;

    TextView mTextViewStep;
    TextView mTextViewDistance;
    TextView mTextViewTime;

    public ImageView mImageViewBack;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    String mStringStartDataTime = "";
    String mStringType = "";
    String mStringTotalStep = "";
    String mStringTotalDistance = "";
    String mStringBPMValues = "";
    String mStringSPO2Values = "";
    String mStringMETValues = "";
    String mStringTotalTime = "";

    ArrayList<Entry> yValueBPM = new ArrayList<Entry>();
    ArrayList<Entry> yValueSPO2 = new ArrayList<Entry>();
    ArrayList<Entry> yValueMET = new ArrayList<Entry>();

    float mFloatMinumBPM = 0f;
    float mFloatMinumSpo2 = 0f;
    float mFloatMinumMet = 0f;
    float mFloatMaxBPM = 0f;
    float mFloatMaxSpo2 = 0f;
    float mFloatMaxMet = 0f;

    ArrayList<Float> mArrayListMinimum = new ArrayList<>();
    ArrayList<Float> mArrayListMaximum = new ArrayList<>();


    Utility mUtility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bpm_details);

        mStringStartDataTime = getIntent().getExtras().getString("mStringStartDataTime");
        mStringType = getIntent().getExtras().getString("mStringType");

        mUtility = new Utility(FragmentBPMDetails.this);
        mDbHelper = new DBHelper(FragmentBPMDetails.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLineChartView = (LineChart) findViewById(R.id.fragment_bpm_details_graph_view);

        mTextViewStep = (TextView) findViewById(R.id.fragment_bpm_details_txt_step);
        mTextViewDistance = (TextView) findViewById(R.id.fragment_bpm_details_txt_distance);
        mTextViewTime = (TextView) findViewById(R.id.fragment_bpm_details_txt_time);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_bpm_details_imageview_back);

        // no description text
        mLineChartView.getDescription().setEnabled(false);

        // enable touch gestures
        mLineChartView.setTouchEnabled(true);

        mLineChartView.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mLineChartView.setDragEnabled(true);
        mLineChartView.setScaleEnabled(true);
        mLineChartView.setDrawGridBackground(false);
        mLineChartView.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChartView.setPinchZoom(true);

        // set an alternative background color
        mLineChartView.setBackgroundColor(Color.LTGRAY);

        getData();

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
        xAxis.setTextSize(11f);
        xAxis.setTextColor(getResources().getColor(R.color.brown));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mLineChartView.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.brown));
        leftAxis.setDrawGridLines(true);
        xAxis.setTextSize(11f);
        leftAxis.setGranularityEnabled(false);

        float mFloatMinValue = Collections.min(mArrayListMinimum);
        float mFloatMaxValue = Collections.max(mArrayListMaximum);

        if (mFloatMinValue > 10) {
            leftAxis.setAxisMinimum(mFloatMinValue - 10);
        } else {
            leftAxis.setAxisMinimum(0);
        }

        leftAxis.setAxisMaximum(mFloatMaxValue + 10);


        mLineChartView.getAxisRight().setEnabled(false);

        mLineChartView.setHighlightPerDragEnabled(true);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

                if(!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
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
        },TAG);

        mUtility.changeStatusbarColor(R.color.custom_header_color);
    }

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    public void getData() {
        mDataHolder = mDbHelper.read("SELECT bpm.id as bpm_id, bpm.bpm_value," +
                " distance.id as distance_id, distance.diatance_value,  " +
                " step.id as step_id, step.step_value, step.date_time, " +
                " spo2.id as spo2_id, spo2.spo2_value," +
                " met.id as met_id, met.met_value," +
                " main.start_time, main.end_time, main.total_time, main.date FROM " + DBHelper.mTableMainActivityTable + " as main " +
                "LEFT JOIN " + DBHelper.mTableStepDetails + " as step ON step.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableDistanceDetails + " as distance ON distance.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableBPMDetails + " as bpm ON bpm.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableSPO2Details + " as spo2 ON spo2.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableMETDetails + " as met ON met.parent_id = main.id " +
                "where main.start_time = '" + mStringStartDataTime + "' AND main.type = '" + mStringType +
                "' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");

        if (mDataHolder != null && mDataHolder.get_Listholder().size() > 0) {

            mStringTotalStep = mDataHolder.get_Listholder().get(0).get(DBHelper.mSTEP_DETAILS_STEP_Value);
            mStringTotalDistance = mDataHolder.get_Listholder().get(0).get(DBHelper.mDISTANCE_DETAILS_Distance_Value);
            mStringTotalTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME);

            mStringBPMValues = mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_BPM_Value);
            mStringSPO2Values = mDataHolder.get_Listholder().get(0).get(DBHelper.mSPO2_DETAILS_SPO2_Value);
            mStringMETValues = mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_Met_Value);

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

            if (mStringMETValues != null && !mStringMETValues.equalsIgnoreCase("")) {
                mStringMETValues = removeLastComma(mStringMETValues);
                if (mStringMETValues.contains(",")) {
                    String[] mStringMET = mStringMETValues.split(",");
                    ArrayList<Float> mIntegersMet = new ArrayList<>();
                    for (int i = 0; i < mStringMET.length; i++) {
                        yValueMET.add(new Entry(i, Float.parseFloat(mStringMET[i])));
                        mIntegersMet.add(Float.parseFloat(mStringMET[i]));
                    }

                    //Finding Minimum from all three Arraylist.
                    ArrayList<Float> mArrayListMinimumMet = new ArrayList<>();
                    mArrayListMinimumMet.add(Collections.min(mIntegersMet));

                    mFloatMinumMet = Collections.min(mArrayListMinimumMet);
                    mFloatMaxMet = Collections.max(mArrayListMinimumMet);
                }
            }

            if (mStringSPO2Values != null && !mStringSPO2Values.equalsIgnoreCase("")) {
                mStringSPO2Values = removeLastComma(mStringSPO2Values);
                String[] mStringSPO2 = mStringSPO2Values.split(",");
                ArrayList<Float> mIntegersSpo2 = new ArrayList<>();
                for (int i = 0; i < mStringSPO2.length; i++) {
                    yValueSPO2.add(new Entry(i, Float.parseFloat(mStringSPO2[i])));
                    mIntegersSpo2.add(Float.parseFloat(mStringSPO2[i]));
                }

                //Finding Minimum from all three Arraylist.
                ArrayList<Float> mArrayListMinimumSpo2 = new ArrayList<>();
                mArrayListMinimumSpo2.add(Collections.min(mIntegersSpo2));

                mFloatMinumSpo2 = Collections.min(mArrayListMinimumSpo2);
                mFloatMaxSpo2 = Collections.max(mArrayListMinimumSpo2);
            }

            if (mStringBPMValues != null && !mStringBPMValues.equalsIgnoreCase("")) {
                mStringBPMValues = removeLastComma(mStringBPMValues);
                String[] mStringBPM = mStringBPMValues.split(",");
                ArrayList<Float> mIntegersBPM = new ArrayList<>();
                for (int i = 0; i < mStringBPM.length; i++) {
                    yValueBPM.add(new Entry(i, Float.parseFloat(mStringBPM[i])));
                    mIntegersBPM.add(Float.parseFloat(mStringBPM[i]));
                }

                //Finding Minimum from all three Arraylist.
                ArrayList<Float> mArrayListMinimumBpm = new ArrayList<>();
                mArrayListMinimumBpm.add(Collections.min(mIntegersBPM));

                mFloatMinumBPM = Collections.min(mArrayListMinimumBpm);
                mFloatMaxBPM = Collections.max(mArrayListMinimumBpm);
            }

            mArrayListMinimum.add(mFloatMinumBPM);
            mArrayListMinimum.add(mFloatMinumSpo2);
            mArrayListMinimum.add(mFloatMinumMet);

            mArrayListMaximum.add(mFloatMaxBPM);
            mArrayListMaximum.add(mFloatMaxSpo2);
            mArrayListMaximum.add(mFloatMaxMet);



            LineDataSet set1, set2, set3;

            if (mLineChartView.getData() != null &&
                    mLineChartView.getData().getDataSetCount() > 0) {

                set1 = (LineDataSet) mLineChartView.getData().getDataSetByIndex(0);
                set2 = (LineDataSet) mLineChartView.getData().getDataSetByIndex(1);
                set3 = (LineDataSet) mLineChartView.getData().getDataSetByIndex(2);

                if (yValueBPM != null && yValueBPM.size() > 0) {
                    set1.setValues(yValueBPM);
                }
                if (yValueSPO2 != null && yValueSPO2.size() > 0) {
                    set2.setValues(yValueSPO2);
                }
                if (yValueMET != null && yValueMET.size() > 0) {
                    set3.setValues(yValueMET);
                }

                mLineChartView.getData().notifyDataChanged();
                mLineChartView.notifyDataSetChanged();

            } else {
                // create a dataset and give it a type
                LineData data = new LineData();

                if (yValueBPM != null && yValueBPM.size() > 0) {
                    set1 = new LineDataSet(yValueBPM, getResources().getString(R.string.tital_bpm));
                    set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                    set1.setColor(getResources().getColor(R.color.bpm_details_bpm_bar));
                    set1.setCircleColor(getResources().getColor(R.color.bpm_details_bpm_bar));
                    set1.setLineWidth(2f);
                    set1.setCircleRadius(3f);
                    set1.setFillAlpha(65);
                    set1.setFillColor(getResources().getColor(R.color.bpm_details_bpm_bar));
                    set1.setHighLightColor(getResources().getColor(R.color.bpm_details_bpm_bar));
                    set1.setDrawCircleHole(false);
                    set1.setDrawHighlightIndicators(true);
                    set1.setDrawVerticalHighlightIndicator(true);
                    set1.setHighlightEnabled(true);
                    data.addDataSet(set1);
                }

                // create a dataset and give it a type
                if (yValueSPO2 != null && yValueSPO2.size() > 0) {
                    set2 = new LineDataSet(yValueSPO2, getResources().getString(R.string.tital_spo2));
                    set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                    set2.setColor(getResources().getColor(R.color.bpm_details_spo2_bar));
                    set2.setCircleColor(getResources().getColor(R.color.bpm_details_spo2_bar));
                    set2.setLineWidth(2f);
                    set2.setCircleRadius(3f);
                    set2.setFillAlpha(65);
                    set2.setFillColor(getResources().getColor(R.color.bpm_details_spo2_bar));
                    set2.setDrawCircleHole(false);
                    set2.setHighlightEnabled(true);
                    set2.setDrawHighlightIndicators(true);
                    set2.setDrawVerticalHighlightIndicator(true);
                    set2.setHighLightColor(getResources().getColor(R.color.bpm_details_spo2_bar));
                    data.addDataSet(set2);
                }

                if (yValueMET != null && yValueMET.size() > 0) {
                    set3 = new LineDataSet(yValueMET, getResources().getString(R.string.tital_met));
                    set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
                    set3.setColor(getResources().getColor(R.color.bpm_details_met_bar));
                    set3.setCircleColor(getResources().getColor(R.color.bpm_details_met_bar));
                    set3.setLineWidth(2f);
                    set3.setCircleRadius(3f);
                    set3.setFillAlpha(65);
                    set3.setFillColor(getResources().getColor(R.color.bpm_details_met_bar));
                    set3.setDrawCircleHole(false);
                    set3.setHighlightEnabled(true);
                    set3.setDrawVerticalHighlightIndicator(true);
                    set3.setDrawHighlightIndicators(true);
                    set3.setHighLightColor(getResources().getColor(R.color.bpm_details_met_bar));
                    data.addDataSet(set3);
                }

                // create a data object with the datasets
                data.setValueTextColor(getResources().getColor(R.color.brown));
                data.setValueTextSize(9f);

                // set data
                mLineChartView.setData(data);
            }
        }
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

    @Override
    protected void onDestroy() {
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();
    }
}
