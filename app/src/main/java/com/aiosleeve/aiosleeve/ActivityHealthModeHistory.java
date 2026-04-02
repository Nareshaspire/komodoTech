package com.aiosleeve.aiosleeve;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiosleeve.aiosleeve.VO.VoBPMListData;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.fragments.FragmentECGDetails;
import com.aiosleeve.aiosleeve.fragments.FragmentECGList;
import com.aiosleeve.aiosleeve.fragments.FragmentHealthMode;
import com.aiosleeve.aiosleeve.fragments.FragmentHealthMode.Event;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityHealthModeHistory extends AppCompatActivity {

    public static final String TAG = "ActivityHealthHistory";


    private ScatterChart scatterChart;

    private CardView mDayScatterChartCardView;

//    private TextView mTextViewOfDayNoData;

    private TextView mTextViewSelectedDayOrWeek;


    private RecyclerView mRecyclerViewOfDay;
    private RecyclerView mRecyclerViewOfWeek;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    DataHolder mBpmDataHolder;
    DataHolder mBpmTableDataHolder;
    DataHolder mHrvDataHolder;
    Utility mUtility;

    String mStringSelectedDate = "";

    private Drawable mDrawableBackgroundSkyBlue;
    private Drawable mDrawableBackgroundGray;

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    SimpleDateFormat mSimpleDateFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
    SimpleDateFormat mSimpleDateFormatTimeAmPm = new SimpleDateFormat("hh:mm aa", Locale.US);

    SimpleDateFormat mSimpleDateFormatForQuery = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.US);


    ArrayList<VoBPMListData> mArrayListDayHistoryData = new ArrayList<>();
    ArrayList<WeekData> mArrayListWeekHistoryData = new ArrayList<>();

    private class WeekData {
        private String name = "";
        private ArrayList<Entry> values = new ArrayList<>();
        private int colorOfDot = R.color.mandatory_color;

        public WeekData(String name) {
            this.name = name;
        }

        public WeekData(String name, int colorOfDot) {
            this.name = name;
            this.colorOfDot = colorOfDot;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Entry> getValues() {
            return values;
        }

        public void setValues(ArrayList<Entry> values) {
            this.values = values;
        }

        public void clearValues() {
            values.clear();
        }
    }


    Calendar startDayOfTheWeek = Calendar.getInstance();
    Calendar lastDayOfTheWeek = Calendar.getInstance();

    Calendar todayCalendar = Calendar.getInstance();
    Calendar currentSelectedDate = Calendar.getInstance();


    private enum TabType {
        DAY,
        WEEK,
        MONTH
    }

    private TabType selectedTabType = TabType.DAY;

    private DayHistoryAdapter dayHistoryAdapter = new DayHistoryAdapter();

    private WeekHistoryAdapter weekHistoryAdapter = new WeekHistoryAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_mode_history);

        mUtility = new Utility(ActivityHealthModeHistory.this);
        mStringSelectedDate = mUtility.getDate();
        mDbHelper = new DBHelper(ActivityHealthModeHistory.this);
        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();

            mDbHelper.removeAllBlankEntries();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDrawableBackgroundSkyBlue = ContextCompat.getDrawable(this, R.drawable.button_background_rounded_sky_blue);
        mDrawableBackgroundGray = ContextCompat.getDrawable(this, R.drawable.button_background_rounded_gray);

        ImageView mImageViewBack = (ImageView) findViewById(R.id.activity_health_mode_history_imageview_back);
        mRecyclerViewOfDay = (RecyclerView) findViewById(R.id.activity_health_mode_history_day_recyclerview);
        mRecyclerViewOfWeek = (RecyclerView) findViewById(R.id.activity_health_mode_history_week_recyclerview);

//        mTextViewOfDayNoData = (TextView) findViewById(R.id.activity_health_mode_history_day_no_data_found_tv);
        mTextViewSelectedDayOrWeek = (TextView) findViewById(R.id.activity_health_mode_history_date_or_week_tv);

        final TextView mTextViewDayButton = (TextView) findViewById(R.id.activity_health_mode_history_day_button);
        final TextView mTextViewWeekButton = (TextView) findViewById(R.id.activity_health_mode_history_week_button);
        final TextView mTextViewMonthButton = (TextView) findViewById(R.id.activity_health_mode_history_month_button);

        final TextView mTextViewHrvTitle = (TextView) findViewById(R.id.activity_health_mode_history_hrv_title);
        final TextView mTextViewEcgTitle = (TextView) findViewById(R.id.activity_health_mode_history_ecg_title);

        final ImageView mImageViewPrevious = (ImageView) findViewById(R.id.activity_health_mode_history_previous_image_view);
        final ImageView mImageViewNext = (ImageView) findViewById(R.id.activity_health_mode_history_next_image_view);

        mImageViewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTabType == TabType.DAY) {
                    moveToPreviousDay();
                    setSelectedDateText();
                } else if (selectedTabType == TabType.WEEK) {
                    moveToPreviousWeek();

                    mTextViewSelectedDayOrWeek.setText(getString(R.string.text_dash_separator,
                            sdf.format(startDayOfTheWeek.getTime()),
                            sdf.format(lastDayOfTheWeek.getTime())));
                }
                setValuesForChart();
            }
        });
        mImageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTabType == TabType.DAY) {

                    if (isThisDayToday()) {
                        return;
                    }


                    moveToNextDay();
                    setSelectedDateText();

                } else if (selectedTabType == TabType.WEEK) {
                    if (isThisWeekCurrentWeek()) {
                        return;
                    }

                    moveToNextWeek();

                    mTextViewSelectedDayOrWeek.setText(getString(R.string.text_dash_separator,
                            sdf.format(startDayOfTheWeek.getTime()),
                            sdf.format(lastDayOfTheWeek.getTime())));
                }

                setValuesForChart();

            }
        });


        mTextViewDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTabType = TabType.DAY;

                resetSelectedDay();

                setValuesForChart();

                mTextViewSelectedDayOrWeek.setVisibility(View.VISIBLE);
                mImageViewPrevious.setVisibility(View.VISIBLE);
                mImageViewNext.setVisibility(View.VISIBLE);


                mTextViewDayButton.setBackground(mDrawableBackgroundSkyBlue);
                mTextViewWeekButton.setBackground(mDrawableBackgroundGray);
                mTextViewMonthButton.setBackground(mDrawableBackgroundGray);


                mDayScatterChartCardView.setVisibility(View.VISIBLE);
                setVisibilityOfDayRecyclerView(true);
                mRecyclerViewOfWeek.setVisibility(View.GONE);

                mTextViewHrvTitle.setVisibility(View.VISIBLE);
                mTextViewEcgTitle.setVisibility(View.VISIBLE);
            }
        });

        mTextViewWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTabType = TabType.WEEK;

                resetSelectedWeek();

                setValuesForChart();

                mTextViewSelectedDayOrWeek.setVisibility(View.VISIBLE);
                mImageViewPrevious.setVisibility(View.VISIBLE);
                mImageViewNext.setVisibility(View.VISIBLE);

                mTextViewDayButton.setBackground(mDrawableBackgroundGray);
                mTextViewWeekButton.setBackground(mDrawableBackgroundSkyBlue);
                mTextViewMonthButton.setBackground(mDrawableBackgroundGray);


                mDayScatterChartCardView.setVisibility(View.GONE);
                setVisibilityOfDayRecyclerView(false);
                mRecyclerViewOfWeek.setVisibility(View.VISIBLE);

                mTextViewHrvTitle.setVisibility(View.GONE);
                mTextViewEcgTitle.setVisibility(View.GONE);
            }
        });

        mTextViewMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTabType = TabType.MONTH;

                Toast.makeText(ActivityHealthModeHistory.this, "Coming Soon", Toast.LENGTH_SHORT).show();

