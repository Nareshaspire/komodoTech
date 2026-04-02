package com.aiosleeve.aiosleeve.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.view.ProgressHUD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by oneclick_sanjay on 20/4/16.
 */
public class Utility {

    private Activity mActivity;
    private ProgressHUD mProgressHUD;

    SimpleDateFormat mSimpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat mSimpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");

    public Utility(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void writeSharedPreferencesString(String key, String value) {
        SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

//    public void writeSharedPreferencesStringInfo(String key, String value) {
//        SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_INFO, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }

    public void writeSharedPreferencesInt(String key, int value) {
        SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void writeSharedPreferencesBool(String key, Boolean value) {
        SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void clearAllPrefData() {
        SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
        settings.edit().clear().commit();
    }

    public String getAppPrefString(String key) {
        try {
            SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
            String value = settings.getString(key, "");
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
    // Copy to sdcard for debug use
    public static void copyDatabase(Context c, String DATABASE_NAME) {
        String databasePath = c.getDatabasePath(DATABASE_NAME).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
        Log.d("testing", " testing db path " + databasePath);
        Log.d("testing", " testing db exist " + f.exists());
        if (f.exists()) {
            try {
                File directory = new File("/mnt/sdcard/DB_DEBUG");
                if (!directory.exists())
                    directory.mkdir();
                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + DATABASE_NAME);
                myInput = new FileInputStream(databasePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

//    public String getAppPrefStringInfo(String key) {
//        try {
//            SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_INFO, 0);
//            String value = settings.getString(key, "");
//            return value;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return "";
//        }
//    }

    public Integer getAppPrefInt(String key) {
        try {
            SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
            int value = settings.getInt(key, 0);
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public Boolean getAppPrefBool(String key) {
        try {
            SharedPreferences settings = mActivity.getSharedPreferences(Constant.PREFS_NAME, 0);
            Boolean value = settings.getBoolean(key, false);
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void errorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        if(!mActivity.isFinishing() && !mActivity.isDestroyed()) {
            alert.show();
        }
    }

    public void errorDialogWithTitle(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean haveInternet() {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }
        return true;
    }

    public void hideKeyboard() {
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public final boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void ShowProgress() {
        if (mProgressHUD != null && mProgressHUD.isShowing()) {
            return;
        }

        mProgressHUD = ProgressHUD.showDialog(mActivity, true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    public void HideProgress() {
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    public boolean setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }

    public OkHttpClient getClient() {
        final String encoding = Base64.encodeToString(("ba190" + ":" + "ba190*").getBytes(), Base64.DEFAULT);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.connectTimeout(3, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);
        httpClient.writeTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Basic " + encoding.trim())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        return client;
    }

    public OkHttpClient getSimpleClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();

        return client;
    }

    public OkHttpClient getSimpleClientWithLogger() {
        HttpLoggingInterceptor logging=new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();
    }

    public String getDateTime() {
        return mSimpleDateFormatDateTime.format(Calendar.getInstance().getTime());
    }

    public String getDate() {
        return mSimpleDateFormatDate.format(Calendar.getInstance().getTime());
    }

    public static boolean isSDK17() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isSDK23() {
        return android.os.Build.VERSION.SDK_INT >= 23;
    }

    public String removeLastComma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public String getProperTimeFormat(int intTime) {
        if (intTime < 10) {
            return "0" + intTime;
        } else {
            return "" + intTime;
        }
    }

    public void changeStatusbarColor(int colorCode) {

        Window window = mActivity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(mActivity, colorCode));
        }
    }

    public String getTimeDiffrence(String startDate, String endDate) {

        try {
            Date mDateStart = mSimpleDateFormatDateTime.parse(startDate);
            Date mDateEnd = mSimpleDateFormatDateTime.parse(endDate);

            long diff = mDateEnd.getTime() - mDateStart.getTime();

            return "" + (int) (diff / (1000));

        } catch (Exception e) {

        }
        return "";
    }
}