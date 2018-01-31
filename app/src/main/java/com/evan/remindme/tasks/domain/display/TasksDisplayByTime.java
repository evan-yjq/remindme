package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.data.source.SortsDataSource;
import com.evan.remindme.sorts.domain.model.Sort;
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
    public Map<Sort,List<Task>> display(List<Task>tasks, List<Sort> sorts, SortsDataSource.GetSortCallback callback) {

        Map<Sort,List<Task>> map = new HashMap<>();

        Collections.sort(tasks);

        map.put(null,tasks);

        return map;
    }
}
