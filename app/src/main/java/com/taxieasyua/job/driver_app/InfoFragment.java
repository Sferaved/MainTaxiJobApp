package com.taxieasyua.job.driver_app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.taxieasyua.job.R;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, data);

        Spinner spinner = view.findViewById(R.id.list_cities);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");
        spinner.setSelection(0);
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
