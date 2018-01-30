package com.evan.remindme.settings;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.evan.remindme.Injection;
import com.evan.remindme.R;
import com.evan.remindme.util.ActivityUtils;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午5:17
 */
public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_act);

        //设置虚拟按键颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.settings_list_title);

        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (settingsFragment == null){
            settingsFragment = SettingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),settingsFragment,R.id.contentFrame);
        }

        new SettingsPresenter(
                settingsFragment,
                Injection.provideEditSetting(getApplicationContext()),
                Injection.provideGetSettings(getApplicationContext()),
                Injection.provideUseCaseHandler());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
