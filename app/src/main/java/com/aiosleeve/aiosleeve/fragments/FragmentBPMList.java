package com.aiosleeve.aiosleeve.fragments;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.VO.VoBPMListData;
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

public class FragmentBPMList extends AppCompatActivity {
    public static final String TAG="FragmentBPMList";

    View createView;


    RecyclerView mRecyclerViewBPMList;

    TextView mTextViewEmpty;
    TextView mTextViewDate;

    String mStringSelectedDate = "";
    String mStringType = "bpm";

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    MyBpmAdpter mMyBpmAdpter;

    ArrayList<VoBPMListData> mArrayListVoBPMListData = new ArrayList<>();

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

        mUtility = new Utility(FragmentBPMList.this);

        mStringSelectedDate = mUtility.getDate();

        mDbHelper = new DBHelper(FragmentBPMList.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerViewBPMList = (RecyclerView) findViewById(R.id.fragment_bpm_list_recyclerview);

        mTextViewDate = (TextView) findViewById(R.id.fragment_bpm_list_txt_data);
        mTextViewEmpty = (TextView) findViewById(R.id.fragment_bpm_list_txt_scanning);
        mTextViewHeader = (TextView) findViewById(R.id.fragment_bpm_textview_header);

        mImageViewBack = (ImageView) findViewById(R.id.fragment_bpm_imageview_back);
        mImageViewAddStory = (ImageView) findViewById(R.id.fragment_bpm_imageview_add_story);
        mTextViewHeader.setText(getResources().getString(R.string.tital_bpm_details));

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

        mRecyclerViewBPMList.addOnItemTouchListener(new RecyclerItemClickListener(FragmentBPMList.this, mRecyclerViewBPMList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent mIntent = new Intent(FragmentBPMList.this, FragmentBPMDetails.class);
                        mIntent.putExtra("mStringStartDataTime", mArrayListVoBPMListData.get(position).getStart_date_time());
                        mIntent.putExtra("mStringType", mStringType);
                        startActivity(mIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        getData();
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

    public void openDatePickerDialog() {

        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(FragmentBPMList.this, R.style.DialogTheme,
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

        mDataHolder = mDbHelper.read("SELECT bpm.id as bpm_id, bpm.bpm_value, bpm.date_time, main.start_time, main.end_time, main.total_time, main.date  FROM bpm_details as bpm INNER JOIN main_activity_table as main ON bpm.parent_id = main.id where main.date = '"
                + mStringSelectedDate + "' AND main.type = '" + mStringType + "' AND main.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");

        mArrayListVoBPMListData = new ArrayList<>();

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {

            for (int i = 0; i < mDataHolder.get_Listholder().size(); i++) {
                VoBPMListData mVoBPMListData = new VoBPMListData();
                mVoBPMListData.setBpm_id(mDataHolder.get_Listholder().get(i).get("bpm_id"));
                mVoBPMListData.setBpm_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mBPM_DETAILS_BPM_Value));
                mVoBPMListData.setDate_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mBPM_DETAILS_date_time));

                mVoBPMListData.setStart_date_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME));
                mVoBPMListData.setEnd_date_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME));

                mVoBPMListData.setStart_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME)));
                mVoBPMListData.setEnd_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME)));
                mVoBPMListData.setTotal_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME));

                mVoBPMListData.setDate(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_DATE)));
                mArrayListVoBPMListData.add(mVoBPMListData);
            }
        }


        if (mArrayListVoBPMListData.size() > 0) {
            mTextViewEmpty.setVisibility(View.GONE);
            mRecyclerViewBPMList.setVisibility(View.VISIBLE);

            mMyBpmAdpter = new MyBpmAdpter();
            LinearLayoutManager manager = new LinearLayoutManager(FragmentBPMList.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewBPMList.setLayoutManager(manager);
            mRecyclerViewBPMList.setAdapter(mMyBpmAdpter);
        } else {
            mTextViewEmpty.setVisibility(View.VISIBLE);
            mRecyclerViewBPMList.setVisibility(View.GONE);
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

    public class MyBpmAdpter extends RecyclerView.Adapter<FragmentBPMList.MyBpmAdpter.ViewHolder> {

        @Override
        public FragmentBPMList.MyBpmAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bpm_data_list, parent, false);
            return new FragmentBPMList.MyBpmAdpter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FragmentBPMList.MyBpmAdpter.ViewHolder holder, final int position) {

            if (mArrayListVoBPMListData.get(position) != null) {

                holder.mTextViewName.setText(getResources().getString(R.string.tital_bpm_activity));
                holder.mTextViewTime.setText(mArrayListVoBPMListData.get(position).getStart_time() + " - "
                        + mArrayListVoBPMListData.get(position).getEnd_time());
            }
        }

        @Override
        public int getItemCount() {
            return mArrayListVoBPMListData.size();
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
