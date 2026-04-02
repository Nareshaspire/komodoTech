package com.aiosleeve.aiosleeve;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosleeve.aiosleeve.VO.VOLogOut;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.fragments.FragmentSettings;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.API;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityProfile extends AppCompatActivity {

    public TextView mTextViewAccount;
    public TextView mTextViewGuides;
    public TextView mTextViewPrivacy;
    public TextView mTextViewSupport;
    public TextView mTextViewNotifications;//May 2021
    public SwitchCompat mSwitchNotificationsStatus;//May 2021
    public TextView mTextViewLogOut;


    public ImageView mImageViewBack;

    public Utility mUtility;
    public Retrofit mRetrofit;
    public API mApiService;

    DBHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mDbHelper = new DBHelper(this);
        try {
            mDbHelper.createDataBase();
            mDbHelper.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextViewAccount = (TextView) findViewById(R.id.activity_profile_account_button);
        mTextViewGuides = (TextView) findViewById(R.id.activity_profile_guides_button);
        mTextViewPrivacy = (TextView) findViewById(R.id.activity_profile_privacy_button);
        mTextViewSupport = (TextView) findViewById(R.id.activity_profile_support_button);
        mTextViewNotifications= (TextView) findViewById(R.id.activity_profile_notification_button);
        mSwitchNotificationsStatus= (SwitchCompat) findViewById(R.id.activity_profile_notification_status_switch);
        mTextViewLogOut = (TextView) findViewById(R.id.activity_profile_logout_button);

        mImageViewBack = (ImageView) findViewById(R.id.activity_profile_imageview_back);


//        TextView mTextViewHealthModeHistory=(TextView)findViewById(R.id.activity_profile_health_mode_history_button);
//        mTextViewHealthModeHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(ActivityProfile.this, ActivityHealthModeHistory.class);
//                startActivity(mIntent);
//            }
//        });


        mUtility = new Utility(ActivityProfile.this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        mDbHelper = new DBHelper(FragmentSettings.this);
        mApiService = mRetrofit.create(API.class);

        mTextViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ActivityProfile.this, FragmentSettings.class);
                startActivity(mIntent);
            }
        });
        mTextViewGuides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityProfile.this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                Intent mIntent = new Intent(ActivityProfile.this, FragmentSettings.class);
//                startActivity(mIntent);
            }
        });
        mTextViewPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityProfile.this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                Intent mIntent = new Intent(ActivityProfile.this, FragmentSettings.class);
//                startActivity(mIntent);
            }
        });

        mTextViewNotifications.setOnClickListener(v -> {
            if(mSwitchNotificationsStatus.isChecked()){
                mSwitchNotificationsStatus.setChecked(false);
                AlarmHelper.cancelMorningAndEveningAlarm(getApplicationContext());
                mUtility.writeSharedPreferencesBool(Constant.PREFS_ALARM_STATUS, false);
            }else{
                mSwitchNotificationsStatus.setChecked(true);
                AlarmHelper.setMorningAndEveningAlarm(getApplicationContext());
                mUtility.writeSharedPreferencesBool(Constant.PREFS_ALARM_STATUS, true);
            }
        });

        mTextViewSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityProfile.this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                Intent mIntent = new Intent(ActivityProfile.this, FragmentSettings.class);
//                startActivity(mIntent);
            }
        });
        mTextViewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUtility.haveInternet()) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ActivityProfile.this);
                    builder.setTitle(getResources().getString(R.string.left_nav_logout))
                            .setMessage(getResources().getString(R.string.text_confirmation_logout))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    mDbHelper.removeAllBlankSleepEntries();
                                    doLogOut();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    android.app.AlertDialog alert = builder.create();
                    alert.show();


                } else {
                    mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                            getResources().getString(R.string.no_internet_msg));
                }
            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //May -2021
        mSwitchNotificationsStatus.setChecked(mUtility.getAppPrefBool(Constant.PREFS_ALARM_STATUS));

    }

    private void doLogOut() {
        mUtility.ShowProgress();
        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("user_id", mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mHashMap.put("device_token", "54321");

        Call<VOLogOut> loginUser = mApiService.normalUserLogut(mHashMap);
        loginUser.enqueue(new Callback<VOLogOut>() {
            @Override
            public void onResponse(Call<VOLogOut> call, Response<VOLogOut> response) {
                mUtility.HideProgress();

                VOLogOut mVoLogOut = response.body();
                if (mVoLogOut != null) {
                    if (mVoLogOut.getSuccess() != null && mVoLogOut.getSuccess().equalsIgnoreCase("1")) {
                        mUtility.clearAllPrefData();
                        Intent intent = new Intent(ActivityProfile.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        if (mVoLogOut.getMessage() != null) {
                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                                    mVoLogOut.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VOLogOut> call, Throwable t) {
                mUtility.HideProgress();
                t.printStackTrace();
            }
        });
    }



}