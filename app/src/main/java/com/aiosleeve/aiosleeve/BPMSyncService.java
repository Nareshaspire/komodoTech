package com.aiosleeve.aiosleeve;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.aiosleeve.aiosleeve.VO.VOSleepSyncDataToServer;
import com.aiosleeve.aiosleeve.VO.VoMedicationTaken;
import com.aiosleeve.aiosleeve.VO.VoResponseBPMSync;
import com.aiosleeve.aiosleeve.VORequest.VoRequestActivityInfoItem;
import com.aiosleeve.aiosleeve.VORequest.VoRequestActivityItem;
import com.aiosleeve.aiosleeve.VORequest.VoRequestBPMDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestBPMItem;
import com.aiosleeve.aiosleeve.VORequest.VoRequestDistanceDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestECGDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestHRVDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestMetDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestSPO2Details;
import com.aiosleeve.aiosleeve.VORequest.VoRequestSleepDetail;
import com.aiosleeve.aiosleeve.VORequest.VoRequestStepsDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestTimeDetails;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.interfaces.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by android on 1/20/18.
 */

public class BPMSyncService extends Service {

    //Global Objects
    public Retrofit mRetrofit;
    public API mApiService;
    DBHelper mDbHelper;
    SharedPreferences mSharedPreferences;

    //VO Array
    ArrayList<VoRequestTimeDetails> mArrayTimeDetails;
    ArrayList<VoRequestBPMDetails> mArrayBPMDetails;
    ArrayList<VoRequestDistanceDetails> mArrayDistanceDetails;
    ArrayList<VoRequestMetDetails> mArrayMetDetails;
    ArrayList<VoRequestSPO2Details> mArraySPO2Details;
    ArrayList<VoRequestStepsDetails> mArrayStepsDetails;
    ArrayList<VoRequestActivityItem> mArrayActivityItem;
    ArrayList<VoRequestActivityInfoItem> mArrayActivityInfoItem;
    ArrayList<VoRequestECGDetails> mArrayECGDetails;
    ArrayList<VoRequestHRVDetails> mArrayHRVDetails;
    ArrayList<VoRequestSleepDetail> mArrayListSleepDetail;

    JSONArray mJsonArraySleepData;

    String mStringStartTime = "";
    String mStringEndTime = "";
    String mStringTotalTime = "";
    String mStringDate = "";
    String mStringRandomNumber = "";



    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new DBHelper(getApplicationContext());


        HttpLoggingInterceptor logging=new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();




        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = mRetrofit.create(API.class);

        mSharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, 0);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "com.aiosleeve.aiosleeve";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mArrayTimeDetails = new ArrayList<>();
        mArrayBPMDetails = new ArrayList<>();
        mArrayDistanceDetails = new ArrayList<>();
        mArrayMetDetails = new ArrayList<>();
        mArraySPO2Details = new ArrayList<>();
        mArrayStepsDetails = new ArrayList<>();
        mArrayActivityItem = new ArrayList<>();
        mArrayActivityInfoItem = new ArrayList<>();
        mArrayHRVDetails = new ArrayList<>();
        mArrayECGDetails = new ArrayList<>();

        mDbHelper.removeAllBlankEntries();//2021-March

        FetchBPMTableData();

        return START_NOT_STICKY;
    }

    public void SyncBPMDataToServer() {

        VoRequestActivityItem mVoRequestActivityItem = new VoRequestActivityItem();
        mVoRequestActivityItem.setEnd_time(mStringEndTime);
        mVoRequestActivityItem.setTotal_time(mStringTotalTime);
        mVoRequestActivityItem.setStart_time(mStringStartTime);
        mVoRequestActivityItem.setDate(mStringDate);
        mVoRequestActivityItem.setTime_details(mArrayTimeDetails);
        mVoRequestActivityItem.setBpm_details(mArrayBPMDetails);
        mVoRequestActivityItem.setDistance_details(mArrayDistanceDetails);
        mVoRequestActivityItem.setMet_details(mArrayMetDetails);
        mVoRequestActivityItem.setSpo2_details(mArraySPO2Details);
        mVoRequestActivityItem.setSteps_details(mArrayStepsDetails);


        mArrayActivityItem.add(mVoRequestActivityItem);

        VoRequestActivityInfoItem mVoRequestActivityInfoItem = new VoRequestActivityInfoItem();
        mVoRequestActivityInfoItem.setActivity_details(mArrayActivityItem);
        mVoRequestActivityInfoItem.setAccess_token(mSharedPreferences.getString(Constant.PREFS_ACCESS_TOKEN, ""));
        mVoRequestActivityInfoItem.setRendom_number(mStringRandomNumber);
        mVoRequestActivityInfoItem.setType("bpm");
        mVoRequestActivityInfoItem.setUser_id(mSharedPreferences.getString(Constant.PREFS_USER_ID, ""));
        mArrayActivityInfoItem.add(mVoRequestActivityInfoItem);

        VoRequestBPMItem mVoRequestBPMItem = new VoRequestBPMItem();
        mVoRequestBPMItem.setActivity_details_info(mArrayActivityInfoItem);

        Call<VoResponseBPMSync> syncBPMDataToServer = mApiService.syncBPMDataToServer(mVoRequestBPMItem);
        syncBPMDataToServer.enqueue(new Callback<VoResponseBPMSync>() {
            @Override
            public void onResponse(Call<VoResponseBPMSync> call, Response<VoResponseBPMSync> response) {

                VoResponseBPMSync mVoResponseBPMSync = response.body();

                if (mVoResponseBPMSync != null) {
                    if (mVoResponseBPMSync.getSuccess() != null &&
                            mVoResponseBPMSync.getSuccess().equalsIgnoreCase("1")) {
                        if (mVoResponseBPMSync.getRendom_number() != null && mVoResponseBPMSync.getRendom_number().size() > 0) {
                            UpdateMainTable(mVoResponseBPMSync.getRendom_number().get(0), 0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VoResponseBPMSync> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void SyncSleepDataToServer() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("access_token", mSharedPreferences.getString(Constant.PREFS_ACCESS_TOKEN, ""));
        hashMap.put("sleepdata", mJsonArraySleepData.toString());
        hashMap.put("user_id", mSharedPreferences.getString(Constant.PREFS_USER_ID, ""));
        Call<VOSleepSyncDataToServer> syncBPMDataToServer = mApiService.syncSleepDataToServer(hashMap);
        syncBPMDataToServer.enqueue(new Callback<VOSleepSyncDataToServer>() {
            @Override
            public void onResponse(Call<VOSleepSyncDataToServer> call, Response<VOSleepSyncDataToServer> response) {

                VOSleepSyncDataToServer mVoResponseBPMSync = response.body();

                if (mVoResponseBPMSync != null) {
                    if (mVoResponseBPMSync.getSuccess() != null &&
                            mVoResponseBPMSync.getSuccess().equalsIgnoreCase("1")) {
                        if (mVoResponseBPMSync.getRendom_number() != null && mVoResponseBPMSync.getRendom_number().size() > 0) {
                            updateSleepRecord (mVoResponseBPMSync.getRendom_number().get(0));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VOSleepSyncDataToServer> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void SyncMedicationTakenDataToServer(String strMedicationName, String strMedicationDateTime,final String strMedicationTakenID) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", mSharedPreferences.getString(Constant.PREFS_USER_ID, ""));
        hashMap.put("access_token", mSharedPreferences.getString(Constant.PREFS_ACCESS_TOKEN, ""));
        hashMap.put("medication_name", strMedicationName);
        hashMap.put("medication_time", strMedicationDateTime);

        Call<VoMedicationTaken> syncBPMDataToServer = mApiService.syncMadicationTakenDataToServer(hashMap);
        syncBPMDataToServer.enqueue(new Callback<VoMedicationTaken>() {
            @Override
            public void onResponse(Call<VoMedicationTaken> call, Response<VoMedicationTaken> response) {

                VoMedicationTaken mVoMedicationTaken = response.body();

                if (mVoMedicationTaken != null) {
                    if (mVoMedicationTaken.getSuccess() != null &&
                            mVoMedicationTaken.getSuccess().equalsIgnoreCase("1")) {
                        UpdateMedicationTakenTable(strMedicationTakenID);
                    }
                }
            }

            @Override
            public void onFailure(Call<VoMedicationTaken> call, Throwable t) {
                t.printStackTrace();
                FetchMedicationTakenData();
            }
        });
    }

    public void SyncECGDataToServer() {

        VoRequestActivityItem mVoRequestActivityItem = new VoRequestActivityItem();
        mVoRequestActivityItem.setEnd_time(mStringEndTime);
        mVoRequestActivityItem.setTotal_time(mStringTotalTime);
        mVoRequestActivityItem.setStart_time(mStringStartTime);
        mVoRequestActivityItem.setDate(mStringDate);

        mVoRequestActivityItem.setTime_details(mArrayTimeDetails);
        mVoRequestActivityItem.setBpm_details(mArrayBPMDetails);
        mVoRequestActivityItem.setDistance_details(mArrayDistanceDetails);
        mVoRequestActivityItem.setMet_details(mArrayMetDetails);
        mVoRequestActivityItem.setHrv_details(mArrayHRVDetails);
        mVoRequestActivityItem.setEcg_details(mArrayECGDetails);
        mVoRequestActivityItem.setSteps_details(mArrayStepsDetails);
        mArrayActivityItem.add(mVoRequestActivityItem);

        VoRequestActivityInfoItem mVoRequestActivityInfoItem = new VoRequestActivityInfoItem();
        mVoRequestActivityInfoItem.setActivity_details(mArrayActivityItem);
        mVoRequestActivityInfoItem.setAccess_token(mSharedPreferences.getString(Constant.PREFS_ACCESS_TOKEN, ""));
        mVoRequestActivityInfoItem.setRendom_number(mStringRandomNumber);
        mVoRequestActivityInfoItem.setType("ecg");
        mVoRequestActivityInfoItem.setUser_id(mSharedPreferences.getString(Constant.PREFS_USER_ID, ""));
        mArrayActivityInfoItem.add(mVoRequestActivityInfoItem);

        VoRequestBPMItem mVoRequestBPMItem = new VoRequestBPMItem();
        mVoRequestBPMItem.setActivity_details_info(mArrayActivityInfoItem);

        Call<VoResponseBPMSync> syncBPMDataToServer = mApiService.syncBPMDataToServer(mVoRequestBPMItem);
        syncBPMDataToServer.enqueue(new Callback<VoResponseBPMSync>() {
            @Override
            public void onResponse(Call<VoResponseBPMSync> call, Response<VoResponseBPMSync> response) {

                VoResponseBPMSync mVoResponseBPMSync = response.body();

                if (mVoResponseBPMSync != null) {
                    if (mVoResponseBPMSync.getSuccess() != null &&
                            mVoResponseBPMSync.getSuccess().equalsIgnoreCase("1")) {

                        if (mVoResponseBPMSync.getRendom_number() != null && mVoResponseBPMSync.getRendom_number().size() > 0) {
                            UpdateMainTable(mVoResponseBPMSync.getRendom_number().get(0), 1);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VoResponseBPMSync> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void FetchBPMTableData() {



        DataHolder mDataHolder = mDbHelper.read("SELECT bpm.id as bpm_id, bpm.bpm_value," +
                " bpm."+DBHelper.mBPM_DETAILS_AVERAGE_BPM+" as "+DBHelper.mBPM_DETAILS_AVERAGE_BPM+","+//2021
                " bpm."+DBHelper.mBPM_DETAILS_MAX_BPM+" as "+DBHelper.mBPM_DETAILS_MAX_BPM+","+//2021
                " distance.id as distance_id, distance.diatance_value,  " +
                " step.id as step_id, step.step_value, step.date_time, " +
                " spo2.id as spo2_id, spo2.spo2_value," +
                " met.id as met_id, met.met_value," +
                " met."+DBHelper.mMET_DETAILS_Average_Met+" as "+DBHelper.mMET_DETAILS_Average_Met+","+//2021
                " met."+DBHelper.mMET_DETAILS_ACTIVITY_TYPE+" as "+DBHelper.mMET_DETAILS_ACTIVITY_TYPE+","+//2021
                " main.start_time, main.end_time, main.random_number, main.total_time, main.date FROM " +
                DBHelper.mTableMainActivityTable + " as main " +
                "LEFT JOIN " + DBHelper.mTableStepDetails + " as step ON step.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableDistanceDetails + " as distance ON distance.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableBPMDetails + " as bpm ON bpm.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableSPO2Details + " as spo2 ON spo2.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableMETDetails + " as met ON met.parent_id = main.id " +
                "where main.is_sync = '" + "no" + "' AND main.type = '" + "bpm" +
                "' AND main.user_id = '" + mSharedPreferences.getString(Constant.PREFS_USER_ID, "") + "'");


        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
            VoRequestTimeDetails mVoRequestTimeDetails = new VoRequestTimeDetails();
            mVoRequestTimeDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestTimeDetails.setTotal_time(",1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1");
            mArrayTimeDetails.add(mVoRequestTimeDetails);

            VoRequestBPMDetails mVoRequestBPMDetails = new VoRequestBPMDetails();
            mVoRequestBPMDetails.setBpm_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_BPM_Value));
            mVoRequestBPMDetails.setBpm_avg(mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_AVERAGE_BPM));//2021
            mVoRequestBPMDetails.setMax_bpm(mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_MAX_BPM));//2021
            mVoRequestBPMDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mArrayBPMDetails.add(mVoRequestBPMDetails);

            VoRequestDistanceDetails mVoRequestDistanceDetails = new VoRequestDistanceDetails();
            mVoRequestDistanceDetails.setTotal_distance(mDataHolder.get_Listholder().get(0).get(DBHelper.mDISTANCE_DETAILS_Distance_Value));
            mVoRequestDistanceDetails.setCurrent_distance(mDataHolder.get_Listholder().get(0).get(DBHelper.mDISTANCE_DETAILS_Distance_Value));
            mVoRequestDistanceDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mArrayDistanceDetails.add(mVoRequestDistanceDetails);

            VoRequestMetDetails mVoRequestMetDetails = new VoRequestMetDetails();
            mVoRequestMetDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestMetDetails.setMet_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_Met_Value));
            mVoRequestMetDetails.setMet_avg(mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_Average_Met));//2021
            mVoRequestMetDetails.setActivity(mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_ACTIVITY_TYPE));//2021
            mArrayMetDetails.add(mVoRequestMetDetails);

            VoRequestSPO2Details mVoRequestSPO2Details = new VoRequestSPO2Details();
            mVoRequestSPO2Details.setSpo2_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mSPO2_DETAILS_SPO2_Value));
            mVoRequestSPO2Details.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mArraySPO2Details.add(mVoRequestSPO2Details);

            VoRequestStepsDetails mVoRequestStepsDetails = new VoRequestStepsDetails();
            mVoRequestStepsDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestStepsDetails.setCurrent_steps(mDataHolder.get_Listholder().get(0).get(DBHelper.mSTEP_DETAILS_STEP_Value));
            mVoRequestStepsDetails.setTotal_steps(mDataHolder.get_Listholder().get(0).get(DBHelper.mSTEP_DETAILS_STEP_Value));
            mArrayStepsDetails.add(mVoRequestStepsDetails);

            mStringStartTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
            mStringEndTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_END_TIME);
            mStringTotalTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME);
            mStringDate = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_DATE);
            mStringRandomNumber = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_RANDOM_NUMBER);

            SyncBPMDataToServer();
        } else {
            FetchECGTableData();
        }
    }

    public void UpdateMedicationTakenTable(String strMedicationTakenID) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mTaken_Medication_Is_Sync, "yes");
        String[] mUpdateValue = new String[]{strMedicationTakenID};

        mDbHelper.updateRecord(DBHelper.mTableTakenMedication,
                mContentValues,
                DBHelper.mTaken_Medication_Table_ID + " =?",
                mUpdateValue);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FetchMedicationTakenData();
            }
        }, 1000);

    }

    public void UpdateMainTable(String mStringRandomNumber, final int type) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mMain_ACTIVITY_Is_Sync, "yes");
        String[] mUpdateValue = new String[]{mStringRandomNumber};

        mDbHelper.updateRecord(DBHelper.mTableMainActivityTable,
                mContentValues,
                DBHelper.mMain_ACTIVITY_RANDOM_NUMBER + " =?",
                mUpdateValue);

        this.mStringRandomNumber = "";
        this.mStringStartTime = "";
        this.mStringEndTime = "";
        this.mStringTotalTime = "";
        this.mStringDate = "";

        mArrayTimeDetails.clear();
        mArrayBPMDetails.clear();
        mArrayDistanceDetails.clear();
        mArrayMetDetails.clear();
        mArraySPO2Details.clear();
        mArrayStepsDetails.clear();
        mArrayActivityItem.clear();
        mArrayActivityInfoItem.clear();

        mDbHelper.removeAllBlankEntries();//2021-March

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(type == 0) {
                    FetchBPMTableData();
                } else if(type == 1) {
                    FetchECGTableData();
                }

            }
        }, 1000);
    }

    //For ECG
    public void FetchECGTableData() {

        DataHolder mDataHolder = mDbHelper.read("SELECT bpm.id as bpm_id, bpm.bpm_value," +
                " bpm."+DBHelper.mBPM_DETAILS_AVERAGE_BPM+" as "+DBHelper.mBPM_DETAILS_AVERAGE_BPM+","+//2021
                " bpm."+DBHelper.mBPM_DETAILS_MAX_BPM+" as "+DBHelper.mBPM_DETAILS_MAX_BPM+","+//2021
                " distance.id as distance_id, distance.diatance_value,  " +
                " step.id as step_id, step.step_value, step.date_time, " +
                " ecg."+DBHelper.mECG_DETAILS_HRV_VALUE+" as "+DBHelper.mECG_DETAILS_HRV_VALUE+","+//2021
                " ecg."+DBHelper.mECG_DETAILS_AVERAGE_HRV+" as "+DBHelper.mECG_DETAILS_AVERAGE_HRV+","+//2021
                " ecg.id as ecg_id, ecg.ecg_value," +
                " hrv.id as hrv_id, hrv.hrv_value," +
                " hrv."+DBHelper.mHRV_DETAILS_AVERAGE_HRV+" as "+DBHelper.mHRV_DETAILS_AVERAGE_HRV+","+//2021
                " hrv."+DBHelper.mHRV_DETAILS_EVENT_TYPE+" as "+DBHelper.mHRV_DETAILS_EVENT_TYPE+","+//2021
                " hrv."+DBHelper.mHRV_DETAILS_EVENT_COMMENT+" as "+DBHelper.mHRV_DETAILS_EVENT_COMMENT+","+//2021
                " met.id as met_id, met.met_value," +
                " met."+DBHelper.mMET_DETAILS_Average_Met+" as "+DBHelper.mMET_DETAILS_Average_Met+","+//2021
                " met."+DBHelper.mMET_DETAILS_ACTIVITY_TYPE+" as "+DBHelper.mMET_DETAILS_ACTIVITY_TYPE+","+//2021
                " main.start_time, main.end_time, main.random_number, main.total_time, main.date FROM " +
                DBHelper.mTableMainActivityTable + " as main " +
                "LEFT JOIN " + DBHelper.mTableStepDetails + " as step ON step.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableDistanceDetails + " as distance ON distance.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableBPMDetails + " as bpm ON bpm.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableECGDetails + " as ecg ON ecg.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableHRVDetails + " as hrv ON hrv.parent_id = main.id " +
                "LEFT JOIN " + DBHelper.mTableMETDetails + " as met ON met.parent_id = main.id " +
                "where main.is_sync = '" + "no" + "' AND main.type = '" + "ecg" +
                "' AND main.user_id = '" + mSharedPreferences.getString(Constant.PREFS_USER_ID, "") + "'");

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {

            VoRequestTimeDetails mVoRequestTimeDetails = new VoRequestTimeDetails();
            mVoRequestTimeDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestTimeDetails.setTotal_time(",1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1");
            mArrayTimeDetails.add(mVoRequestTimeDetails);

            VoRequestBPMDetails mVoRequestBPMDetails = new VoRequestBPMDetails();
            mVoRequestBPMDetails.setBpm_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_BPM_Value));
            mVoRequestBPMDetails.setBpm_avg(mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_AVERAGE_BPM));//2021
            mVoRequestBPMDetails.setMax_bpm(mDataHolder.get_Listholder().get(0).get(DBHelper.mBPM_DETAILS_MAX_BPM));//2021
            mVoRequestBPMDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mArrayBPMDetails.add(mVoRequestBPMDetails);

            VoRequestDistanceDetails mVoRequestDistanceDetails = new VoRequestDistanceDetails();
            mVoRequestDistanceDetails.setTotal_distance(mDataHolder.get_Listholder().get(0).get(DBHelper.mDISTANCE_DETAILS_Distance_Value));
            mVoRequestDistanceDetails.setCurrent_distance(mDataHolder.get_Listholder().get(0).get(DBHelper.mDISTANCE_DETAILS_Distance_Value));
            mVoRequestDistanceDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mArrayDistanceDetails.add(mVoRequestDistanceDetails);

            VoRequestMetDetails mVoRequestMetDetails = new VoRequestMetDetails();
            mVoRequestMetDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestMetDetails.setMet_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_Met_Value));
            mVoRequestMetDetails.setMet_avg(mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_Average_Met));//2021
            mVoRequestMetDetails.setActivity(mDataHolder.get_Listholder().get(0).get(DBHelper.mMET_DETAILS_ACTIVITY_TYPE));//2021
            mArrayMetDetails.add(mVoRequestMetDetails);

            VoRequestECGDetails mVoRequestECGDetails = new VoRequestECGDetails();
            mVoRequestECGDetails.setEcg_values(mDataHolder.get_Listholder().get(0).get(DBHelper.mECG_DETAILS_ECG_VALUE));
            mVoRequestECGDetails.setHrv_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mECG_DETAILS_HRV_VALUE));//2021
            mVoRequestECGDetails.setHrv_avg(mDataHolder.get_Listholder().get(0).get(DBHelper.mECG_DETAILS_AVERAGE_HRV));//2021
            mVoRequestECGDetails.setEcg_start_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestECGDetails.setEcg_end_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_END_TIME));
            mArrayECGDetails.add(mVoRequestECGDetails);

            VoRequestStepsDetails mVoRequestStepsDetails = new VoRequestStepsDetails();
            mVoRequestStepsDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestStepsDetails.setCurrent_steps(mDataHolder.get_Listholder().get(0).get(DBHelper.mSTEP_DETAILS_STEP_Value));
            mVoRequestStepsDetails.setTotal_steps(mDataHolder.get_Listholder().get(0).get(DBHelper.mSTEP_DETAILS_STEP_Value));
            mArrayStepsDetails.add(mVoRequestStepsDetails);

            VoRequestHRVDetails mVoRequestHRVDetails = new VoRequestHRVDetails();
            mVoRequestHRVDetails.setDate_time(mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME));
            mVoRequestHRVDetails.setHrv_value(mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_HRV_VALUE));

            mVoRequestHRVDetails.setEvent(mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_EVENT_TYPE));//2021
            mVoRequestHRVDetails.setEvent_comment(mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_EVENT_COMMENT));//2021
            mVoRequestHRVDetails.setHrv_avg(mDataHolder.get_Listholder().get(0).get(DBHelper.mHRV_DETAILS_AVERAGE_HRV));//2021
            mArrayHRVDetails.add(mVoRequestHRVDetails);

            mStringStartTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_START_TIME);
            mStringEndTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_END_TIME);
            mStringTotalTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_TOTAL_TIME);
            mStringDate = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_DATE);
            mStringRandomNumber = mDataHolder.get_Listholder().get(0).get(DBHelper.mMain_ACTIVITY_RANDOM_NUMBER);

            SyncECGDataToServer();
        } else {

            if(!mDbHelper.isThereAnyOnGoingSleepRecord()) {
                FetchSleepData();


            }
        }
    }


    public void FetchSleepData() {

        DataHolder mDataHolder = mDbHelper.read("SELECT * from "+DBHelper.mTableSleepDetail + " WHERE "
               + DBHelper.mSLEEP_DETAIL_USER_ID + "= '" + mSharedPreferences.getString(Constant.PREFS_USER_ID, "") +"' "
               + "AND " + DBHelper.mSLEEP_DETAIL_IS_SYNC + "= 'no' AND NULLIF("+DBHelper.mSLEEP_DETAIL_SLEEP_VALUE+", ' ') IS NOT NULL");

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {

            mJsonArraySleepData = new JSONArray();

            mStringStartTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_START_TIME);
            mStringEndTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_END_TIME);
            mStringTotalTime = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_TOTAL_TIME);
            mStringDate = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_DATE);
            mStringRandomNumber = mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_RANDOM_NUMBER);

            try {
                JSONObject mVoRequestSleepDetail = new JSONObject();
                mVoRequestSleepDetail.put("date_data", mStringDate);
                mVoRequestSleepDetail.put("start_time", mStringStartTime);
                mVoRequestSleepDetail.put("end_time", mStringEndTime);
                mVoRequestSleepDetail.put("rendom_number", mStringRandomNumber);
                mVoRequestSleepDetail.put("sleep_differenceTime", mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_SLEEP_DIFFERENCE));
                mVoRequestSleepDetail.put("sleep_value", mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_SLEEP_VALUE));
                mVoRequestSleepDetail.put("total_sleep_time", getConvertedTime(mDataHolder.get_Listholder().get(0).get(DBHelper.mSLEEP_DETAIL_TOTAL_TIME)));

                mJsonArraySleepData.put(mVoRequestSleepDetail);

                SyncSleepDataToServer();
            } catch (JSONException e) {
            }
        } else {
            FetchMedicationTakenData();
        }
    }

    public void FetchMedicationTakenData() {

        DataHolder mDataHolder = mDbHelper.read("SELECT * from "+DBHelper.mTableTakenMedication + " WHERE "
                + DBHelper.mTaken_Medication_Table_User_ID + "= '" + mSharedPreferences.getString(Constant.PREFS_USER_ID, "") +"' "
                + "AND " + DBHelper.mTaken_Medication_Is_Sync + "= 'no'");

        if (mDataHolder != null && mDataHolder.get_Listholder() != null && mDataHolder.get_Listholder().size() > 0) {
            SyncMedicationTakenDataToServer(mDataHolder.get_Listholder().get(0).get(DBHelper.mTaken_Medication_Table_Medication_Name),
                    mDataHolder.get_Listholder().get(0).get(DBHelper.mTaken_Medication_Date_N_Time),
                    mDataHolder.get_Listholder().get(0).get(DBHelper.mTaken_Medication_Table_ID));
        }
    }

    public int getConvertedTime(String totalSecs) {
        if(totalSecs != null && !totalSecs.equalsIgnoreCase("")) {
            return Integer.parseInt(totalSecs)/ 60;
        } else {
            return 0;
        }
    }

    public void updateSleepRecord(String mStringRandomNumber) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(DBHelper.mMain_ACTIVITY_Is_Sync, "yes");
        String[] mUpdateValue = new String[]{mStringRandomNumber};

        mDbHelper.updateRecord(DBHelper.mTableSleepDetail,
                mContentValues,
                DBHelper.mMain_ACTIVITY_RANDOM_NUMBER + " =?",
                mUpdateValue);

        this.mStringRandomNumber = "";
        this.mStringStartTime = "";
        this.mStringEndTime = "";
        this.mStringTotalTime = "";
        this.mStringDate = "";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FetchSleepData();
            }
        }, 1000);
    }
}