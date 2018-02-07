package com.evan.remindme.addedittask;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import com.evan.remindme.Injection;
import com.evan.remindme.R;
import com.evan.remindme.allclassify.domain.model.Classify;
import com.evan.remindme.util.ActivityUtils;

import java.util.Date;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/1
 * Time: 下午5:10
 */
public class AddEditTaskActivity extends AppCompatActivity{

    public static final int REQUEST_ADD_TASK = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";
    public static final String SELECT_CLASSIFY = "SELECT_CLASSIFY";
    public static final String SELECT_DATE = "SELECT_DATE";

    private ActionBar mActionBar;

    private AddEditTaskPresenter mAddEditTaskPresenter;
    private AddEditTaskFragment mAddEditTaskFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        //设置虚拟按键颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        String taskId = getIntent().getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID);
        setToolbarTitle(taskId);

        mAddEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mAddEditTaskFragment == null){
            //创建fragment
            mAddEditTaskFragment = AddEditTaskFragment.newInstance();
            if (getIntent().hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)) {
                Bundle bundle = new Bundle();
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                mAddEditTaskFragment.setArguments(bundle);
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),mAddEditTaskFragment,R.id.contentFrame);
        }

        boolean shouldLoadDataFromRepo = true;

        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }

        mAddEditTaskPresenter = new AddEditTaskPresenter(mAddEditTaskFragment,
                Injection.provideGetTask(getApplicationContext()),
                Injection.provideGetAllClassify(getApplicationContext()),
                Injection.provideUseCaseHandler(),
                Injection.provideGetClassify(getApplicationContext()),
                Injection.provideSaveTask(getApplicationContext()),
                Injection.provideSaveClassify(getApplicationContext()),
                taskId,shouldLoadDataFromRepo);
        if (savedInstanceState != null) {
            mAddEditTaskPresenter.setDate((Date)savedInstanceState.getSerializable(SELECT_DATE));
            mAddEditTaskPresenter.setClassify((Classify) savedInstanceState.getSerializable(SELECT_CLASSIFY));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN&&mAddEditTaskFragment.isActive()) {
            mAddEditTaskFragment.hiddenInput();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the state so that next time we know if we need to refresh data.
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddEditTaskPresenter.isDataMissing());
        outState.putSerializable(SELECT_CLASSIFY,mAddEditTaskPresenter.getClassify());
        outState.putSerializable(SELECT_DATE,mAddEditTaskPresenter.getDate());
        super.onSaveInstanceState(outState);
    }

    private void setToolbarTitle(@Nullable String taskId) {
        if(taskId == null) {
            mActionBar.setTitle("添加提醒");
        } else {
            mActionBar.setTitle("修改提醒");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
