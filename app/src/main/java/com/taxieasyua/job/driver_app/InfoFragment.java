package com.taxieasyua.job.driver_app;

import static com.taxieasyua.job.start.StartActivity.Driver_Info;
import static com.taxieasyua.job.start.StartActivity.TABLE_AUTO_INFO;
import static com.taxieasyua.job.start.StartActivity.TABLE_DRIVER_INFO;
import static com.taxieasyua.job.start.StartActivity.logCursor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.taxieasyua.job.R;
import com.taxieasyua.job.start.StartActivity;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment{
    EditText firstName;
    EditText secondName;
    EditText email;
    EditText phoneNumber;
    Activity activity;
    public static String city;

    private String[] data = cities();
    public static final String TAG = "InfoFragment";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_app_info, container, false);
        firstName = view.findViewById(R.id.first_name);
        secondName =  view.findViewById(R.id.second_name);
        email = view.findViewById(R.id.email);
        phoneNumber =  view.findViewById(R.id.phone_number);

        Driver_Info = logCursor(TABLE_DRIVER_INFO);
        if (Driver_Info.size() == 6) {
            firstName.setText(StartActivity.Driver_Info.get(2));
            secondName.setText(StartActivity.Driver_Info.get(3));
            email.setText(StartActivity.Driver_Info.get(4));
            phoneNumber.setText(StartActivity.Driver_Info.get(5));
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, data);

        Spinner spinner = view.findViewById(R.id.list_cities);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");
//        Log.d(TAG, "StartActivity.Driver_Info.get(1):" + StartActivity.Driver_Info.get(1));
        if (Driver_Info.size() == 6) {
            for (int i = 0; i < data.length; i++) {
//                Log.d(TAG, "data[i].equals(StartActivity.Driver_Info.get(1): " + data[i].equals(StartActivity.Driver_Info.get(1)));
                if(data[i].equals(StartActivity.Driver_Info.get(1))) {
                    spinner.setSelection(i);
                    city = data[i];
                    break;
                }
            }
        } else spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    private String[] cities () {
        return new String[]{
                "Київ",
                "Вінниця",
                "Дніпро",
                "Донецьк",
                "Житомир",
                "Запоріжжя",
                "Івано-Франківськ",
                "Кропивницький",
                "Луганськ",
                "Луцьк",
                "Львів",
                "Миколаїв",
                "Одеса",
                "Полтава",
                "Рівне",
                "Суми",
                "Тернопіль",
                "Ужгород",
                "Харків",
                "Херсон",
                "Хмельницький",
                "Черкаси",
                "Чернігів",
                "Чернівці"};
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        List<String> infoList = new ArrayList<>();

        infoList.add(city);
        infoList.add(firstName.getText().toString());
        infoList.add(secondName.getText().toString());
        infoList.add(email.getText().toString());
        infoList.add(phoneNumber.getText().toString());

        try {
            ((Postman) activity).fragmentMailInfo(infoList);
        } catch (ClassCastException ignored) {}

    }

    public void onClick (View view) {

    }
}
