package com.evan.remindme.tasks.domain.display;

import com.evan.remindme.data.source.ClassifyDataSource;
import com.evan.remindme.allclassify.domain.model.Classify;
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
    Map<Classify,List<Task>> display(List<Task>tasks, List<Classify> classifies, ClassifyDataSource.GetClassifyCallback callback);
}
