package com.taxieasyua.job.about;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.taxieasyua.job.R;
import com.taxieasyua.job.driver_app.DriverActivity;
import com.taxieasyua.job.start.StartActivity;


public class AboutActivity extends AppCompatActivity implements ActionBar.TabListener{
    private AuthorFragment firstFragment;
    private GdprFragment secondFragment;
    private ContractFragment thirdFragment;
    private Intent intent;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_main_layout);



        firstFragment = new AuthorFragment();
        secondFragment = new GdprFragment();
        thirdFragment = new ContractFragment();

        fragmentManager = getFragmentManager();


        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar.newTab();
        tab.setText(getString(R.string.author));
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText(getString(R.string.confidantional));
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText(getString(R.string.contract));
        tab.setTabListener(this);
        bar.addTab(tab);
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

    private void initFragment (int tab) {
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (tab) {
        case 1:
        fragmentTransaction.add(R.id.frame_layout, firstFragment, AuthorFragment.TAG).commit();
        break;
        case 2:
        fragmentTransaction .add(R.id.frame_layout, secondFragment, GdprFragment.TAG).commit();
        break;
        case 3:
        fragmentTransaction .add(R.id.frame_layout,thirdFragment, ContractFragment.TAG).commit();
        break;
        }

        }

    private void removeFragment () {
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragmentManager.findFragmentByTag(AuthorFragment.TAG) != null) {
        fragmentTransaction.remove(firstFragment).commit();
        };

        if(fragmentManager.findFragmentByTag(GdprFragment.TAG) != null) {
        fragmentTransaction.remove(secondFragment).commit();
        };
        if(fragmentManager.findFragmentByTag(ContractFragment.TAG) != null) {
        fragmentTransaction.remove(thirdFragment).commit();
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.main:
                 intent = new Intent(this, DriverActivity.class);
                startActivity(intent);
                this.finish();

                break;
            case R.id.friend:
                String subject = "Андроид-додаток для влаштування на роботу.";
                String body = "Мої вітання, друже. \n \n Знайшов чудовий додаток для влаштування на роботу водієм. \n \n Раджу спробувати за посиланням в офіційному магазині Google: \n\n https://play.google.com/store/apps/details?id=com.taxieasyua.job \n\n Гарного дня, друже. \n Ще побачимось.";

                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Порадити другові додаток..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutActivity.this, "Поштовий клієнт не встановлено.", Toast.LENGTH_SHORT).show();
                }
                this.finish();
                break;
        }
        return false;
    }
}