//                mTextViewSelectedDayOrWeek.setVisibility(View.GONE);
//                mImageViewPrevious.setVisibility(View.GONE);
//                mImageViewNext.setVisibility(View.GONE);
//
//                mTextViewDayButton.setBackground(mDrawableBackgroundGray);
//                mTextViewWeekButton.setBackground(mDrawableBackgroundGray);
//                mTextViewMonthButton.setBackground(mDrawableBackgroundSkyBlue);
//
//                mDayScatterChartCardView.setVisibility(View.GONE);
//                setVisibilityOfDayRecyclerView(false);
//                mRecyclerViewOfWeek.setVisibility(View.GONE);
//
//                mTextViewHrvTitle.setVisibility(View.GONE);
//                mTextViewEcgTitle.setVisibility(View.GONE);
            }
        });

        scatterChart = (ScatterChart) findViewById(R.id.activity_health_mode_history_history_graph);
        mDayScatterChartCardView = (CardView) findViewById(R.id.activity_health_mode_history_history_graph_card_view);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setChart(new ArrayList<Entry>());


        //Setting up the time for the first time.
        resetSelectedWeek();
        resetSelectedDay();
//        lastDayOfTheWeek = Calendar.getInstance();
//        String lastDay = sdf.format(lastDayOfTheWeek.getTime());
//        startDayOfTheWeek.add(Calendar.DATE, -6);
//
//        String firstDay = sdf.format(startDayOfTheWeek.getTime());
//        Log.d(TAG, firstDay);
//        Log.d(TAG, lastDay);

        setRecyclerViewDataOfToday();
        setRecyclerViewDataOfWeek();

        setValuesForChart();

    }

    private void resetSelectedDay() {
        currentSelectedDate = Calendar.getInstance();
        setSelectedDateText();
    }

    private void setSelectedDateText() {
        if (isThisDayToday()) {
            mTextViewSelectedDayOrWeek.setText(getString(R.string.text_today));
        } else if (isThisDayYesterday()) {
            mTextViewSelectedDayOrWeek.setText(getString(R.string.text_yesterday));
        } else {
            mTextViewSelectedDayOrWeek.setText(sdf.format(currentSelectedDate.getTime()));
        }
    }

    private void resetSelectedWeek() {
        lastDayOfTheWeek = Calendar.getInstance();
        startDayOfTheWeek = Calendar.getInstance();

        //************************************************************************************
        //                                  June 2021
        //************************************************************************************

        lastDayOfTheWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);


        if (lastDayOfTheWeek.getTime().before(todayCalendar.getTime()) &&
                !areThisSameDates(lastDayOfTheWeek, todayCalendar)
        ) {
            lastDayOfTheWeek.add(Calendar.DATE, 7);
        }

        startDayOfTheWeek.setTime(lastDayOfTheWeek.getTime());


