package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.tasks.domain.model.Task;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午12:41
 */
public class TasksDisplayBySort implements TaskDisplay {
    @Override
    public Map<String, List<Task>> display(List<Task> tasks, String[] rules) {
        Map<String, List<Task>> displayedTasks = new HashMap<>();

        int count = 0;
        for (String rule : rules) {
            List<Task>list = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getSortId().toString().equals(rule)){
                    list.add(task);
                    count++;
                }
                /*
                 * 设置count，如果count==数据总长
                 * 就可以提前停止循环，避免浪费系统资源
                 */
                if (count==tasks.size())break;
            }
            Collections.sort(list);
            displayedTasks.put(rule,list);
        }

        return displayedTasks;
    }
}
