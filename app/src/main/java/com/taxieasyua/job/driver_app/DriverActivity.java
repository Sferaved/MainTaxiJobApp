package com.taxieasyua.job.driver_app;

import static android.graphics.Color.RED;
import static com.taxieasyua.job.start.StartActivity.Auto_Info;
import static com.taxieasyua.job.start.StartActivity.Driver_Info;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.taxieasyua.job.R;
import com.taxieasyua.job.about.AboutActivity;
import com.taxieasyua.job.start.StartActivity;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import javax.net.ssl.HttpsURLConnection;

public class DriverActivity extends AppCompatActivity implements Postman, ActionBar.TabListener, Dialog.OnClickListener  {
    private Driver driver;
    private Intent intent;
    public static List<String> infoList;
    public static List<String> autoList;
    private List<String> servicesList;
    private boolean valid;

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
    private Button bigBtnSend;
    public static ProgressBar progressBar;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_app_main_layout);
        infoFragment = new InfoFragment();
        autoFragment = new AutoFragment();
        try {
            servicesFragment = new ServicesFragment();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        bigBtnSend = findViewById(R.id.big_btn_send);
        progressBar = findViewById(R.id.progressBar);

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
            case R.id.taxi:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.taxi.easy.ua"));
                startActivity(browserIntent);
                break;

            case R.id.exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("  ")
                        .setCancelable(false)
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setMessage("Бажаєте завершити?")
                        .setNegativeButton("Ні", ((dialog, which) -> {
                            Toast.makeText(DriverActivity.this, "Продовжуйте заповнення анкети", Toast.LENGTH_SHORT).show();
                        }))
                        .setPositiveButton("Так", ((dialog, which) -> {
                            StartActivity.database.close();
                            finishAffinity();
                        }));
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }

        return false;
    }

    protected void sendEmail() throws IOException, InterruptedException {
        pauseFragment();

        if (verifyComplete())  {
            Log.d("TAG", "sendEmail: " + autoList.get(0));

            StringBuilder serviceSend = new StringBuilder();
            for (String value :servicesList) {
                serviceSend.append(value);
                serviceSend.append("*");
            }

            String autoSend = "https://m.easy-order-taxi.site/api/driverAuto" +
                    "/" +
                    infoList.get(0) +
                    "/" +
                    infoList.get(1) +
                    "/" +
                    infoList.get(2) +
                    "/" +
                    infoList.get(3) +
                    "/" +
                    infoList.get(4) +
                    "/" +
                    autoList.get(0) +
                    "/" +
                    autoList.get(1) +
                    "/" +
                    autoList.get(2) +
                    "/" +
                    autoList.get(3) +
                    "/" +
                    autoList.get(4) +
                    "/" +
                    autoList.get(5) +
                    "/" +
                    serviceSend;

            if (Driver_Info.size() == 0) {
                StartActivity.insertRecordsDriver(infoList);
            } else {
                StartActivity.updateRecordsDriver(infoList);
            }

            if (Auto_Info.size() == 0) {
                StartActivity.insertRecordsAuto(autoList);
            } else {
                StartActivity.updateRecordsAuto(autoList);
            }

            URL url = new URL(autoSend);
            Log.d("TAG", "sendEmail: " + url);
            if(sendURL(url) == 200) {
                bigBtnSend.setEnabled(false);
                showNotification("Повідомлення відправлено. Зачекайте на відповідь оператора за телефоном або в месенджерах.");
                startActivity(new Intent(this, StartActivity.class));
            } else {
                showNotification("Немає зв'язку із сервером. Спробуйте пізніше.");
            }

        }
        else {
            Toast.makeText(this, "Перед надсиланням перевірте всі поля Анкети.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        bigBtnSend.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    private boolean verifyComplete() {
        infoComplete = true;
        Log.d("TAG", "verifyComplete infoList: " + infoList.toString());

        if (infoList.get(1).equals("")) {
            infoComplete = false;
        }

        if (infoList.get(4).equals("")) {
            infoComplete = false;
        }
        if (infoList.get(2).equals("")) {
            infoList.set(2, "не вказав");
        }
        if (infoList.get(3).equals("")) {
            infoList.set(3, "не вказав");
        }


        if (autoList.get(1).equals("")) {
            infoComplete = false;
        }
        if (autoList.get(3).equals("")) {
            infoComplete = false;
        }if (autoList.get(4).equals("")) {
            infoComplete = false;
        }
        if (autoList.get(5).equals("")) {
            infoComplete = false;
        }

        for (int i = 0; i < autoList.size(); i++) {
            if (autoList.get(i).equals("")) {
                infoComplete = false;
                break;}
        }

        if (AutoFragment.modelAuto != null && AutoFragment.modelAuto.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AutoFragment.modelAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }
        if (AutoFragment.colorAuto != null && AutoFragment.colorAuto.getText().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AutoFragment.colorAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }

        if (AutoFragment.yearsAuto != null && AutoFragment.yearsAuto.getText().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AutoFragment.yearsAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }
        if (AutoFragment.numberAuto != null && AutoFragment.numberAuto.getText().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AutoFragment.numberAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }


        if (!isValidPhoneNumber(InfoFragment.phoneNumber.getText().toString())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                InfoFragment.phoneNumber.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }
        if (InfoFragment.firstName.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                InfoFragment.firstName.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }


        return infoComplete;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^\\+380\\d{9}$";
        return phoneNumber.matches(regex);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        showNotification("До побачення. Чекаємо наступного разу.");
    }
    public void showNotification(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.auto_dialog_layout, null);
        builder.setTitle("Вкажіть марку Вашого автомобіля:")
                .setView(view)
                .setPositiveButton( "Зберегти", this);
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
        autoList.set(0, nameTxt.getText().toString());
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Збережено: "  + autoList.get(0), Toast.LENGTH_SHORT).show();
        try {
            sendEmail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer sendURL (URL url) throws InterruptedException {

        Exchanger<Integer> exchanger = new Exchanger<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    int message = urlConnection.getResponseCode();
                    Log.d("TAG", "run message: " + message);
                    exchanger.exchange(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                urlConnection.disconnect();
            }
        });
        DriverActivity.ResultFromThread first = new DriverActivity.ResultFromThread(exchanger);
        return first.message;
    }
    public static class ResultFromThread {
        public Integer message;

        public ResultFromThread(Exchanger<Integer> exchanger) throws InterruptedException {
            this.message = exchanger.exchange(message);
        }

    }
    public void onClickBigBtnSend(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if(InfoFragment.phoneNumber != null && isValidPhoneNumber(InfoFragment.phoneNumber.getText().toString())) {
            if(autoList.get(0).equals("інше")){
                showDialog(DIALOG);
            } else {
                try {
                    sendEmail();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }






}
