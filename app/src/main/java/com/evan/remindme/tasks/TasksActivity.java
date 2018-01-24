package com.evan.remindme.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.evan.remindme.R;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午9:00
 */
public class TasksActivity extends AppCompatActivity{

    //当前视图
    private static final String CURRENT_DISPLAY_KEY = "CURRENT_DISPLAY_KEY";

    private DrawerLayout mDrawerLayout;

    private TasksPresenter mTasksPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //设置侧边栏
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
//            setupDrawerContent(navigationView);
        }

        TasksFragment tasksFragment = (TasksFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null){
            //创建fragment
            tasksFragment = TasksFragment.newInstance();
        }

    }
}
