package com.aiosleeve.aiosleeve.fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aiosleeve.aiosleeve.ActivityActivityHistory;
import com.aiosleeve.aiosleeve.ActivityHealthModeHistory;
import com.aiosleeve.aiosleeve.ActivitySleepModeHistory;
import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.EmojiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;


public class FragmentHome extends Fragment {

    public static final String TAG = "FragmentHome";

    View createView;
    MainActivity mMainActivity;

    public TextView mTextViewBPM;
    public TextView mTextViewECG_HRV;
    public TextView mTextViewSleep;
    public TextView mTextViewSettings;
    public TextView mTextViewMedicationTaken;
    public TextView mTextViewLongTermECG;

    public TextView mTextViewHealthModeSummary;
    public TextView mTextViewHealthModeSummaryDetailsButton;
    public TextView mTextViewLatestWorkoutDetailsButton;
    public TextView mTextViewLatestSleepDetailsButton;

    public Button mButtonConnect;
    public Button mButtonDisconnect;

    public CardView mCardViewBPM;
    public CardView mCardViewECG_HRV;
    public CardView mCardViewSleep;
    public CardView mCardViewSettings;
    public CardView mCardViewMedicationTaken;
    public CardView mCardViewLongTermECG;

    public ImageView mImageViewBPM;
    public ImageView mImageViewECG;
    public ImageView mImageViewSLEEP;
    public ImageView mImageViewSETTIGS;
    public ImageView mImageViewMedicationTaken;
    public ImageView mImageViewLongTermECG;


    DBHelper mDbHelper;
    DataHolder mDataHolder;

    //    DataHolder mDataHolderEcgDetails;
    DataHolder mDataHolderStressRecord;
    DataHolder mDataHolderSleepRecord;
    Utility mUtility;


    public FragmentHome() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();

        mUtility = new Utility(mMainActivity);
        mDbHelper = new DBHelper(mMainActivity);
        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        createView = inflater.inflate(R.layout.fragment_home, container, false);

        initBottomNavigationDrawer();

        setUpTheOnClickListeners();

        return createView;
    }

    public void initBottomNavigationDrawer() {
        mButtonConnect = (Button) createView.findViewById(R.id.fragment_home_button_scan_connect);
        mButtonDisconnect = (Button) createView.findViewById(R.id.fragment_home_button_scan_disconnect);

        mCardViewBPM = (CardView) createView.findViewById(R.id.fragment_home_cardview_bpm);
        mCardViewECG_HRV = (CardView) createView.findViewById(R.id.fragment_home_cardview_ecg);
        mCardViewSleep = (CardView) createView.findViewById(R.id.fragment_home_cardview_sleep);
        mCardViewSettings = (CardView) createView.findViewById(R.id.fragment_home_cardview_settings);
        mCardViewLongTermECG = (CardView) createView.findViewById(R.id.fragment_home_cardview_long_term_ecg);
        mCardViewMedicationTaken = (CardView) createView.findViewById(R.id.fragment_home_cardview_medication_taken);

        mTextViewBPM = (TextView) createView.findViewById(R.id.fragment_home_textview_bpm);
        mTextViewECG_HRV = (TextView) createView.findViewById(R.id.fragment_home_textview_ecg_hrv);
        mTextViewSleep = (TextView) createView.findViewById(R.id.fragment_home_textview_sleep);
        mTextViewSettings = (TextView) createView.findViewById(R.id.fragment_home_textview_settings);
        mTextViewLongTermECG = (TextView) createView.findViewById(R.id.fragment_home_textview_long_term_ecg);
        mTextViewMedicationTaken = (TextView) createView.findViewById(R.id.fragment_home_textview_medication_taken);

        mImageViewBPM = (ImageView) createView.findViewById(R.id.img_heart);
        mImageViewECG = (ImageView) createView.findViewById(R.id.img_ecg);
        mImageViewSLEEP = (ImageView) createView.findViewById(R.id.img_sleep);
        mImageViewSETTIGS = (ImageView) createView.findViewById(R.id.img_setting);
        mImageViewLongTermECG = (ImageView) createView.findViewById(R.id.img_long_term_ecg);
        mImageViewMedicationTaken = (ImageView) createView.findViewById(R.id.img_medication_taken);

        mTextViewHealthModeSummary = (TextView) createView.findViewById(R.id.fragment_home_health_mode_summary_title);
        mTextViewHeartRateValue = (TextView) createView.findViewById(R.id.fragment_home_health_mode_heart_rate_value);
        mTextViewHeartDateLabel = createView.findViewById(R.id.fragment_home_health_date_label);
//        mTextViewStressTypeLabel = createView.findViewById(R.id.fragment_home_stress_type_label);
        mTextViewStressTypeNALabel = createView.findViewById(R.id.fragment_home_stress_type_na_label);
        mTextViewStressTypeTextLabel = createView.findViewById(R.id.fragment_home_stress_type_label_text);
        mTextViewStressTypeEmojiLabel = createView.findViewById(R.id.fragment_home_stress_type_label_emoji);
        mClStress = createView.findViewById(R.id.fragment_home_stress_cl);


        mRatingBarHeader = createView.findViewById(R.id.fragment_home_health_mode_rating_bar);
        mTextViewTotalSleepValue = createView.findViewById(R.id.fragment_home_total_sleep_time_value);
        mTextViewDeepSleepPercentageValue = createView.findViewById(R.id.fragment_home_deep_sleep_percentage_value);

        mTextViewUpDownIcon = createView.findViewById(R.id.fragment_home_health_up_down_image);
        mTextViewHeartPreviousValue = createView.findViewById(R.id.fragment_home_health_previous_value);

        mTextViewAverageHeartRateValue = createView.findViewById(R.id.fragment_home_avg_heart_rate_value);
        mTextViewMaxHeartRateValue = createView.findViewById(R.id.fragment_home_max_heart_rate_value);

        mTextViewActivityIntensityValue = createView.findViewById(R.id.fragment_home_activity_intensity_value);
        mTextViewActivityTimeValue = createView.findViewById(R.id.fragment_home_activity_time_value);

        scatterChart = createView.findViewById(R.id.fragment_home_health_mode_graph_view);

        mTextViewHealthModeSummaryDetailsButton = createView.findViewById(R.id.fragment_health_mode_summary_details);
        mTextViewLatestWorkoutDetailsButton = createView.findViewById(R.id.fragment_home_latest_workout_details);
        mTextViewLatestSleepDetailsButton = createView.findViewById(R.id.fragment_home_last_sleep_details);


        getSetViewData();
    }

    TextView mTextViewHeartRateValue;
    TextView mTextViewHeartDateLabel;
    TextView mTextViewStressTypeNALabel;
    TextView mTextViewStressTypeTextLabel;
    EmojiTextView mTextViewStressTypeEmojiLabel;
    ConstraintLayout mClStress;

    TextView mTextViewHeartPreviousValue;
    ImageView mTextViewUpDownIcon;

    TextView mTextViewAverageHeartRateValue;
    TextView mTextViewMaxHeartRateValue;

    TextView mTextViewActivityIntensityValue;
    TextView mTextViewActivityTimeValue;

    TextView mTextViewTotalSleepValue;
    TextView mTextViewDeepSleepPercentageValue;

    RatingBar mRatingBarHeader;
    ScatterChart scatterChart;

    BluetoothAdapter mBtAdapter;

    private void setUpTheOnClickListeners() {
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice == null) {

//                    mMainActivity.removeAllFragmentFromBack();
//                    if (mMainActivity.viewPager != null) {
//                        mMainActivity.viewPager.setCurrentItem(5);
                    if (MainActivity.verifyPermissions(mMainActivity)) {
                        BluetoothManager mBluetoothManager = (BluetoothManager) mMainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
                        mBtAdapter = mBluetoothManager.getAdapter();

                        //If Bluetooth is on.
                        if (mBtAdapter.isEnabled()) {
                            Intent intent = new Intent(mMainActivity, FragmentManageDevice.class);
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mMainActivity);
                            mBuilder.setTitle(getResources().getString(R.string.lbl_bluetooth_enable));
                            mBuilder.setMessage(getResources().getString(R.string.msg_ble_enable));
                            mBuilder.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mBtAdapter.enable();
                                    if (mUtility.isSDK23()) {
                                        mMainActivity.displayLocationSettingsRequest(mMainActivity);
                                    }
                                }
                            });
                            mBuilder.show();
                        }
                    }
