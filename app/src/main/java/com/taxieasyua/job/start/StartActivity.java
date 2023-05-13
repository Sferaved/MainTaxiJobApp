package com.taxieasyua.job.start;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.taxieasyua.job.R;
import com.taxieasyua.job.about.AboutActivity;
import com.taxieasyua.job.driver_app.DriverActivity;
import com.taxieasyua.job.search3.Search3Activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import javax.net.ssl.HttpsURLConnection;


public class StartActivity extends Activity {
    private static final String DB_NAME = "DbQuest" ;
    public static final String TABLE_DRIVER_INFO = "driverInfo" ;
    public static final String TABLE_AUTO_INFO = "autoInfo" ;
    public static SQLiteDatabase database;
    public static List<String>  Driver_Info, Auto_Info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_start_layout);

        initDB();


            final FloatingActionButton fabStart = findViewById(R.id.btn_1);
            final FloatingActionButton fabInfo = findViewById(R.id.btn_2);
            final FloatingActionButton fabAdmin = findViewById(R.id.btn_3);
            fabStart.setOnClickListener(view -> {
                if(!hasConnection()) {
                    Toast.makeText(StartActivity.this, "Перевірте інтернет-підключення або зателефонуйте оператору.", Toast.LENGTH_LONG).show();
//                    Intent setIntent = new Intent(Settings.ACTION_SETTINGS);
//                    startActivity(setIntent);
                } else {
                    Intent intent = new Intent(StartActivity.this, DriverActivity.class);
                    try {
                        if (verifyConnection("https://m.easy-order-taxi.site/api/driver").equals("200")) {
                            startActivity(intent);
                            Toast.makeText(StartActivity.this, "Вітаємо. Заповніть будь-ласка всі поля для надсилання заявки", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(StartActivity.this, "Помилка підключення до сервера. Перевірте підключення до Інтернету або спробуйте пізніше.", Toast.LENGTH_LONG).show();
//                            Intent setIntent = new Intent(Settings.ACTION_SETTINGS);
//                            startActivity(setIntent);
                        }
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

            fabInfo.setOnClickListener(view -> {
                if(!hasConnection()) {
                    Toast.makeText(StartActivity.this, "Перевірте інтернет-підключення або зателефонуйте оператору.", Toast.LENGTH_LONG).show();
//                    Intent setIntent = new Intent(Settings.ACTION_SETTINGS);
//                    startActivity(setIntent);
                } else {
                    Intent intent = new Intent(StartActivity.this, AboutActivity.class); //*******************
                    try {
                        if (verifyConnection("https://m.easy-order-taxi.site/api/driver").equals("200")) {
                            startActivity(intent);
                            Toast.makeText(StartActivity.this, "Вітаємо. Ознайомтеся з інформацією про додаток.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(StartActivity.this, "Помилка підключення до сервера. Перевірте інтернет-підключення або зателефонуйте оператору.", Toast.LENGTH_LONG).show();
//                            Intent setIntent = new Intent(Settings.ACTION_SETTINGS);
//                            startActivity(setIntent);
                        }
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            fabAdmin.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:0934066749"));
                if (ActivityCompat.checkSelfPermission(StartActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(StartActivity.this, "Дозвольте застосунку отримати доступ у панелі налаштувань телефону та спробуйте ще.", Toast.LENGTH_LONG).show();
//                    final Intent i = new Intent();
//                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    i.addCategory(Intent.CATEGORY_DEFAULT);
//                    i.setData(Uri.parse("package:" + this.getPackageName()));
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                    this.startActivity(i);
                    return;
                }
                startActivity(intent);
            });

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

    private void initDB(){
        database = this.openOrCreateDatabase( DB_NAME , MODE_PRIVATE , null );
        Log.d("TAG", "initDB: " + database);

        database.execSQL( "CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_INFO + "(id integer primary key autoincrement," +
                " city," +
                " first_name text," +
                " second_name text," +
                " email text," +
                " phone_number text);" );
        database.execSQL( "CREATE TABLE IF NOT EXISTS " + TABLE_AUTO_INFO + "(id integer primary key autoincrement," +
                " brand text," +
                " model text," +
                " type text," +
                " color text," +
                " years text," +
                " number text);" );



//        database.delete( TABLE_DRIVER_INFO, null , null );
//        database.delete( TABLE_AUTO_INFO, null , null );
    }

    public static void insertRecordsDriver(List<String> values){
        String sql = "INSERT INTO " + TABLE_DRIVER_INFO + " VALUES(?,?,?,?,?,?);";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();
        try {
           statement.clearBindings();
           statement.bindString(2, values.get(0));
           statement.bindString(3, values.get(1));
           statement.bindString(4, values.get(2));
           statement.bindString(5, values.get(3));
           statement.bindString(6, values.get(4));

           statement.execute();
           database.setTransactionSuccessful();
            logCursor(TABLE_DRIVER_INFO);
        } finally {
            database.endTransaction();
        }
    }
    public static void updateRecordsDriver(List<String> values){
        ContentValues cv = new ContentValues();

        cv.put("city", values.get(0));
        cv.put("first_name", values.get(1));
        cv.put("second_name", values.get(2));
        cv.put("email", values.get(3));

        // обновляем по id
        int updCount = database.update(TABLE_DRIVER_INFO, cv, "id = ?",
                new String[] { "1" });
        Log.d("TAG", "updated rows count = " + updCount);
        // обновляем по id
        int updCountDriver = database.update(TABLE_DRIVER_INFO, cv, "id = ?",
                new String[] { "1" });
        Log.d("TAG", "updated rows count = " + updCountDriver);

    }
    public static void insertRecordsAuto(List<String> values){

        String sql = "INSERT INTO " + TABLE_AUTO_INFO + " VALUES(?,?,?,?,?,?,?);";

        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();
        try {
           statement.clearBindings();
           statement.bindString(2, values.get(0));
           statement.bindString(3, values.get(1));
           statement.bindString(4, values.get(2));
           statement.bindString(5, values.get(3));
           statement.bindString(6, values.get(4));
           statement.bindString(7, values.get(5));

           statement.execute();
           database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }
    }
    public static void updateRecordsAuto(List<String> values){

        ContentValues cv = new ContentValues();

        cv.put("brand", values.get(0));
        cv.put("model", values.get(1));
        cv.put("type", values.get(2));
        cv.put("color", values.get(3));
        cv.put("years", values.get(4));
        cv.put("number", values.get(5));

        // обновляем по id
        int updCount = database.update(TABLE_AUTO_INFO, cv, "id = ?",
                new String[] { "1" });
        Log.d("TAG", "updated rows count = " + updCount);
    }
    @SuppressLint("Range")
    public static List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        Cursor c = database.query(table, null, null, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                        list.add(c.getString(c.getColumnIndex(cn)));

                    }

                } while (c.moveToNext());
            }
        }
        return list;
    }

}
