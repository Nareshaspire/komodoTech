package com.aiosleeve.aiosleeve.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.VO.VOGetUserData;
import com.aiosleeve.aiosleeve.VO.VOGetUserDataItems;
import com.aiosleeve.aiosleeve.VO.VOLogOut;
import com.aiosleeve.aiosleeve.VO.VOUpdateUserProfile;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.API;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FragmentSettings extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {

    public static final String TAG = "FragmentSettings";

    public EditText mEditTextUsername;
    //    public EditText mEditTextEmail;
    public EditText mEditTextBirthDate;
    public TextView mTextViewGender;
    public TextView mTextViewMetric;
    public EditText mEditTextWeight;
    public EditText mEditTextHeight;
    public EditText mEditTextTown;
    public EditText mEditTextMedication;
    public EditText mEditTextHeartCondition;
    public EditText mEditTextMedicationOne;
    public EditText mEditTextMedicationTwo;
    public EditText mEditTextMedicationThree;

    public TextView mTextViewEmail;
    public TextView mTextWeightUnit;
    public TextView mTextViewHeightUnit;
//    public TextView mTextViewHome;
//    public TextView mTextViewBPM;
//    public TextView mTextViewSLEEP;
//    public TextView mTextViewHRV;

    public Button mButtonSave;
    public Button mButtonCancel;

//    public LinearLayout mLinearLayoutTabsHOME;
//    public LinearLayout mLinearLayoutTabsBPM;
//    public LinearLayout mLinearLayoutTabsSLEEp;
//    public LinearLayout mLinearLayoutTabsHRV;

    //    RelativeLayout mRelativeLayoutBottomBar;
    RelativeLayout mRelativeLayoutMain;

    //    public ImageView mImageViewHome;
    public ImageView mImageViewBack;
//    public ImageView mImageViewBPM;
//    public ImageView mImageViewSLEEp;
//    public ImageView mImageViewHRV;

    public static float mHeightFeet;
    public static float mHeightInch;
    public static float mHeightCms;
    public static float mWeightKg;
    public static float mWeightLbs;

    public Utility mUtility;
    public Retrofit mRetrofit;
    public API mApiService;

    public Button mButtonOk;

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    int mMinute = 0;
    int mHour = 0;

    //Arraylist
    ArrayList<VOGetUserDataItems> mVoGetUserDataItemses = new ArrayList<>();
    ArrayList<String> arrayListCentimeter = new ArrayList<>();

    private String mStringStartDate = "";
    String mStringStartDateTime = "";

    private Dialog openDialog;
    private DBHelper mDbHelper;
    private DataHolder mDataHolder;

    public static final int REQUEST_LOCATION = 001;

    GoogleApiClient mGoogleApiClient;

    LocationManager locationManager;
    LocationRequest locationRequest;
    LocationSettingsRequest.Builder locationSettingsRequest;

    PendingResult<LocationSettingsResult> pendingResult;

    private Location mLocation;
    private LocationRequest mLocationRequest;

    String strLatitude = "";
    String strLongitude = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_settings);
        mUtility = new Utility(FragmentSettings.this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClientWithLogger())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mDbHelper = new DBHelper(FragmentSettings.this);
        mApiService = mRetrofit.create(API.class);

        mVoGetUserDataItemses = new ArrayList<>();

        mImageViewBack = (ImageView) findViewById(R.id.fragment_setting_imageview_back);

//        mLinearLayoutTabsHOME = (LinearLayout) findViewById(R.id.activity_main_linear_tab_home);
//        mLinearLayoutTabsBPM = (LinearLayout) findViewById(R.id.activity_main_linear_bpm);
//        mLinearLayoutTabsSLEEp = (LinearLayout) findViewById(R.id.activity_main_linear_tab_sleep);
//        mLinearLayoutTabsHRV = (LinearLayout) findViewById(R.id.activity_main_linear_tab_hrv);

//        mRelativeLayoutBottomBar = (RelativeLayout) findViewById(R.id.fragment_seting_bottombar);
        mRelativeLayoutMain = (RelativeLayout) findViewById(R.id.fragment_setting_main);

