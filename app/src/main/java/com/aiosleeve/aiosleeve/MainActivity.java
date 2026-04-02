package com.aiosleeve.aiosleeve;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.aiosleeve.aiosleeve.VO.VoBleDevice;
import com.aiosleeve.aiosleeve.VO.VoGetMedication;
import com.aiosleeve.aiosleeve.VO.VoTakenMedine;
import com.aiosleeve.aiosleeve.database.DBHelper;
import com.aiosleeve.aiosleeve.database.DataHolder;
import com.aiosleeve.aiosleeve.databinding.ActivityMainBinding;
import com.aiosleeve.aiosleeve.fragments.FragmentActivityMode;
import com.aiosleeve.aiosleeve.fragments.FragmentHealthMode;
import com.aiosleeve.aiosleeve.fragments.FragmentHome;
import com.aiosleeve.aiosleeve.fragments.FragmentManageDevice;
import com.aiosleeve.aiosleeve.fragments.FragmentQuickScan;
import com.aiosleeve.aiosleeve.fragments.FragmentSettings;
import com.aiosleeve.aiosleeve.fragments.FragmentSleepMode;
import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;
import com.aiosleeve.aiosleeve.interfaces.API;
import com.aiosleeve.aiosleeve.interfaces.DevicesStatus;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.appbar.AppBarLayout;
import com.roughike.bottombar.OnTabSelectListener;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.EmojiCategory;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.toshiba.semicon.hcsdp.brighton.controllib.BleCallback;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.BleConstants;
import jp.co.toshiba.semicon.hcsdp.brighton.controllib.LibraryController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements BleCallback {

    public static final String CUSTOM_INTENT = "com.aiosleeve.aiosleeve.intent.action.connection_state";

    public static final String SHOW_NO_DEVICE_CONNECTED_DIALOG = "show_no_device_connected_dialog";

    static ActivityMainBinding activityMainBinding;


    Toolbar mToolbar;
    AppBarLayout mAppBarLayout;
    FragmentTransaction fragmentTransaction;
//    DrawerLayout drawerLayout;

    public FragmentHome fragmentHomeInstance;
    public ViewPager viewPager;

//    public TextView mTextViewBPM;
//    public TextView mTextViewECG_HRV;
//    public TextView mTextViewSleep;
//    public TextView mTextViewSettings;
//    public TextView mTextViewMedicationTaken;
//    public TextView mTextViewLongTermECG;
//
//    public Button mButtonConnect;
//    public Button mButtonDisconnect;
//
//    public CardView mCardViewBPM;
//    public CardView mCardViewECG_HRV;
//    public CardView mCardViewSleep;
//    public CardView mCardViewSettings;
//    public CardView mCardViewMedicationTaken;
//    public CardView mCardViewLongTermECG;
//
//    public ImageView mImageViewBPM;
//    public ImageView mImageViewECG;
//    public ImageView mImageViewSLEEP;
//    public ImageView mImageViewSETTIGS;
//    public ImageView mImageViewMedicationTaken;
//    public ImageView mImageViewLongTermECG;

    //Global Objects
    public Utility mUtility;
    public Retrofit mRetrofit;
    public API mApiService;

    //Toolbar Layouts
    public TextView mTextViewHeader;
    //    public ImageView mImageViewDrawer;
    public ImageView mImageViewBack;
    public ImageView mImageViewAddStory;
    public ImageView mImageViewProfileIcon;
    public GradientDrawable gradientDrawable;
    public RelativeLayout mRelativeLayoutMain;

    //Navigation Layouts
//    NavigationView navigationView;

    public static LibraryController brighton;

    //    public static DevicesStatus mDevicesStatus;
    public static HashMap<String, DevicesStatus> mDevicesStatus = new HashMap<>();

    //    public ArrayList<VoBleDevice> mArrayListBledevices = new ArrayList<>();
    public static ArrayList<VoBleDevice> mArrayListBledevices = new ArrayList<>();

    /* mutex object */
    /**
     * Object for receive data update exclusive.
     */
    public final Object RECEIVE_DATA_LOCK = new Object();
    /* Object for ble communication exclusive. */
    private static final Object BLE_COM_LOK = new Object();

    /**
     * Connected device instance.
     */
    public static BluetoothDevice connectedDevice = null;

    /**
     * ui handler
     */
    private Handler handler;

    /**
     * Heart rate control characteristicUUID
     */
    public static final String HR_MEASUREMENT_CONTROL_CHARACTERISTIC_UUID = "1d4de0a0-9f84-11e4-a042-0002a5d5c51b";
    /**
     * characteristic write result for {@link #enableControl(String)}.
     */
    public static boolean write_result = false;
    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBtAdapter = null;

    public static final int REQUEST_CHECK_SETTINGS = 205;
    private boolean isRegisterd = false;
    public DBHelper dbHelper;

    DataHolder mDataHolder_Medication;

    private ArrayList<VoTakenMedine> mArrayListMyGroup = new ArrayList<>();

    private Dialog openDialog;

    String mStringMedicationStartDateTime = "";
    String mStringMedicationStartDate = "";

    RecyclerView mRecyclerViewGroupChannel;

    private Button mButtonDismiss;
    private Button mButtonSubmit;

    private TextView mTextViewNoMedicineLabel;

    private MyMedicineAdapter mMymedicineAdpter;

    public enum TabType {
        ACTIVITY_MODE(3, R.string.text_activity_mode_lock_alert),
        HEALTH_MODE(1, R.string.text_health_mode_lock_alert),
        QUICK_SCAN_MODE(2, R.string.text_quick_scan_lock_alert),
        SLEEP_MODE(4, R.string.text_sleep_mode_lock_alert),
        NONE(-1, -1);

        public final int tabIndex;
        public final int tabLockAlert;

        // getter method
//        public int getTabIndex() {
//            return this.tabIndex;
//        }

        // enum constructor - cannot be public or protected
        private TabType(int tabIndex, int tabLockAlert) {
            this.tabIndex = tabIndex;
            this.tabLockAlert = tabLockAlert;
        }
    }

    private static TabType tabLock = TabType.NONE;
    private int colorGray = -1;
    private int colorWhite = -1;
    public static boolean isDisconnectedFromFragmentHome = false;


    //2021-APRIL
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());//For Emojis

        colorGray = ContextCompat.getColor(MainActivity.this, R.color.toolbar_bg_color);
        colorWhite = ContextCompat.getColor(MainActivity.this, R.color.color_white);


        dbHelper = new DBHelper(MainActivity.this);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mUtility = new Utility(MainActivity.this);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_main_appBarLayout);

        handler = new Handler();

        fragmentHomeInstance = new FragmentHome();
        viewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        setStatePageAdapter();

        initToolbar();
//        initNavigationDrawer();
//        initBottomNavigationDrawer();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mUtility.getSimpleClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = mRetrofit.create(API.class);

