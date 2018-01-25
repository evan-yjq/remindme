package com.evan.remindme.data.source;

import android.support.annotation.NonNull;
import com.evan.remindme.tasks.domain.model.Task;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午9:55
 */
public interface TasksDataSource {
    interface LoadTasksCallback{

        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    interface GetTaskCallback{

        void onTaskLoaded(Task task);

        void onDataNotAvailable();
    }

    void turnOnTask(@NonNull Task task);

    void turnOnTask(@NonNull Long taskId);

    void turnOffTask(@NonNull Task task);

    void turnOffTask(@NonNull Long taskId);

    void updateTask(@NonNull Task task);

    void getTasks(@NonNull LoadTasksCallback callback);

    void getTask(@NonNull Long taskId, @NonNull GetTaskCallback callback);

    void saveTask(@NonNull Task task);

    void refreshTasks();

    void deleteTask(@NonNull Long taskId);

    void deleteAllTasks();
}