//        mImageViewHome = (ImageView) findViewById(R.id.activity_main_imageview_home);
//        mImageViewBPM = (ImageView) findViewById(R.id.activity_main_imageview_bpm);
//        mImageViewSLEEp = (ImageView) findViewById(R.id.activity_main_imageview_sleep);
//        mImageViewHRV = (ImageView) findViewById(R.id.activity_main_imageview_hrv);
//
//        mTextViewHome = (TextView) findViewById(R.id.activity_main_textview_home);
//        mTextViewBPM = (TextView) findViewById(R.id.activity_main_textview_bpm);
//        mTextViewSLEEP = (TextView) findViewById(R.id.activity_main_textview_sleep);
//        mTextViewHRV = (TextView) findViewById(R.id.activity_main_textview_hrv);
        mTextViewHeightUnit = (TextView) findViewById(R.id.fragment_seting_textview_heightunit);
        mTextWeightUnit = (TextView) findViewById(R.id.fragment_seting_textview_weightunit);

        mEditTextUsername = (EditText) findViewById(R.id.fragment_settings_edittext_username);
        mTextViewEmail = (TextView) findViewById(R.id.fragment_settings_edittext_email);
        mEditTextBirthDate = (EditText) findViewById(R.id.fragment_settings_edittext_dob);
        mTextViewGender = (TextView) findViewById(R.id.fragment_settings_edittext_gender);
        mTextViewMetric = (TextView) findViewById(R.id.fragment_settings_edittext_metric);
        mEditTextWeight = (EditText) findViewById(R.id.fragment_settings_edittext_weight);
        mEditTextHeight = (EditText) findViewById(R.id.fragment_settings_edittext_height);
        mEditTextTown = (EditText) findViewById(R.id.fragment_settings_edittext_town);
        mEditTextMedication = (EditText) findViewById(R.id.fragment_settings_edittext_medication);
        mEditTextHeartCondition = (EditText) findViewById(R.id.fragment_settings_edittext_heart_condition);

        mButtonSave = (Button) findViewById(R.id.fragment_settings_button_save);

//        mLinearLayoutTabsHOME.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        for (int i = 40; i <= 240; i++) {
//            arrayListCentimeter.add(String.valueOf(i));
//        }
//
//        mLinearLayoutTabsSLEEp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(FragmentSettings.this, FragmentSleep.class);
//                startActivity(mIntent);
//                finish();
//            }
//        });
//
//        mLinearLayoutTabsBPM.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(FragmentSettings.this, FragmentBPM.class);
//                startActivity(mIntent);
//                finish();
//            }
//        });
//
//        mLinearLayoutTabsHRV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(FragmentSettings.this, FragmentECG.class);
//                startActivity(mIntent);
//                finish();
//            }
//        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (mUtility.haveInternet()) {
                        updateData();
                    } else {
                        mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                                getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
        });

        mTextViewMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    showOptionsDialog();
//                    return true;
//                }
//                return false;
//            }
        });

//        mEditTextMedication.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    showMedicationDialog();
//                    return true;
//                }
//                return false;
//            }
//        });

        mEditTextBirthDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    openDatePickerDialog();
                    return true;
                }
                return false;
            }
        });

        mEditTextHeight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mTextViewMetric.getText().toString().equals(Constant.UNIT_METRIC)) {
                        showCentimeterOptionsDialog();
                    } else if (mTextViewMetric.getText().toString().equals(Constant.UNIT_IMPERIAL)) {
                        showFeetsOptionsDialog();
                    }
                    return true;
                }
                return false;
            }
        });

        mTextViewGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderOptionsDialog();
            }

//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    showGenderOptionsDialog();
//                    return true;
//                }
//                return false;
//
//            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mUtility.haveInternet()) {
            getUserDetails();
        } else {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name), getResources().getString(R.string.no_internet_msg));
        }

//        if (MainActivity.connectedDevice != null) {
//            mRelativeLayoutBottomBar.setVisibility(View.VISIBLE);
//        } else {
//            mRelativeLayoutBottomBar.setVisibility(View.GONE);
//        }

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
//            public void onDisconnect(String devicesName, String devicesAddress) {
////                mRelativeLayoutBottomBar.setVisibility(View.GONE);
//                Intent intent = new Intent(FragmentSettings.this,MainActivity.class);
//                intent.putExtra(MainActivity.IS_FROM_SETTINGS_SCREEN,true);
//                startActivity(intent);
//                finishAffinity();
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