//        mImageViewDrawer.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUtility.hideKeyboard();
//                drawerLayout.openDrawer(Gravity.LEFT);
//            }
//        });

        brighton = new LibraryController();
        brighton.initLibrary(this.getApplication(), this);

        if (dbHelper.checkDataBase() == false) {
            try {
                dbHelper.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TO COPY DATABASE in EXTERNAL SD CARD (DEBUG PURPOSE ONLY) REMOVE THIS METHOD WHEN RELEASE APP
//        if (dbHelper != null) {
//            try {
//                dbHelper.copyDatabaseToExternalStoage(MainActivity.this, DBHelper.DB_NAME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


        //Ankit

//        mButtonConnect.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectedDevice == null) {
//                    mTextViewHeader.setText("MANAGE DEVICE");
//                    removeAllFragmentFromBack();
//                    FragmentManageDevice mFragmentDrawerManageDevice = new FragmentManageDevice();
//                    replacesFragment(mFragmentDrawerManageDevice, true, null, 1);
//                }
//            }
//        });
//
//        mButtonDisconnect.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (connectedDevice != null) {
//                    disConnectDevice();
//                }
//
//                mButtonConnect.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_background_enabled));
//                mButtonDisconnect.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_background_disabled));
//                mButtonConnect.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//                mButtonDisconnect.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//
//                mCardViewBPM.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//                mCardViewECG_HRV.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//                mCardViewSleep.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//                mCardViewSettings.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//                mCardViewLongTermECG.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//
//                mTextViewBPM.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mTextViewECG_HRV.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mTextViewSleep.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mTextViewSettings.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mTextViewLongTermECG.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//
//                mImageViewBPM.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mImageViewECG.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mImageViewSLEEP.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mImageViewSETTIGS.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//                mImageViewLongTermECG.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//
//                mArrayListBledevices.clear();
//            }
//        });
//
//        mCardViewBPM.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectedDevice != null) {
//                    Intent mIntent = new Intent(MainActivity.this, FragmentBPM.class);
//                    startActivity(mIntent);
//                }
//            }
//        });
//
//        mCardViewECG_HRV.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectedDevice != null) {
//                    Intent mIntent = new Intent(MainActivity.this, FragmentECG.class);
//                    startActivity(mIntent);
//                }
//            }
//        });
//
//        mCardViewSleep.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectedDevice != null) {
//                    Intent mIntent = new Intent(MainActivity.this, FragmentSleep.class);
//                    startActivity(mIntent);
//                }
//            }
//        });
//
//        mCardViewSettings.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectedDevice != null) {
//                    Intent mIntent = new Intent(MainActivity.this, FragmentSettings.class);
//                    startActivity(mIntent);
//                }
//            }
//        });
//
//        mCardViewMedicationTaken.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showMedicationDialog();
//            }
//        });
//
//        mCardViewMedicationTaken.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_enabled_color));
//        mTextViewMedicationTaken.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//        mImageViewMedicationTaken.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.color_white));

        getMedicationAPI();

        if (getIntent() != null) {
            if (getIntent().hasExtra(SHOW_NO_DEVICE_CONNECTED_DIALOG)) {
                mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
            }
        }


        //Setting up the alarm
        if (mUtility.getAppPrefBool(Constant.PREFS_ALARM_STATUS) && !AlarmHelper.areAlarmsScheduled(getApplicationContext())) {
            AlarmHelper.setMorningAndEveningAlarm(getApplicationContext());
        }


    }


    public void getMedicationAPI() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", mUtility.getAppPrefString(Constant.PREFS_USER_ID));
        hashMap.put("access_token", mUtility.getAppPrefString(Constant.PREFS_ACCESS_TOKEN));

        Call<VoGetMedication> mCallGetMedication = mApiService.getMedactions(hashMap);
        mCallGetMedication.enqueue(new Callback<VoGetMedication>() {
            @Override
            public void onResponse(Call<VoGetMedication> call, Response<VoGetMedication> response) {
                VoGetMedication voGetMedication = response.body();

                if (voGetMedication != null) {
                    if (voGetMedication.getSuccess().equalsIgnoreCase("1")) {
                        if (voGetMedication.getData() != null && voGetMedication.getData().size() > 0) {
                            if (voGetMedication.getData().get(0).getMedactions() != null
                                    && !voGetMedication.getData().get(0).getMedactions().equalsIgnoreCase("")) {
                                String[] mStringMedication = voGetMedication.getData().get(0).getMedactions().split(",");
                                dbHelper.deleteTableData(DBHelper.mTableMedication);
                                for (int i = 0; i < mStringMedication.length; i++) {
                                    if (!mStringMedication[i].equalsIgnoreCase(""))
                                        insertIntoMeditationTable(mStringMedication[i]);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VoGetMedication> call, Throwable t) {

            }
        });
    }

    public void insertIntoMeditationTable(String medicine) {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mTable_Medication_name, medicine);
            mContentValues.put(DBHelper.mTable_Medication_User_ID, mUtility.getAppPrefString(Constant.PREFS_USER_ID));
            dbHelper.insertRecord(DBHelper.mTableMedication, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMedicationDialog() {

        try {
            mDataHolder_Medication = dbHelper.read("SELECT * from " + DBHelper.mTableMedication + " Where " + DBHelper.mTaken_Medication_Table_User_ID + " ='" + mUtility.getAppPrefString(Constant.PREFS_USER_ID) + "'");


            mArrayListMyGroup.clear();
            if (mDataHolder_Medication != null && mDataHolder_Medication.get_Listholder() != null && mDataHolder_Medication.get_Listholder().size() > 0) {
                VoTakenMedine mvoTakenMedine;

                for (int i = 0; i < mDataHolder_Medication.get_Listholder().size(); i++) {
                    mvoTakenMedine = new VoTakenMedine();
                    mvoTakenMedine.setMedicine_name(mDataHolder_Medication.get_Listholder().get(i).get(DBHelper.mTable_Medication_name));
                    mvoTakenMedine.setmID(mDataHolder_Medication.get_Listholder().get(i).get(DBHelper.mTaken_Medication_Table_ID));
                    mArrayListMyGroup.add(mvoTakenMedine);
                }
            } else {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Please add medication first from setting").setCancelable(false).setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        mTextViewHeader.setText(getResources().getString(R.string.tital_settings));
                        Intent mIntent = new Intent(MainActivity.this, FragmentSettings.class);
                        startActivity(mIntent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                android.app.AlertDialog alert = builder.create();
                alert.show();

                return;
            }

            openDialog = new Dialog(MainActivity.this);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            openDialog.setContentView(R.layout.pop_taken_medication_check_box_layout);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            openDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mRecyclerViewGroupChannel = (RecyclerView) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_recyclerview);
            mButtonSubmit = (Button) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_button_ok);
            mButtonDismiss = (Button) openDialog.findViewById(R.id.pop_taken_medicine_checkbox_layout_button_cancel);

            mMymedicineAdpter = new MyMedicineAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewGroupChannel.setLayoutManager(manager);
            mRecyclerViewGroupChannel.setAdapter(mMymedicineAdpter);
            mMymedicineAdpter.notifyDataSetChanged();

            mStringMedicationStartDateTime = mUtility.getDateTime();
            mStringMedicationStartDate = mUtility.getDate();
            mButtonSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog.dismiss();

                    if (mArrayListMyGroup != null && mArrayListMyGroup.size() > 0) {
                        for (int i = 0; i < mArrayListMyGroup.size(); i++) {
                            if (mArrayListMyGroup.get(i).isChecked()) {
                                insertIntoMeditationTakenTable(mArrayListMyGroup.get(i).getmID(), mArrayListMyGroup.get(i).getMedicine_name(), mUtility.getAppPrefString(Constant.PREFS_USER_ID), mStringMedicationStartDateTime, mStringMedicationStartDate);
                            } else {
                                Log.e("ERROR", " :( not Checked");
                            }
                        }
                    }
                }
            });

            mButtonDismiss.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog.dismiss();
                }
            });

            openDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyMedicineAdapter extends RecyclerView.Adapter<MyMedicineAdapter.ViewHolder> {

        @Override
        public MyMedicineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_my_checkbox_item, parent, false);
            return new MyMedicineAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyMedicineAdapter.ViewHolder holder, final int position) {

            if (mArrayListMyGroup != null && mArrayListMyGroup.size() > 0) {

                holder.mCheckBox.setText(mArrayListMyGroup.get(position).getMedicine_name());
                holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            if (isChecked) {
                                mArrayListMyGroup.get(position).setChecked(true);
                            } else {
                                mArrayListMyGroup.get(position).setChecked(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mArrayListMyGroup.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CheckBox mCheckBox;

            public ViewHolder(View itemView) {
                super(itemView);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.raw_my_checkbox_item_checkbox);
            }
        }
    }

    public void insertIntoMeditationTakenTable(String mId, String medication_name, String UserId, String DateNTime, String Date) {
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(DBHelper.mTaken_Medication_Table_Medication_Name, medication_name);
            mContentValues.put(DBHelper.mTaken_Medication_Table_User_ID, UserId);
            mContentValues.put(DBHelper.mTaken_Medication_Date_N_Time, DateNTime);
            mContentValues.put(DBHelper.mTaken_Medication_Date, Date);
            mContentValues.put(DBHelper.mTaken_Medication_Is_Sync, "no");
            dbHelper.insertRecord(DBHelper.mTableTakenMedication, mContentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeTimeOnDevice(Calendar mCal) {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null) {
                brighton.setSensorTime(mCal);
            }
        }
    }

//    public void updateView() {
//
//
//        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_main_content_container);
//
//        if (mFragment instanceof FragmentHome) {
//            ((FragmentHome) mFragment).updateView(connectedDevice);
//        }

//
//        if (connectedDevice != null) {
//
//            mButtonDisconnect.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_background_enabled));
//            mButtonDisconnect.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mButtonConnect.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_background_disabled));
//            mButtonConnect.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//
//            mCardViewBPM.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_enabled_color));
//            mCardViewECG_HRV.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_enabled_color));
//            mCardViewSleep.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_enabled_color));
//            mCardViewSettings.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_enabled_color));
//            mCardViewLongTermECG.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_enabled_color));
//
//            mTextViewBPM.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mTextViewECG_HRV.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mTextViewSleep.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mTextViewSettings.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mTextViewLongTermECG.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//
//            mImageViewBPM.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mImageViewECG.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mImageViewSLEEP.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mImageViewSETTIGS.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mImageViewLongTermECG.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//
//        } else {
//
//            mButtonConnect.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_background_enabled));
//            mButtonDisconnect.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_background_disabled));
//            mButtonConnect.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_white));
//            mButtonDisconnect.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//
//            mCardViewBPM.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//            mCardViewECG_HRV.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//            mCardViewSleep.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//            mCardViewSettings.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//            mCardViewLongTermECG.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_disabled_color));
//
//            mTextViewBPM.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mTextViewECG_HRV.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mTextViewSleep.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mTextViewSettings.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mTextViewLongTermECG.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//
//            mImageViewBPM.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mImageViewECG.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mImageViewSLEEP.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mImageViewSETTIGS.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//            mImageViewLongTermECG.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.button_text_eanbled));
//        }
//    }

    public static void startScan() {
        if (brighton != null) {
            brighton.scanSensorDevice(null, null);
        }
    }

    public static void setMetsValue(final int value, final boolean mode) {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null && brighton != null) {
                brighton.setMETs(value, mode);
            }
        }
    }

    public static void setSleepMode(final boolean mode) {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null && brighton != null) {
                brighton.setSensorSleepMode(mode);
            }
        }
    }

    public static void getSleepMode() {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null && brighton != null) {
                try {
                    brighton.getSensorSleepMode();
                } catch (Exception e) {

                }
            }
        }
    }

    public static void getSleepData(Calendar starttime, Calendar endtime) {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null && brighton != null) {
                brighton.getSleepData(starttime, endtime);
            }
        }
    }

    public static void clearSleepData() {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null && brighton != null) {
                brighton.clearAllSleepData();
            }
        }
    }

    public static void getSleepSensorTime() {
        synchronized (BLE_COM_LOK) {
            if (connectedDevice != null && brighton != null) {
                brighton.getSensorTime();
            }
        }
    }

    public static void enableMonitorize(final boolean enabled, final ArrayList<BleConstants.NotificationDataType> dataTypeList) {
        synchronized (BLE_COM_LOK) {
            try {
                if (connectedDevice != null && brighton != null) {
                    if (enabled) {
                        brighton.startReceiveData(dataTypeList); /* start receive data */
                    } else {
                        brighton.stopReceiveData(dataTypeList);  /* stop receive data */
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setToolbarColor(boolean isTransparent) {
        if (isTransparent) {
//            mTextViewHeader.setTextColor(colorWhite);
//            mRelativeLayoutMain.setBackgroundColor(Color.TRANSPARENT);
            mAppBarLayout.setVisibility(View.GONE);
            mUtility.changeStatusbarColor(R.color.color_violet);
        } else {
            mAppBarLayout.setVisibility(View.VISIBLE);
            mTextViewHeader.setTextColor(colorGray);
            mRelativeLayoutMain.setBackgroundColor(colorWhite);
            mUtility.changeStatusbarColor(R.color.color_white);

        }
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.activity_main_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        View custom_actionbar = getLayoutInflater().inflate(R.layout.custom_actionbar_admin, null);

        mRelativeLayoutMain = (RelativeLayout) custom_actionbar.findViewById(R.id.custom_actionbar_main_layout);
        mTextViewHeader = (TextView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_textview_header);
//        mImageViewDrawer = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_drawer);
        mImageViewBack = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_back);
        mImageViewBack.setVisibility(View.GONE);
        mImageViewAddStory = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_add_story);
        mImageViewProfileIcon = (ImageView) custom_actionbar.findViewById(R.id.custom_actionbar_admin_imageview_right_drawer_icon);


        mTextViewHeader.setText(getResources().getString(R.string.text_home));
        mTextViewHeader.setTextColor(colorGray);
        mTextViewHeader.setTypeface(mTextViewHeader.getTypeface(), Typeface.BOLD);
        mRelativeLayoutMain.setBackgroundColor(colorWhite);

        mImageViewProfileIcon.setVisibility(View.VISIBLE);
        mImageViewProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (connectedDevice != null) {
                Intent mIntent = new Intent(MainActivity.this, ActivityProfile.class);
                startActivity(mIntent);
//                } else {
//
//
//                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
//
//
//                }
            }
        });

        mImageViewAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLock == TabType.NONE) {//If the process is in progress then history cannot be opened.
                    if (viewPager.getCurrentItem() == TabType.ACTIVITY_MODE.tabIndex) {
                        Intent mIntent = new Intent(MainActivity.this, ActivityActivityHistory.class);
                        startActivity(mIntent);
                    } else if (viewPager.getCurrentItem() == TabType.HEALTH_MODE.tabIndex) {
                        Intent mIntent = new Intent(MainActivity.this, ActivityHealthModeHistory.class);
                        startActivity(mIntent);
                    }
                }
            }
        });