//                    }
                }
            }
        });

        mButtonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice == null) {
                    return;
                } else {
                    MainActivity.isDisconnectedFromFragmentHome = true;
                    mMainActivity.disConnectDevice();
                }

                System.out.println("Fragment Home = mButtonDisconnect() - called");
                mButtonConnect.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.bg_shadow_white_fragment_home_button));
                mButtonDisconnect.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.bg_shadow_gray_fragment_home_button));
                mButtonConnect.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mButtonDisconnect.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));

                mButtonConnect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth, 0, 0, 0);

                mCardViewBPM.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewECG_HRV.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewSleep.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewSettings.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewLongTermECG.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));

                mTextViewBPM.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewECG_HRV.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewSleep.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewSettings.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewLongTermECG.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));

                mImageViewBPM.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewECG.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewSLEEP.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewSETTIGS.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewLongTermECG.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));

                MainActivity.mArrayListBledevices.clear();
            }
        });

        mCardViewBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    Intent mIntent = new Intent(mMainActivity, FragmentBPM.class);
                    startActivity(mIntent);
                }
            }
        });

        mCardViewECG_HRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    Intent mIntent = new Intent(mMainActivity, FragmentECG.class);
                    startActivity(mIntent);
                }
            }
        });

        mCardViewSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    Intent mIntent = new Intent(mMainActivity, FragmentSleep.class);
                    startActivity(mIntent);
                }
            }
        });

        mCardViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connectedDevice != null) {
                    Intent mIntent = new Intent(mMainActivity, FragmentSettings.class);
                    startActivity(mIntent);
                }
            }
        });

        mCardViewMedicationTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.showMedicationDialog();
            }
        });

        mTextViewHealthModeSummaryDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mMainActivity, ActivityHealthModeHistory.class);
                startActivity(mIntent);
            }
        });

        mTextViewLatestWorkoutDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mMainActivity, ActivityActivityHistory.class);
                startActivity(mIntent);
            }
        });

        mTextViewLatestSleepDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mMainActivity, ActivitySleepModeHistory.class);
                startActivity(mIntent);
            }
        });

        mCardViewMedicationTaken.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_enabled_color));
        mTextViewMedicationTaken.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
        mImageViewMedicationTaken.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white));

    }

    public void updateView(BluetoothDevice connectedDevice) {
        try {
            if (connectedDevice != null) {
                System.out.println("Fragment Home = updateView() - connectedDevice not null");
                System.out.println("Fragment Home = mButtonConnect =" + (mButtonConnect == null));

                mButtonDisconnect.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.bg_shadow_white_fragment_home_button));

                mButtonConnect.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.bg_shadow_white_fragment_home_button));
                mButtonConnect.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_green_active));
                mButtonConnect.setText(mMainActivity.getString(R.string.text_connected_all_caps));
                mButtonDisconnect.setTextColor(ContextCompat.getColor(mMainActivity, R.color.mandatory_color));

                mButtonConnect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth_connected, 0, 0, 0);

                mCardViewBPM.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_enabled_color));
                mCardViewECG_HRV.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_enabled_color));
                mCardViewSleep.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_enabled_color));
                mCardViewSettings.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_enabled_color));
                mCardViewLongTermECG.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_enabled_color));

                mTextViewBPM.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mTextViewECG_HRV.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mTextViewSleep.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mTextViewSettings.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mTextViewLongTermECG.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_white));

                mImageViewBPM.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mImageViewECG.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mImageViewSLEEP.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mImageViewSETTIGS.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white));
                mImageViewLongTermECG.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.color_white));

            } else {
                System.out.println("Fragment Home = updateView() - connectedDevice null");

                mButtonConnect.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.bg_shadow_white_fragment_home_button));

                mButtonDisconnect.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.bg_shadow_gray_fragment_home_button));
                mButtonConnect.setTextColor(ContextCompat.getColor(mMainActivity, R.color.text_color));
                mButtonConnect.setText(mMainActivity.getString(R.string.text_scan_connect_all_caps));
                mButtonDisconnect.setTextColor(ContextCompat.getColor(mMainActivity, R.color.text_color));

                mButtonConnect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth, 0, 0, 0);

                mCardViewBPM.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewECG_HRV.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewSleep.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewSettings.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));
                mCardViewLongTermECG.setCardBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.button_disabled_color));

                mTextViewBPM.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewECG_HRV.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewSleep.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewSettings.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mTextViewLongTermECG.setTextColor(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));

                mImageViewBPM.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewECG.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewSLEEP.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewSETTIGS.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
                mImageViewLongTermECG.setColorFilter(ContextCompat.getColor(mMainActivity, R.color.button_text_eanbled));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (MainActivity.connectedDevice != null) {
                updateView(MainActivity.connectedDevice);
            } else {
                System.out.println("connected Device is  null");
            }
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void getSetViewData() {
        System.out.println("abc");
        if (mMainActivity == null) {
            return;
        }
        System.out.println("abc");
        setSmallChart();
        System.out.println("abc");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.US);
        SimpleDateFormat timeFormatterForBPM = new SimpleDateFormat("h:mm", Locale.US);
        SimpleDateFormat dateShortFormatter = new SimpleDateFormat("d MMM", Locale.US);
        // ====== HEALTH SCORE == START ======