//        Calendar tempDayOfWeek=Calendar.getInstance();
//        int date=tempDayOfWeek.get(Calendar.DAY_OF_MONTH);
//
//        tempDayOfWeek.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
//        String dateOfSunday=sdf.format(tempDayOfWeek.getTime());
//
//
//        if(tempDayOfWeek.getTime().before(todayCalendar.getTime())&& ! areThisSameDates(
//                tempDayOfWeek,todayCalendar
//        )){
//            tempDayOfWeek.add(Calendar.DATE,7);
//            dateOfSunday=sdf.format(tempDayOfWeek.getTime());
//        }
//
//        tempDayOfWeek.add(Calendar.DATE,-6);
//        String dateOfMonday=sdf.format(tempDayOfWeek.getTime());
//
//        System.out.println(dateOfSunday);
//        System.out.println("abc");
        //************************************************************************************
        startDayOfTheWeek.add(Calendar.DATE, -6);
        mTextViewSelectedDayOrWeek.setText(getString(R.string.text_dash_separator,
                sdf.format(startDayOfTheWeek.getTime()),
                sdf.format(lastDayOfTheWeek.getTime())));
    }

    private ArrayList<String> getWeekDays(Calendar startDayReceived,
                                          Calendar endDayReceived) {

        Calendar startDay = Calendar.getInstance();//This is required to avoid the change in reference variable.
        Calendar endDay = Calendar.getInstance();//This is required to avoid the change in reference variable.

        startDay.setTime(startDayReceived.getTime());
        endDay.setTime(endDayReceived.getTime());
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "getWeekDays()");
        Log.d(TAG, "___________________________________________________");

        Log.d(TAG, sdf.format(startDay.getTime()));
        Log.d(TAG, sdf.format(endDay.getTime()));


        ArrayList<String> days = new ArrayList<String>();
        days.add(" ");
        Log.d(TAG, "Inside While........");
        while (startDay.before(endDay)) {

            Log.d(TAG, sdf.format(startDay.getTime()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDay.getTime());
            days.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
                    Locale.US));
            startDay.add(Calendar.DATE, 1);
        }
        Log.d(TAG, "**************************");
        Log.d(TAG, "Result=" + days.toString());
        Log.d(TAG, "**************************");
        Log.d(TAG, "___________________________________________________");
        return days;
    }

    private void moveToPreviousDay() {
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "moveToPreviousDay()");
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "Before Update:");
        Log.d(TAG, sdf.format(currentSelectedDate.getTime()));


        currentSelectedDate.add(Calendar.DATE, -1);

        Log.d(TAG, "\nAfter Update:");
        Log.d(TAG, sdf.format(currentSelectedDate.getTime()));
        Log.d(TAG, "___________________________________________________");

    }

    private void moveToNextDay() {
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "moveToNextDay()");
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "Before Update:");
        Log.d(TAG, sdf.format(currentSelectedDate.getTime()));


        currentSelectedDate.add(Calendar.DATE, 1);

        Log.d(TAG, "\nAfter Update:");
        Log.d(TAG, sdf.format(currentSelectedDate.getTime()));
        Log.d(TAG, "___________________________________________________");

    }

    private boolean isThisDayToday() {
        todayCalendar = Calendar.getInstance();
        return areThisSameDates(currentSelectedDate, todayCalendar);
    }

    private boolean isThisDayYesterday() {
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DATE, -1);

        return areThisSameDates(currentSelectedDate, yesterdayCalendar);
    }

    private boolean isThisWeekCurrentWeek() {
//        return areThisSameDates(lastDayOfTheWeek, todayCalendar);
        //************************************************************
        //                      June -2021
        //************************************************************
        return areThisSameDates(lastDayOfTheWeek, todayCalendar) || lastDayOfTheWeek.getTime().after(todayCalendar.getTime());
        //************************************************************
    }

    private boolean areThisSameDates(Calendar firstDate, Calendar secondDate) {
        return (firstDate.get(Calendar.DATE) == secondDate.get(Calendar.DATE) &&
                firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH) &&
                firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR));
    }

    private void moveToPreviousWeek() {
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "moveToPreviousWeek()");
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "Before Update:");
        Log.d(TAG, sdf.format(startDayOfTheWeek.getTime()));
        Log.d(TAG, sdf.format(lastDayOfTheWeek.getTime()));

        lastDayOfTheWeek.add(Calendar.DATE, -7);
        startDayOfTheWeek.setTime(lastDayOfTheWeek.getTime());
        startDayOfTheWeek.add(Calendar.DATE, -6);

        Log.d(TAG, "\nAfter Update:");
        Log.d(TAG, sdf.format(startDayOfTheWeek.getTime()));
        Log.d(TAG, sdf.format(lastDayOfTheWeek.getTime()));
        Log.d(TAG, "___________________________________________________");

    }

    private void moveToNextWeek() {
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "moveToNextWeek()");
        Log.d(TAG, "___________________________________________________");
        Log.d(TAG, "Before Update:");
        Log.d(TAG, sdf.format(startDayOfTheWeek.getTime()));
        Log.d(TAG, sdf.format(lastDayOfTheWeek.getTime()));

        startDayOfTheWeek.add(Calendar.DATE, 7);
        lastDayOfTheWeek.setTime(startDayOfTheWeek.getTime());
        lastDayOfTheWeek.add(Calendar.DATE, 6);

        Log.d(TAG, "\nAfter Update:");
        Log.d(TAG, sdf.format(startDayOfTheWeek.getTime()));
        Log.d(TAG, sdf.format(lastDayOfTheWeek.getTime()));
        Log.d(TAG, "___________________________________________________");

    }


    private void setVisibilityOfDayRecyclerView(boolean visible) {
        if (visible) {
            if (mArrayListDayHistoryData.isEmpty()) {
//                mTextViewOfDayNoData.setVisibility(View.VISIBLE);
                mRecyclerViewOfDay.setVisibility(View.GONE);
            } else {
//                mTextViewOfDayNoData.setVisibility(View.GONE);
                mRecyclerViewOfDay.setVisibility(View.VISIBLE);
            }
        } else {
//            mTextViewOfDayNoData.setVisibility(View.GONE);
            mRecyclerViewOfDay.setVisibility(View.GONE);
        }

    }

    private void setRecyclerViewDataOfToday() {

//        getDataForDay();
        //Static data.
//        mArrayListDayHistoryData.add("09:27:33 - 09:44:25");
//        mArrayListDayHistoryData.add("10:27:33 - 10:44:25");
//        mArrayListDayHistoryData.add("11:27:33 - 11:44:25");
//        mArrayListDayHistoryData.add("12:27:33 - 12:44:25");
//        mArrayListDayHistoryData.add("13:27:33 - 13:44:25");
//        mArrayListDayHistoryData.add("14:27:33 - 14:44:25");


        dayHistoryAdapter = new DayHistoryAdapter();

        LinearLayoutManager manager = new LinearLayoutManager(ActivityHealthModeHistory.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewOfDay.setLayoutManager(manager);
        mRecyclerViewOfDay.setAdapter(dayHistoryAdapter);


        setVisibilityOfDayRecyclerView(true);
    }

    private void setRecyclerViewDataOfWeek() {

        //Static data.
        mArrayListWeekHistoryData.add(new WeekData("Daily Avg. HRV", R.color.color_health_mode_history_day));
        mArrayListWeekHistoryData.add(new WeekData(Event.MORNING.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.BEFORE_WORK.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.AFTER_WORK.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.POST_EXERCISE.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.AFTER_MEDICATION.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.AFTER_ALCOHOL.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.AFTER_FOOD.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.BEFORE_BED.getValue(), R.color.color_health_mode_history_week));
        mArrayListWeekHistoryData.add(new WeekData(Event.OTHER.getValue(), R.color.color_health_mode_history_week));


        weekHistoryAdapter = new WeekHistoryAdapter();

        Log.d(TAG, "setRecyclerViewDataOfWeek()");
        Log.d(TAG, "setRecyclerViewDataOfWeek()=" + getWeekDays(startDayOfTheWeek, lastDayOfTheWeek).toString());

        weekHistoryAdapter.setXAxisLabels(getWeekDays(startDayOfTheWeek, lastDayOfTheWeek));

        LinearLayoutManager manager = new LinearLayoutManager(ActivityHealthModeHistory.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewOfWeek.setLayoutManager(manager);
        mRecyclerViewOfWeek.setAdapter(weekHistoryAdapter);
    }

    public void setChart(ArrayList<Entry> values) {
        if (scatterChart == null) {
            return;
        }
        scatterChart.setDrawGridBackground(false);
        scatterChart.setTouchEnabled(false);
        scatterChart.setDragEnabled(false);
        scatterChart.setScaleEnabled(false);

        scatterChart.getAxisRight().setEnabled(false);
        scatterChart.setPinchZoom(false);
        scatterChart.getLegend().setEnabled(false);

//        Legend l = scatterChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
////        l.setTypeface(tfLight);
//        l.setXOffset(5f);
        IAxisValueFormatter yAxisFormatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(Locale.US, "%.1f", value);
            }
        };

        YAxis yl = scatterChart.getAxisLeft();
        yl.setAxisMaximum(5);
        yl.setAxisMinimum(1);
//        yl.set
        yl.setLabelCount(5, true);
        yl.setTextColor(ContextCompat.getColor(this, R.color.color_gray_10));

        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._2ssp), getResources().getDisplayMetrics());

        float sizeYAxis = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._3ssp), getResources().getDisplayMetrics());

        yl.setTextSize(sizeYAxis);
        yl.setGridColor(ContextCompat.getColor(this, R.color.color_gray_10_55_opacity));
        yl.setAxisLineColor(Color.WHITE);
        yl.setGranularity(1f);
        yl.setValueFormatter(yAxisFormatter);//For String Values

        final String[] xAxisLabels = new String[]{"", "12AM", "4AM", "8AM", "12PM", "4PM", "8PM", "12AM"};
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabels[(int) value];
            }
        };


        XAxis xl = scatterChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTextColor(ContextCompat.getColor(this, R.color.color_gray_10));
        xl.setDrawGridLines(false);
        xl.setAxisMaximum(7);
        xl.setAxisMinimum(0);
        xl.setLabelCount(8, true);
        xl.setSpaceMin(1);
        xl.setValueFormatter(formatter);//For String Values
        xl.setTextSize(size);
        xl.setYOffset(15);//This will add space between x-axis and labels.(without this, lowest values will overlap with labels.)


        scatterChart.getDescription().setEnabled(false);


//        ArrayList<Entry> values = getChartData();


        ScatterDataSet scatterDataSet = new ScatterDataSet(values, "abc");
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setColor(ContextCompat.getColor(this, R.color.color_health_mode_history_day));
        scatterDataSet.setDrawValues(false);
        scatterDataSet.setScatterShapeSize(
                this.getResources().getDimension(R.dimen._14sdp));

        ScatterData data = new ScatterData(scatterDataSet);


        scatterChart.setData(data);
        scatterChart.setClipDataToContent(false);//This line is extremely important. Without this line, maximum and minimum data will get cutoff.
        scatterChart.setExtraOffsets(0, 0, 0, 8f);
        scatterChart.invalidate();
    }


//    public void getDataForDay() {
//
//        mDataHolder = mDbHelper.read("SELECT ecg.id as ecg_id, ecg.ecg_value, ecg.date_time, main.start_time, main.end_time, main.total_time, main.date " +
//                " FROM ecg_details as ecg " +
//                "INNER JOIN main_activity_table as main ON ecg.parent_id = main.id where main.date = '"
//                + mStringSelectedDate + "' AND main.type = 'ecg' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");
//
//        mArrayListDayHistoryData = new ArrayList<>();
//
//        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
//
//            for (int i = 0; i < mDataHolder.get_Listholder().size(); i++) {
//                VoBPMListData mVoBPMListData = new VoBPMListData();
//                mVoBPMListData.setBpm_id(mDataHolder.get_Listholder().get(i).get("ecg_id"));
//                mVoBPMListData.setBpm_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_ECG_VALUE));
//                mVoBPMListData.setDate_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_DATE_TIME));
//
//                mVoBPMListData.setStart_date_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME));
//                mVoBPMListData.setEnd_date_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME));
//
//                mVoBPMListData.setStart_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME)));
//                mVoBPMListData.setEnd_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME)));
//                mVoBPMListData.setTotal_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME));
//
//                mVoBPMListData.setDate(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_DATE)));
//                mArrayListDayHistoryData.add(mVoBPMListData);
//            }
//        }
//
//
////        if (mArrayListVoBPMListData.size() > 0) {
////            mTextViewEmpty.setVisibility(View.GONE);
////            mRecyclerViewBPMList.setVisibility(View.VISIBLE);
////
////            mMyBpmAdpter = new FragmentECGList.MyBpmAdpter();
////            LinearLayoutManager manager = new LinearLayoutManager(FragmentECGList.this, LinearLayoutManager.VERTICAL, false);
////            mRecyclerViewBPMList.setLayoutManager(manager);
////            mRecyclerViewBPMList.setAdapter(mMyBpmAdpter);
////        } else {
////            mTextViewEmpty.setVisibility(View.VISIBLE);
////            mRecyclerViewBPMList.setVisibility(View.GONE);
////        }
//
//    }

