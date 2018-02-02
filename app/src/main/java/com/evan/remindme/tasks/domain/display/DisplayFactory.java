package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.tasks.TasksDisplayType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午12:38
 */
public class DisplayFactory {

    private static final Map<TasksDisplayType,TaskDisplay>mDisplays = new HashMap<>();

    public DisplayFactory(){
        mDisplays.put(TasksDisplayType.TASKS_BY_CLASSIFY, new TasksDisplayByClassify());
        mDisplays.put(TasksDisplayType.TASKS_BY_TIME, new TasksDisplayByTime());
    }

    public TaskDisplay create(TasksDisplayType displayType){
        return mDisplays.get(displayType);
    }
}
