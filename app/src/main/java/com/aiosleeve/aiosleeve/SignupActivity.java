package com.aiosleeve.aiosleeve;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aiosleeve.aiosleeve.VO.VOLogin;
import com.aiosleeve.aiosleeve.VO.VOSignUp;
import com.aiosleeve.aiosleeve.fragments.FragmentSettings;
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

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener{

    private Utility mUtility;
    private Retrofit mRetrofit;
    private API mApiService;
    public GradientDrawable gradientDrawable;

    //Views
    EditText mTextViewConfirmPassword;
    EditText mEditTextEmail;
    EditText mEditTextUsrname;
    EditText mEditTextPassword;
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
    

    TextView mTextViewSignInHere;

    ImageView mImageViewBack;

    Button mButtonSignUp;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    public static final int REQUEST_LOCATION=001;

    GoogleApiClient mGoogleApiClient;

    LocationManager locationManager;
    LocationRequest locationRequest;
    LocationSettingsRequest.Builder locationSettingsRequest;

    PendingResult<LocationSettingsResult> pendingResult;

    private Location mLocation;
    private LocationRequest mLocationRequest;

    String strLatitude = "";
    String strLongitude = "";

    public static float mHeightFeet;
    public static float mHeightInch;
    public static float mHeightCms;
    public static float mWeightKg;
    public static float mWeightLbs;

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;

    private String mStringStartDate = "";
    String mStringStartDateTime = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mUtility = new Utility(SignupActivity.this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClientWithLogger())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = mRetrofit.create(API.class);

        mTextViewSignInHere = (TextView) findViewById(R.id.activity_signup_textview_sign_up);
        mTextViewConfirmPassword = (EditText) findViewById(R.id.activity_signup_edittext_confirm_password);

        mEditTextUsrname = (EditText) findViewById(R.id.activity_signup_edittext_username);
        mEditTextEmail = (EditText) findViewById(R.id.activity_signup_edittext_email);
        mEditTextPassword = (EditText) findViewById(R.id.activity_signup_edittext_password);

        mTextViewHeightUnit = (TextView) findViewById(R.id.activity_signup_textview_heightunit);
        mTextWeightUnit = (TextView) findViewById(R.id.activity_signup_textview_weightunit);

        
        mEditTextBirthDate = (EditText) findViewById(R.id.activity_signup_edittext_dob);
        mTextViewGender = (TextView) findViewById(R.id.activity_signup_edittext_gender);
        mTextViewMetric = (TextView) findViewById(R.id.activity_signup_edittext_metric);
        mEditTextWeight = (EditText) findViewById(R.id.activity_signup_edittext_weight);
        mEditTextHeight = (EditText) findViewById(R.id.activity_signup_edittext_height);
        mEditTextTown = (EditText) findViewById(R.id.activity_signup_edittext_town);
        mEditTextMedication = (EditText) findViewById(R.id.activity_signup_edittext_medication);
        mEditTextHeartCondition = (EditText) findViewById(R.id.activity_signup_edittext_heart_condition);

        mImageViewBack=(ImageView) findViewById(R.id.activity_signup_imageview_back);

        mButtonSignUp = (Button) findViewById(R.id.activity_signup_button_signup);

        SetSpanToTextView(mTextViewSignInHere);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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


        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (mUtility.haveInternet()) {
                        SignupUser();
                    } else {
                        mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                                getResources().getString(R.string.no_internet_msg));
                    }
                }

            }
        });

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

                            status.startResolutionForResult(SignupActivity.this, REQUEST_LOCATION);
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

    private boolean isValid() {
        if (mEditTextUsrname.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_signup_empty_name));
            mEditTextUsrname.requestFocus();
            return false;
        }
        if (mEditTextEmail.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_signup_empty_email));
            mEditTextEmail.requestFocus();
            return false;
        }
        if (!mUtility.isValidEmail(mEditTextEmail.getText().toString().trim())) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_signup_invalid_email));
            mEditTextEmail.requestFocus();
            return false;
        }

        if (mEditTextPassword.getText().toString().trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_signup_empty_password));
            mEditTextPassword.requestFocus();
            return false;
        }
