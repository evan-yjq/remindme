package com.evan.remindme.tasks.domain.display;

import android.support.annotation.NonNull;
import com.evan.remindme.tasks.domain.model.Sort;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午12:41
 */
public class TasksDisplayByTime implements TaskDisplay{

    private List<Task>mTasks;

    @Override
    public void setList(List<Task> tasks) {
        mTasks = tasks;
    }

    @Override
    public List<Task> display(List<Sort> sorts) {

        Collections.sort(mTasks);

        return mTasks;
    }
}
