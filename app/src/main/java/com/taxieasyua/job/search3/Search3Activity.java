package com.taxieasyua.job.search3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.taxieasyua.job.R;

public class Search3Activity extends Activity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = findViewById(R.id.countries_list);
        textView.setAdapter(adapter);

        textView.setOnItemClickListener((parent, view, position, id)
                -> Log.d("TAG", "onCreate: " +  adapter.getItem(position)));


    }

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain", "Belgium2", "France2", "Italy2", "Germany2", "Spain2", "Belgium3", "France3", "Italy3", "Germany3", "Spain3"
    };
}
