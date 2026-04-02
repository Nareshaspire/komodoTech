package com.aiosleeve.aiosleeve;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiosleeve.aiosleeve.VO.VOLogin;
import com.aiosleeve.aiosleeve.VO.VOLoginData;
import com.aiosleeve.aiosleeve.VO.VOResponseForgotPassword;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.API;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * Created by oneclick-android on 21/12/17.
 */

public class LoginActivity extends AppCompatActivity {

    public Utility mUtility;
    public Retrofit mRetrofit;
    public API mApiService;

    TextView mTextViewForgotPassword;
    TextView mTextViewSignupHere;

    EditText mEditTextEmail;
    EditText mEditTextPassword;
    EditText mEditTextForgotPasswordEmail;

    Button mButtonLogin;
    Button mButtonOk;
    Button mButtonCancel;

    AlertDialog alertDialogForgotPassword;
    EditText forgotPasswordEt;

    //Toolbar Layouts
    public ImageView mImageViewBack;

    private static final int MULTIPLE_PERMISSIONS_RESPONSE_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.INTERNET};

    Dialog openDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUtility = new Utility(LoginActivity.this);

        verifyPermissions(LoginActivity.this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClientWithLogger())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(API.class);

        mTextViewForgotPassword = (TextView) findViewById(R.id.activity_login_textview_forgot_password);
        mTextViewSignupHere = (TextView) findViewById(R.id.activity_login_textview_sign_up);

        mEditTextEmail = (EditText) findViewById(R.id.activity_login_edittext_email);
        mEditTextPassword = (EditText) findViewById(R.id.activity_login_edittext_password);

        mButtonLogin = (Button) findViewById(R.id.activity_login_button_signin);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (mUtility.haveInternet()) {
                        doLogin();
                    } else {
                        mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
                                getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
        });

        mTextViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword();
            }
        });
        SetSpanToTextView(mTextViewSignupHere);

    }

    public void ForgotPassword() {


        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View layoutView=getLayoutInflater().inflate(R.layout.dialog_forgot_password,null);
        TextView cancelButton=layoutView.findViewById(R.id.dialog_forgot_password_cancel_tv);
        TextView okButton=layoutView.findViewById(R.id.dialog_forgot_password_ok_tv);
        forgotPasswordEt=layoutView.findViewById(R.id.dialog_forgot_password_email_et);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogForgotPassword.dismiss();
                //OR
                String editTextValue = forgotPasswordEt.getText().toString().trim();

//                Log.d("Ankit","ForgotPass Value="+editTextValue);
                if (isValidForgotPassword(editTextValue)) {
                    if (mUtility.haveInternet()) {
                        ForgotPassword(editTextValue);
                    } else {
                        mUtility.errorDialogWithTitle(getResources().getString(R.string.alert_title),
                                getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogForgotPassword.dismiss();
            }
        });

        alert.setView(layoutView);
        alertDialogForgotPassword=alert.create();
//        alertDialogForgotPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialogForgotPassword.show();
//
//        final EditText edittext = new EditText(LoginActivity.this);
//        edittext.setHint("Enter Email");
//        alert.setMessage(getString(R.string.text_forget_password_popup));
//        alert.setTitle(getString(R.string.app_name));
//        alert.setView(edittext);
//
//        alert.setPositiveButton(getString(R.string.text_ok), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                dialog.dismiss();
//                //OR
//                String editTextValue = edittext.getText().toString().trim();
//
////                Log.d("Ankit","ForgotPass Value="+editTextValue);
//                if (isValidForgotPassword(editTextValue)) {
//                    if (mUtility.haveInternet()) {
//                        ForgotPassword(editTextValue);
//                    } else {
//                        mUtility.errorDialogWithTitle(getResources().getString(R.string.alert_title),
//                                getResources().getString(R.string.no_internet_msg));
//                    }
//                }
//            }
//        });
//
//        alert.setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                dialog.dismiss();
//                // what ever you want to do with No option.
//            }
//        });
//
//        alert.show();
    }

    public void ForgotPassword(String mStringEmail) {
        mUtility.ShowProgress();

        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("email", mStringEmail);

        Call<VOResponseForgotPassword> forgotPassword = mApiService.forgotPassword(mHashMap);
        forgotPassword.enqueue(new Callback<VOResponseForgotPassword>() {
            @Override
            public void onResponse(Call<VOResponseForgotPassword> call, Response<VOResponseForgotPassword> response) {
                mUtility.HideProgress();

                VOResponseForgotPassword mVoResponseForgotPassword = response.body();
                if (mVoResponseForgotPassword != null) {
                    if (mVoResponseForgotPassword.getMessage() != null && !mVoResponseForgotPassword.getMessage().equalsIgnoreCase("")) {
                        mUtility.errorDialogWithTitle(getResources().getString(R.string.alert_title),
                                mVoResponseForgotPassword.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<VOResponseForgotPassword> call, Throwable t) {
                mUtility.HideProgress();
                t.printStackTrace();
            }
        });
    }

    public boolean isValidForgotPassword(String mStringEmail) {
        if (mStringEmail.trim().equalsIgnoreCase("")) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.alert_title),
                    getResources().getString(R.string.text_signup_empty_email));
            return false;
        }
        if (!mUtility.isValidEmail(mStringEmail.trim())) {
            mUtility.errorDialogWithTitle(getResources().getString(R.string.alert_title),
                    getResources().getString(R.string.text_signup_invalid_email));
            return false;
        }

        return true;
    }

    private void doLogin() {
        mUtility.ShowProgress();

        Map<String, String> mHashMap = new HashMap<>();
        mHashMap.put("email", mEditTextEmail.getText().toString().trim());
        mHashMap.put("password", mEditTextPassword.getText().toString().trim());
        mHashMap.put("device_type", "android");
        mHashMap.put("device_token", "54321");

        Call<VOLogin> loginUser = mApiService.normalUserLogin(mHashMap);
        loginUser.enqueue(new Callback<VOLogin>() {
            @Override
            public void onResponse(Call<VOLogin> call, Response<VOLogin> response) {
                mUtility.HideProgress();
                VOLogin mVoResponseLogin = response.body();

                if (mVoResponseLogin != null) {
                    if (mVoResponseLogin.getSuccess() != null && mVoResponseLogin.getSuccess().equalsIgnoreCase("1")) {
                        if (mVoResponseLogin.getData() != null) {
                            mUtility.writeSharedPreferencesString(Constant.PREFS_USER_ID, mVoResponseLogin.getData().getUser_id());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_USER_NAME, mVoResponseLogin.getData().getName());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_EMAIL, mVoResponseLogin.getData().getEmail());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_MOBILE_NUMBER, mVoResponseLogin.getData().getPhone_no());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_USER_ROLE_ID, mVoResponseLogin.getData().getRole_id());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_DOB, mVoResponseLogin.getData().getDob());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_CREATED_DATE, mVoResponseLogin.getData().getCreated_date());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_GENDER, mVoResponseLogin.getData().getGender());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_IS_ACTIVE, mVoResponseLogin.getData().getIs_active());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_HEIGHT, mVoResponseLogin.getData().getHeight());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_WEIGHT, mVoResponseLogin.getData().getWeight());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_TOWN, mVoResponseLogin.getData().getTown());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_LAT, mVoResponseLogin.getData().getLat());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_LON, mVoResponseLogin.getData().getLon());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_IMAGE, mVoResponseLogin.getData().getPhoto());
                            mUtility.writeSharedPreferencesString(Constant.PREFS_ACCESS_TOKEN, mVoResponseLogin.getData().getAccess_token());
                            //////////////////
                            //May -2021
                            AlarmHelper.setMorningAndEveningAlarm(getApplicationContext());
                            mUtility.writeSharedPreferencesBool(Constant.PREFS_ALARM_STATUS, true);
                            //////////////////

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        }
                    } else {
                        if (mVoResponseLogin.getMessage() != null && !mVoResponseLogin.getMessage().equalsIgnoreCase("")) {
                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name), mVoResponseLogin.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VOLogin> call, Throwable t) {
                mUtility.HideProgress();
                t.printStackTrace();
            }
        });
    }

    public boolean isValid() {
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

        return true;
    }

    public void SetSpanToTextView(TextView mTextView) {
        StringBuilder mStringBuilder = new StringBuilder(getResources().getString(R.string.text_login_signup_here));

        SpannableString ss = new SpannableString(getResources().getString(R.string.txt_no_account));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(mIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 21, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(1.2f), 21, 25, 1);
        ss.setSpan(new UnderlineSpan(), 21, 25, 1);

        mTextView.setText(ss);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setLinkTextColor(ContextCompat.getColor(this, R.color.login_signin_button_color));
        mTextView.setHighlightColor(Color.TRANSPARENT);
    }

    public static boolean verifyPermissions(Activity activity) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS_STORAGE) {
            result = ActivityCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    MULTIPLE_PERMISSIONS_RESPONSE_CODE);
            return false;
        }
        return true;
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
}