//    public String getTimeFromDateTime(String strDateTime) {
//
//        if (strDateTime != null && !strDateTime.equalsIgnoreCase(""))
//            try {
//                Date mDate = mSimpleDateFormatDateTime.parse(strDateTime);
//                return mSimpleDateFormatTime.format(mDate);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        return "";
//    }

    private void getTableBpmHistoryData() {
        String strQuery = "";
        switch (selectedTabType) {

            case DAY:
                strQuery = "Select * from main_activity_table WHERE type='ecg' and date = '" + mSimpleDateFormatForQuery.format(currentSelectedDate.getTime()) + "' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC ";

                break;
            case WEEK:
//                strQuery = "Select * from main_activity_table WHERE type='ecg' and date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC ";

                strQuery = "Select * from main_activity_table WHERE type='ecg' and date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id ASC ";

                break;
            case MONTH:
                strQuery = "Select * from main_activity_table WHERE type='ecg' and date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC ";
                break;
        }


        mBpmTableDataHolder = mDbHelper.read(strQuery);

    }

    private void getBpmHistoryData() {
        String strQuery = "";
        switch (selectedTabType) {

            case DAY:
//                strQuery = "Select * from main_activity_table INNER JOIN bpm_details ON main_activity_table.id = bpm_details.parent_id INNER JOIN met_details ON main_activity_table.id = met_details.parent_id INNER JOIN step_details ON main_activity_table.id = step_details.parent_id INNER JOIN distance_details ON main_activity_table.id = distance_details.parent_id INNER JOIN spo2_details ON main_activity_table.id = spo2_details.parent_id INNER JOIN ecg_details ON main_activity_table.id = ecg_details.parent_id INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id WHERE main_activity_table.type='ecg' and main_activity_table.date = '" + mSimpleDateFormatForQuery.format(currentSelectedDate.getTime()) + "' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC ";

//                strQuery = "SELECT ecg.id as ecg_id, ecg.ecg_value, ecg.date_time, main.start_time, main.end_time, main.total_time, main.date " +
//                " FROM ecg_details as ecg " +
//                "INNER JOIN main_activity_table as main ON ecg.parent_id = main.id where main.date = '"
//
//                + mStringSelectedDate + "' AND main.type = 'ecg' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'";


                strQuery = "SELECT ecg.id as ecg_id, ecg.average_hrv, ecg.ecg_value, ecg.date_time, main.start_time, main.end_time, main.total_time, main.date " +
                        " FROM ecg_details as ecg "

                        +
                        "INNER JOIN main_activity_table as main ON ecg.parent_id = main.id "
//                        + "INNER JOIN hrv_details as hrv ON main.id = hrv.parent_id " +
                        + "where main.date = '"

                        + mSimpleDateFormatForQuery.format(currentSelectedDate.getTime()) + "' AND main.type = 'ecg' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'";


                break;
            case WEEK:
////                strQuery = "Select * from main_activity_table INNER JOIN bpm_details ON main_activity_table.id = bpm_details.parent_id INNER JOIN met_details ON main_activity_table.id = met_details.parent_id INNER JOIN step_details ON main_activity_table.id = step_details.parent_id INNER JOIN distance_details ON main_activity_table.id = distance_details.parent_id INNER JOIN spo2_details ON main_activity_table.id = spo2_details.parent_id INNER JOIN ecg_details ON main_activity_table.id = ecg_details.parent_id INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id WHERE main_activity_table.type='ecg' and main_activity_table.date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC ";
//
//                strQuery = "SELECT ecg.id as ecg_id, ecg.average_hrv, ecg.ecg_value, ecg.date_time, main.start_time, main.end_time, main.total_time, main.date " +
//                        " FROM ecg_details as ecg "
//
//                        +
//                        "INNER JOIN main_activity_table as main ON ecg.parent_id = main.id "
////                        + "INNER JOIN hrv_details as hrv ON main.id = hrv.parent_id " +
//                        + "where main.date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' AND main.type = 'ecg' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'";
//
//
//
                strQuery = "SELECT ecg.id as ecg_id, ecg.average_hrv, ecg.ecg_value, ecg.date_time, main.activity_type, main.start_time, main.end_time, main.total_time, main.date FROM ecg_details as ecg INNER JOIN main_activity_table as main ON ecg.parent_id = main.id where main.date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' AND main.type = 'ecg' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'";

                break;
            case MONTH:
                strQuery = "Select * from main_activity_table INNER JOIN bpm_details ON main_activity_table.id = bpm_details.parent_id INNER JOIN met_details ON main_activity_table.id = met_details.parent_id INNER JOIN step_details ON main_activity_table.id = step_details.parent_id INNER JOIN distance_details ON main_activity_table.id = distance_details.parent_id INNER JOIN spo2_details ON main_activity_table.id = spo2_details.parent_id INNER JOIN ecg_details ON main_activity_table.id = ecg_details.parent_id INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id WHERE main_activity_table.type='ecg' and main_activity_table.date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC ";
                break;
        }

        Log.d(TAG, "bpmDataHolder query= " + strQuery);

        mBpmDataHolder = mDbHelper.read(strQuery);


        if (mBpmDataHolder != null && mBpmDataHolder.get_Listholder() != null && mBpmDataHolder.get_Listholder().size() > 0) {

            Log.d(TAG, "AAAAAAAAAAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx");
            for (int i = 0; i < mBpmDataHolder.get_Listholder().size(); i++) {

                Log.d(TAG, "--------------------------------------------------------------------");

                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get("ecg_id"));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_ECG_VALUE));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_DATE_TIME));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_AVERAGE_HRV));

                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME));
                Log.d(TAG, mBpmDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_DATE));

