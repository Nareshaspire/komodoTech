package com.aiosleeve.aiosleeve;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiosleeve.aiosleeve.VO.VoBPMListData;
import com.aiosleeve.aiosleeve.VO.VoSleepDataList;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.fragments.FragmentECGDetails;
import com.aiosleeve.aiosleeve.fragments.FragmentECGList;
import com.aiosleeve.aiosleeve.fragments.FragmentSleepDataList;
import com.aiosleeve.aiosleeve.fragments.FragmentSleepDetails;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.RecyclerItemClickListener;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivitySleepModeHistory extends AppCompatActivity {
    public static final String TAG = "ActivitySleepHistory";
    RecyclerView mRecyclerViewSleepList;

    TextView mTextViewEmpty;
    TextView mTextViewDate;

    String mStringSelectedDate = "";

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    MySleepAdapter mMySleepAdpter;

    ArrayList<VoSleepDataList> mArrayListVoSleepListData = new ArrayList<>();

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    SimpleDateFormat mSimpleDateFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.US);

    SimpleDateFormat mSimpleDateFormatForQuery = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;


    Utility mUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_mode_history);
        mUtility = new Utility(ActivitySleepModeHistory.this);

        mUtility.changeStatusbarColor(R.color.color_violet);

        mStringSelectedDate = mUtility.getDate();

        mDbHelper = new DBHelper(ActivitySleepModeHistory.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerViewSleepList = (RecyclerView) findViewById(R.id.fragment_bpm_list_recyclerview);

        mTextViewDate = (TextView) findViewById(R.id.fragment_bpm_list_txt_data);
        mTextViewEmpty = (TextView) findViewById(R.id.fragment_bpm_list_txt_scanning);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_bpm_imageview_back);
        mImageViewAddStory = (ImageView) findViewById(R.id.fragment_bpm_imageview_add_story);

        mTextViewDate.setText(mStringSelectedDate);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageViewAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        mRecyclerViewSleepList.addOnItemTouchListener(new RecyclerItemClickListener(ActivitySleepModeHistory.this, mRecyclerViewSleepList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent mIntent = new Intent(ActivitySleepModeHistory.this, FragmentSleepDetails.class);
                        mIntent.putExtra("mStringStartDataTime", mArrayListVoSleepListData.get(position).getmStringStartDateTime());
                        startActivity(mIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));


        //************************************************************
        getData();
    }


    public void openDatePickerDialog() {

        final Calendar c = Calendar.getInstance();

        try {
            //Setting up the default set up date.
            c.setTime(mSimpleDateFormatForQuery.parse(mStringSelectedDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(ActivitySleepModeHistory.this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mStringSelectedDate = getProperTimeFormat(year) + "-" + getProperTimeFormat((monthOfYear + 1)) + "-" + getProperTimeFormat(dayOfMonth);
                        mTextViewDate.setText(mStringSelectedDate);

                        getData();
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime() - 10000);
        datePickerDialog.show();
    }

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    public void getData() {

        mDataHolder = mDbHelper.read("SELECT * FROM " + DBHelper.mTableSleepDetail + " where " + DBHelper.mSLEEP_DETAIL_DATE + "= '"
                + mStringSelectedDate + "' AND " + DBHelper.mSLEEP_DETAIL_USER_ID + "='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'" +
                " AND NULLIF(" + DBHelper.mSLEEP_DETAIL_SLEEP_VALUE + ", ' ') IS NOT NULL");

        mArrayListVoSleepListData = new ArrayList<>();

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {

            for (int i = 0; i < mDataHolder.get_Listholder().size(); i++) {
                VoSleepDataList mVoSleepDataList = new VoSleepDataList();

                mVoSleepDataList.setmStringSleepID(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_ID));
                mVoSleepDataList.setmStringRandomNumber(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_RANDOM_NUMBER));

                mVoSleepDataList.setmStringStartTime(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_START_TIME)));
                mVoSleepDataList.setmStringEndTime(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_END_TIME)));

                mVoSleepDataList.setmStringStartDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_START_TIME));
                mVoSleepDataList.setmStringEndDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_END_TIME));
                mVoSleepDataList.setmStringDate(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_DATE));

                mVoSleepDataList.setmStringValue(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_SLEEP_VALUE));
                mVoSleepDataList.setmStringTimeDiffrence(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_SLEEP_DIFFERENCE));
                mVoSleepDataList.setmStringTotlaTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mSLEEP_DETAIL_TOTAL_TIME));

                mArrayListVoSleepListData.add(mVoSleepDataList);
            }
        }

        if (mArrayListVoSleepListData.size() > 0) {
            mTextViewEmpty.setVisibility(View.GONE);
            mRecyclerViewSleepList.setVisibility(View.VISIBLE);

            mMySleepAdpter = new MySleepAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(ActivitySleepModeHistory.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewSleepList.setLayoutManager(manager);
            mRecyclerViewSleepList.setAdapter(mMySleepAdpter);
        } else {
            mTextViewEmpty.setVisibility(View.VISIBLE);
            mRecyclerViewSleepList.setVisibility(View.GONE);
        }

    }

    public String getTimeFromDateTime(String strDateTime) {

        if (strDateTime != null && !strDateTime.equalsIgnoreCase(""))
            try {
                Date mDate = mSimpleDateFormatDateTime.parse(strDateTime);
                return mSimpleDateFormatTime.format(mDate);
            } catch (Exception e) {

            }
        return "";
    }

    public class MySleepAdapter extends RecyclerView.Adapter<MySleepAdapter.ViewHolder> {

        @Override
        public MySleepAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_sleep_history_item, parent, false);
            return new MySleepAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MySleepAdapter.ViewHolder holder, final int position) {

            if (mArrayListVoSleepListData.get(position) != null) {
                holder.mTextViewName.setText(getResources().getString(R.string.tital_sleep_activity));
                holder.mTextViewTime.setText(mArrayListVoSleepListData.get(position).getmStringStartTime() + " - "
                        + mArrayListVoSleepListData.get(position).getmStringEndTime());
            }
        }

        @Override
        public int getItemCount() {
            return mArrayListVoSleepListData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTextViewName;
            TextView mTextViewTime;

            public ViewHolder(View itemView) {
                super(itemView);


                mTextViewName = (TextView) itemView.findViewById(R.id.raw_sleep_history_item_title);
                mTextViewTime = (TextView) itemView.findViewById(R.id.raw_sleep_history_item_time);
            }
        }
    }


}
