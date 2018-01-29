package com.evan.remindme.sorts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
 * Time: 下午10:20
 */
public class SortsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorts_act);

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
        ab.setTitle(R.string.sorts_list_title);

        SortsFragment sortsFragment = (SortsFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (sortsFragment == null){
            //创建fragment
            sortsFragment = SortsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),sortsFragment,R.id.contentFrame);
        }

        //创建presenter
        new SortsPresenter(
                Injection.provideUseCaseHandler(),
                sortsFragment,
                Injection.provideGetSorts(getApplicationContext()),
                Injection.provideSaveSort(getApplicationContext()),
                Injection.provideDeleteSort(getApplicationContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
