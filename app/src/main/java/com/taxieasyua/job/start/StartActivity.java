package com.taxieasyua.job.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.taxieasyua.job.R;
import com.taxieasyua.job.driver_app.DriverActivity;


public class StartActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_start_layout);
    }

    public void onClick (View view) {
        Intent intent = new Intent(this, DriverActivity.class);
        startActivity(intent);
    }

}
