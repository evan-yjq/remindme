package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.tasks.domain.model.Sort;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午12:41
 */
public class TasksDisplayBySort implements TaskDisplay {
    private List<Task>mTasks;
    @Override
    public Map<String,List<Task>> display(List<Sort>sorts) {
        Map<String,List<Task>> displayedTasks = new HashMap<>();

        int count = 0;
        for (Sort sort : sorts) {
            List<Task>list = new ArrayList<>();
            for (Task task : mTasks) {
                if (task.getSortId().equals(sort.getId())){
                    list.add(task);
                    count++;
                }
                /*
                 * 设置count，如果count==数据总长
                 * 就可以提前停止循环，避免浪费系统资源
                 */
                if (count==mTasks.size())break;
            }
            Collections.sort(list);
            displayedTasks.put(sort.getName(),list);
        }
        List<Task> tmp = new ArrayList<>();
        if (count < mTasks.size()){
            for (Task task : mTasks) {
                int i = 0;
                for (Sort sort : sorts) {
                    if (task.getSortId().equals(sort.getId()))
                        break;
                    i++;
                }
                if (i == sorts.size())
                    tmp.add(task);
            }
            displayedTasks.get("默认").addAll(tmp);
            Collections.sort(displayedTasks.get("默认"));
        }

        return displayedTasks;
    }

    @Override
    public void setList(List<Task> tasks) {
        mTasks = tasks;
    }
}