//        if (mTextViewConfirmPassword.getText().toString().trim().equalsIgnoreCase("")) {
//            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
//                    getResources().getString(R.string.text_signup_empty_confirm_password));
//            mTextViewConfirmPassword.requestFocus();
//            return false;
//        }
//
//        if (!mEditTextPassword.getText().toString().trim().equalsIgnoreCase(
//                mTextViewConfirmPassword.getText().toString().trim())) {
//            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
//                    getResources().getString(R.string.text_signup_mismatch_password));
//            mEditTextPassword.requestFocus();
//            return false;
//        }
        return true;
    }

    public void SetSpanToTextView(TextView mTextView) {
        StringBuilder mStringBuilder = new StringBuilder(getResources().getString(R.string.text_login_signin_here));

        SpannableString ss = new SpannableString(getResources().getString(R.string.text_login_signin_here));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finishAffinity();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 24, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(1.2f), 24, 32, 1);
        ss.setSpan(new UnderlineSpan(), 24, 32, 1);

        mTextView.setText(ss);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setLinkTextColor(ContextCompat.getColor(this, R.color.color_signin_button));
        mTextView.setHighlightColor(Color.TRANSPARENT);

    }

    public void SignupUser() {

        mUtility.ShowProgress();
        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("name", mEditTextUsrname.getText().toString().trim());
        mHashMap.put("email", mEditTextEmail.getText().toString().trim());
        mHashMap.put("password", mEditTextPassword.getText().toString().trim());
        mHashMap.put("dob", mEditTextBirthDate.getText().toString().trim());
        if (mTextViewGender.getText().toString().trim().equalsIgnoreCase("male")) {
            mHashMap.put("gender", "1");
        } else {
            mHashMap.put("gender", "2");
        }
        if(!mEditTextHeight.getText().toString().trim().isEmpty())
        {
            if (mTextViewHeightUnit.getText().toString().equals("cms")) {
                mHashMap.put("height", String.valueOf(mHeightCms));
            } else {
                //double newWeight=feetToCentimeter(mEditTextWeight.getText().toString().trim());
                if (mHeightFeet != 0 && mHeightInch != 0) {
                    double newWeight = feetToCentimeter(mHeightFeet + "' " + mHeightInch + "\"");
                    mHeightCms = (int) newWeight; // height in centimeters
                }
                mHashMap.put("height", String.valueOf(mHeightCms));
            }
        }else{
            mHashMap.put("height", "");
        }
        if(!mEditTextWeight.getText().toString().trim().isEmpty())
        {
            if (mTextWeightUnit.getText().toString().equals("kg")) {
                if (!mTextWeightUnit.getText().toString().equals("0")) {
                    mHashMap.put("weight", mEditTextWeight.getText().toString().trim());
                } else {
                    mHashMap.put("weight", String.valueOf(mWeightKg));
                }
            } else {
                mHashMap.put("weight", String.valueOf(mWeightKg));
            }
        }else{
            mHashMap.put("weight", "");
        }
        mHashMap.put("town", mEditTextTown.getText().toString().trim());
        mHashMap.put("medication", mEditTextMedication.getText().toString().trim());
        mHashMap.put("heart_condition", mEditTextHeartCondition.getText().toString().trim());

        mHashMap.put("lat", strLatitude);
        mHashMap.put("lon", strLongitude);
        mHashMap.put("device_type", "android");
        mHashMap.put("device_token", "asdfghjklpoiuytrewqzxvcbnm");
        mHashMap.put("phone_no", "");

        Call<VOSignUp> socialSignup = mApiService.normalUserSignup(mHashMap);
        socialSignup.enqueue(new Callback<VOSignUp>() {
            @Override
            public void onResponse(Call<VOSignUp> call, Response<VOSignUp> response) {
                mUtility.HideProgress();

                VOSignUp mVoResponseSignup = response.body();
                if (mVoResponseSignup != null) {

                    if (mVoResponseSignup.getSuccess() != null && mVoResponseSignup.getSuccess().equalsIgnoreCase("1")) {
                        mUtility.writeSharedPreferencesString(Constant.PREFS_USER_NAME, mVoResponseSignup.getData().getUser_name());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_EMAIL, mVoResponseSignup.getData().getEmail());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_USER_ID, mVoResponseSignup.getData().getUser_id());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_MOBILE_NUMBER, mVoResponseSignup.getData().getContact_no());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_USER_ROLE_ID, mVoResponseSignup.getData().getFk_role_id());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_CREATED_DATE, mVoResponseSignup.getData().getCreated_date());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_DOB, mVoResponseSignup.getData().getDob());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_IMAGE, mVoResponseSignup.getData().getPhoto());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_WEIGHT, mVoResponseSignup.getData().getWeight());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_HEIGHT, mVoResponseSignup.getData().getHeight());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_ACCESS_TOKEN, mVoResponseSignup.getData().getAccess_token());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_TOWN, mVoResponseSignup.getData().getTown());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_LAT, mVoResponseSignup.getData().getLat());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_LON, mVoResponseSignup.getData().getLon());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_GENDER, mVoResponseSignup.getData().getGender());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_CREATED_BY, mVoResponseSignup.getData().getCreated_by());
                        mUtility.writeSharedPreferencesString(Constant.PREFS_IS_ACTIVE, mVoResponseSignup.getData().getIs_active());
                        Intent mIntent = new Intent(SignupActivity.this, MainActivity.class);
                        finishAffinity();
                        startActivity(mIntent);
                    } else {
                        if (mVoResponseSignup.getMessage() != null) {
                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                                    mVoResponseSignup.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VOSignUp> call, Throwable t) {
                t.printStackTrace();
                mUtility.HideProgress();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        } startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            strLatitude = String.valueOf(mLocation.getLatitude());
            strLongitude = String.valueOf(mLocation.getLongitude());
        } else {
            // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
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
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
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


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void showGenderOptionsDialog() {

        List<String> mGender = new ArrayList<String>();
        mGender.add(Constant.GENDER_MALE);
        mGender.add(Constant.GENDER_FEMALE);
        String choice;
        // Create sequence of items
        final CharSequence[] Animals = mGender.toArray(new String[mGender.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignupActivity.this);
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

    //Unit Change Dialog(Metric/Imperial)
    public void showOptionsDialog() {

        List<String> mUnitData = new ArrayList<String>();
        mUnitData.add(Constant.UNIT_METRIC);
        mUnitData.add(Constant.UNIT_IMPERIAL);
        String choice;
        // Create sequence of items
        final CharSequence[] mUnitCharcter = mUnitData.toArray(new String[mUnitData.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignupActivity.this);
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
                                    weight = Integer.parseInt(mStringHeight);
                                }

                                double conversionkg = Math.round((weight * 2.2) * 10) / 10.0; //kg convereted to pounds
                                mWeightLbs = (float) conversionkg;
                                getHeightAndWeightFromNMetric(String.valueOf(mHeightFeet) + "." + String.valueOf(mHeightInch), String.valueOf(mWeightLbs));
                            }
                        } else {
                            getHeightAndWeightFromNMetric(String.valueOf(mHeightFeet) + "." + String.valueOf(mHeightInch), String.valueOf(mWeightLbs));
                        }
                    }
                }
            }
        });
        // Create alert dialog object via builder
        dialogBuilder.create();
        // Show the dialog
        dialogBuilder.show();
    }

    //Shows Dialog to choose Centimeters for Height.
    public void showCentimeterOptionsDialog() {

        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(SignupActivity.this)
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
        final Dialog openDialogSuccess = new Dialog(SignupActivity.this);
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, R.style.DialogTheme,
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

}