//                VoBPMListData mVoBPMListData = new VoBPMListData();
//                mVoBPMListData.setBpm_id(mDataHolder.get_Listholder().get(i).get("ecg_id"));
//                mVoBPMListData.setBpm_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_ECG_VALUE));
//                mVoBPMListData.setDate_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mECG_DETAILS_DATE_TIME));
//
//                mVoBPMListData.setStart_date_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME));
//                mVoBPMListData.setEnd_date_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME));
//
//                mVoBPMListData.setStart_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME)));
//                mVoBPMListData.setEnd_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME)));
//                mVoBPMListData.setTotal_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME));
//
//                mVoBPMListData.setDate(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_DATE)));
//                mArrayListDayHistoryData.add(mVoBPMListData);
            }
            Log.d(TAG, "AAAAAAAAAAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx");


        }

    }

    private void getHrvHistoryData() {
        String strQuery = "";
        switch (selectedTabType) {

            case DAY:
//                strQuery = "SELECT ecg.id as ecg_id, ecg.average_hrv, ecg.ecg_value, ecg.date_time, main.start_time, main.end_time, main.total_time, main.date " +
//                        " FROM ecg_details as ecg "
//
//                        +
//                        "INNER JOIN main_activity_table as main ON ecg.parent_id = main.id "
////                        + "INNER JOIN hrv_details as hrv ON main.id = hrv.parent_id " +
//                        + "where main.date = '"
//
//                        + mSimpleDateFormatForQuery.format(currentSelectedDate.getTime()) + "' AND main.type = 'ecg' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'";

                strQuery = "Select * from main_activity_table " +
                        "INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id " +
                        "where " +
                        "main_activity_table.type='ecg' and " +
                        "main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' and " +
                        "main_activity_table.date = '" + mSimpleDateFormatForQuery.format(currentSelectedDate.getTime()) + "' " +
                        "ORDER BY main_activity_table.id";


                break;
            case WEEK:
//                strQuery="SELECT ecg.id as ecg_id, ecg.average_hrv, ecg.ecg_value, ecg.date_time, main.activity_type, main.start_time, main.end_time, main.total_time, main.date FROM ecg_details as ecg " +
//                        "INNER JOIN main_activity_table as main ON ecg.parent_id = main.id " +
//                        "where " +
//                        "main.date BETWEEN '"+ mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime())+"' and '"+ mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime())+"' AND " +
//                        "main.type = 'ecg' AND " +
//                        "main.user_id = '"+mUtility.getAppPrefString(Constant.PREFS_USER_ID) +"'";

                strQuery = "Select * from main_activity_table " +
                        "INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id " +
                        "where " +
                        "main_activity_table.type='ecg' and " +
                        "main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' and " +
                        "main_activity_table.date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' " +
                        "ORDER BY main_activity_table.id";
                break;
            case MONTH:
                strQuery = "Select * from main_activity_table INNER JOIN bpm_details ON main_activity_table.id = bpm_details.parent_id INNER JOIN met_details ON main_activity_table.id = met_details.parent_id INNER JOIN step_details ON main_activity_table.id = step_details.parent_id INNER JOIN distance_details ON main_activity_table.id = distance_details.parent_id INNER JOIN spo2_details ON main_activity_table.id = spo2_details.parent_id INNER JOIN ecg_details ON main_activity_table.id = ecg_details.parent_id INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id WHERE main_activity_table.type='ecg' and main_activity_table.date BETWEEN '" + mSimpleDateFormatForQuery.format(startDayOfTheWeek.getTime()) + "' and '" + mSimpleDateFormatForQuery.format(lastDayOfTheWeek.getTime()) + "' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC ";
                break;
        }

        Log.d(TAG, "mHrvDataHolder query= " + strQuery);

        mHrvDataHolder = mDbHelper.read(strQuery);


        if (mHrvDataHolder != null && mHrvDataHolder.get_Listholder() != null && mHrvDataHolder.get_Listholder().size() > 0) {

            Log.d(TAG, "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
            for (int i = 0; i < mHrvDataHolder.get_Listholder().size(); i++) {

                Log.d(TAG, "--------------------------------------------------------------------");
                try {
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_EVENT_TYPE));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_EVENT_COMMENT));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_HRV_VALUE));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_DATE_TIME));

                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME));
                    Log.d(TAG, mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_DATE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");


        }

    }

    private void setValuesForChart() {
        Log.d(TAG, "setValuesForChart() called.");
        getBpmHistoryData();
        getTableBpmHistoryData();
        getHrvHistoryData();//May-2021

        if (mBpmDataHolder.get_Listholder().isEmpty()) {
            Log.d(TAG, "mBpmDataHolder is empty.");
        }
        if (mBpmTableDataHolder.get_Listholder().isEmpty()) {
            Log.d(TAG, "mBpmTableDataHolder is empty.");
        }

        switch (selectedTabType) {

            case DAY:
                //Resetting the chart
                setChart(new ArrayList<Entry>());
                //*****************************************************************************
                //                                  Chart Data
                //*****************************************************************************
                if (!mHrvDataHolder.get_Listholder().isEmpty()) {

                    ArrayList<Entry> chartEntries = new ArrayList<>();
                    for (int i = 0; i < mHrvDataHolder.get_Listholder().size(); i++) {
                        String strBPM = mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV);


                        if (strBPM == null || strBPM.trim().isEmpty()) {
                            strBPM = "1.00";
                        }

                        float fltHRV = 0;
                        fltHRV = Float.parseFloat(strBPM);
                        if (fltHRV < 1.0) {
                            fltHRV = 1.0f;
                        } else if (fltHRV > 5.0) {
                            fltHRV = 5.0f;
                        }

                        ///===============

                        String strStartTime = "";
                        strStartTime = mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_DATE_TIME);

                        if (strStartTime.trim().isEmpty()) {
                            continue;
                        }

                        SimpleDateFormat mSdf24HourFormat = new SimpleDateFormat("HH", Locale.US);

                        int hours = -1;

                        try {
                            hours = Integer.parseInt(mSdf24HourFormat.format(mSimpleDateFormatDateTime.parse(strStartTime)));

                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
//                        final String[] xAxisLabels = new String[]{"", "12AM", "4AM", "8AM", "12PM", "4PM", "8PM", "12AM"};
                        Log.d(TAG, "hours=" + hours);

                        float fixValue = 0.25f;
                        if (hours == 0 || hours > 0 && hours < 4) {

                            if (hours == 0) {
                                chartEntries.add(new Entry(1, fltHRV));
                            } else {
                                float x = 1 + (fixValue * hours);
                                chartEntries.add(new Entry(x, fltHRV));
                            }
                        } else if (hours == 4 || hours > 4 && hours < 8) {
                            int diff = hours - 4;

                            if (diff == 0) {
                                chartEntries.add(new Entry(2, fltHRV));

                            } else {
                                float x = 2 + (fixValue * diff);
                                chartEntries.add(new Entry(x, fltHRV));

                            }

                        } else if (hours == 8 || hours > 8 && hours < 12) {

                            int diff = hours - 8;

                            if (diff == 0) {
                                chartEntries.add(new Entry(3, fltHRV));

                            } else {
                                float x = 3 + (fixValue * diff);
                                chartEntries.add(new Entry(x, fltHRV));

                            }

                        } else if (hours == 12 || hours > 12 && hours < 16) {

                            int diff = hours - 12;

                            if (diff == 0) {
                                chartEntries.add(new Entry(4, fltHRV));

                            } else {
                                float x = 4 + (fixValue * diff);
                                chartEntries.add(new Entry(x, fltHRV));

                            }

                        } else if (hours == 16 || hours > 16 && hours < 20) {

                            int diff = hours - 16;

                            if (diff == 0) {
                                chartEntries.add(new Entry(5, fltHRV));

                            } else {
                                float x = 5 + (fixValue * diff);
                                chartEntries.add(new Entry(x, fltHRV));

                            }

                        } else if (hours == 20 || hours > 20 && hours < 24) {

                            int diff = hours - 20;

                            if (diff == 0) {
                                chartEntries.add(new Entry(6, fltHRV));

                            } else {
                                float x = 6 + (fixValue * diff);
                                chartEntries.add(new Entry(x, fltHRV));

                            }

                        }

                    }
                    setChart(chartEntries);

                }
                //*****************************************************************************
                //                                  List Data
                //*****************************************************************************
                //Restting the previous data.
                mArrayListDayHistoryData.clear();
                //Adding new data.
                if (!mBpmTableDataHolder.get_Listholder().isEmpty()) {

                    String startTime, endTime;
                    String startTimeFromDb, endTimeFromDb;

                    for (int i = 0; i < mBpmTableDataHolder.get_Listholder().size(); i++) {

                        VoBPMListData mVoBPMListData = new VoBPMListData();

                        startTime = "";
                        endTime = "";

                        startTimeFromDb = mBpmTableDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME);
                        endTimeFromDb = mBpmTableDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME);
                        if (startTimeFromDb != null) {
                            if (!startTimeFromDb.trim().isEmpty()) {
                                try {
                                    startTime = mSimpleDateFormatTimeAmPm.format(mSimpleDateFormatDateTime.parse(startTimeFromDb));
                                    endTime = mSimpleDateFormatTimeAmPm.format(mSimpleDateFormatDateTime.parse(endTimeFromDb));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                        mVoBPMListData.setStart_time(startTime);
                        mVoBPMListData.setEnd_time(endTime);

                        mVoBPMListData.setStart_date_time(startTimeFromDb);
                        mVoBPMListData.setEnd_date_time(endTimeFromDb);

                        mArrayListDayHistoryData.add(mVoBPMListData);

                    }
                }


                //Updating the adapter
                mRecyclerViewOfDay.getAdapter().notifyDataSetChanged();
                setVisibilityOfDayRecyclerView(true);
                break;
            case WEEK:
                //Resetting the already existing values.
                for (int i = 0; i < mArrayListWeekHistoryData.size(); i++) {
                    mArrayListWeekHistoryData.get(i).clearValues();
                }
                weekHistoryAdapter.notifyDataSetChanged();
                //*****************************************************************************
                //                                  First Chart Data
                //*****************************************************************************
                if (!mHrvDataHolder.get_Listholder().isEmpty()) {

                    ArrayList<Entry> chartEntries = new ArrayList<>();

                    Calendar selectedDayInWhile = Calendar.getInstance();
                    selectedDayInWhile.setTime(startDayOfTheWeek.getTime());

                    float valueOfX = 0.0f;

                    Calendar lastDay = Calendar.getInstance();
                    lastDay.setTime(lastDayOfTheWeek.getTime());
                    lastDay.add(Calendar.DATE, 1);


                    Log.d(TAG, "lastDay=" + mSimpleDateFormatDateTime.format(lastDay.getTime()));


                    while (selectedDayInWhile.before(lastDay)) {
                        valueOfX += 1;


                        int countData = 0;
                        float avgHRVData = 0.0f;

                        for (int i = 0; i < mHrvDataHolder.get_Listholder().size(); i++) {
//                            if(i>=mBpmTableDataHolder.get_Listholder().size()){
//                                Log.e(TAG, "setValuesForChart- Case Week- i>=mBpmTableDataHolder.get_Listholder().size().");
//                                break;
//                            }
                            String strStartTime = mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_DATE_TIME); //mBpmTableDataHolder.get_Listholder().get(i).get("start_time");

                            if (strStartTime == null || strStartTime.trim().isEmpty()) {
                                Log.e(TAG, "setValuesForChart- Case Week- strStartTime is null or empty. ");
                                continue;
                            }
                            Calendar calStartTime = Calendar.getInstance();
                            try {
                                calStartTime.setTime(mSimpleDateFormatDateTime.parse(strStartTime));
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }

                            if (areThisSameDates(calStartTime, selectedDayInWhile)) {
                                Log.d(TAG, mSimpleDateFormatDateTime.format(calStartTime.getTime()));
                                Log.d(TAG, mSimpleDateFormatDateTime.format(selectedDayInWhile.getTime()));

                                String strBPM = mHrvDataHolder.get_Listholder().get(i).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV);

                                if (strBPM == null || strBPM.trim().isEmpty()) {
                                    strBPM = "1.00";
                                }

                                float fltHRV = 0;
                                fltHRV = Float.parseFloat(strBPM);
                                if (fltHRV < 1.0) {
                                    fltHRV = 1.0f;
                                } else if (fltHRV > 5.0) {
                                    fltHRV = 5.0f;
                                }

                                countData = countData + 1;
                                avgHRVData = avgHRVData + fltHRV;
                            }


                        }


                        float finalHRV = 0.0f;
                        finalHRV = avgHRVData / countData;

                        if (finalHRV < 1.0) {
                            finalHRV = 1.0f;
                        } else if (finalHRV > 5.0) {
                            finalHRV = 5.0f;
                        }
                        if (avgHRVData != 0.0) {
                            chartEntries.add(new Entry(valueOfX, finalHRV));
                            Log.d(TAG, "chartEntry");
                        }


                        selectedDayInWhile.add(Calendar.DATE, 1);

                    }
                    mArrayListWeekHistoryData.get(0).setValues(chartEntries);

                }

                //*****************************************************************************
                //                                  Other Charts' Data
                //*****************************************************************************
                if (!mHrvDataHolder.get_Listholder().isEmpty()) {

                    for (int i = 1; i < mArrayListWeekHistoryData.size(); i++) {

                        Calendar selectedDayInWhile = Calendar.getInstance();
                        selectedDayInWhile.setTime(startDayOfTheWeek.getTime());

                        Calendar lastDay = Calendar.getInstance();
                        lastDay.setTime(lastDayOfTheWeek.getTime());
                        lastDay.add(Calendar.DATE, 1);

                        float valueOfX = 0.0f;

                        Log.d(TAG, "lastDayOfWeek=" + mSimpleDateFormatDateTime.format(lastDayOfTheWeek.getTime()));


                        ArrayList<Entry> chartEntries = new ArrayList<>();
                        while (selectedDayInWhile.before(lastDay)) {
                            valueOfX += 1;
                            Log.d(TAG, mSimpleDateFormatDateTime.format(selectedDayInWhile.getTime()));

                            int countData = 0;
                            float avgHRVData = 0.0f;

                            for (int j = 0; j < mHrvDataHolder.get_Listholder().size(); j++) {
                                if (j >= mHrvDataHolder.get_Listholder().size()) {
                                    Log.e(TAG, "setValuesForChart- Case Week- Other Charts j>=mBpmTableDataHolder.get_Listholder().size().");
                                    break;
                                }
                                String strStartTime = mHrvDataHolder.get_Listholder().get(j).get(DBHelper.mHRV_DETAILS_DATE_TIME);

                                Log.d(TAG, "strStartTime=" + strStartTime);
                                if (strStartTime == null || strStartTime.trim().isEmpty()) {
                                    Log.e(TAG, "setValuesForChart- Case Week- strStartTime is null or empty. ");
                                    selectedDayInWhile.add(Calendar.DATE, 1);

                                    continue;
                                }
                                Calendar calStartTime = Calendar.getInstance();
                                try {
                                    calStartTime.setTime(mSimpleDateFormatDateTime.parse(strStartTime));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    continue;
                                }

                                if (areThisSameDates(calStartTime, selectedDayInWhile)) {
                                    String strEventType = mHrvDataHolder.get_Listholder().get(j).get(DBHelper.mHRV_DETAILS_EVENT_TYPE);
                                    Log.d(TAG, strEventType.toString());
                                    if (strEventType != null && !strEventType.trim().isEmpty() && mArrayListWeekHistoryData.get(i).getName().equalsIgnoreCase(strEventType)) {
                                        String strBPM = mHrvDataHolder.get_Listholder().get(j).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV);

                                        Log.d(TAG, strBPM.toString());

                                        if (strBPM == null || strBPM.trim().isEmpty()) {
                                            strBPM = "1.00";
                                        }

                                        float fltHRV = 0;
                                        fltHRV = Float.parseFloat(strBPM);
                                        if (fltHRV < 1.0) {
                                            fltHRV = 1.0f;
                                        } else if (fltHRV > 5.0) {
                                            fltHRV = 5.0f;
                                        }

                                        countData = countData + 1;
                                        avgHRVData = avgHRVData + fltHRV;

                                    }


                                }


                            }
                            if (countData == 0) {
                                selectedDayInWhile.add(Calendar.DATE, 1);

                                continue;
                            }
                            float finalHRV = avgHRVData / countData;

                            if (finalHRV < 1.0) {
                                finalHRV = 1.0f;
                            } else if (finalHRV > 5.0) {
                                finalHRV = 5.0f;
                            }
                            if (avgHRVData != 0.0) {
                                chartEntries.add(new Entry(valueOfX, finalHRV));
                                Log.d(TAG, "chartEntries +");
                            }

                            selectedDayInWhile.add(Calendar.DATE, 1);

                        }
                        mArrayListWeekHistoryData.get(i).setValues(chartEntries);


                    }


                }

                //Updating the adapter
                mRecyclerViewOfWeek.getAdapter().notifyDataSetChanged();
