package com.taxieasyua.job.start;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.taxieasyua.job.R;
import com.taxieasyua.job.driver_app.DriverActivity;
import com.taxieasyua.job.driver_app.PhoneValidator;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Exchanger;

import javax.net.ssl.HttpsURLConnection;


public class StartActivity extends Activity {
    private final String TAG = "TAG";
    private String resp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_start_layout);
//        ActivityManager am = (ActivityManager) this
//                .getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
//
//        for (int i = 0; i < rs.size(); i++) {
//            ActivityManager.RunningServiceInfo rsi = rs.get(i);
//            Log.i("Service", "Process " + rsi.process + " with component "
//                    + rsi.service.getClassName());
//        }
        if(!hasConnection()) {
            Toast.makeText(this, "Перевірте інтернет-підключення", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void onClick (View view) throws MalformedURLException, InterruptedException {
        if(!hasConnection()) {
            Toast.makeText(this, "Перевірте інтернет-підключення", Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            Intent intent = new Intent(this, DriverActivity.class);
            if (verifyConnection("https://m.easy-order-taxi.site/api/driver").equals("200")) {
                startActivity(intent);
                Toast.makeText(this, "Вітаємо. Заповніть будь-ласка всі поля для надсилання заявки", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Помилка підключення до сервера. Перевірте підключення до Інтернету або спробуйте пізніше.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public String verifyConnection (String urlString) throws MalformedURLException, InterruptedException {

        URL url = new URL(urlString);
        final String TAG = "TAG";

        Exchanger<String> exchanger = new Exchanger<>();

        AsyncTask.execute(() -> {
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if (urlConnection.getResponseCode() == 200) {
                    Log.d(TAG, "urlConnection.getResponseMessage() " + urlConnection.getResponseMessage());
                    Log.d(TAG, "urlConnection.getResponseCode() " + urlConnection.getResponseCode());
                    StringBuffer buffer = new StringBuffer();
                    InputStream is = urlConnection.getInputStream();
                    byte[] b = new byte[3];
                    while ( is.read(b) != -1)
                        buffer.append(new String(b));
                    exchanger.exchange(buffer.toString());
                } else {
                    exchanger.exchange("400");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            urlConnection.disconnect();
        });

        StartActivity.ResultFromThread first = new ResultFromThread(exchanger);
        Log.d(TAG, "sendCode: " + first.message);
        return first.message;
    }

    public static class ResultFromThread {
        public String message;

        public ResultFromThread(Exchanger<String> exchanger) throws InterruptedException {
            this.message = exchanger.exchange(message);
        }

    }

    public boolean hasConnection() {

        ConnectivityManager cm = (ConnectivityManager) StartActivity.this.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

}
