package com.taxieasyua.job.start;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.taxieasyua.job.R;
import com.taxieasyua.job.driver_app.DriverActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class StartActivity extends Activity {
    private final String TAG = "TAG";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_start_layout);
    }

    public void onClick (View view) {
        Intent intent = new Intent(this, DriverActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Вітаємо. Заповніть будь-ласка всі поля для надсилання заявки", Toast.LENGTH_SHORT).show();

    }



}