//        mRelativeLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                int heightDiff = mRelativeLayoutMain.getRootView().getHeight() - mRelativeLayoutMain.getHeight();
//
//                if (heightDiff > 300) {
//                    mRelativeLayoutBottomBar.setVisibility(View.GONE);
//                } else {
//                    if (MainActivity.connectedDevice != null) {
//                        mRelativeLayoutBottomBar.setVisibility(View.VISIBLE);
//                    } else {
//                        mRelativeLayoutBottomBar.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        } else {
            mEnableGps();
        }
    }

    public void mEnableGps() {
        mGoogleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        mResult();
    }

    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();


                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(FragmentSettings.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }

        });
    }


    //callback method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void showGenderOptionsDialog() {

        List<String> mGender = new ArrayList<String>();
        mGender.add(Constant.GENDER_MALE);
        mGender.add(Constant.GENDER_FEMALE);
        String choice;
        // Create sequence of items
        final CharSequence[] Animals = mGender.toArray(new String[mGender.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentSettings.this);
        dialogBuilder.setTitle("Choose Gender");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int item) {
                String selectedText = Animals[item].toString();

                if (selectedText.equals(Constant.GENDER_MALE)) {
                    mTextViewGender.setText(Constant.GENDER_MALE);
                } else if (selectedText.equals(Constant.GENDER_FEMALE)) {
                    mTextViewGender.setText(Constant.GENDER_FEMALE);
                }
            }
        });
        // Create alert dialog object via builder
        dialogBuilder.create();
        // Show the dialog
        dialogBuilder.show();
    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (MainActivity.connectedDevice != null) {
//            mRelativeLayoutBottomBar.setVisibility(View.VISIBLE);
//        } else {
//            mRelativeLayoutBottomBar.setVisibility(View.GONE);
//        }
//
//    }

    public void showMedicationDialog() {
        try {
            mDataHolder = mDbHelper.read("SELECT * from " + DBHelper.mTableMedication + " Where " + DBHelper.mTable_Medication_User_ID + " ='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }

        openDialog = new Dialog(FragmentSettings.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.popup_medication_dialog_layout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        openDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mButtonOk = (Button) openDialog.findViewById(R.id.popup_medication_dialog_layout_button_ok);
        mButtonCancel = (Button) openDialog.findViewById(R.id.popup_medication_dialog_layout_button_cancel);
        mEditTextMedicationOne = (EditText) openDialog.findViewById(R.id.popup_medication_dialog_layout_edittext_medication1);
        mEditTextMedicationTwo = (EditText) openDialog.findViewById(R.id.popup_medication_dialog_layout_edittext_medication2);
        mEditTextMedicationThree = (EditText) openDialog.findViewById(R.id.popup_medication_dialog_layout_edittext_medication3);

        String[] mStringMedication = mEditTextMedication.getText().toString().trim().split(",");

        for (int i = 0; i < mStringMedication.length; i++) {
            if (i == 0) {
                if (!mStringMedication[i].equalsIgnoreCase(""))
                    mEditTextMedicationOne.setText(mStringMedication[i]);
            } else if (i == 1) {
                if (!mStringMedication[i].equalsIgnoreCase(""))
                    mEditTextMedicationTwo.setText(mStringMedication[i]);
            } else if (i == 2) {
                if (!mStringMedication[i].equalsIgnoreCase(""))
                    mEditTextMedicationThree.setText(mStringMedication[i]);
            }
        }

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strMedications = "";

                if (mEditTextMedicationOne.getText().toString().trim().equalsIgnoreCase("")
                        && mEditTextMedicationTwo.getText().toString().trim().equalsIgnoreCase("")
                        && mEditTextMedicationThree.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(FragmentSettings.this, "Please add at least one medication", Toast.LENGTH_LONG).show();
                } else {

                    openDialog.dismiss();
                    mDbHelper.deleteTableData(DBHelper.mTableMedication);

                    if (!mEditTextMedicationOne.getText().toString().trim().equalsIgnoreCase("")) {
                        strMedications = mEditTextMedicationOne.getText().toString().trim() + ",";
                        insertIntoMeditationTable(mEditTextMedicationOne.getText().toString().trim());
                    }

                    if (!mEditTextMedicationTwo.getText().toString().trim().equalsIgnoreCase("")) {
                        strMedications = strMedications + mEditTextMedicationTwo.getText().toString().trim() + ",";
                        insertIntoMeditationTable(mEditTextMedicationTwo.getText().toString().trim());
                    }

                    if (!mEditTextMedicationThree.getText().toString().trim().equalsIgnoreCase("")) {
                        strMedications = strMedications + mEditTextMedicationThree.getText().toString().trim();
                        insertIntoMeditationTable(mEditTextMedicationThree.getText().toString().trim());
                    }

                    mEditTextMedication.setText(strMedications);
                    saveMedicationData(strMedications);
                }
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });

        openDialog.show();
    }

    public void saveMedicationData(String FullMedinieList) {
        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("user_id", mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mHashMap.put("access_token", mUtility.getAppPrefString(Constant.PREFS_ACCESS_TOKEN));
        mHashMap.put("medication_name", FullMedinieList);

        Call<VOLogOut> mSetMedicationcall = mApiService.setMedication(mHashMap);
        mSetMedicationcall.enqueue(new Callback<VOLogOut>() {
            @Override
            public void onResponse(Call<VOLogOut> call, Response<VOLogOut> response) {
                VOLogOut mVoLogOut = response.body();
                if (mVoLogOut != null) {
                    if (mVoLogOut.getSuccess().equalsIgnoreCase("1")) {

                    }
                }
            }

            @Override
            public void onFailure(Call<VOLogOut> call, Throwable t) {

            }
        });

    }

    public String TrimLastStringCharacterMethod(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void insertIntoMeditationTable(String medicine) {
        try {
            mStringStartDateTime = mUtility.getDateTime();

            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mTable_Medication_name, medicine);
            mContentValues.put(DBHelper.mTable_Medication_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            mDbHelper.insertRecord(DBHelper.mTableMedication, mContentValues);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateIntoMainActivity(String mId, String medication_name) {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mTable_Medication_name, medication_name);

            mDbHelper.updateRecord(DBHelper.mTableMedication, mContentValues, DBHelper.mTable_Medication_ID + " = ?", new String[]{mId});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Unit Change Dialog(Metric/Imperial)
    public void showOptionsDialog() {

        List<String> mUnitData = new ArrayList<String>();
        mUnitData.add(Constant.UNIT_METRIC);
        mUnitData.add(Constant.UNIT_IMPERIAL);
        String choice;
        // Create sequence of items
        final CharSequence[] mUnitCharcter = mUnitData.toArray(new String[mUnitData.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentSettings.this);
        dialogBuilder.setTitle("Choose Unit");
        dialogBuilder.setItems(mUnitCharcter, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int item) {
                String selectedText = mUnitCharcter[item].toString();

                if (selectedText.equals(Constant.UNIT_METRIC)) {
                    if (!(mTextViewMetric.getText().toString().equalsIgnoreCase(Constant.UNIT_METRIC))) {

                        mTextViewMetric.setText(Constant.UNIT_METRIC);
                        mTextWeightUnit.setText("kg");
                        mTextViewHeightUnit.setText("cms");
                        if (mWeightKg == 0) {
                            if (!(mEditTextWeight != null && mEditTextWeight.getText().toString().equalsIgnoreCase(""))) {
                                String mStringHeight = mEditTextWeight.getText().toString().replace("kgs", "").trim();
                                int weight = 0;
                                if (mStringHeight.equalsIgnoreCase("0")
                                        || mStringHeight.equalsIgnoreCase("0.0")
                                        || mStringHeight.equalsIgnoreCase("0.00")) {
                                    weight = 0;
                                } else {
                                    weight = Integer.parseInt(mStringHeight);
                                }

                                double conversionlb = Math.round((weight * .453) * 10) / 10.0; //pound convereted to kg
                                mWeightKg = (float) conversionlb;
                                getHeightAndWeightFromNMetric(String.valueOf(mHeightCms), String.valueOf(mWeightKg));
                            }
                        } else {
                            getHeightAndWeightFromNMetric(String.valueOf(mHeightCms), String.valueOf(mWeightKg));
                        }
                    }
                } else if (selectedText.equals(Constant.UNIT_IMPERIAL)) {
                    if (!(mTextViewMetric.getText().toString().equalsIgnoreCase(Constant.UNIT_IMPERIAL))) {

                        setImperial();

                    }
                }
//                else {
//
//                }
            }
        });
        // Create alert dialog object via builder
        dialogBuilder.create();
        // Show the dialog
        dialogBuilder.show();
    }


    private void setImperial() {
        mTextViewMetric.setText(Constant.UNIT_IMPERIAL);
        mTextWeightUnit.setText("lbs");
        mTextViewHeightUnit.setText("ft");
        if (mWeightLbs == 0) {
            if (!(mEditTextWeight != null && mEditTextWeight.getText().toString().equalsIgnoreCase(""))) {
                String mStringHeight = mEditTextWeight.getText().toString().replace("kgs", "").trim();
                int weight = 0;
                if (mStringHeight.equalsIgnoreCase("0")
                        || mStringHeight.equalsIgnoreCase("0.0")
                        || mStringHeight.equalsIgnoreCase("0.00")) {
                    weight = 0;
                } else {
                    weight = getParsedIntOrZero(mStringHeight);
                }

                double conversionkg = Math.round((weight * 2.2) * 10) / 10.0; //kg convereted to pounds
                mWeightLbs = (float) conversionkg;
                getHeightAndWeightFromNMetric(String.valueOf(mHeightFeet) + "." + String.valueOf(mHeightInch), String.valueOf(mWeightLbs));
            }
        } else {
            getHeightAndWeightFromNMetric(String.valueOf(mHeightFeet) + "." + String.valueOf(mHeightInch), String.valueOf(mWeightLbs));
        }
    }

    //Shows Dialog to choose Centimeters for Height.
    public void showCentimeterOptionsDialog() {

        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(FragmentSettings.this)
                .minValue(40)
                .maxValue(240)
                .defaultValue(40)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.DKGRAY)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new AlertDialog.Builder(this)
                .setTitle("Choose height in centimeters")
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Snackbar.make(findViewById(R.id.fragment_setting_main), "You picked : " + numberPicker.getValue(), Snackbar.LENGTH_LONG).show();
                        mEditTextHeight.setText(String.valueOf(numberPicker.getValue()));
                        mHeightCms = numberPicker.getValue();
                    }
                })
                .show();
    }

    // Shows Dialog to choose Feets and Inches for Height.
    public void showFeetsOptionsDialog() {
        final Dialog openDialogSuccess = new Dialog(FragmentSettings.this);
        openDialogSuccess.setContentView(R.layout.popup_list_layout);
        openDialogSuccess.setCancelable(true);
        openDialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        openDialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Button mButtonNext = (Button) openDialogSuccess.findViewById(R.id.popup_list_layout_button_next);
        final NumberPicker mFeetPicker = (NumberPicker) openDialogSuccess.findViewById(R.id.popup_list_layout_feet_picker);
        final NumberPicker mInchPicker = (NumberPicker) openDialogSuccess.findViewById(R.id.popup_list_layout_inch_picker);

        String[] feetArray = {"1 Ft", "2 Ft", "3 Ft", "4 Ft", "5 Ft", "6 Ft", "7 Ft", "8 Ft"};
        mFeetPicker.setMaxValue(feetArray.length);
        mFeetPicker.setMinValue(1);
        mFeetPicker.setDisplayedValues(feetArray);

        String[] inchArray = {"1 Inch", "2 Inch", "3 Inch", "4 Inch", "5 Inch", "6 Inch", "7 Inch", "8 Inch", "9 Inch", "10 Inch", "11 Inch"};
        mInchPicker.setMaxValue(inchArray.length);
        mInchPicker.setMinValue(1);
        mInchPicker.setDisplayedValues(inchArray);

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mEditTextHeight.setText(String.valueOf(mFeetPicker.getValue() + "." + mInchPicker.getValue()));
                mEditTextHeight.setText(
                        getString(R.string.text_dynamic_feet_inch,
                                String.valueOf(Math.round(mFeetPicker.getValue())),
                                String.valueOf(Math.round(mInchPicker.getValue()))
                        )
                );
                openDialogSuccess.dismiss();
                mHeightFeet = mFeetPicker.getValue();
                mHeightInch = mInchPicker.getValue();
            }
        });
        openDialogSuccess.show();
    }

    // Validation Method for Updating Data
    private boolean isValid() {
        if (mEditTextUsername.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_name));
            mEditTextUsername.requestFocus();
            return false;
        }
