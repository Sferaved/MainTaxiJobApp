package com.taxieasyua.job.driver_app;


import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.RED;
import static com.taxieasyua.job.start.StartActivity.Auto_Info;
import static com.taxieasyua.job.start.StartActivity.TABLE_AUTO_INFO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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


public class AutoFragment extends Fragment {
    public static EditText modelAuto;
    public static EditText colorAuto;
    public static EditText yearsAuto;
    public static EditText numberAuto;
    private String[] autos = autos();
    private String[] types = types();
    Activity activity;

    public static String auto, type_auto;
    public static final String TAG = "AutoFragment";
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_app_auto, container, false);
        modelAuto = view.findViewById(R.id.model_auto);
        colorAuto = view.findViewById(R.id.color_auto);
        yearsAuto = view.findViewById(R.id.years_auto);
        numberAuto =  view.findViewById(R.id.number_auto);

        Auto_Info = logCursor(TABLE_AUTO_INFO);

        ArrayAdapter<String> adapterAutos = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, autos);
        Spinner spinnerAutos = view.findViewById(R.id.list_auto);
        spinnerAutos.setAdapter(adapterAutos);
        spinnerAutos.setPrompt("Title");
        int i;
        Log.d("TAG", "onCreateView: Auto_Info.size():" + Auto_Info.size() + "autos.length" + autos.length);
        if (Auto_Info.size() == 7) {
            modelAuto.setText(Auto_Info.get(2));
            colorAuto.setText(Auto_Info.get(4));
            yearsAuto.setText(Auto_Info.get(5));
            numberAuto.setText(Auto_Info.get(6));
            for (i = 0; i < autos.length; i++) {
                if (autos[i].equals(Auto_Info.get(1))) {
                    spinnerAutos.setSelection(i);
                    auto = autos[i];

                    break;
                }
            }

            if (i == autos.length) {
                spinnerAutos.setSelection(autos.length - 1);
            }
        } else
            spinnerAutos.setSelection(0);


        spinnerAutos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                auto = autos[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, types);
        Spinner spinnerTypes = view.findViewById(R.id.type_auto);
        spinnerTypes.setAdapter(adapterTypes);
        spinnerTypes.setPrompt("Title");

        if (Auto_Info.size() == 7) {
            for (i = 0; i < types.length; i++) {
                Log.d(TAG, "types[i].equals(Auto_Info.get(3)): " + types[i].equals(Auto_Info.get(3)));
                if(types[i].equals(Auto_Info.get(3))) {
                    spinnerTypes.setSelection(i);
                    type_auto = types[i];
                    break;
                }
            }
        } else spinnerTypes.setSelection(0);
        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type_auto = types[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (modelAuto.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                modelAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }
        if (colorAuto.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }if (yearsAuto.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                yearsAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }
        if (numberAuto.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                numberAuto.setBackgroundTintList(ColorStateList.valueOf(RED));
            }
        }
    }

    private String[] autos () {
        return new String[]{
                "Toyota",
                "Mercedes",
                "BMW",
                "Honda",
                "BMW",
                "Volkswagen",
                "Ford",
                "Hyundai",
                "Renault",
                "Skoda",
                "Kia",
                "Hyundai",
                "Nissan",
                "Chery",
                "Mitsubishi",
                "Suzuki",
                "інше",};
    }
    private String[] types () {
        return new String[]{
                "седан",
                "універсал",
                "хетчбек",
                "мікроавтобус",
                "мінівен",
                "позашляховик",
                "вантажний (менше 4 посадочних місць)",
                "купе",
                "кабріолет",
                "пікап"
        };
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
        List<String> autoList = new ArrayList<>();

        autoList.add(auto);
        autoList.add(modelAuto.getText().toString());
        autoList.add(type_auto);
        autoList.add(colorAuto.getText().toString());
        autoList.add(yearsAuto.getText().toString());
        autoList.add(numberAuto.getText().toString());

        try {
            ((Postman) activity).fragmentMailAuto(autoList);
        } catch (ClassCastException ignored) {}

    }
    @SuppressLint("Range")
    private List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        @SuppressLint({"NewApi", "LocalSuppress"}) SQLiteDatabase database = getContext().openOrCreateDatabase( StartActivity.DB_NAME , MODE_PRIVATE , null );
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
        if (c != null) {
            c.close();
        }
        database.close();
        return list;
    }
}