//        mImageViewBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        mUtility.changeStatusbarColor(R.color.color_white);

        mToolbar.addView(custom_actionbar);

        activityMainBinding.bottomNavigationView.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                Log.d("Ankit", "onTabSelected() called.");
                switch (tabId) {

                    case R.id.admin_menu_nav_home:
                        Log.d("Ankit", "selected tab= 0");
//                        moveToFirstTab();
                        if (isTheTabNotLocked()) {
                            if (viewPager.getAdapter() != null) {
                                ((FragmentHome) ((MyViewPageStateAdapter) viewPager.getAdapter()).getItem(0)).getSetViewData();
                            }
                            setToolbarColor(false);

                            mTextViewHeader.setText(getResources().getString(R.string.text_home));
                            viewPager.setCurrentItem(0);
                            mImageViewProfileIcon.setVisibility(View.VISIBLE);
                            mImageViewAddStory.setVisibility(View.INVISIBLE);
                            Log.e("xxxxxxxxxxx", "home");
                        } else {
                            if (isThisTabIsNotTheLockedTab(TabType.NONE)) {
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        moveBottomTabToLockedTab();

                                        showAlertDialogOfLockedScreen();

                                    }
                                }, 200);
                            }
                        }
                        break;
                    case R.id.admin_menu_nav_health_mode:

                        Log.d("Ankit", "selected tab= 1");
                        if (MainActivity.connectedDevice != null) {
                            if (isTheTabNotLocked()) {
                                ((MyViewPageStateAdapter) viewPager.getAdapter()).resetFragment(TabType.HEALTH_MODE);
                                setToolbarColor(false);
                                mTextViewHeader.setText(getResources().getString(R.string.label_health_mode_all_caps));
                                viewPager.setCurrentItem(1);
                                mImageViewProfileIcon.setVisibility(View.GONE);
                                mImageViewAddStory.setVisibility(View.VISIBLE);
                            } else {
                                if (isThisTabIsNotTheLockedTab(TabType.HEALTH_MODE)) {
                                    (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveBottomTabToLockedTab();
                                            showAlertDialogOfLockedScreen();
                                        }
                                    }, 200);
                                }
                            }
                        } else {


                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    moveToFirstTab();
                                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                                }
                            }, 200);


                        }
                        break;
                    case R.id.admin_menu_nav_quick_scan:
                        Log.d("Ankit", "selected tab= 2");
                        if (MainActivity.connectedDevice != null) {
                            if (isTheTabNotLocked()) {

                                ((MyViewPageStateAdapter) viewPager.getAdapter()).resetFragment(TabType.QUICK_SCAN_MODE);
                                setToolbarColor(false);
                                mTextViewHeader.setText(getResources().getString(R.string.label_quick_scan_all_caps));
                                viewPager.setCurrentItem(2);
                                mImageViewProfileIcon.setVisibility(View.GONE);
                                mImageViewAddStory.setVisibility(View.INVISIBLE);
                            } else {
                                if (isThisTabIsNotTheLockedTab(TabType.QUICK_SCAN_MODE)) {
                                    (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveBottomTabToLockedTab();
                                            showAlertDialogOfLockedScreen();
                                        }
                                    }, 200);
                                }
                            }
                        } else {
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    moveToFirstTab();
                                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                                }
                            }, 200);
                        }
                        break;
                    case R.id.admin_menu_nav_activity_mode:
                        Log.d("Ankit", "selected tab= 3");
                        if (MainActivity.connectedDevice != null) {
                            if (isTheTabNotLocked()) {
                                ((MyViewPageStateAdapter) viewPager.getAdapter()).resetFragment(TabType.ACTIVITY_MODE);
                                setToolbarColor(false);
                                viewPager.setCurrentItem(3);
                                mTextViewHeader.setText(getResources().getString(R.string.label_activity_mode_all_caps));
                                Log.e("xxxxxxxxxxx", "about us");
                                mImageViewProfileIcon.setVisibility(View.GONE);
                                mImageViewAddStory.setVisibility(View.VISIBLE);
                            } else {

                                Log.d("MainActivity", "isThisTabIsNotTheLockedTab(TabLockType.ACTIVITY_MODE)=" + isThisTabIsNotTheLockedTab(TabType.ACTIVITY_MODE));
                                if (isThisTabIsNotTheLockedTab(TabType.ACTIVITY_MODE)) {
                                    (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveBottomTabToLockedTab();
                                            showAlertDialogOfLockedScreen();
                                        }
                                    }, 200);
                                }
                            }
                        } else {
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    moveToFirstTab();
                                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                                }
                            }, 200);
                        }
                        break;
                    case R.id.admin_menu_nav_sleep_mode:
                        Log.d("Ankit", "selected tab= 4");
                        if (MainActivity.connectedDevice != null) {

                            if (isTheTabNotLocked()) {
                                ((MyViewPageStateAdapter) viewPager.getAdapter()).resetFragment(TabType.SLEEP_MODE);
                                setToolbarColor(true);
                                viewPager.setCurrentItem(4);
                                mTextViewHeader.setText(getResources().getString(R.string.label_sleep_mode_all_caps));
                                mImageViewProfileIcon.setVisibility(View.GONE);
                                mImageViewAddStory.setVisibility(View.INVISIBLE);
                            } else {
                                if (isThisTabIsNotTheLockedTab(TabType.NONE)) {
                                    (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveBottomTabToLockedTab();
                                            showAlertDialogOfLockedScreen();
                                        }
                                    }, 200);
                                }
                            }

                        } else {
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    moveToFirstTab();
                                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                                }
                            }, 200);
                        }
                        break;
                }

            }
        });
    }

    private void setStatePageAdapter() {
        MyViewPageStateAdapter myViewPageStateAdapter = new MyViewPageStateAdapter(getSupportFragmentManager());

        myViewPageStateAdapter.addFragment(new FragmentHome(), "HOME");
        myViewPageStateAdapter.addFragment(new FragmentHealthMode(), "HEALTH MODE");
        myViewPageStateAdapter.addFragment(new FragmentQuickScan(), "QUICK SCAN");
        myViewPageStateAdapter.addFragment(new FragmentActivityMode(), "ACTIVITY MODE");
        myViewPageStateAdapter.addFragment(new FragmentSleepMode(), "SLEEP MODE");
//        myViewPageStateAdapter.addFragment(new FragmentManageDevice(), "Booking");

        viewPager.setAdapter(myViewPageStateAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        ((FragmentHome) ((MyViewPageStateAdapter) viewPager.getAdapter()).getItem(0)).updateView(connectedDevice);
                        activityMainBinding.bottomNavigationView.setDefaultTab(R.id.admin_menu_nav_home);
                        break;
                    case 1:
                        activityMainBinding.bottomNavigationView.setDefaultTab(R.id.admin_menu_nav_health_mode);
                        break;
                    case 2:
                        activityMainBinding.bottomNavigationView.setDefaultTab(R.id.admin_menu_nav_quick_scan);
                        break;
                    case 3:
                        activityMainBinding.bottomNavigationView.setDefaultTab(R.id.admin_menu_nav_activity_mode);
                        break;
                    case 4:
                        activityMainBinding.bottomNavigationView.setDefaultTab(R.id.admin_menu_nav_sleep_mode);
                        break;
                    default:
                        break;

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private boolean isTheTabNotLocked() {
        return tabLock == TabType.NONE;
    }

    public static void setTabLock(TabType tabLockType) {
        tabLock = tabLockType;
//        activityMainBinding.activityMainViewPager.setSwipeToChangePagingEnabled(false);
    }

    public static TabType getTabLock() {
        return tabLock;
//        activityMainBinding.activityMainViewPager.setSwipeToChangePagingEnabled(false);
    }

    public static void resetTabLock() {
        tabLock = TabType.NONE;
//        activityMainBinding.activityMainViewPager.setSwipeToChangePagingEnabled(true);
    }

    public boolean isThisTabIsNotTheLockedTab(TabType currentTab) {
        return !(currentTab == tabLock);
    }

    private void moveBottomTabToLockedTab() {

        if (tabLock != TabType.NONE) {
            activityMainBinding.bottomNavigationView.selectTabAtPosition(tabLock.tabIndex);
        }
//        switch (tabLock) {
//            case ACTIVITY_MODE:
//                activityMainBinding.bottomNavigationView.selectTabAtPosition(3);
//                break;
//            case HEALTH_MODE:
//                activityMainBinding.bottomNavigationView.selectTabAtPosition(1);
//                break;
//            case QUICK_SCAN_MODE:
//                activityMainBinding.bottomNavigationView.selectTabAtPosition(2);
//                break;
//            case NONE:
//                break;
//        }
    }

    private void showAlertDialogOfLockedScreen() {

        mUtility.errorDialog(getResources().getString(tabLock.tabLockAlert));
    }


    public static final class MyViewPageStateAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<Fragment> fragmentList;
        private final ArrayList<String> fragmentTitleList;


        public Fragment getItem(int position) {
            return (Fragment) this.fragmentList.get(position);
        }

        public void resetFragment(TabType tabType) {
            switch (tabType) {
                case ACTIVITY_MODE:
                    ((FragmentActivityMode) fragmentList.get(tabType.tabIndex)).reset();
                    break;
                case HEALTH_MODE:
                    ((FragmentHealthMode) fragmentList.get(tabType.tabIndex)).reset();
                    break;
                case QUICK_SCAN_MODE:
                    ((FragmentQuickScan) fragmentList.get(tabType.tabIndex)).reset();
                    break;
                case SLEEP_MODE:
                    ((FragmentSleepMode) fragmentList.get(tabType.tabIndex)).reset();
                    break;
                case NONE:
                    break;
            }
        }

        public int getCount() {
            return this.fragmentList.size();
        }

        @Nullable
        public CharSequence getPageTitle(int position) {
            return (CharSequence) this.fragmentTitleList.get(position);
        }

        public final void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        public MyViewPageStateAdapter(FragmentManager fm) {
            super(fm);
            fragmentList = new ArrayList<Fragment>();
            fragmentTitleList = new ArrayList<String>();
        }
    }

//    public void initNavigationDrawer() {
//        navigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);
//
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                switch (id) {
//                    case R.id.admin_menu_nav_home:
//                        drawerLayout.closeDrawers();
//                        mTextViewHeader.setText(getResources().getString(R.string.text_home));
//
//                        break;
//                    case R.id.admin_menu_nav_manage_device:
//                        drawerLayout.closeDrawers();
//                        mTextViewHeader.setText(getResources().getString(R.string.tital_manage_device));
//                        removeAllFragmentFromBack();
//                        FragmentManageDevice mFragmentDrawerManageDevice = new FragmentManageDevice();
//                        replacesFragment(mFragmentDrawerManageDevice, true, null, 1);
//                        break;
//
//                    case R.id.admin_menu_nav_manage_settings:
//                        drawerLayout.closeDrawers();
//                        mTextViewHeader.setText(getResources().getString(R.string.tital_settings));
//                        Intent mIntent = new Intent(MainActivity.this, FragmentSettings.class);
//                        startActivity(mIntent);
//
//                        break;
//
//                    case R.id.admin_menu_nav_manage_about_us:
//                        drawerLayout.closeDrawers();
//                        mTextViewHeader.setText(getResources().getString(R.string.tital_about_us));
//                        FragmentAboutUs mFragmentDrawerAboutUs = new FragmentAboutUs();
//                        replacesFragment(mFragmentDrawerAboutUs, true, null, 1);
//                        break;
//                    case R.id.admin_menu_nav_manage_logout:
//                        drawerLayout.closeDrawers();
//                        if (mUtility.haveInternet()) {
//
//                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                            builder.setTitle(getResources().getString(R.string.left_nav_logout))
//                                    .setMessage(getResources().getString(R.string.text_confirmation_logout))
//                                    .setCancelable(false)
//                                    .setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.dismiss();
//                                            doLogOut();
//                                        }
//                                    })
//                                    .setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    });
//
//                            android.app.AlertDialog alert = builder.create();
//                            alert.show();
//
//
//                        } else {
//                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
//                                    getResources().getString(R.string.no_internet_msg));
//                        }
//                        break;
//                    default:
//                        break;
//
//                }
//                return true;
//            }
//        });
//
//        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//
//
//
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
//            @Override
//            public void onDrawerClosed(View v) {
//                super.onDrawerClosed(v);
//            }
//
//            @Override
//            public void onDrawerOpened(View v) {
//                super.onDrawerOpened(v);
//            }
//        };
//
//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        drawerLayout.setDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//    }

//    public void initBottomNavigationDrawer() {
//        mButtonConnect = (Button) findViewById(R.id.fragment_dashboard_button_scan_connect);
//        mButtonDisconnect = (Button) findViewById(R.id.fragment_dashboard_button_scan_disconnect);
//
//        mCardViewBPM = (CardView) findViewById(R.id.fragment_dashboard_cardview_bpm);
//        mCardViewECG_HRV = (CardView) findViewById(R.id.fragment_dashboard_cardview_ecg);
//        mCardViewSleep = (CardView) findViewById(R.id.fragment_dashboard_cardview_sleep);
//        mCardViewSettings = (CardView) findViewById(R.id.fragment_dashboard_cardview_settings);
//        mCardViewLongTermECG = (CardView) findViewById(R.id.fragment_dashboard_cardview_long_term_ecg);
//        mCardViewMedicationTaken = (CardView) findViewById(R.id.fragment_dashboard_cardview_medication_taken);
//
//        mTextViewBPM = (TextView) findViewById(R.id.fragment_dashboard_textview_bpm);
//        mTextViewECG_HRV = (TextView) findViewById(R.id.fragment_dashboard_textview_ecg_hrv);
//        mTextViewSleep = (TextView) findViewById(R.id.fragment_dashboard_textview_sleep);
//        mTextViewSettings = (TextView) findViewById(R.id.fragment_dashboard_textview_settings);
//        mTextViewLongTermECG = (TextView) findViewById(R.id.fragment_dashboard_textview_long_term_ecg);
//        mTextViewMedicationTaken = (TextView) findViewById(R.id.fragment_dashboard_textview_medication_taken);
//
//        mImageViewBPM = (ImageView) findViewById(R.id.img_heart);
//        mImageViewECG = (ImageView) findViewById(R.id.img_ecg);
//        mImageViewSLEEP = (ImageView) findViewById(R.id.img_sleep);
//        mImageViewSETTIGS = (ImageView) findViewById(R.id.img_setting);
//        mImageViewLongTermECG = (ImageView) findViewById(R.id.img_long_term_ecg);
//        mImageViewMedicationTaken = (ImageView) findViewById(R.id.img_medication_taken);
//
//    }

    @Override
    protected void onPause() {

        try {
            Intent intent = new Intent(CUSTOM_INTENT);
            intent.putExtra("state", "paused");
            sendBroadcast(intent);
            if (isRegisterd) {
                isRegisterd = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        Log.d("MainActivity","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
//        Log.d("MainActivity","Alarm is set :"+AlarmHelper.areAlarmsScheduled(getApplicationContext()));
//        Log.d("MainActivity","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");

        isRegisterd = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBtAdapter = mBluetoothManager.getAdapter();

                if (mBtAdapter.isEnabled() == false) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    mBuilder.setTitle(getResources().getString(R.string.lbl_bluetooth_enable));
                    mBuilder.setMessage(getResources().getString(R.string.msg_ble_enable));
                    mBuilder.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mBtAdapter.enable();
                            if (mUtility.isSDK23()) {
                                displayLocationSettingsRequest(MainActivity.this);
                            }
                        }
                    });
                    mBuilder.show();
                }

                ((FragmentHome) ((MyViewPageStateAdapter) viewPager.getAdapter()).getItem(0)).updateView(connectedDevice);


//                Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_main_content_container);
//
//                if (mFragment instanceof FragmentManageDevice) {
//
//                } else if (mFragment instanceof FragmentAboutUs) {
//
//                } else {
//                    mImageViewDrawer.setVisibility(View.VISIBLE);
//                    mImageViewBack.setVisibility(View.GONE);
//                    mTextViewHeader.setText(getResources().getString(R.string.text_home));
//                    mRelativeLayoutMain.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.dashboard_header_color));
//
//                    mUtility.changeStatusbarColor(R.color.dashboard_header_color);
//                }

                ((FragmentHome) ((MyViewPageStateAdapter) viewPager.getAdapter()).getItem(0)).updateView(connectedDevice);
            }
        }, 500);


        Intent mIntent = new Intent(MainActivity.this, BPMSyncService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mIntent);
        } else {
            startService(mIntent);
        }

