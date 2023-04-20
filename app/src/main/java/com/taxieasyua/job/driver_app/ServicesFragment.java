package com.taxieasyua.job.driver_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.taxieasyua.job.R;

import java.util.ArrayList;
import java.util.List;


public class ServicesFragment extends Fragment {
    private ListView listView;
    private String[] array = services();
    Activity activity;
    public static final String TAG = "ServicesFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_app_servises, container, false);
        listView = view.findViewById(R.id.list);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.services_adapter_layout, array);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(0,true);

        return view;
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

        List<String> servicesList = new ArrayList<>();
        SparseBooleanArray booleanArray = listView.getCheckedItemPositions();
        for (int i = 0; i < booleanArray.size(); i++) {
            if(booleanArray.get(booleanArray.keyAt(i))) {
                servicesList.add(array[booleanArray.keyAt(i)]);
                Log.d(TAG, "click: " + array[booleanArray.keyAt(i)]);
            }
        }

        try {
            ((Postman) activity).fragmentMailService(servicesList);
        } catch (ClassCastException ignored) {}
    }
    private String[] services () {
        return new String[]{
                "Терминал",
                "Таксі Лайт Юа",
                "UBER",
                "UKLON",
                "BOLT",
                "OnTaxi",
                "838",
                "Lubimoe Taxi",
                "3040",
                "Максім"
        };
    }

}