//                //Resetting the already existing values.
//                for(int i=0;i<mArrayListWeekHistoryData.size();i++){
//                    mArrayListWeekHistoryData.get(i).clearValues();
//                }
//                weekHistoryAdapter.notifyDataSetChanged();
//                //*****************************************************************************
//                //                                  First Chart Data
//                //*****************************************************************************
//                if (!mBpmDataHolder.get_Listholder().isEmpty()) {
//
//                    ArrayList<Entry> chartEntries = new ArrayList<>();
//
//                    Calendar selectedDayInWhile = Calendar.getInstance();
//                    selectedDayInWhile.setTime(startDayOfTheWeek.getTime());
//
//                    float valueOfX = 0.0f;
//
//                    Calendar lastDay = Calendar.getInstance();
//                    lastDay.setTime(lastDayOfTheWeek.getTime());
//                    lastDay.add(Calendar.DATE,1);
//
//
//                    Log.d(TAG,"lastDay="+mSimpleDateFormatDateTime.format(lastDay.getTime()));
//
//
//                    while (selectedDayInWhile.before(lastDay)) {
//                        valueOfX += 1;
//
//
//                        int countData = 0;
//                        float avgHRVData = 0.0f;
//
//                        for (int i = 0; i < mBpmDataHolder.get_Listholder().size(); i++) {
//                            if(i>=mBpmTableDataHolder.get_Listholder().size()){
//                                Log.e(TAG, "setValuesForChart- Case Week- i>=mBpmTableDataHolder.get_Listholder().size().");
//                                break;
//                            }
//                            String strStartTime = mBpmTableDataHolder.get_Listholder().get(i).get("start_time");
//
//                            if (strStartTime == null || strStartTime.trim().isEmpty()) {
//                                Log.e(TAG, "setValuesForChart- Case Week- strStartTime is null or empty. ");
//                                continue;
//                            }
//                            Calendar calStartTime = Calendar.getInstance();
//                            try {
//                                calStartTime.setTime(mSimpleDateFormatDateTime.parse(strStartTime));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                continue;
//                            }
//
//                            if (areThisSameDates(calStartTime, selectedDayInWhile)) {
//                                Log.d(TAG,mSimpleDateFormatDateTime.format(calStartTime.getTime()));
//                                Log.d(TAG,mSimpleDateFormatDateTime.format(selectedDayInWhile.getTime()));
//
//                                String strBPM = mBpmDataHolder.get_Listholder().get(i).get("average_hrv");
//
//                                if (strBPM == null || strBPM.trim().isEmpty()) {
//                                    strBPM = "1.00";
//                                }
//
//                                float fltHRV = 0;
//                                fltHRV = Float.parseFloat(strBPM);
//                                if (fltHRV < 1.0) {
//                                    fltHRV = 1.0f;
//                                } else if (fltHRV > 5.0) {
//                                    fltHRV = 5.0f;
//                                }
//
//                                countData = countData + 1;
//                                avgHRVData = avgHRVData + fltHRV;
//                            }
//
//
//                        }
//
//
//                        float finalHRV = 0.0f;
//                        finalHRV = avgHRVData / countData;
//
//                        if (finalHRV < 1.0) {
//                            finalHRV = 1.0f;
//                        } else if (finalHRV > 5.0) {
//                            finalHRV = 5.0f;
//                        }
//                        if (avgHRVData != 0.0) {
//                            chartEntries.add(new Entry(valueOfX, finalHRV));
//                            Log.d(TAG,"chartEntry");
//                        }
//
//
//                        selectedDayInWhile.add(Calendar.DATE, 1);
//
//                    }
//                    mArrayListWeekHistoryData.get(0).setValues(chartEntries);
//
//                }
//
//                //*****************************************************************************
//                //                                  Other Charts' Data
//                //*****************************************************************************
//                if (!mBpmTableDataHolder.get_Listholder().isEmpty()) {
//
//                    for (int i = 1; i < mArrayListWeekHistoryData.size(); i++) {
//
//                        if (!mBpmDataHolder.get_Listholder().isEmpty() && !mBpmTableDataHolder.get_Listholder().isEmpty()) {
//                            Calendar selectedDayInWhile = Calendar.getInstance();
//                            selectedDayInWhile.setTime(startDayOfTheWeek.getTime());
//
//                            Calendar lastDay = Calendar.getInstance();
//                            lastDay.setTime(lastDayOfTheWeek.getTime());
//                            lastDay.add(Calendar.DATE,1);
//
//                            float valueOfX = 0.0f;
//
//                            Log.d(TAG,"lastDayOfWeek="+mSimpleDateFormatDateTime.format(lastDayOfTheWeek.getTime()));
//
//
//
//                            ArrayList<Entry> chartEntries = new ArrayList<>();
//                            while (selectedDayInWhile.before(lastDay)) {
//                                valueOfX+=1;
//                                Log.d(TAG,mSimpleDateFormatDateTime.format(selectedDayInWhile.getTime()));
//
//                                int countData = 0;
//                                float avgHRVData = 0.0f;
//
//                                for (int j = 0; j < mBpmDataHolder.get_Listholder().size(); j++) {
//                                    if(j>=mBpmTableDataHolder.get_Listholder().size()){
//                                        Log.e(TAG, "setValuesForChart- Case Week- Other Charts j>=mBpmTableDataHolder.get_Listholder().size().");
//                                        break;
//                                    }
//                                    String strStartTime = mBpmTableDataHolder.get_Listholder().get(j).get("start_time");
//
//                                    Log.d(TAG,"strStartTime="+strStartTime);
//                                    if (strStartTime == null || strStartTime.trim().isEmpty()) {
//                                        Log.e(TAG, "setValuesForChart- Case Week- strStartTime is null or empty. ");
//                                        selectedDayInWhile.add(Calendar.DATE, 1);
//
//                                        continue;
//                                    }
//                                    Calendar calStartTime = Calendar.getInstance();
//                                    try {
//                                        calStartTime.setTime(mSimpleDateFormatDateTime.parse(strStartTime));
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        continue;
//                                    }
//
//                                    if (areThisSameDates(calStartTime, selectedDayInWhile)) {
//                                        String strEventType = mBpmDataHolder.get_Listholder().get(j).get("activity_type");
//                                        Log.d(TAG,strEventType.toString());
//                                        if (strEventType != null && !strEventType.trim().isEmpty() && mArrayListWeekHistoryData.get(i).getName().equalsIgnoreCase(strEventType)) {
//                                            String strBPM = mBpmDataHolder.get_Listholder().get(j).get("average_hrv");
//
//                                            Log.d(TAG,strBPM.toString());
//
//                                            if (strBPM == null || strBPM.trim().isEmpty()) {
//                                                strBPM = "1.00";
//                                            }
//
//                                            float fltHRV = 0;
//                                            fltHRV = Float.parseFloat(strBPM);
//                                            if (fltHRV < 1.0) {
//                                                fltHRV = 1.0f;
//                                            } else if (fltHRV > 5.0) {
//                                                fltHRV = 5.0f;
//                                            }
//
//                                            countData = countData + 1;
//                                            avgHRVData = avgHRVData + fltHRV;
//
//                                        }
//
//
//                                    }
//
//
//                                }
//                                if (countData == 0) {
//                                    selectedDayInWhile.add(Calendar.DATE, 1);
//
//                                    continue;
//                                }
//                                float finalHRV = avgHRVData / countData;
//
//                                if (finalHRV < 1.0) {
//                                    finalHRV = 1.0f;
//                                } else if (finalHRV > 5.0) {
//                                    finalHRV = 5.0f;
//                                }
//                                if (avgHRVData != 0.0) {
//                                    chartEntries.add(new Entry(valueOfX, finalHRV));
//                                    Log.d(TAG,"chartEntries +");
//                                }
//
//                                selectedDayInWhile.add(Calendar.DATE, 1);
//
//                            }
//                            mArrayListWeekHistoryData.get(i).setValues(chartEntries);
//
//                        }
//
//
//                    }
//
//
//                }
//
//                //Updating the adapter
//                mRecyclerViewOfWeek.getAdapter().notifyDataSetChanged();
//                break;
            case MONTH:
                break;


        }


    }

    public class DayHistoryAdapter extends RecyclerView.Adapter<DayHistoryAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_health_history_ecg_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            if (mArrayListDayHistoryData.get(position) != null) {

                holder.mTextViewTime.setText(
                        getString(R.string.text_dash_separator,
                                mArrayListDayHistoryData.get(position).getStart_time(),
                                mArrayListDayHistoryData.get(position).getEnd_time()
                        ));
                holder.mClMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Implementation Pending.
                        Intent mIntent = new Intent(ActivityHealthModeHistory.this, FragmentECGDetails.class);
                        mIntent.putExtra("mStringStartDataTime", mArrayListDayHistoryData.get(position).getStart_date_time());
                        mIntent.putExtra("mStringType", "ecg");
                        startActivity(mIntent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mArrayListDayHistoryData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTextViewTime;
            ConstraintLayout mClMain;

            public ViewHolder(View itemView) {
                super(itemView);


                mTextViewTime = (TextView) itemView.findViewById(R.id.raw_health_history_ecg_item_time);
                mClMain = (ConstraintLayout) itemView.findViewById(R.id.raw_health_history_ecg_item_main_cl);
            }
        }
    }


    public class WeekHistoryAdapter extends RecyclerView.Adapter<WeekHistoryAdapter.ViewHolder> {

        String[] xAxisLabels = new String[]{"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        public void setXAxisLabels(ArrayList<String> xAxisLabels) {

            Log.d(TAG, "setXAxisLabels()");
            Log.d(TAG, xAxisLabels.toString());

            if (xAxisLabels.isEmpty()) {
                return;
            }
            if (xAxisLabels.size() < this.xAxisLabels.length) {
                Log.e(TAG, "WeekHistoryAdapter- Error: setting less number of labels than values.");
                return;
            }

            String[] labels = new String[xAxisLabels.size()];
            for (int j = 0; j < xAxisLabels.size(); j++) {
                labels[j] = xAxisLabels.get(j);
            }

            this.xAxisLabels = labels;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_health_history_week_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            if (mArrayListWeekHistoryData.get(position) != null) {

                holder.mTextViewTitle.setText(mArrayListWeekHistoryData.get(position).getName());


                ArrayList<Entry> chartValues = mArrayListWeekHistoryData.get(position).getValues();
                for (int i = 0; i < chartValues.size(); i++) {
                    if (chartValues.get(i).getY() < 1) {
                        chartValues.get(i).setY(1);
                    }
                }

                setChart(holder.mScatterChart, chartValues, mArrayListWeekHistoryData.get(position).colorOfDot);
            }
        }

        @Override
        public int getItemCount() {
            return mArrayListWeekHistoryData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTextViewTitle;
            ScatterChart mScatterChart;

            public ViewHolder(View itemView) {
                super(itemView);


                mTextViewTitle = (TextView) itemView.findViewById(R.id.raw_health_history_week_item_title);
                mScatterChart = (ScatterChart) itemView.findViewById(R.id.raw_health_history_week_item_graph);
            }
        }

        private void setChart(ScatterChart scatterChart, ArrayList<Entry> values, int colorOfDot) {
            if (scatterChart == null) {
                return;
            }
            scatterChart.setDrawGridBackground(false);
            scatterChart.setTouchEnabled(false);
            scatterChart.setDragEnabled(false);
            scatterChart.setScaleEnabled(false);

            scatterChart.getAxisRight().setEnabled(false);
            scatterChart.setPinchZoom(false);
            scatterChart.getLegend().setEnabled(false);

//        Legend l = scatterChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
////        l.setTypeface(tfLight);
//        l.setXOffset(5f);
            IAxisValueFormatter yAxisFormatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.format(Locale.US, "%.1f", value);
                }
            };

            YAxis yl = scatterChart.getAxisLeft();
            yl.setAxisMaximum(5);
            yl.setAxisMinimum(1);
            yl.setLabelCount(5, true);
            yl.setTextColor(ContextCompat.getColor(ActivityHealthModeHistory.this, R.color.color_gray_10));

            float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._3ssp), getResources().getDisplayMetrics());
            yl.setTextSize(size);
            yl.setGridColor(ContextCompat.getColor(ActivityHealthModeHistory.this, R.color.color_gray_10_55_opacity));
            yl.setAxisLineColor(Color.WHITE);
            yl.setGranularity(1f);
            yl.setValueFormatter(yAxisFormatter);//For String Values


            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xAxisLabels[(int) value];
                }
            };


            XAxis xl = scatterChart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setTextColor(ContextCompat.getColor(ActivityHealthModeHistory.this, R.color.color_gray_10));
            xl.setDrawGridLines(false);
            xl.setAxisMaximum(7);
            xl.setAxisMinimum(0);
            xl.setLabelCount(8, true);
            xl.setSpaceMin(1);
            xl.setValueFormatter(formatter);//For String Values
            xl.setTextSize(size);
            xl.setYOffset(15);//This will add space between x-axis and labels.(without this, lowest values will overlap with labels.)


            scatterChart.getDescription().setEnabled(false);


            ScatterDataSet scatterDataSet = new ScatterDataSet(values, "abc");
            scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            scatterDataSet.setColor(ContextCompat.getColor(ActivityHealthModeHistory.this, colorOfDot));
            scatterDataSet.setDrawValues(false);
            scatterDataSet.setScatterShapeSize(
                    ActivityHealthModeHistory.this.getResources().getDimension(R.dimen._14sdp));

            ScatterData data = new ScatterData(scatterDataSet);


            scatterChart.setData(data);
            scatterChart.setClipDataToContent(false);//This line is extremely important. Without this line, maximum and minimum data will get cutoff.

            scatterChart.setExtraOffsets(0, 0, 0, 5f);
            scatterChart.invalidate();
        }
    }


//    @Override
//    protected void onDestroy() {
//        MainActivity.removeDeviceStatusListner(TAG);
//        super.onDestroy();
//    }
//
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
//
//                            Intent intent = new Intent(ActivityHealthModeHistory.this, MainActivity.class);
//                            intent.putExtra(MainActivity.SHOW_NO_DEVICE_CONNECTED_DIALOG, true);
//                            startActivity(intent);
//                            finishAffinity();
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
//        }, TAG);
//
//    }


}