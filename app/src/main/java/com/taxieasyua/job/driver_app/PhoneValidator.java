package com.taxieasyua.job.driver_app;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Exchanger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class PhoneValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String PHONE_PATTERN =
            "((\\+?380)(\\d{9}))$";

    public PhoneValidator() {
        pattern = Pattern.compile(PHONE_PATTERN);
    }

    public boolean validate(final String hex) {
        matcher = pattern.matcher(hex);

        return matcher.matches();
    }

//String urlString = "https://m.easy-order-taxi.site/api/driverAuto/sendCode/+380936734488"
    public String sendCode (String urlString) throws MalformedURLException, InterruptedException {
        URL url = new URL(urlString);
        final String TAG = "TAG";

        Exchanger<String> exchanger = new Exchanger<>();

        AsyncTask.execute(() -> {
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if (urlConnection.getResponseCode() == 200) {
//                    Log.d(TAG, "urlConnection.getResponseMessage() " + urlConnection.getResponseMessage());
//                    Log.d(TAG, "urlConnection.getResponseCode() " + urlConnection.getResponseCode());
                    StringBuffer buffer = new StringBuffer();
                    InputStream is = urlConnection.getInputStream();
                    byte[] b = new byte[3];
                    while ( is.read(b) != -1)
                        buffer.append(new String(b));
                    exchanger.exchange(buffer.toString());
                } else {

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            urlConnection.disconnect();
        });

        ResultFromThread first = new ResultFromThread(exchanger);
//        Log.d(TAG, "sendCode: " + first.message);
        return first.message;
    }

    public static class ResultFromThread {
        public String message;

        public ResultFromThread(Exchanger<String> exchanger) throws InterruptedException {
            this.message = exchanger.exchange(message);
        }

    }



}