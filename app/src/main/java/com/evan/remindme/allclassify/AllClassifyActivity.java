package com.evan.remindme.allclassify;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.evan.remindme.Injection;
import com.evan.remindme.R;
import com.evan.remindme.util.ActivityUtils;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/28
 * Time: 下午10:20
 */
public class AllClassifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_act);

        //设置虚拟按键颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(R.string.all_classify_list_title);

        AllClassifyFragment allClassifyFragment = (AllClassifyFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (allClassifyFragment == null){
            //创建fragment
            allClassifyFragment = AllClassifyFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), allClassifyFragment,R.id.contentFrame);
        }

        //创建presenter
        new AllClassifyPresenter(
                Injection.provideUseCaseHandler(),
                allClassifyFragment,
                Injection.provideGetAllClassify(getApplicationContext()),
                Injection.provideSaveClassify(getApplicationContext()),
                Injection.provideDeleteClassify(getApplicationContext()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
