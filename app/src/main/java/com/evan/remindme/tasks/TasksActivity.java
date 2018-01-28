package com.evan.remindme.tasks;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import com.evan.remindme.Injection;
import com.evan.remindme.R;
import com.evan.remindme.util.ActivityUtils;

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

        //设置虚拟按键颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.list_title);

        //设置侧边栏
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        TasksFragment tasksFragment = (TasksFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null){
            //创建fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),tasksFragment,R.id.contentFrame);
        }

        //创建presenter
        mTasksPresenter = new TasksPresenter(
                Injection.provideUseCaseHandler(),
                tasksFragment,
                Injection.provideGetTasks(getApplicationContext()),
                Injection.provideTurnOnTask(getApplicationContext()),
                Injection.provideTurnOffTasks(getApplicationContext()),
                Injection.provideSaveTasks(getApplicationContext()),
                Injection.provideOpenSort(getApplicationContext()),
                Injection.provideCloseSort(getApplicationContext()),
                Injection.provideGetSortByName(getApplicationContext()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //加载之前保存的状态（如果可用）。
        if (savedInstanceState != null) {
            TasksDisplayType currentDisplaying =
                    (TasksDisplayType) savedInstanceState.getSerializable(CURRENT_DISPLAY_KEY);
            mTasksPresenter.setDisplay(currentDisplaying);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_DISPLAY_KEY,mTasksPresenter.getDisplaying());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //从工具栏中选择主图标时，打开导航抽屉。
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.list_navigation_menu_item:
                        //已经再次界面，什么都不用做
                        break;
                    case R.id.statistics_navigation_menu_item:
                        //TODO 等页面做完再来跳转
                        break;
                    default:
                        break;
                }
                //当选择一个项目时，关闭导航抽屉。
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