//        mDataHolder = mDbHelper.read("Select * from main_activity_table where type='ecg' and user_id='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC LIMIT 5");

        mDataHolder = mDbHelper.read("Select * from main_activity_table INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id where main_activity_table.type='ecg' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC LIMIT 2");

        if (mDataHolder != null && mDataHolder.get_Listholder().size() > 0) {
            float lastRating = 0f;
            float secondLastRating = 0f;
            mTextViewHealthModeSummary.setText(mMainActivity.getString(R.string.text_last_health_score));

            //Latest Item

            String avgHrv = mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV);

            if (avgHrv == null || avgHrv.trim().isEmpty() || avgHrv.equalsIgnoreCase("null")) {
                mTextViewHeartRateValue.setText("0.0");
                mRatingBarHeader.setRating(0);
            } else {
                lastRating = Float.parseFloat(avgHrv);
                if (lastRating > 5) {
                    mTextViewHeartRateValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, 5.0));
                    mRatingBarHeader.setRating(5);
                } else {
                    mTextViewHeartRateValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, lastRating));
                    mRatingBarHeader.setRating(lastRating);
                }


            }

            String strStartDate = mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_DATE_TIME);


            if (strStartDate != null && !strStartDate.trim().isEmpty()) {

                Date dateStart;
                Date startDate;

                String strFinalStartTime;
                String strDate;
                String strFinalStartDate;

                String strTodayDate;
                String strYesterdayDate;
                try {
                    dateStart = formatter.parse(strStartDate);
                    strFinalStartTime = timeFormatter.format(dateStart);

                    startDate = formatter.parse(strStartDate);
                    strDate = dateFormatter.format(startDate);
                    strFinalStartDate = dateShortFormatter.format(startDate);

                    Calendar calOfToday = Calendar.getInstance();

                    Date dateOfToday = calOfToday.getTime();
                    strTodayDate = dateFormatter.format(dateOfToday);
                    calOfToday.add(Calendar.DATE, -1);
                    Date dateOfYesterday = calOfToday.getTime();
                    strYesterdayDate = dateFormatter.format(dateOfYesterday);

                    if (strTodayDate.equalsIgnoreCase(strDate)) {
                        mTextViewHeartDateLabel.setText("Today, " + strFinalStartTime);
                    } else if (strYesterdayDate.equalsIgnoreCase(strDate)) {
                        mTextViewHeartDateLabel.setText("Yesterday, " + strFinalStartTime);
                    } else {
                        mTextViewHeartDateLabel.setText(strFinalStartDate + ", " + strFinalStartTime);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

            }

            //Second Last Record
            if (mDataHolder.get_Listholder().size() > 1) {
                String secondAvgHrv = mDataHolder.get_Listholder().get(1).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV);
                if (!(secondAvgHrv == null || secondAvgHrv.trim().isEmpty() || secondAvgHrv.equalsIgnoreCase("null"))) {
                    secondLastRating = Float.parseFloat(secondAvgHrv);
                }
            }

            if (lastRating > 5) {
                lastRating = 5.0f;
            }
            if (secondLastRating > 5) {
                secondLastRating = 5.0f;
            }
            if (lastRating > secondLastRating) {
                // Green
                float tmp = lastRating - secondLastRating;
                mTextViewHeartPreviousValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, tmp));
                mTextViewHeartPreviousValue.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_green_1));
                mTextViewUpDownIcon.setRotation(0);

            } else if (lastRating < secondLastRating) {
                // Red
                float tmp = secondLastRating - lastRating;
                mTextViewHeartPreviousValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, tmp));
                mTextViewHeartPreviousValue.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_red_1));
                mTextViewUpDownIcon.setRotation(180);


            } else {
                // Green
                mTextViewHeartPreviousValue.setText("0.0");
                mTextViewHeartPreviousValue.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_green_1));
                mTextViewUpDownIcon.setRotation(0);

            }
            // ====== HEALTH SCORE == END ======


//            DataHolder mDataHolderBPMLastRecord;
//            DataHolder mDataHolderBpmDetails;
//            DataHolder mDataHolderMetDetails;
//
//
//            // ====== LATEST WORKOUT == START ======
//            mDataHolderBPMLastRecord = mDbHelper.read("Select * from main_activity_table where type='bpm' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC LIMIT 1");
//            System.out.println("abc");
//
//            if (mDataHolderBPMLastRecord.get_Listholder().size() > 0) {
//                String strFinalStartTime = "0:00";
//                String strFinalEndTime = "0:00";
//
//                String bpmStartTime = mDataHolderBPMLastRecord.get_Listholder().get(0).get("start_time");
//                String bpmEndTime = mDataHolderBPMLastRecord.get_Listholder().get(0).get("end_time");
//
//                if (bpmStartTime != null && !bpmStartTime.trim().isEmpty()) {
//
//                    Date startTime;
//                    Date endTime;
//                    try {
//                        startTime = formatter.parse(bpmStartTime);
//                        strFinalStartTime = timeFormatterForBPM.format(startTime);
//
//                        endTime = formatter.parse(bpmEndTime);
//                        strFinalEndTime = timeFormatterForBPM.format(endTime);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return;
//                    }
//
//                }
//
//                mDataHolderBpmDetails = mDbHelper.read("Select * from bpm_details where parent_id='" + mDataHolderBPMLastRecord.get_Listholder().get(0).get("id") + "' AND type='bpm'");
//
//                if (mDataHolderBpmDetails.get_Listholder().size() > 0) {
//
//                    String strAvgBPM = mDataHolderBpmDetails.get_Listholder().get(0).get("average_bpm");
//
//                    if (strAvgBPM != null && !strAvgBPM.trim().isEmpty()) {
//                        float avgBpm = Float.parseFloat(strAvgBPM);
//                        mTextViewAverageHeartRateValue.setText(String.valueOf(Math.round(avgBpm)));
//
//                        String strMaxBpm = mDataHolderBpmDetails.get_Listholder().get(0).get("max_bpm");
//
//                        if (strMaxBpm != null && !strAvgBPM.trim().isEmpty()) {
//                            float maxBpm = Float.parseFloat(strMaxBpm);
//                            mTextViewMaxHeartRateValue.setText(String.valueOf(Math.round(maxBpm)));
//                        }
//                    }
//                }
//                mDataHolderMetDetails = mDbHelper.read("Select * from met_details where parent_id='" + mDataHolderBPMLastRecord.get_Listholder().get(0).get("id") + "' AND type='bpm'");
//
//                if (mDataHolderMetDetails.get_Listholder().size() > 0) {
//                    String strActivityType = mDataHolderMetDetails.get_Listholder().get(0).get("activity_type");
//                    String strAverageMet = mDataHolderMetDetails.get_Listholder().get(0).get("average_met");
//
//                    if (strAverageMet != null && !strAverageMet.isEmpty()) {
//                        float avgMET = Float.parseFloat(strAverageMet);
//                        mTextViewActivityIntensityValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, avgMET));
//
//
//                        mTextViewActivityTimeValue.setText(String.format("%s, %s - %s", strActivityType, strFinalStartTime, strFinalEndTime));
//                    }
//
//                }
//
//            }


        }


