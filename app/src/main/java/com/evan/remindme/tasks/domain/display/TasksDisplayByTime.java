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

    @Override
    public Map<String,List<Task>> display(List<Task>tasks,List<Sort> sorts) {

        Map<String,List<Task>> map = new HashMap<>();

        Collections.sort(tasks);

        map.put("",tasks);

        return map;
    }
}
