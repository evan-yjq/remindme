package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.allclassify.domain.model.Classify;
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
    public Map<Classify,List<Task>> display(List<Task>tasks, List<Classify> classifies, ClassifyDataSource.GetClassifyCallback callback) {

        Map<Classify,List<Task>> map = new HashMap<>();

        Collections.sort(tasks);

        map.put(null,tasks);

        return map;
    }
}
