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
public class TasksDisplayByClassify implements TaskDisplay {
    @Override
    public Map<Classify,List<Task>> display(List<Task>tasks, List<Classify> classifies,
                                            ClassifyDataSource.GetClassifyCallback callback) {
        Map<Classify,List<Task>> displayedTasks = new HashMap<>();

        if (classifies ==null||tasks==null){
            displayedTasks.put(new Classify((long)1,"默认"),tasks);
            return displayedTasks;
        }

        int count = 0;
        for (Classify classify : classifies) {
            List<Task>list = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getClassifyId().equals(classify.getId())){
                    list.add(task);
                    count++;
                }
                //设置count，如果count==数据总长
                //就可以提前停止循环，避免浪费系统资源
                if (count==tasks.size())break;
            }
            if (list.isEmpty())continue;
            Collections.sort(list);
            displayedTasks.put(classify,list);
        }
        List<Task> tmp = new ArrayList<>();
        if (count < tasks.size()){
            Classify s = null;
            for (Task task : tasks) {
                int i = 0;
                for (Classify classify : classifies) {
                    if (Objects.equals(classify.getName(),"默认"))
                        s = classify;
                    if (Objects.equals(task.getClassifyId(),(classify.getId())))
                        break;
                    i++;
                }
                if (i == classifies.size())
                    tmp.add(task);
            }
            if (s == null){
                s = new Classify((long)1,"默认");
                callback.onClassifyLoaded(s);
                Collections.sort(tmp);
                displayedTasks.put(s,tmp);
            }else {
                displayedTasks.get(s).addAll(tmp);
                Collections.sort(displayedTasks.get(s));
            }
        }

        return displayedTasks;
    }
}
