package com.evan.remindme.tasks.domain.display;

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
    public Map<String, List<Task>> display(List<Task> tasks, String[] rules) {
        Map<String, List<Task>> displayedTasks = new HashMap<>();

        Collections.sort(tasks);

        displayedTasks.put("",tasks);

        return displayedTasks;
    }
}
