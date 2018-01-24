package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.tasks.domain.model.Task;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午9:47
 */
public interface TaskDisplay {
    Map<String, List<Task>> display(List<Task> tasks, String[] rules);
}
