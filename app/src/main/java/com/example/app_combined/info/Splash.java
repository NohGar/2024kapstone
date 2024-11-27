package com.example.app_combined.info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.app_combined.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //화면 계속 켜짐
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //delay 실행
        new Handler().postDelayed(new Runnable() {//3초 후 실행행
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
                finish();

            }
        }, 3000);

    }
}