//        if (mEditTextEmail.getText().toString().trim().equalsIgnoreCase("")) {
//            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
//                    getResources().getString(R.string.text_settings_empty_email));
//            mEditTextEmail.requestFocus();
//            return false;
//        }
//        if (!mUtility.isValidEmail(mEditTextEmail.getText().toString().trim())) {
//            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
//                    getResources().getString(R.string.text_settings_invalid_email));
//            mEditTextEmail.requestFocus();
//            return false;
//        }

        if (mEditTextBirthDate.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_dob));
            mEditTextBirthDate.requestFocus();
            return false;
        }
        if (mTextViewGender.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_gender));
//            mTextViewGender.requestFocus();
            return false;
        }

        if (mTextViewMetric.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_metric));
            mTextViewMetric.requestFocus();
            return false;
        }

        if (mEditTextWeight.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_weight));
            mEditTextWeight.requestFocus();
            return false;
        }

        if (mEditTextHeight.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_height));
            mEditTextHeight.requestFocus();
            return false;
        }

        if (mEditTextTown.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_settings_empty_town));
            mEditTextTown.requestFocus();
            return false;
        }

        return true;
    }

    // Update Data Service
    private void updateData() {

        mUtility.ShowProgress();

        mUtility.writeSharedPreferencesString(Constant.PREFS_METRIC_OR_IMPERIAL, mTextViewMetric.getText().toString());


        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("user_id", mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        mHashMap.put("name", mEditTextUsername.getText().toString().trim());
        mHashMap.put("email", mTextViewEmail.getText().toString().trim());
        mHashMap.put("phone_no", "1234567890");
        mHashMap.put("dob", mEditTextBirthDate.getText().toString().trim());
        if (mTextViewGender.getText().toString().trim().equalsIgnoreCase("male")) {
            mHashMap.put("gender", "1");
        } else {
            mHashMap.put("gender", "2");
        }
        if (mTextViewHeightUnit.getText().toString().equals("cms")) {
            mHashMap.put("height", String.valueOf(mHeightCms));
        } else {
            //double newWeight=feetToCentimeter(mEditTextWeight.getText().toString().trim());
            if (mHeightFeet != 0 && mHeightInch != 0) {
                double newWeight = feetToCentimeter(mHeightFeet + "' " + mHeightInch + "\"");
                mHeightCms = (int) newWeight; // height in centimeters
                mHashMap.put("height", String.valueOf(mHeightCms));
            } else {
                mHashMap.put("height", String.valueOf(mHeightCms));
            }
        }
        if (mTextWeightUnit.getText().toString().equals("kg")) {
            if (!mTextWeightUnit.getText().toString().equals("0")) {
                mHashMap.put("weight", mEditTextWeight.getText().toString().trim());
            } else {
                mHashMap.put("weight", String.valueOf(mWeightKg));
            }
        } else {
            mHashMap.put("weight", String.valueOf(mWeightKg));
        }

        String medication = TrimLastStringCharacterMethod(mEditTextMedication.getText().toString().trim());

        mHashMap.put("town", mEditTextTown.getText().toString().trim());
        mHashMap.put("photo", mUtility.getAppPrefString(Constant.PREFS_IMAGE));
        mHashMap.put("photo_flag", "0");
        mHashMap.put("lat", strLatitude);
        mHashMap.put("lon", strLongitude);
        mHashMap.put("medication", medication);
        mHashMap.put("heart_condition", mEditTextHeartCondition.getText().toString().trim());

        Call<VOUpdateUserProfile> loginUser = mApiService.updateUserProfile(mHashMap);
        loginUser.enqueue(new Callback<VOUpdateUserProfile>() {
            @Override
            public void onResponse(Call<VOUpdateUserProfile> call, Response<VOUpdateUserProfile> response) {
                mUtility.HideProgress();
                VOUpdateUserProfile mVoUpdateUserProfile = response.body();

                if (mVoUpdateUserProfile != null) {
                    if (mVoUpdateUserProfile.getSuccess() != null && mVoUpdateUserProfile.getSuccess().equalsIgnoreCase("1")) {
                        Toast.makeText(FragmentSettings.this, mVoUpdateUserProfile.getMessage(), Toast.LENGTH_LONG).show();
                        String[] mStringMedication = mEditTextMedication.getText().toString().trim().split(",");
                        mDbHelper.deleteTableData(DBHelper.mTableMedication);
                        for (int i = 0; i < mStringMedication.length; i++) {
                            if (!mStringMedication[i].equalsIgnoreCase(""))
                                insertIntoMeditationTable(mStringMedication[i]);
                        }
                    } else {
                        if (mVoUpdateUserProfile.getMessage() != null) {
                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name), mVoUpdateUserProfile.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VOUpdateUserProfile> call, Throwable t) {
                mUtility.HideProgress();
                t.printStackTrace();
            }
        });
    }

    //Get All Profile data from Service
    private void getUserDetails() {
        mUtility.ShowProgress();

        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("user_id", mUtility.getAppPrefString(Constant.PREFS_USER_ID));

        System.out.println("");

        Call<VOGetUserData> loginUser = mApiService.getUserDataByUserId(mHashMap);
        loginUser.enqueue(new Callback<VOGetUserData>() {
            @Override
            public void onResponse(Call<VOGetUserData> call, Response<VOGetUserData> response) {
                mUtility.HideProgress();
                VOGetUserData mVoGetUserData = response.body();

                System.out.println("getUserDetails..." + new Gson().toJson(mVoGetUserData));

                if (mVoGetUserData != null) {
                    if (mVoGetUserData.getSuccess() != null && mVoGetUserData.getSuccess().equalsIgnoreCase("1")) {
                        mVoGetUserDataItemses = (ArrayList<VOGetUserDataItems>) mVoGetUserData.getUserdata();
                        fetchFieldData(mVoGetUserDataItemses.get(0));
                    } else {
                        if (mVoGetUserData.getMessage() != null) {
                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name), mVoGetUserData.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VOGetUserData> call, Throwable t) {
                mUtility.HideProgress();
                t.printStackTrace();
            }
        });
    }

    //Fetch Data after getting response from service
    private void fetchFieldData(VOGetUserDataItems voGetUserDataItems) {

        if (!voGetUserDataItems.getMedactions().equalsIgnoreCase("")) {
            String[] mStringMedication = voGetUserDataItems.getMedactions().split(",");
            mDbHelper.deleteTableData(DBHelper.mTableMedication);
            for (int i = 0; i < mStringMedication.length; i++) {
                if (!mStringMedication[i].equalsIgnoreCase(""))
                    insertIntoMeditationTable(mStringMedication[i]);
            }
            mEditTextMedication.setText(voGetUserDataItems.getMedactions());
        }
        mEditTextUsername.setText(voGetUserDataItems.getName());
        mEditTextBirthDate.setText(voGetUserDataItems.getDob());
        mTextViewEmail.setText(voGetUserDataItems.getEmail());
        mEditTextHeartCondition.setText(voGetUserDataItems.getHeart_condition());

        if (voGetUserDataItems.getGender().equalsIgnoreCase("1")) {
            mTextViewGender.setText(Constant.GENDER_MALE);
        } else if (voGetUserDataItems.getGender().equalsIgnoreCase("2")) {
            mTextViewGender.setText(Constant.GENDER_FEMALE);
        } else {
            mTextViewGender.setText("");
        }
        mEditTextHeight.setText(voGetUserDataItems.getHeight());
        mEditTextWeight.setText(voGetUserDataItems.getWeight());
        mEditTextTown.setText(voGetUserDataItems.getTown());
        mTextViewMetric.setText(Constant.UNIT_METRIC);
        mTextWeightUnit.setText("kg");
        mTextViewHeightUnit.setText("cms");
        String intWeight = voGetUserDataItems.getWeight().replace("kgs", "").replace("kg", "").trim();
        String intHeight = voGetUserDataItems.getHeight().replace("cms", "").replace("cm", "").trim();
        if (intHeight.equalsIgnoreCase("")) {
            intHeight = "0";
        }
        if (intWeight.equalsIgnoreCase("")) {
            intWeight = "0";
        }
        try {
            mHeightCms = Float.parseFloat(intHeight);
            mWeightKg = Float.parseFloat(intWeight);
            double weight = Double.parseDouble(intWeight);

            double conversionkg = Math.round((weight * 2.2) * 10) / 10.0;
            mWeightLbs = (int) conversionkg;
            mHeightFeet = centimeterToFeet(intHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mUtility.getAppPrefString(Constant.PREFS_METRIC_OR_IMPERIAL) != null && mUtility.getAppPrefString(Constant.PREFS_METRIC_OR_IMPERIAL).equalsIgnoreCase(Constant.UNIT_IMPERIAL)) {
            setImperial();
        }
    }

    //To Convert  Height and Weight Units in Metric and Imperial.
    private void getHeightAndWeightFromNMetric(String Height, String Weight) {
        double weight = Double.parseDouble(Weight);
        Log.e("BOTH", Height + ", " + Weight);

        if (mTextViewMetric.getText().toString().contains(Constant.UNIT_METRIC)) {
            double conversionkg = Math.round((weight * 2.2) * 10) / 10.0; //kg convereted to pounds
//            double conversionkg =  Double.parseDouble(String.format(Locale.US, "%.2f",weight / 0.45359237)); //kg convereted to pounds
            mTextViewMetric.setText(Constant.UNIT_METRIC);
            mWeightLbs = (float) conversionkg;
            mEditTextWeight.setText(Weight);
            if (mHeightFeet != 0 && mHeightInch != 0) {
                double newWeight = feetToCentimeter(mHeightFeet + "' " + mHeightInch + "\"");
                mHeightCms = (float) newWeight; // height in centimeters
                mEditTextHeight.setText(String.valueOf(mHeightCms));
            } else {
                mEditTextHeight.setText(Height);
            }
        } else {
            double conversionlb = Math.round((weight * .453) * 10) / 10.0; //pound convereted to kg
//            double conversionlb = Double.parseDouble(String.format(Locale.US, "%.2f", (weight * 0.45359237))); //pound convereted to kg
            mTextViewMetric.setText(Constant.UNIT_IMPERIAL);
            if (mHeightCms != 0) {
                double newWeight = centimeterToFeet(String.valueOf(mHeightCms));
                mHeightFeet = (float) newWeight;  // height in feets
//                mEditTextHeight.setText(String.valueOf(mHeightFeet));

                mEditTextHeight.setText(
                        getString(R.string.text_dynamic_feet_inch,
                                String.valueOf(Math.round(mHeightFeet)),
                                String.valueOf(Math.round(mHeightInch))
                        )
                );
            } else {
                mEditTextHeight.setText(Height);
            }
            mWeightKg = (float) conversionlb;
            mEditTextWeight.setText(Weight);
        }
    }

    public static double feetToCentimeter(String feet) {
        double dCentimeter = 0d;
        if (!TextUtils.isEmpty(feet)) {
            if (feet.contains("'")) {
                String tempfeet = feet.substring(0, feet.indexOf("'"));
                if (!TextUtils.isEmpty(tempfeet)) {
                    dCentimeter += ((Double.parseDouble(tempfeet)) * 30.48);
                }
            }
            if (feet.contains("\"")) {
                String tempinch = feet.substring(feet.indexOf("'") + 1, feet.indexOf("\""));
                if (!TextUtils.isEmpty(tempinch)) {
                    dCentimeter += ((Double.parseDouble(tempinch)) * 2.54);
                }
            }
        }
//        return String.valueOf(dCentimeter); // will require in future
        return dCentimeter;
        //Format to decimal digit as per your requirement
    }

    public static int centimeterToFeet(String centemeter) {
        int feetPart = 0;
        int inchesPart = 0;
        if (!TextUtils.isEmpty(centemeter)) {
            double dCentimeter;
            if (centemeter.contains("cms")) {
                String newCMS = centemeter.replace("cms", "").trim();
                dCentimeter = Double.parseDouble(newCMS);
            } else {
                dCentimeter = Double.parseDouble(centemeter);
            }

            feetPart = (int) Math.floor((dCentimeter / 2.54) / 12);
            System.out.println((dCentimeter / 2.54) - (feetPart * 12));
            inchesPart = (int) Math.ceil((dCentimeter / 2.54) - (feetPart * 12));
        }
        mHeightInch = inchesPart;
        return feetPart;
    }

    public void openDatePickerDialog() {

        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(FragmentSettings.this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mStringStartDate = getProperTimeFormat(dayOfMonth) + "-" + getProperTimeFormat((monthOfYear + 1)) + "-" + getProperTimeFormat(year);
                        mEditTextBirthDate.setText(mStringStartDate);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            strLatitude = String.valueOf(mLocation.getLatitude());
            strLongitude = String.valueOf(mLocation.getLongitude());
        }
//        else {
//            // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
//        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        strLatitude = String.valueOf(location.getLatitude());
        strLongitude = String.valueOf(location.getLongitude());
    }

    @Override
    protected void onDestroy() {
        MainActivity.removeDeviceStatusListner(TAG);
        super.onDestroy();
    }

    /**
     * Description: This method is used for parsing string to int or 0 if parsing fails.
     *
     * @author Ankit Mishra
     * @since 06/01/22
     */
    private int getParsedIntOrZero(String string) {
        try {
            float floatOrZero=Float.parseFloat(string);
            return Math.round(floatOrZero);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
