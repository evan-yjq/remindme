package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.sorts.domain.model.Sort;
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
    public Map<Sort,List<Task>> display(List<Task>tasks,List<Sort>sorts) {
        Map<Sort,List<Task>> displayedTasks = new HashMap<>();

        if (sorts==null||tasks==null){
            displayedTasks.put(new Sort("默认"),tasks);
            return displayedTasks;
        }

        int count = 0;
        for (Sort sort : sorts) {
            List<Task>list = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getSortId().equals(sort.getId())){
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
            displayedTasks.put(sort,list);
        }
        List<Task> tmp = new ArrayList<>();
        if (count < tasks.size()){
            Sort s = new Sort();
            for (Task task : tasks) {
                int i = 0;
                for (Sort sort : sorts) {
                    if (Objects.equals(sort.getName(),"默认"))
                        s = sort;
                    if (Objects.equals(task.getSortId(),(sort.getId())))
                        break;
                    i++;
                }
                if (i == sorts.size())
                    tmp.add(task);
            }
            displayedTasks.get(s).addAll(tmp);
            Collections.sort(displayedTasks.get(s));
        }

        return displayedTasks;
    }
}
