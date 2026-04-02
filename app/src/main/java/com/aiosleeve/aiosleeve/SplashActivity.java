package com.aiosleeve.aiosleeve;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aiosleeve.aiosleeve.helper.Constant;
import com.aiosleeve.aiosleeve.helper.Utility;

public class SplashActivity extends AppCompatActivity {

    Utility mUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mUtility=new Utility(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mUtility.getAppPrefString(Constant.PREFS_USER_ID).equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }else {
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        }, 3000);
    }

}
