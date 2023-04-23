package com.taxieasyua.job.driver_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.taxieasyua.job.R;
import com.taxieasyua.job.about.AboutActivity;
import com.taxieasyua.job.start.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DriverActivity extends AppCompatActivity implements Postman, ActionBar.TabListener, Dialog.OnClickListener  {
    private Driver driver;
    private Intent intent;
    private List<String> infoList;
    private List<String> autoList;
    private List<String> servicesList;
    private boolean valid;
    private final int NOTIFICATION_ID = 127;
    private final String TAG = "TAG";
    private EmailValidator emailValidator;
    private PhoneValidator phoneValidator;

    private InfoFragment infoFragment;
    private AutoFragment autoFragment;
    private ServicesFragment servicesFragment;
    private  FragmentManager fragmentManager;
    android.app.FragmentTransaction fragmentTransaction;
    boolean infoComplete;
    private final int DIALOG = 1;
    private EditText nameTxt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_app_main_layout);

        infoFragment = new InfoFragment();
        autoFragment = new AutoFragment();
        servicesFragment = new ServicesFragment();

        infoList = initArray(5);
        autoList = initArray(6);
        servicesList = new ArrayList<>();

        infoComplete = true;

        fragmentManager = getFragmentManager();

        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar.newTab();
        tab.setText(getString(R.string.services_info));
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText(getString(R.string.auto_info));
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText(getString(R.string.contact_info));

        tab.setTabListener(this);
        bar.addTab(tab);


        emailValidator = new EmailValidator();
        phoneValidator = new PhoneValidator();

    }

    private ArrayList<String> initArray (int length) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            arrayList.add("");
        }
        return arrayList;
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()) {
            case 0: initFragment(1); break;
            case 1: initFragment(2); break;
            case 2: initFragment(3); break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {removeFragment();}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu_items, menu);
        return true;
    }
    private void initFragment (int tab) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (tab) {
            case 1:
                fragmentTransaction .add(R.id.frame_layout, servicesFragment, ServicesFragment.TAG).commit();
                break;
            case 2:
                fragmentTransaction .add(R.id.frame_layout, autoFragment, AutoFragment.TAG).commit();
                break;
            case 3:
                fragmentTransaction.add(R.id.frame_layout, infoFragment, InfoFragment.TAG).commit();
                break;
        }

    }

    private void removeFragment () {
        fragmentTransaction = fragmentManager.beginTransaction();
        if(fragmentManager.findFragmentByTag(InfoFragment.TAG) != null) {
            fragmentTransaction.remove(infoFragment).commit();
        };

        if(fragmentManager.findFragmentByTag(AutoFragment.TAG) != null) {
            fragmentTransaction.remove(autoFragment).commit();
        };
        if(fragmentManager.findFragmentByTag(ServicesFragment.TAG) != null) {
            fragmentTransaction.remove(servicesFragment).commit();
        };
    }

    private void pauseFragment () {
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragmentManager.findFragmentByTag(InfoFragment.TAG) != null) {
            infoFragment.onPause();
        };

        if(fragmentManager.findFragmentByTag(AutoFragment.TAG) != null) {
            autoFragment.onPause();
        };
        if(fragmentManager.findFragmentByTag(ServicesFragment.TAG) != null) {
            servicesFragment.onPause();
        };
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.send_message:
                try {
                    sendEmail();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Таксі Лайт Юа")
                        .setCancelable(false)
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setMessage("Бажаєте завершити?")
                        .setNegativeButton("Нет", ((dialog, which) -> {
                            Toast.makeText(DriverActivity.this, "Продовжуйте заповнення анкети", Toast.LENGTH_SHORT).show();
                        }))
                        .setPositiveButton("Да", ((dialog, which) -> {
                            finish();
                            Intent intent = new Intent(this, StartActivity.class);
                            startActivity(intent);
                        }));
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }

        return false;
    }

    protected void sendEmail() throws IOException {
        pauseFragment();



        infoComplete = true;
        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).equals("")) {
                infoComplete = false;
                break;
            }
        }
        for (int i = 0; i < autoList.size(); i++) {
            if (autoList.get(i).equals("")) {
                infoComplete = false;
                break;}
        }
        if (infoComplete == false) {
            Toast.makeText(this, "Вибачьте. Вказано не всі дані. Відправка заявки неможлива.", Toast.LENGTH_SHORT).show();
        } else if (isValid(infoList)) {

            if(autoList.get(0).equals("інше")){
                showDialog(DIALOG);
                infoComplete = true;
            } else {
                StringBuilder serviceSend = new StringBuilder();
                for (String value :servicesList) {
                    serviceSend.append(value);
                    serviceSend.append("*");
                }
                Log.d(TAG, "sendEmail: " + servicesList);
                StringBuilder autoSend = new StringBuilder();
                autoSend
                        .append("https://m.easy-order-taxi.site/api/driverAuto")
                        .append("/")
                        .append(infoList.get(0))
                        .append("/")
                        .append(infoList.get(1))
                        .append("/")
                        .append(infoList.get(2))
                        .append("/")
                        .append(infoList.get(3))
                        .append("/")
                        .append(infoList.get(4))
                        .append("/")
                        .append(autoList.get(0))
                        .append("/")
                        .append(autoList.get(1))
                        .append("/")
                        .append(autoList.get(2))
                        .append("/")
                        .append(autoList.get(3))
                        .append("/")
                        .append(autoList.get(4))
                        .append("/")
                        .append(autoList.get(5))
                        .append("/")
                        .append(serviceSend);

                Log.d(TAG, "sendEmail: " + autoSend);
                URL url = new URL(autoSend.toString());
                sendURL(url);
                showNotification();
                this.finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "До побачення. Чекаємо наступного разу.", Toast.LENGTH_SHORT).show();
    }

    public void showNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                .setTicker("New notification")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Повідомлення надіслано адміністратору.")
                .setContentText("Очікуйте відповіді на електронну пошту або дзвінок.");

        Notification notification = builder.build();

        long[] vibrate = {1500,1000, 1500, 1000};
        notification.vibrate = vibrate;
        notification.flags = notification.flags | Notification.FLAG_INSISTENT;
        manager.notify(NOTIFICATION_ID, notification);
        Toast.makeText(this, "Повідомлення надіслано адміністратору.", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void fragmentMailInfo(List<String> infoList) {
        this.infoList = infoList;
    }
    @Override
    public void fragmentMailAuto(List<String> autoList) {
        this.autoList = autoList;
    }
    @Override
    public void fragmentMailService(List<String> servicesList) {
        this.servicesList = servicesList;
    }


    private boolean isValid(List <String> infoList) {
        valid = true;

        if(!emailValidator.validate(infoList.get(3))){
            valid = false;
            Toast.makeText(this, "Перевірте формат вводу електронної пошти: " + infoList.get(3), Toast.LENGTH_SHORT).show();
        };

        if(!phoneValidator.validate(infoList.get(4))){
            valid = false;
            Toast.makeText(this, "Формат вводу номера телефону: +380936665544", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.auto_dialog_layout, null);
        builder.setTitle("Вкажіть марку Вашого автомобіля:")
                .setView(view)
                .setPositiveButton( "Зберегти", this)
                .setNegativeButton("Скасувати", null);
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG) {
            nameTxt = dialog.getWindow().findViewById(R.id.text_add);

        }
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.autoList.set(0, nameTxt.getText().toString());
        Toast.makeText(this, "Збережено: "  + autoList.get(0), Toast.LENGTH_SHORT).show();
    }

    private void sendURL (URL url) {
        AsyncTask.execute(() -> {

            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (urlConnection.getResponseCode() == 200) {
                    Log.d(TAG, "run: " + urlConnection.getResponseCode());
                } else {
                    Log.d(TAG, "run: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            urlConnection.disconnect();
        });
    }
}