//        navigationView.setCheckedItem(R.id.admin_menu_nav_home);
    }

    public void displayLocationSettingsRequest(Context context) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("Google API", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("Google API", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("Google API", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("Google API", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

//    private void doLogOut() {
//        mUtility.ShowProgress();
//        Map<String, String> mHashMap = new HashMap<>();
//        mHashMap.put("user_id", mUtility.getAppPrefString(Constant.PREFS_USER_ID));
//        mHashMap.put("device_token", "54321");
//
//        Call<VOLogOut> loginUser = mApiService.normalUserLogut(mHashMap);
//        loginUser.enqueue(new Callback<VOLogOut>() {
//            @Override
//            public void onResponse(Call<VOLogOut> call, Response<VOLogOut> response) {
//                mUtility.HideProgress();
//
//                VOLogOut mVoLogOut = response.body();
//                if (mVoLogOut != null) {
//                    if (mVoLogOut.getSuccess() != null && mVoLogOut.getSuccess().equalsIgnoreCase("1")) {
//                        mUtility.clearAllPrefData();
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finishAffinity();
//                    } else {
//                        if (mVoLogOut.getMessage() != null) {
//                            mUtility.errorDialogWithTitle(getResources().getString(R.string.app_name),
//                                    mVoLogOut.getMessage());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<VOLogOut> call, Throwable t) {
//                mUtility.HideProgress();
//                t.printStackTrace();
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        try {
            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_main_content_container);
            mFragment.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void replacesFragment(Fragment mFragment, boolean isBackState, Bundle mBundle, int animationType) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//
//        if (isBackState)
//            fragmentTransaction.addToBackStack(null);
//
//        if (mBundle != null)
//            mFragment.setArguments(mBundle);
//
//        fragmentTransaction.replace(R.id.activity_main_main_content_container, mFragment);
//        fragmentTransaction.commitAllowingStateLoss();
//    }

//    public void addFragment(Fragment mFragment, boolean isBackState, Bundle mBundle, int animationType) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//
//        if (isBackState)
//            fragmentTransaction.addToBackStack(null);
//
//        if (mBundle != null)
//            mFragment.setArguments(mBundle);
//
//        fragmentTransaction.add(R.id.activity_main_main_content_container, mFragment);
//        fragmentTransaction.commitAllowingStateLoss();
//    }

//    public void removeAllFragmentFromBack() {
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//    }
//
//    public void removeNumberOfFragmnet(int num) {
//        for (int i = 0; i < num; ++i) {
//            getSupportFragmentManager().popBackStack();
//        }
//    }

    @Override
    public void onBackPressed() {

        System.out.println("onBackPressed() viewPager.getCurrentItem()=" + viewPager.getCurrentItem());
        if (viewPager.getCurrentItem() != 0) {
            moveToFirstTab();
            return;
        }

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count > 0) {
            getSupportFragmentManager().popBackStack();

//            mImageViewDrawer.setVisibility(View.VISIBLE);
//            mImageViewBack.setVisibility(View.GONE);
//            mTextViewHeader.setText(getResources().getString(R.string.text_home));
//            mRelativeLayoutMain.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.dashboard_header_color));


//            navigationView.setCheckedItem(R.id.admin_menu_nav_home);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent mIntent = new Intent(MainActivity.this, BPMSyncService.class);
                startForegroundService(mIntent);
            } else {
                Intent mIntent = new Intent(MainActivity.this, BPMSyncService.class);
                startService(mIntent);
            }

        } else {

            importDB();

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.lbl_exit));
            builder.setCancelable(false);
            builder.setMessage(getResources().getString(R.string.alert_exit));
            builder.setPositiveButton(getResources().getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.btn_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    /**
     * Set pulse count measurement mode.
     *
     * @param mode string of setting mode. "HR" or "PR".
     * @return true is the mode setting success, false is the mode setting failure.
     */
    public static boolean enableControl(final String mode) {

        boolean result = false;

        synchronized (BLE_COM_LOK) {

            BleConstants.HRMeasurementType measurementType = BleConstants.HRMeasurementType.HR_MEASUREMENT_CONTROL_PULSE;
            BleConstants.FWType type = brighton.getFWType(connectedDevice);

            if (mode.equals("HR")) {
                measurementType = BleConstants.HRMeasurementType.HR_MEASUREMENT_CONTROL_ECG;   /* set HR mode */
            } else if (mode.equals("PR")) {
                measurementType = BleConstants.HRMeasurementType.HR_MEASUREMENT_CONTROL_PULSE; /* set PR mode */
            }

            write_result = false; /* initialize write result */
            brighton.setMeasurementType(measurementType); /* set measurement mode */

            try {
                BLE_COM_LOK.wait(5000); /* wait write completion */
            } catch (InterruptedException e) {
            }
        }

        return result;
    }

    public static void connectDevice(final BluetoothDevice selectDevice) {
        if (brighton != null) {
            brighton.selectSensorDevice(selectDevice);
            brighton.connectSensorDevice();
        }
    }

    public static void disConnectDevice() {

        synchronized (BLE_COM_LOK) {
            if (brighton != null) {
                brighton.disconnectSensorDevice();
            }
        }

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (connectedDevice != null) {
                    disConnectDevice();
                    connectedDevice = null;
                }
            }
        }, 500);
    }

    @Override
    public void callbackOnSensorDeviceDetected(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null && bluetoothDevice.getName() != null) {
            if (mDevicesStatus != null) {
//                mDevicesStatus.addScanDevices(bluetoothDevice);
                try {
                    if (viewPager.getCurrentItem() == TabType.HEALTH_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentHealthMode.TAG).addScanDevices(bluetoothDevice);
                    } else if (viewPager.getCurrentItem() == TabType.QUICK_SCAN_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentQuickScan.TAG).addScanDevices(bluetoothDevice);
                    } else if (viewPager.getCurrentItem() == TabType.ACTIVITY_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentActivityMode.TAG).addScanDevices(bluetoothDevice);
                    } else if (viewPager.getCurrentItem() == TabType.SLEEP_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentSleepMode.TAG).addScanDevices(bluetoothDevice);
                    } else {
                        for (Map.Entry<String, DevicesStatus> entry : mDevicesStatus.entrySet()) {
                            entry.getValue().addScanDevices(bluetoothDevice);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void callbackOnSensorConnected(BluetoothDevice bluetoothDevice) {

        connectedDevice = bluetoothDevice;
        System.out.println("callbackOnSensorConnected() called");
//        ((FragmentHome)((MyViewPageStateAdapter)viewPager.getAdapter()).getItem(0)).updateView(connectedDevice);

        if (mDevicesStatus != null) {
//            mDevicesStatus.onConnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
            try {
                if (viewPager.getCurrentItem() == TabType.HEALTH_MODE.tabIndex) {
                    mDevicesStatus.get(FragmentHealthMode.TAG).onConnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                } else if (viewPager.getCurrentItem() == TabType.QUICK_SCAN_MODE.tabIndex) {
                    mDevicesStatus.get(FragmentQuickScan.TAG).onConnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                } else if (viewPager.getCurrentItem() == TabType.ACTIVITY_MODE.tabIndex) {
                    mDevicesStatus.get(FragmentActivityMode.TAG).onConnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                } else if (viewPager.getCurrentItem() == TabType.SLEEP_MODE.tabIndex) {
                    mDevicesStatus.get(FragmentSleepMode.TAG).onConnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                } else {
                    for (Map.Entry<String, DevicesStatus> entry : mDevicesStatus.entrySet()) {
                        entry.getValue().onConnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void callbackOnSensorDisconnected(BluetoothDevice bluetoothDevice) {

        connectedDevice = null;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((FragmentHome) ((MyViewPageStateAdapter) viewPager.getAdapter()).getItem(0)).updateView(connectedDevice);


            }
        });

        if (isDisconnectedFromFragmentHome) {
            isDisconnectedFromFragmentHome = false;
            return;
        }


        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("viewPager.getCurrentItem() =" + viewPager.getCurrentItem());
        System.out.println("tablock =" + tabLock.toString());
        if (mDevicesStatus != null) {

            if (mDevicesStatus.containsKey(FragmentManageDevice.TAG)) {
                Log.d("MainActivity", "Device Disconnected but Device Scanning Screen is visible.");
                return;
            }
            //Below Code might be required in the future.
//            else if (mDevicesStatus.containsKey(ActivityActivityHistory.TAG)) {
//                mDevicesStatus.get(ActivityActivityHistory.TAG).onDisconnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
//                return;
//            }else if (mDevicesStatus.containsKey(ActivityHealthModeHistory.TAG)) {
//                mDevicesStatus.get(ActivityHealthModeHistory.TAG).onDisconnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
//                return;
//            }
            //When the tabLock is on/off, and user is on Dashboard.
            try {
                switch (tabLock) {

                    case ACTIVITY_MODE:
                        mDevicesStatus.get(FragmentActivityMode.TAG).onDisconnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                        break;
                    case HEALTH_MODE:
                        mDevicesStatus.get(FragmentHealthMode.TAG).onDisconnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                        break;
                    case QUICK_SCAN_MODE:
                        mDevicesStatus.get(FragmentQuickScan.TAG).onDisconnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                        break;
                    case SLEEP_MODE:
                        mDevicesStatus.get(FragmentSleepMode.TAG).onDisconnect(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                        break;

                    case NONE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //If the device is disconnected by user(without clicking on disconnect button) and home fragment is selected at that time.
                                //If there is no lock then calling onDisconnect of different fragment can result into multiple 0 value entries in tables.

                                Log.d("MainActivity", "_______________________________________________");
                                Log.d("MainActivity", "No Lock Found.");
                                Log.d("MainActivity", "Moving the tab to 0th index.");
                                Log.d("MainActivity", "_______________________________________________");
                                if (!isFinishing()) {
                                    mUtility.errorDialog(getResources().getString(R.string.alert_connect_device));
                                }
                                moveToFirstTab();

                            }
                        });

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void callbackOnSensorDataReceived(final Object obj, BluetoothDevice bluetoothDevice) {
        synchronized (RECEIVE_DATA_LOCK) {
            if (mDevicesStatus != null) {
//                mDevicesStatus.dataAvailable(obj);
                try {
                    if (viewPager.getCurrentItem() == TabType.HEALTH_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentHealthMode.TAG).dataAvailable(obj);
                    } else if (viewPager.getCurrentItem() == TabType.QUICK_SCAN_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentQuickScan.TAG).dataAvailable(obj);
                    } else if (viewPager.getCurrentItem() == TabType.ACTIVITY_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentActivityMode.TAG).dataAvailable(obj);
                    } else if (viewPager.getCurrentItem() == TabType.SLEEP_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentSleepMode.TAG).dataAvailable(obj);

                    } else {
                        for (Map.Entry<String, DevicesStatus> entry : mDevicesStatus.entrySet()) {
                            entry.getValue().dataAvailable(obj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void callbackOnSensorDataReceived(byte[] bytes, BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void callbackOnSensorDataReceived(BleConstants.StatusParamType dataType, int status, BluetoothDevice bluetoothDevice) {
        if (dataType == BleConstants.StatusParamType.SLEEP_MODE_STATUS) {
            if (mDevicesStatus != null) {
//                mDevicesStatus.dataStatus(status);
                try {
                    if (viewPager.getCurrentItem() == TabType.HEALTH_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentHealthMode.TAG).dataStatus(status);
                    } else if (viewPager.getCurrentItem() == TabType.QUICK_SCAN_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentQuickScan.TAG).dataStatus(status);
                    } else if (viewPager.getCurrentItem() == TabType.ACTIVITY_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentActivityMode.TAG).dataStatus(status);
                    } else if (viewPager.getCurrentItem() == TabType.SLEEP_MODE.tabIndex) {
                        mDevicesStatus.get(FragmentSleepMode.TAG).dataStatus(status);

                    } else {
                        for (Map.Entry<String, DevicesStatus> entry : mDevicesStatus.entrySet()) {
                            entry.getValue().dataStatus(status);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void callbackOnCharacteristicRead(BluetoothGattCharacteristic
                                                     bluetoothGattCharacteristic, boolean b, BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void callbackOnCharacteristicWrite(BluetoothGattCharacteristic
                                                      bluetoothGattCharacteristic, boolean gattStatus, BluetoothDevice bluetoothDevice) {
        String uuidString = bluetoothGattCharacteristic.getUuid().toString();

        if (HR_MEASUREMENT_CONTROL_CHARACTERISTIC_UUID.equals(uuidString)) {
            synchronized (BLE_COM_LOK) {
                write_result = gattStatus; /* set write result */
                BLE_COM_LOK.notify(); /* notify write completion */
            }
        }
    }

    @Override
    public void callbackOnDescriptorRead(BluetoothGattDescriptor bluetoothGattDescriptor,
                                         boolean b, BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void callbackOnDescriptorWrite(BluetoothGattDescriptor bluetoothGattDescriptor,
                                          boolean b, BluetoothDevice bluetoothDevice) {

    }

    public static void setDeviceStatusListner(DevicesStatus mDevicesStatu, String TAG) {
//        mDevicesStatus = mDevicesStatu;
        mDevicesStatus.put(TAG, mDevicesStatu);
        Log.d("MainActivity", "setDeviceStatusListner()");
        Log.d("MainActivity", "XXXXXXXXXXXXXXXXXXXXXXXXXX");
        for (Map.Entry<String, DevicesStatus> entry : mDevicesStatus.entrySet()) {
            Log.d("MainActivity", entry.getKey());
        }
        Log.d("MainActivity", "XXXXXXXXXXXXXXXXXXXXXXXXXX");

    }

    public static void removeDeviceStatusListner(String TAG) {
        mDevicesStatus.remove(TAG);
        Log.d("MainActivity", "removeDeviceStatusListner()");
        Log.d("MainActivity", "YYYYYYYYYYYYYYYYYYYYYYYYYYY");
        for (Map.Entry<String, DevicesStatus> entry : mDevicesStatus.entrySet()) {
            Log.d("MainActivity", entry.getKey());
        }
        Log.d("MainActivity", "YYYYYYYYYYYYYYYYYYYYYYYYYYY");
    }

    @Override
    protected void onDestroy() {

        try {
            if (isRegisterd) {
                isRegisterd = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disConnectDevice();
        super.onDestroy();
    }

    // Now not required put for testing purpose
    public void importDB() {

        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File sd = new File(dir);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null; //sqlite
        String backupDBPath = "data/com.aiosleeve.aiosleeve/databases/AIOSleeve.sqlite";
        String currentDBPath = "AIOSleeve.sqlite";
        File currentDB = new File(data, backupDBPath);
        File backupDB = new File(sd, currentDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void moveToFirstTab() {
        activityMainBinding.bottomNavigationView.selectTabAtPosition(0);
    }

    public String getAverageValue(String values) {
        String result = "";
        if (values.trim().isEmpty()) {
            return result;
        }
        String[] dataValues = values.split(",");
        float totalOfDataValues = 0;

        for (String dataValue : dataValues) {
            totalOfDataValues += Float.parseFloat(dataValue);
        }
        if (dataValues.length != 0) {
            result = getResources().getString(R.string.two_digit_after_decimal, (double) ((float) totalOfDataValues / (float) dataValues.length));
        }
        return result;
    }


    public void syncTheTables() {
        Intent mIntent = new Intent(MainActivity.this, BPMSyncService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mIntent);
        } else {
            startService(mIntent);
        }
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
}
