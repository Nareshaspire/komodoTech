package com.aiosleeve.aiosleeve;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiosleeve.aiosleeve.VO.VoActivityHistoryListData;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityActivityHistory extends AppCompatActivity {

    public static final String TAG = "ActivityHistory";

    RecyclerView mRecyclerViewActivityList;

    TextView mTextViewEmpty;
//    TextView mTextViewDate;

    public ImageView mImageViewBack;
//    public ImageView mImageViewAddStory;

    String mStringSelectedDate = "";

    private String mStringStartDate = "";
    private String mStringEndDate = "";

    String mStringType = "bpm";

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;

    DBHelper mDbHelper;
    DataHolder mDataHolder;

    ActivityHistoryAdapter mActivityHistoryAdapter;

    ArrayList<VoActivityHistoryListData> mArrayListVoBPMListData = new ArrayList<>();

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat mSimpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat mSimpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");

    SimpleDateFormat mSimpleDateFormatTime12Hours = new SimpleDateFormat("hh:mm aa");

    Utility mUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_history);
        mUtility = new Utility(ActivityActivityHistory.this);
        mStringSelectedDate = mUtility.getDate();

        Log.d(TAG, "mStringDate=" + mStringSelectedDate);
        mStringSelectedDate = "2021-03-01";

        //Setting up the Dates.
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println(c.getTime());
        mStringStartDate = mSimpleDateFormatDate.format(c.getTime());
        mStringEndDate = mUtility.getDate();
        Log.d(TAG, "mStringStartDate=" + mStringStartDate);
        Log.d(TAG, "mStringEndDate=" + mStringEndDate);

        mDbHelper = new DBHelper(ActivityActivityHistory.this);

        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerViewActivityList = (RecyclerView) findViewById(R.id.activity_history_recyclerview);

        mTextViewEmpty = (TextView) findViewById(R.id.activity_history_no_data_found_tv);

        mImageViewBack = (ImageView) findViewById(R.id.activity_history_imageview_back);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        getData();


    }


    public void getData() {

        String sqlQuery = "Select * from main_activity_table INNER JOIN bpm_details " +
                "ON main_activity_table.id = bpm_details.parent_id INNER JOIN met_details " +
                "ON main_activity_table.id = met_details.parent_id INNER JOIN step_details " +
                "ON main_activity_table.id = step_details.parent_id INNER JOIN distance_details " +
                "ON main_activity_table.id = distance_details.parent_id INNER JOIN spo2_details " +
                "ON main_activity_table.id = spo2_details.parent_id " +
                "WHERE main_activity_table.type='bpm' " +
                "and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' " +//March-2021
                "and main_activity_table.date " +
                "BETWEEN '" + mStringStartDate + "' and '" + mStringEndDate + "' ORDER BY main_activity_table.id DESC ";

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println(sqlQuery);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        mDataHolder = mDbHelper.read(sqlQuery);


        mArrayListVoBPMListData = new ArrayList<>();

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {

            for (int i = 0; i < mDataHolder.get_Listholder().size(); i++) {
                VoActivityHistoryListData mVoBPMListData = new VoActivityHistoryListData();
                mVoBPMListData.setBpm_id(mDataHolder.get_Listholder().get(i).get("bpm_id"));
                mVoBPMListData.setBpm_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mBPM_DETAILS_BPM_Value));
                mVoBPMListData.setDate_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mBPM_DETAILS_date_time));

                mVoBPMListData.setStart_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_START_TIME)));
                mVoBPMListData.setEnd_time(getTimeFromDateTime(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_END_TIME)));
                mVoBPMListData.setTotal_time(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME));

                mVoBPMListData.setDate(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_DATE));


                //2021
                mVoBPMListData.setAverage_bpm_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mBPM_DETAILS_AVERAGE_BPM));
                mVoBPMListData.setMax_bpm_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mBPM_DETAILS_MAX_BPM));
                mVoBPMListData.setActivity_type(mDataHolder.get_Listholder().get(i).get(DBHelper.mMain_ACTIVITY_ACTIVITY_TYPE));
                mVoBPMListData.setMet_value(mDataHolder.get_Listholder().get(i).get(DBHelper.mMET_DETAILS_Average_Met));

                if (isObjectUniqueInList(mVoBPMListData)) {
                    mArrayListVoBPMListData.add(mVoBPMListData);
                }
            }
        }
        if (!mArrayListVoBPMListData.isEmpty()) {
            Log.d(TAG, "__________________________________________________________________");
            Log.d(TAG, "                     Activity History");
            Log.d(TAG, "__________________________________________________________________");
            Log.d(TAG, "Bpm Id: " + mArrayListVoBPMListData.get(0).getBpm_id());
            Log.d(TAG, "Bpm Value: " + mArrayListVoBPMListData.get(0).getBpm_value());
            Log.d(TAG, "Date Time: " + mArrayListVoBPMListData.get(0).getDate_time());
            Log.d(TAG, "Start Time: " + mArrayListVoBPMListData.get(0).getStart_time());//Compare
            Log.d(TAG, "End Time: " + mArrayListVoBPMListData.get(0).getEnd_time());
            Log.d(TAG, "Total Time: " + mArrayListVoBPMListData.get(0).getTotal_time());
            Log.d(TAG, "Date: " + mArrayListVoBPMListData.get(0).getDate());
            Log.d(TAG, "Avg. Bpm Value: " + mArrayListVoBPMListData.get(0).getAverage_bpm_value());//Compare
            Log.d(TAG, "Max. Bpm Value: " + mArrayListVoBPMListData.get(0).getMax_bpm_value());//Compare
            Log.d(TAG, "Activity Type: " + mArrayListVoBPMListData.get(0).getActivity_type());
            Log.d(TAG, "Met Value: " + mArrayListVoBPMListData.get(0).getMet_value());//Compare
            Log.d(TAG, "__________________________________________________________________");

        }

        if (mArrayListVoBPMListData.size() > 0) {
            mTextViewEmpty.setVisibility(View.GONE);
            mRecyclerViewActivityList.setVisibility(View.VISIBLE);

            mActivityHistoryAdapter = new ActivityHistoryAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(ActivityActivityHistory.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewActivityList.setLayoutManager(manager);
            mRecyclerViewActivityList.setAdapter(mActivityHistoryAdapter);
        } else {
            mTextViewEmpty.setVisibility(View.VISIBLE);
            mRecyclerViewActivityList.setVisibility(View.GONE);
        }

    }

    /**
     * To check if the object is unique in mArrayListVoBPMListData.
     * @since 31st May, 2021
     * @param singleObj VoActivityHistoryListData object which is needed to be checked.
     * @return true if the object is unique else false
     */
    private boolean isObjectUniqueInList(VoActivityHistoryListData singleObj) {
        for (VoActivityHistoryListData singleObjInList : mArrayListVoBPMListData) {
            try {
                if (singleObj.getStart_time().equals(singleObjInList.getStart_time()) &&
                        singleObj.getAverage_bpm_value().equals(singleObjInList.getAverage_bpm_value()) &&
                        singleObj.getMax_bpm_value().equals(singleObjInList.getMax_bpm_value()) &&
                        singleObj.getMet_value().equals(singleObjInList.getMet_value())
                ) {
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public String getTimeFromDateTime(String strDateTime) {

        if (strDateTime != null && !strDateTime.equalsIgnoreCase(""))
            try {
                Date mDate = mSimpleDateFormatDateTime.parse(strDateTime);
                return mSimpleDateFormatTime.format(mDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "";
    }

    public class ActivityHistoryAdapter extends RecyclerView.Adapter<ActivityHistoryAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_activity_history_item, parent, false);
            return new ActivityHistoryAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ActivityHistoryAdapter.ViewHolder holder, final int position) {

            if (mArrayListVoBPMListData.get(position) != null) {

                if (mArrayListVoBPMListData.get(position).getExpanded()) {
                    holder.mClMain.setVisibility(View.GONE);
                    holder.mClExpanded.setVisibility(View.VISIBLE);

                } else {

                    holder.mClMain.setVisibility(View.VISIBLE);
                    holder.mClExpanded.setVisibility(View.GONE);
                }

                holder.mImageViewClMain.setText(mArrayListVoBPMListData.get(position).getActivityImage());
                holder.mImageViewClExpanded.setText(mArrayListVoBPMListData.get(position).getActivityImage());

                holder.mClMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < mArrayListVoBPMListData.size(); i++) {
                            mArrayListVoBPMListData.get(i).setExpanded(i == position);
                        }
                        notifyDataSetChanged();

                    }
                });
                holder.mClExpanded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mArrayListVoBPMListData.get(position).setExpanded(false);
                        notifyItemChanged(position);

                    }
                });

                float seconds = Float.parseFloat(mArrayListVoBPMListData.get(position).getTotal_time());
                double min = seconds / 60.0;
                holder.mTextViewTotalDurationValue.setText(
                        getString(
                                R.string.text_min_postfix,
                                min
                        )
                );
                if (mArrayListVoBPMListData.get(position).getMax_bpm_value() == null || mArrayListVoBPMListData.get(position).getMax_bpm_value().trim().isEmpty()) {
                    holder.mTextViewMaxBpmValue.setText("0");
                } else {

                    holder.mTextViewMaxBpmValue.setText(mArrayListVoBPMListData.get(position).getMax_bpm_value());
                }

                if (mArrayListVoBPMListData.get(position).getAverage_bpm_value() == null || mArrayListVoBPMListData.get(position).getAverage_bpm_value().trim().isEmpty()) {
                    holder.mTextViewAverageBpmValue.setText("0");
                } else {

                    holder.mTextViewAverageBpmValue.setText(mArrayListVoBPMListData.get(position).getAverage_bpm_value());
                }

                holder.mTextViewActivityName.setText(mArrayListVoBPMListData.get(position).getActivity_type());




                if(isThisDateOfToday(mArrayListVoBPMListData.get(position).getDate())){
                    holder.mTextViewStartTime.setText(getTimeIn12HourFormat(mArrayListVoBPMListData.get(position).getStart_time()));
                }else{
                    holder.mTextViewStartTime.setText(getDay(mArrayListVoBPMListData.get(position).getDate()));
                }

                if (mArrayListVoBPMListData.get(position).getMet_value() == null || mArrayListVoBPMListData.get(position).getMet_value().trim().isEmpty()) {
                    holder.mTextViewMetValue.setText(
                            getString(
                                    R.string.text_met_postfix,
                                    "0"
                            )
                    );

                } else {
                    holder.mTextViewMetValue.setText(
                            getString(
                                    R.string.text_met_postfix,
                                    mArrayListVoBPMListData.get(position).getMet_value()
                            )
                    );
                }
                holder.mTextViewActivityNameAndTime.setText(

                        getString(
                                R.string.text_comma_separator,
                                mArrayListVoBPMListData.get(position).getActivity_type(),
                                getTimeIn12HourFormat(mArrayListVoBPMListData.get(position).getStart_time()
                                )
                        )
                );


            }
        }

        @Override
        public int getItemCount() {
            return mArrayListVoBPMListData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            EmojiTextView mImageViewClMain;
            EmojiTextView mImageViewClExpanded;

            ConstraintLayout mClMain;
            ConstraintLayout mClExpanded;

            TextView mTextViewStartTime;
            TextView mTextViewActivityName;

            //Bottom Layout Views
            TextView mTextViewActivityNameAndTime;
            TextView mTextViewAverageBpmValue;
            TextView mTextViewMetValue;
            TextView mTextViewMaxBpmValue;
            TextView mTextViewTotalDurationValue;

            public ViewHolder(View itemView) {
                super(itemView);

                mImageViewClMain = (EmojiTextView) itemView.findViewById(R.id.raw_activity_history_item_activity_image_cl_main);
                mImageViewClExpanded = (EmojiTextView) itemView.findViewById(R.id.raw_activity_history_item_activity_image_cl_expanded);

                mClMain = itemView.findViewById(R.id.raw_activity_history_main_cl);
                mClExpanded = itemView.findViewById(R.id.raw_activity_history_bottom_cl);

                mTextViewStartTime = itemView.findViewById(R.id.raw_activity_history_day_of_the_week);
                mTextViewActivityName = itemView.findViewById(R.id.raw_activity_history_activity_name);

                mTextViewActivityNameAndTime = itemView.findViewById(R.id.raw_activity_history_activity_name_and_time);
                mTextViewAverageBpmValue = itemView.findViewById(R.id.raw_activity_history_item_average_bpm_value);
                mTextViewMetValue = itemView.findViewById(R.id.raw_activity_history_item_met_value);
                mTextViewMaxBpmValue = itemView.findViewById(R.id.raw_activity_history_item_max_bpm_value);
                mTextViewTotalDurationValue = itemView.findViewById(R.id.raw_activity_history_item_total_duration_value);
            }
        }

        private String getTimeIn12HourFormat(String dataIn24HourFormat) {
            try {
                return mSimpleDateFormatTime12Hours.format(mSimpleDateFormatTime.parse(dataIn24HourFormat));
            } catch (Exception e) {
                return "";
            }
        }

        private boolean isThisDateOfToday(String date) {
            if(date==null){
                return false;
            }
            try {
                Date suppliedDate=mSimpleDateFormatDate.parse(date);
                Calendar todayCal=Calendar.getInstance();
                Calendar suppliedCal=Calendar.getInstance();
                suppliedCal.setTime(suppliedDate);
                return todayCal.get(Calendar.DAY_OF_MONTH)==suppliedCal.get(Calendar.DAY_OF_MONTH)&&
                        todayCal.get(Calendar.MONTH)==suppliedCal.get(Calendar.MONTH)&&
                        todayCal.get(Calendar.YEAR)==suppliedCal.get(Calendar.YEAR);
            } catch (Exception e) {
                return false;
            }
        }

        private String getDay(String date) {
            try {
                Date suppliedDate=mSimpleDateFormatDate.parse(date);
                SimpleDateFormat dayFormat=new SimpleDateFormat("E");
                return dayFormat.format(suppliedDate);
            } catch (Exception e) {
                return "";
            }
        }
    }

}
