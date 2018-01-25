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
    public List<Task> display(List<Sort>sorts) {
        List<Task> displayedTasks = new ArrayList<>();

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
            Task s = new Task();
            s.setId((long) -1);
            s.setTitle(sort.getName());
            s.setSortId(sort.getId());
            displayedTasks.add(s);
            displayedTasks.addAll(list);
        }

        return displayedTasks;
    }

    @Override
    public void setList(List<Task> tasks) {
        mTasks = tasks;
    }
}
