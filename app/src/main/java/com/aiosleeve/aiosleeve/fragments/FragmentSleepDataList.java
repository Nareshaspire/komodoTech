package com.aiosleeve.aiosleeve.fragments;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.VO.VoSleepDataList;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.RecyclerItemClickListener;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oneclickpc001 on 18/1/18.
 */

public class FragmentSleepDataList extends AppCompatActivity {

    public static final String TAG="FragmentSleepDataList";

    RecyclerView mRecyclerViewSleepList;

    TextView mTextViewEmpty;
    TextView mTextViewDate;

    String mStringSelectedDate = "";

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    MySleepAdpter mMySleepAdpter;

    ArrayList<VoSleepDataList> mArrayListVoSleepListData = new ArrayList<>();

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat mSimpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");

    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;

    public TextView mTextViewHeader;

    Utility mUtility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bpm_list);

        mUtility = new Utility(FragmentSleepDataList.this);

        mStringSelectedDate = mUtility.getDate();

        mDbHelper = new DBHelper(FragmentSleepDataList.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerViewSleepList = (RecyclerView) findViewById(R.id.fragment_bpm_list_recyclerview);

        mTextViewDate = (TextView) findViewById(R.id.fragment_bpm_list_txt_data);
        mTextViewEmpty = (TextView) findViewById(R.id.fragment_bpm_list_txt_scanning);
        mTextViewHeader = (TextView) findViewById(R.id.fragment_bpm_textview_header);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_bpm_imageview_back);
        mImageViewAddStory = (ImageView) findViewById(R.id.fragment_bpm_imageview_add_story);
        mTextViewHeader.setText(getResources().getString(R.string.tital_sleep_details));

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

        mRecyclerViewSleepList.addOnItemTouchListener(new RecyclerItemClickListener(FragmentSleepDataList.this, mRecyclerViewSleepList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent mIntent = new Intent(FragmentSleepDataList.this, FragmentSleepDetails.class);
                        mIntent.putExtra("mStringStartDataTime", mArrayListVoSleepListData.get(position).getmStringStartDateTime());
                        startActivity(mIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

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

        getData();
    }

    public void openDatePickerDialog() {

        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(FragmentSleepDataList.this, R.style.DialogTheme,
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

        mDataHolder = mDbHelper.read("SELECT * FROM "+DBHelper.mTableSleepDetail+" where " + DBHelper.mSLEEP_DETAIL_DATE + "= '"
                + mStringSelectedDate + "' AND " + DBHelper.mSLEEP_DETAIL_USER_ID + "='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'" +
                " AND NULLIF("+DBHelper.mSLEEP_DETAIL_SLEEP_VALUE+", ' ') IS NOT NULL");

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

            mMySleepAdpter = new MySleepAdpter();
            LinearLayoutManager manager = new LinearLayoutManager(FragmentSleepDataList.this, LinearLayoutManager.VERTICAL, false);
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

    public class MySleepAdpter extends RecyclerView.Adapter<FragmentSleepDataList.MySleepAdpter.ViewHolder> {

        @Override
        public FragmentSleepDataList.MySleepAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bpm_data_list, parent, false);
            return new FragmentSleepDataList.MySleepAdpter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FragmentSleepDataList.MySleepAdpter.ViewHolder holder, final int position) {

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
            ImageView mImageView;

            TextView mTextViewName;
            TextView mTextViewTime;

            public ViewHolder(View itemView) {
                super(itemView);

                mImageView = (ImageView) itemView.findViewById(R.id.custom_device_list_row_img_arrow);

                mTextViewName = (TextView) itemView.findViewById(R.id.row_bpm_data_list_txt_name);
                mTextViewTime = (TextView) itemView.findViewById(R.id.row_bpm_data_list_txt_start_stop_time);
            }
        }
    }

    @Override
    protected void onDestroy() {
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();
    }
}