//        if (mDataHolder != null && mDataHolder.get_Listholder().size() > 0) {
//            float lastRating = 0f;
//            float secondLastRating = 0f;
//            mTextViewHealthModeSummary.setText(mMainActivity.getString(R.string.text_last_health_score));
//            for (int i = 0; i < mDataHolder.get_Listholder().size(); i++) {
//                mDataHolderEcgDetails = mDbHelper.read("Select * from ecg_details where parent_id='" + mDataHolder.get_Listholder().get(i).get("id") + "' AND type='ecg'");
//
//                System.out.println("abc");
//
//                if (i == 0) {
//
//                    if (mDataHolderEcgDetails.get_Listholder().size() > 0) {
//
//
//                        String avgHrv = mDataHolderEcgDetails.get_Listholder().get(0).get("average_hrv");
//
//                        if (avgHrv == null || avgHrv.trim().isEmpty() || avgHrv.equalsIgnoreCase("null")) {
//                            mTextViewHeartRateValue.setText("0.0");
//                            mRatingBarHeader.setRating(0);
//                        } else {
//                            lastRating = Float.parseFloat(avgHrv);
//                            if (lastRating > 5) {
//                                mTextViewHeartRateValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, 5.0));
//                                mRatingBarHeader.setRating(5);
//                            } else {
//                                mTextViewHeartRateValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, lastRating));
//                                mRatingBarHeader.setRating(lastRating);
//                            }
//
//
//                        }
//
//                        String strStartDate = mDataHolderEcgDetails.get_Listholder().get(0).get("date_time");
//
//
//                        if (strStartDate != null && !strStartDate.trim().isEmpty()) {
//
//                            Date dateStart;
//                            Date startDate;
//
//                            String strFinalStartTime;
//                            String strDate;
//                            String strFinalStartDate;
//
//                            String strTodayDate;
//                            String strYesterdayDate;
//                            try {
//                                dateStart = formatter.parse(strStartDate);
//                                strFinalStartTime = timeFormatter.format(dateStart);
//
//                                startDate = formatter.parse(strStartDate);
//                                strDate = dateFormatter.format(startDate);
//                                strFinalStartDate = dateShortFormatter.format(startDate);
//
//                                Calendar calOfToday = Calendar.getInstance();
//
//                                Date dateOfToday = calOfToday.getTime();
//                                strTodayDate = dateFormatter.format(dateOfToday);
//                                calOfToday.add(Calendar.DATE, -1);
//                                Date dateOfYesterday = calOfToday.getTime();
//                                strYesterdayDate = dateFormatter.format(dateOfYesterday);
//
//                                if (strTodayDate.equalsIgnoreCase(strDate)) {
//                                    mTextViewHeartDateLabel.setText("Today, " + strFinalStartTime);
//                                } else if (strYesterdayDate.equalsIgnoreCase(strDate)) {
//                                    mTextViewHeartDateLabel.setText("Yesterday, " + strFinalStartTime);
//                                } else {
//                                    mTextViewHeartDateLabel.setText(strFinalStartDate + ", " + strFinalStartTime);
//                                }
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                return;
//                            }
//
//                        }
//
//                    }
//                } else if (i == 1) {
//                    mDataHolderEcgDetails = mDbHelper.read("Select * from ecg_details where parent_id='" + mDataHolder.get_Listholder().get(i).get("id") + "' AND type='ecg'");
//
//                    if (mDataHolderEcgDetails.get_Listholder().size() > 0) {
//                        String avgHrv = mDataHolderEcgDetails.get_Listholder().get(0).get("average_hrv");
//                        if (!(avgHrv == null || avgHrv.trim().isEmpty() || avgHrv.equalsIgnoreCase("null"))) {
//                            secondLastRating = Float.parseFloat(avgHrv);
//                        }
//
//                    }
//                }
//            }
//
//            if (lastRating > 5) {
//                lastRating = 5.0f;
//            }
//            if (secondLastRating > 5) {
//                secondLastRating = 5.0f;
//            }
//            if (lastRating > secondLastRating) {
//                // Green
//                float tmp = lastRating - secondLastRating;
//                mTextViewHeartPreviousValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, tmp));
//                mTextViewHeartPreviousValue.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_green_1));
//                mTextViewUpDownIcon.setRotation(0);
//
//            } else if (lastRating < secondLastRating) {
//                // Red
//                float tmp = secondLastRating - lastRating;
//                mTextViewHeartPreviousValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, tmp));
//                mTextViewHeartPreviousValue.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_red_1));
//                mTextViewUpDownIcon.setRotation(180);
//
//
//            } else {
//                // Green
//                mTextViewHeartPreviousValue.setText("0.0");
//                mTextViewHeartPreviousValue.setTextColor(ContextCompat.getColor(mMainActivity, R.color.color_green_1));
//                mTextViewUpDownIcon.setRotation(0);
//
//            }
//            // ====== HEALTH SCORE == END ======
//
//
////            DataHolder mDataHolderBPMLastRecord;
////            DataHolder mDataHolderBpmDetails;
////            DataHolder mDataHolderMetDetails;
////
////
////            // ====== LATEST WORKOUT == START ======
////            mDataHolderBPMLastRecord = mDbHelper.read("Select * from main_activity_table where type='bpm' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC LIMIT 1");
////            System.out.println("abc");
////
////            if (mDataHolderBPMLastRecord.get_Listholder().size() > 0) {
////                String strFinalStartTime = "0:00";
////                String strFinalEndTime = "0:00";
////
////                String bpmStartTime = mDataHolderBPMLastRecord.get_Listholder().get(0).get("start_time");
////                String bpmEndTime = mDataHolderBPMLastRecord.get_Listholder().get(0).get("end_time");
////
////                if (bpmStartTime != null && !bpmStartTime.trim().isEmpty()) {
////
////                    Date startTime;
////                    Date endTime;
////                    try {
////                        startTime = formatter.parse(bpmStartTime);
////                        strFinalStartTime = timeFormatterForBPM.format(startTime);
////
////                        endTime = formatter.parse(bpmEndTime);
////                        strFinalEndTime = timeFormatterForBPM.format(endTime);
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                        return;
////                    }
////
////                }
////
////                mDataHolderBpmDetails = mDbHelper.read("Select * from bpm_details where parent_id='" + mDataHolderBPMLastRecord.get_Listholder().get(0).get("id") + "' AND type='bpm'");
////
////                if (mDataHolderBpmDetails.get_Listholder().size() > 0) {
////
////                    String strAvgBPM = mDataHolderBpmDetails.get_Listholder().get(0).get("average_bpm");
////
////                    if (strAvgBPM != null && !strAvgBPM.trim().isEmpty()) {
////                        float avgBpm = Float.parseFloat(strAvgBPM);
////                        mTextViewAverageHeartRateValue.setText(String.valueOf(Math.round(avgBpm)));
////
////                        String strMaxBpm = mDataHolderBpmDetails.get_Listholder().get(0).get("max_bpm");
////
////                        if (strMaxBpm != null && !strAvgBPM.trim().isEmpty()) {
////                            float maxBpm = Float.parseFloat(strMaxBpm);
////                            mTextViewMaxHeartRateValue.setText(String.valueOf(Math.round(maxBpm)));
////                        }
////                    }
////                }
////                mDataHolderMetDetails = mDbHelper.read("Select * from met_details where parent_id='" + mDataHolderBPMLastRecord.get_Listholder().get(0).get("id") + "' AND type='bpm'");
////
////                if (mDataHolderMetDetails.get_Listholder().size() > 0) {
////                    String strActivityType = mDataHolderMetDetails.get_Listholder().get(0).get("activity_type");
////                    String strAverageMet = mDataHolderMetDetails.get_Listholder().get(0).get("average_met");
////
////                    if (strAverageMet != null && !strAverageMet.isEmpty()) {
////                        float avgMET = Float.parseFloat(strAverageMet);
////                        mTextViewActivityIntensityValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, avgMET));
////
////
////                        mTextViewActivityTimeValue.setText(String.format("%s, %s - %s", strActivityType, strFinalStartTime, strFinalEndTime));
////                    }
////
////                }
////
////            }
//
//
//        }

        DataHolder mDataHolderBPMLastRecord;
        DataHolder mDataHolderBpmDetails;
        DataHolder mDataHolderMetDetails;


        // ====== LATEST WORKOUT == START ======
        mDataHolderBPMLastRecord = mDbHelper.read("Select * from main_activity_table where type='bpm' and user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC LIMIT 1");
        System.out.println("abc");

        if (mDataHolderBPMLastRecord.get_Listholder().

                size() > 0) {
            String strFinalStartTime = "0:00";
            String strFinalEndTime = "0:00";

            String bpmStartTime = mDataHolderBPMLastRecord.get_Listholder().get(0).get("start_time");
            String bpmEndTime = mDataHolderBPMLastRecord.get_Listholder().get(0).get("end_time");

            if (bpmStartTime != null && !bpmStartTime.trim().isEmpty()) {

                Date startTime;
                Date endTime;
                try {
                    startTime = formatter.parse(bpmStartTime);
                    strFinalStartTime = timeFormatterForBPM.format(startTime);

                    endTime = formatter.parse(bpmEndTime);
                    strFinalEndTime = timeFormatterForBPM.format(endTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

            }

            mDataHolderBpmDetails = mDbHelper.read("Select * from bpm_details where parent_id='" + mDataHolderBPMLastRecord.get_Listholder().get(0).get("id") + "' AND type='bpm'");

            if (mDataHolderBpmDetails.get_Listholder().size() > 0) {

                String strAvgBPM = mDataHolderBpmDetails.get_Listholder().get(0).get("average_bpm");

                if (strAvgBPM != null && !strAvgBPM.trim().isEmpty()) {
                    float avgBpm = Float.parseFloat(strAvgBPM);
                    mTextViewAverageHeartRateValue.setText(String.valueOf(Math.round(avgBpm)));

                    String strMaxBpm = mDataHolderBpmDetails.get_Listholder().get(0).get("max_bpm");

                    if (strMaxBpm != null && !strAvgBPM.trim().isEmpty()) {
                        float maxBpm = Float.parseFloat(strMaxBpm);
                        mTextViewMaxHeartRateValue.setText(String.valueOf(Math.round(maxBpm)));
                    }
                }
            }
            mDataHolderMetDetails = mDbHelper.read("Select * from met_details where parent_id='" + mDataHolderBPMLastRecord.get_Listholder().get(0).get("id") + "' AND type='bpm'");

            if (mDataHolderMetDetails.get_Listholder().size() > 0) {
                String strActivityType = mDataHolderMetDetails.get_Listholder().get(0).get("activity_type");
                String strAverageMet = mDataHolderMetDetails.get_Listholder().get(0).get("average_met");

                if (strAverageMet != null && !strAverageMet.isEmpty()) {
                    float avgMET = Float.parseFloat(strAverageMet);
                    mTextViewActivityIntensityValue.setText(mMainActivity.getString(R.string.one_digit_after_decimal, avgMET));


                    mTextViewActivityTimeValue.setText(String.format("%s, %s - %s", strActivityType, strFinalStartTime, strFinalEndTime));
                }

            }

        }


        // ====== STRESS LEVEL == START ======
        Calendar calOfToday = Calendar.getInstance();

        Date dateOfToday = calOfToday.getTime();
        String strTodayDate;
        try {
            strTodayDate = dateFormatter.format(dateOfToday);
        } catch (
                Exception e) {
            e.printStackTrace();
            return;
        }
//        mDataHolderStressRecord = mDbHelper.read("Select * from main_activity_table INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id where main_activity_table.type='ecg' and main_activity_table.date = '" + strTodayDate + "' and main_activity_table.user_id='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC");
        mDataHolderStressRecord = mDbHelper.read("Select * from main_activity_table INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id where (main_activity_table.type='ecg' and main_activity_table.date = '" + strTodayDate + "' and main_activity_table.user_id='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "') and (hrv_details.event_type='" + FragmentHealthMode.Event.MORNING.getValue() + "' or hrv_details.event_type='" + FragmentHealthMode.Event.BEFORE_WORK.getValue() + "' or hrv_details.event_type='" + FragmentHealthMode.Event.AFTER_WORK.getValue() + "' or hrv_details.event_type='" + FragmentHealthMode.Event.BEFORE_BED.getValue() + "') ORDER BY main_activity_table.id DESC");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
        System.out.println("Select * from main_activity_table INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id where (main_activity_table.type='ecg' and main_activity_table.date = '" + strTodayDate + "' and main_activity_table.user_id='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "') and (hrv_details.event_type='" + FragmentHealthMode.Event.MORNING.getValue() + "' or hrv_details.event_type='" + FragmentHealthMode.Event.BEFORE_WORK.getValue() + "' or hrv_details.event_type='" + FragmentHealthMode.Event.AFTER_WORK.getValue() + "' or hrv_details.event_type='" + FragmentHealthMode.Event.BEFORE_BED.getValue() + "') ORDER BY main_activity_table.id DESC");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");

        if (mDataHolderStressRecord.get_Listholder().

                size() > 0) {
            float stressLevel = 0;
//            int stressLevelCount = 0;

//            for (int i = 0; i < mDataHolderStressRecord.get_Listholder().size(); i++) {
//                String strEventType = mDataHolderStressRecord.get_Listholder().get(0).get("event_type");
//
//                if (strEventType != null &&
//                        (strEventType.equalsIgnoreCase(FragmentHealthMode.Event.MORNING.getValue()) ||
//                                strEventType.equalsIgnoreCase(FragmentHealthMode.Event.BEFORE_WORK.getValue()) ||
//                                strEventType.equalsIgnoreCase(FragmentHealthMode.Event.AFTER_WORK.getValue()) ||
//                                strEventType.equalsIgnoreCase(FragmentHealthMode.Event.BEFORE_BED.getValue())
//                        )
//                ) {
//                    String strHRVValue = mDataHolderStressRecord.get_Listholder().get(0).get("hrv_value");
////                    NSString * strHRVValue = [NSString stringWithFormat:@ "%@",[[
////                    arrStressLevelRecord objectAtIndex:i]valueForKey:
////                    @ "hrv_value"]];
//                    if (strHRVValue != null && !strHRVValue.trim().isEmpty()) {
//                        String[] arrBPMTemp = strHRVValue.split(",");
////                    NSArray * arrBPMTemp = [strHRVValue componentsSeparatedByString:@ ","];
//                        float totalBPMTemp = 0;
//                        float maxBPMTemp = 0;
//                        if (arrBPMTemp.length > 0) {
//                            for (String s : arrBPMTemp) {
//                                float bpmValue = Float.parseFloat(s);
//                                if (maxBPMTemp < bpmValue) {
//                                    maxBPMTemp = bpmValue;
//                                }
//                                totalBPMTemp = totalBPMTemp + bpmValue;
//                            }
//                        }
//                        float tempStress = totalBPMTemp / (float) arrBPMTemp.length;
//                        stressLevel = stressLevel + tempStress;
//                        stressLevelCount = stressLevelCount + 1;
//                    }
//
//                }
//            }
            //*******************************************************************************
            //                              May - 2021
            //*******************************************************************************
            String strEventType = mDataHolderStressRecord.get_Listholder().get(0).get("event_type");

            if (strEventType != null) {
                String strHRVValue = mDataHolderStressRecord.get_Listholder().get(0).get("hrv_value");
//                    NSString * strHRVValue = [NSString stringWithFormat:@ "%@",[[
//                    arrStressLevelRecord objectAtIndex:i]valueForKey:
//                    @ "hrv_value"]];
                if (strHRVValue != null && !strHRVValue.trim().isEmpty()) {
                    String[] arrBPMTemp = strHRVValue.split(",");
//                    NSArray * arrBPMTemp = [strHRVValue componentsSeparatedByString:@ ","];
                    float totalBPMTemp = 0;
                    float maxBPMTemp = 0;
                    if (arrBPMTemp.length > 0) {
                        for (String s : arrBPMTemp) {
                            float bpmValue = Float.parseFloat(s);
                            if (maxBPMTemp < bpmValue) {
                                maxBPMTemp = bpmValue;
                            }
                            totalBPMTemp = totalBPMTemp + bpmValue;
                        }
                    }
                    float tempStress = totalBPMTemp / (float) arrBPMTemp.length;
                    stressLevel = stressLevel + tempStress;
                }

            }
            //*******************************************************************************

//            stressLevel = stressLevel / stressLevelCount;


            mTextViewStressTypeNALabel.setVisibility(View.GONE);
            mClStress.setVisibility(View.VISIBLE);

            if (stressLevel < 1) {
//                mTextViewStressTypeLabel.setText(FaceEmojis.VERY_HIGH.emoji+"\nVery High");
                mTextViewStressTypeEmojiLabel.setText(FaceEmojis.VERY_HIGH.emoji);
                mTextViewStressTypeTextLabel.setText("Very High");

            } else if (stressLevel < 2) {
//                mTextViewStressTypeLabel.setText(FaceEmojis.HIGH.emoji+"\nHigh");
                mTextViewStressTypeEmojiLabel.setText(FaceEmojis.HIGH.emoji);
                mTextViewStressTypeTextLabel.setText("High");


            } else if (stressLevel < 3) {
//                mTextViewStressTypeLabel.setText(FaceEmojis.MEDIUM.emoji+"\nMedium");
                mTextViewStressTypeEmojiLabel.setText(FaceEmojis.MEDIUM.emoji);
                mTextViewStressTypeTextLabel.setText("Medium");


            } else if (stressLevel < 4) {
//                mTextViewStressTypeLabel.setText(FaceEmojis.LOW.emoji+"\nLow");
                mTextViewStressTypeEmojiLabel.setText(FaceEmojis.LOW.emoji);
                mTextViewStressTypeTextLabel.setText("Low");


            } else {
//                mTextViewStressTypeLabel.setText(FaceEmojis.VERY_LOW.emoji+"\nVery Low");
                mTextViewStressTypeEmojiLabel.setText(FaceEmojis.VERY_LOW.emoji);
                mTextViewStressTypeTextLabel.setText("Very Low");

            }
        } else {
            mTextViewStressTypeNALabel.setVisibility(View.VISIBLE);
            mClStress.setVisibility(View.GONE);
//            mTextViewStressTypeLabel.setText("N/A");
//            mTextViewStressTypeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    mMainActivity.getResources().getDimension(R.dimen._36ssp));
        }

        // ====== STRESS LEVEL == END ======


        // Last Sleep Data === START
        mDataHolderSleepRecord = mDbHelper.read("select * from sleep_details where user_id='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY id DESC LIMIT 1");
//        NSMutableArray * arrSleepDetail = [[NSMutableArray alloc]init];
////    NSString  *strQuery = @"select * from sleep_details where user_id='%@' ORDER BY id DESC LIMIT 1",CURRENT_USER_ID;
//        NSString * strQuery = [NSString stringWithFormat:@
//        "select * from sleep_details where user_id='%@' ORDER BY id DESC LIMIT 1", CURRENT_USER_ID];
//    [[DataBaseManager dataBaseManager]execute:
//        strQuery resultsArray:arrSleepDetail];

        if (mDataHolderSleepRecord.get_Listholder().

                size() > 0) {
            String strTotalTime = mDataHolderSleepRecord.get_Listholder().get(0).get("sleep_difference");

//            arrayTempSleep = [arrSleepDetail mutableCopy];
//            NSString * strTotalTime = [NSString stringWithFormat:@ "%@",[[arrSleepDetail lastObject]
//            valueForKey:
//            @ "sleep_difference"]];
            if (strTotalTime == null || strTotalTime.trim().isEmpty()) {
                return;
            }
            String[] arrTime = strTotalTime.split(",");
//            NSArray * arrTime = [strTotalTime componentsSeparatedByString:@ ","];
            int totalTime = 0;

            int totalRecordCount = 0;
            int totalDeeperCount = 0;
            int totalDeepCount = 0;
            int totalLightCount = 0;
            int totalLighterCount = 0;
            int totalAwakeCount = 0;

            if (arrTime.length > 0) {
                for (String s : arrTime) {
                    totalTime = totalTime + Integer.parseInt(s);
                }
            }
            String strTotalSleepValue = mDataHolderSleepRecord.get_Listholder().get(0).get("sleep_value");
            if (strTotalSleepValue == null || strTotalSleepValue.trim().isEmpty()) {
                return;
            }
            String[] arrSleepValue = strTotalSleepValue.split(",");
//            NSString * strTotalSleepValue = [NSString stringWithFormat:@ "%@",[[
//            arrSleepDetail lastObject]valueForKey:
//            @ "sleep_value"]];
//            NSArray * arrSleepValue = [strTotalSleepValue componentsSeparatedByString:@ ","];
            if (arrSleepValue.length > 0) {
                for (String s : arrSleepValue) {
                    Log.d(TAG, s);
                    Log.d(TAG, ".");
//                    NSString * strValue = [NSString stringWithFormat:@ "%@",[
//                    arrSleepValue objectAtIndex:a]];
//                NSLog(@"strValue===%@",strValue);
                    if (s.equalsIgnoreCase("6")) {
                        totalDeeperCount = totalDeeperCount + 1;
                    } else if (s.equalsIgnoreCase("5")) {
                        totalDeepCount = totalDeepCount + 1;
                    } else if (s.equalsIgnoreCase("4")) {
                        totalLightCount = totalLightCount + 1;
                    } else if (s.equalsIgnoreCase("3")) {
                        totalLighterCount = totalLighterCount + 1;
                    } else { //if ([strValue isEqualToString:@"2"]) {
                        totalAwakeCount = totalAwakeCount + 1;
                    }
                    totalRecordCount = totalRecordCount + 1;
                }
            }
//            if ([arrTime count]>0)
//            {
//                for (int a = 0; a <[arrTime count];
//                a++)
//                {
//                    totalTime = totalTime + [[arrTime objectAtIndex:a]intValue];
//                }
//            }
//
//            NSString * strTotalSleepValue = [NSString stringWithFormat:@ "%@",[[
//            arrSleepDetail lastObject]valueForKey:
//        @ "sleep_value"]];
//            NSArray * arrSleepValue = [strTotalSleepValue componentsSeparatedByString:@ ","];
//            if ([arrSleepValue count]>0)
//            {
//                for (int a = 0; a <[arrSleepValue count];
//                a++)
//                {
//                    NSString * strValue = [NSString stringWithFormat:@ "%@",[
//                    arrSleepValue objectAtIndex:a]];
////                NSLog(@"strValue===%@",strValue);
//                    if ([strValue isEqualToString:@ " 6"]){
//                    totalDeeperCount = totalDeeperCount + 1;
//                } else if ([strValue isEqualToString:@ " 5"]){
//                    totalDeepCount = totalDeepCount + 1;
//                } else if ([strValue isEqualToString:@ " 4"]){
//                    totalLightCount = totalLightCount + 1;
//                } else if ([strValue isEqualToString:@ " 3"]){
//                    totalLighterCount = totalLighterCount + 1;
//                } else{ //if ([strValue isEqualToString:@"2"]) {
//                    totalAwakeCount = totalAwakeCount + 1;
//                }
//                    totalRecordCount = totalRecordCount + 1;
//                }
//            }


//        NSLog(@"totalRecordCount===%d",totalRecordCount);
//        NSLog(@"totalDeeperCount===%d",totalDeeperCount);
//        NSLog(@"totalDeepCount===%d",totalDeepCount);
//        NSLog(@"totalLightCount===%d",totalLightCount);
//        NSLog(@"totalLighterCount===%d",totalLighterCount);
//        NSLog(@"totalAwakeCount===%d",totalAwakeCount);


            if (totalTime != 0) {
                float avgTime = 0;
                avgTime = totalTime / (float) totalRecordCount;

                int actualDeeper = (int) avgTime * totalDeeperCount;
                int minutesDeeper = ((actualDeeper / 60) % 60);
                int hoursDeeper = actualDeeper / (60 * 60);
//            lblDetailsDeeperTime.text =  [NSString stringWithFormat:@"\%02d %@ %02d %@",hoursDeeper ,@"hours",minutesDeeper,@"minutes"];

                int actualDeep = (int) avgTime * totalDeepCount;
                int minutesDeep = ((actualDeep / 60) % 60);
                int hoursDeep = actualDeep / (60 * 60);
//            lblDetailsDeepTime.text =  [NSString stringWithFormat:@"\%02d %@ %02d %@",hoursDeep ,@"hours",minutesDeep,@"minutes"];

                int actualLight = (int) avgTime * totalLightCount;
                int minutesLight = ((actualLight / 60) % 60);
                int hoursLight = actualLight / (60 * 60);
//            lblDetailsLightTime.text =  [NSString stringWithFormat:@"\%02d %@ %02d %@",hoursLight ,@"hours",minutesLight,@"minutes"];

                int actualLighter = (int) avgTime * totalLighterCount;
                int minutesLighter = ((actualLighter / 60) % 60);
                int hoursLighter = actualLighter / (60 * 60);
//            lblDetailsLighterTime.text =  [NSString stringWithFormat:@"\%02d %@ %02d %@",hoursLighter ,@"hours",minutesLighter,@"minutes"];

                int actualAwake = (int) avgTime * totalAwakeCount;
                int minutesAwake = ((actualAwake / 60) % 60);
                int hoursAwake = actualAwake / (60 * 60);
//            lblDetailsAwakeTime.text =  [NSString stringWithFormat:@"\%02d %@ %02d %@",hoursAwake ,@"hours",minutesAwake,@"minutes"];

                int deepPerc = (totalDeepCount * 100) / totalRecordCount;

                mTextViewDeepSleepPercentageValue.setText(deepPerc + "%");

//            NSLog(@"actualDeeper===%@",[NSString stringWithFormat:@"\%02f",actualDeeper]);
//            NSLog(@"actualDeep===%@",[NSString stringWithFormat:@"\%02f",actualDeep]);
//            NSLog(@"actualLight===%@",[NSString stringWithFormat:@"\%02f",actualLight]);
//            NSLog(@"actualLighter===%@",[NSString stringWithFormat:@"\%02f",actualLighter]);
//            NSLog(@"actualAwake===%@",[NSString stringWithFormat:@"\%02f",actualAwake]);

            }


//        int minutes = totalTime/60;
//        int hours = totalTime / (60 * 60);
//        //        int seconds = totalTime%60 ;


//            int seconds = totalTime % 60;
            int minutes = (int) ((float) totalTime / (float) 60) % 60;
            int hours = totalTime / 3600;

            Log.d("HomeSleep", "totalTime=" + totalTime);


            if (hours == 0) {
                mTextViewTotalSleepValue.setText(mMainActivity.getString(R.string.text_fragment_home_sleep_m, minutes));
//                lblTotalSleepTimeNum.text =  [NSString stringWithFormat:@ "\%02d%@", minutes,@ "m"];
            } else {
                mTextViewTotalSleepValue.setText(mMainActivity.getString(R.string.text_fragment_home_sleep_h_m, hours, minutes));
//                lblTotalSleepTimeNum.text =  [NSString stringWithFormat:@ "\%2d%@ %2d%@", hours ,@
//                "h", minutes,@ "m"];
            }
        }


    }


    public void setSmallChart() {
        if (scatterChart == null || mMainActivity == null) {
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
                return String.format(Locale.US, "%.0f", value);
            }
        };

        YAxis yl = scatterChart.getAxisLeft();
        yl.setAxisMaximum(5);
        yl.setAxisMinimum(0);
        yl.setLabelCount(6, true);
        yl.setTextColor(ContextCompat.getColor(mMainActivity, R.color.text_dashboard));
        yl.setGridColor(ContextCompat.getColor(mMainActivity, R.color.text_dashboard));
        yl.setAxisLineColor(Color.WHITE);
        yl.setGranularity(1f);
        yl.setValueFormatter(yAxisFormatter);//For String Values


        XAxis xl = scatterChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAxisMaximum(5);
        xl.setAxisMinimum(0);
        xl.setLabelCount(6, true);
        xl.setSpaceMin(1);


        scatterChart.getDescription().setEnabled(false);


        ArrayList<Entry> values = getChartData();


        ScatterDataSet scatterDataSet = new ScatterDataSet(values, "abc");
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setColor(ContextCompat.getColor(mMainActivity, R.color.mandatory_color));
        scatterDataSet.setValueTextColor(ContextCompat.getColor(mMainActivity, R.color.text_history));

        scatterDataSet.setScatterShapeSize(mMainActivity.getResources().getDimension(R.dimen._4sdp));

        ScatterData data = new ScatterData(scatterDataSet);


        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                return String.format(Locale.US, "%.1f", value);
            }
        });
        scatterChart.setData(data);
        scatterChart.setClipDataToContent(false);//This line is extremely important. Without this line, maximum and minimum data will get cutoff.
        scatterChart.setExtraOffsets(0, 6f, 0, 5f);
        scatterChart.invalidate();
    }

    private ArrayList<Entry> getChartData() {

        ArrayList<Entry> values = new ArrayList<>();

        float space = 0.9f;

        DataHolder mDataHolderChart;

//        mDataHolderChart = mDbHelper.read("Select * from main_activity_table INNER JOIN ecg_details ON main_activity_table.id = ecg_details.parent_id where main_activity_table.type='ecg' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC LIMIT 5");
        mDataHolderChart = mDbHelper.read("Select * from main_activity_table INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id where main_activity_table.type='ecg' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC LIMIT 5");

        System.out.println("------------------------------Chart Query Dashboard--------------------------------------");
        System.out.println("Select * from main_activity_table INNER JOIN hrv_details ON main_activity_table.id = hrv_details.parent_id where main_activity_table.type='ecg' and main_activity_table.user_id = '" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "' ORDER BY main_activity_table.id DESC LIMIT 5");
        if (mDataHolderChart.get_Listholder().size() > 0) {

            ArrayList<LinkedHashMap<String, String>> data = mDataHolderChart.get_Listholder();
            Collections.reverse(data);

            for (int i = 0; i < data.size(); i++) {

                String strBpm = data.get(i).get("average_hrv");

                if (strBpm == null || strBpm.trim().isEmpty()) {
                    strBpm = "0.00";
                }


                float fltHRV = getParsedFloatOrZero(strBpm);
                if (fltHRV > 5) {
                    fltHRV = 5.00f;
                }

                values.add(new Entry(space * i + 1, fltHRV));

            }
        }
//        Collections.reverse(values);
        return values;
    }

    public enum FaceEmojis {
        VERY_HIGH(0x1F629),
        HIGH(0x1F61F),
        MEDIUM(0x1F60C),
        LOW(0x1F600),
        VERY_LOW(0x1F607);

        private final String emoji;

        public String getEmoji() {
            return emoji;
        }

        FaceEmojis(int emojiUnicode) {
            this.emoji = new String(Character.toChars(emojiUnicode));
        }

    }

    /**
     * Description: This method is used for parsing string to float or 0.0f if parsing fails.
     *
     * @author Ankit Mishra
     * @since 06/01/22
     */
    private float getParsedFloatOrZero(String string) {
        try {
            return Float.parseFloat(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }
}